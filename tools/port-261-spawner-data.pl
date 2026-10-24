#!/usr/bin/perl
#
# 26.1 moved the spawn weight out of MobSpawnSettings.SpawnerData and onto the
# builder, so the record is (type, minCount, maxCount) and the weight is the
# second argument of addSpawn:
#
#   addSpawn(cat, new SpawnerData(TYPE, 10, 1, 4))   // 1.21.4
#   addSpawn(cat, 10, new SpawnerData(TYPE, 1, 4))   // 26.1
#
# Only the two-argument addSpawn(category, SpawnerData) form is rewritten. A bare
# SpawnerData built for a WeightedList (AddSpawnsBiomeModifier takes one now) has no
# weight to move and is left alone - those sites are few and need a choice of
# container, so they are done by hand.
#
#   tools/port-261-spawner-data.pl [--dry-run]

use strict;
use warnings;
use File::Find;

my $root = 'src/main/java';
my $dry  = grep { $_ eq '--dry-run' } @ARGV;

my @files;
find( sub { push @files, $File::Find::name if /\.java$/ }, $root );

sub skip_string {
    my ($s, $i, $q) = @_;
    my $n = length $s;
    $i++;
    while ($i < $n) {
        my $c = substr($s, $i, 1);
        if ($c eq '\\') { $i += 2; next; }
        return $i + 1 if $c eq $q;
        $i++;
    }
    return $n;
}

sub scan_to_close {
    my ($s, $open) = @_;
    my $n = length $s;
    my $depth = 0;
    for (my $i = $open; $i < $n; $i++) {
        my $c = substr($s, $i, 1);
        if ($c eq '"')  { $i = skip_string($s, $i, '"') - 1; next; }
        if ($c eq "'")  { $i = skip_string($s, $i, "'") - 1; next; }
        $depth++ if $c eq '(' || $c eq '[' || $c eq '{';
        if ($c eq ')' || $c eq ']' || $c eq '}') {
            $depth--;
            return $i if $depth == 0;
        }
    }
    return -1;
}

sub split_args {
    my ($s) = @_;
    my @ranges;
    my $depth = 0;
    my $start = 0;
    my $n = length $s;
    for (my $i = 0; $i < $n; $i++) {
        my $c = substr($s, $i, 1);
        if ($c eq '"') { $i = skip_string($s, $i, '"') - 1; next; }
        if ($c eq "'") { $i = skip_string($s, $i, "'") - 1; next; }
        if ($c eq '(' || $c eq '[' || $c eq '{') { $depth++; next; }
        if ($c eq ')' || $c eq ']' || $c eq '}') { $depth--; next; }
        if ($c eq ',' && $depth == 0) {
            push @ranges, [$start, $i];
            $start = $i + 1;
        }
    }
    push @ranges, [$start, $n];
    return @ranges;
}

sub trim { my $s = shift; $s =~ s/^\s+//; $s =~ s/\s+$//; return $s }

my $done = 0;
my $files_changed = 0;
my @skipped;

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    local $/;
    my $c = <$in>;
    close $in;
    my $orig = $c;

    my $pos = 0;
    while ($c =~ /\baddSpawn\s*\(/g) {
        my $open  = index($c, '(', $+[0] - 1);
        my $close = scan_to_close($c, $open);
        next if $open < 0 || $close < 0;
        next if $open < $pos;

        my $inner = substr($c, $open + 1, $close - $open - 1);
        my @args  = split_args($inner);
        next unless @args == 2;

        my $second = trim(substr($inner, $args[1][0], $args[1][1] - $args[1][0]));
        unless ($second =~ /^new\s+MobSpawnSettings\.SpawnerData\s*\(/) {
            push @skipped, "$f:" . (1 + (substr($c, 0, $open) =~ tr/\n//));
            next;
        }

        my $ctor_open  = index($second, '(');
        my $ctor_close = scan_to_close($second, $ctor_open);
        my $ctor_inner = substr($second, $ctor_open + 1, $ctor_close - $ctor_open - 1);
        my @spawn      = map { trim(substr($ctor_inner, $_->[0], $_->[1] - $_->[0])) } split_args($ctor_inner);
        unless (@spawn == 4) {
            push @skipped, "$f:" . (1 + (substr($c, 0, $open) =~ tr/\n//));
            next;
        }

        my $cat  = substr($inner, $args[0][0], $args[0][1] - $args[0][0]);
        my $repl = $cat . ', ' . $spawn[1] . ', new MobSpawnSettings.SpawnerData('
                 . join(', ', $spawn[0], $spawn[2], $spawn[3]) . ')';

        substr($c, $open + 1, $close - $open - 1, $repl);
        $close = $open + 1 + length $repl;
        $pos = $close + 1;
        pos($c) = $close + 1;
        $done++;
    }

    if ( $c ne $orig ) {
        unless ($dry) {
            open my $out, '>:raw', $f or die "write $f: $!";
            print $out $c;
            close $out;
        }
        $files_changed++;
    }
}

printf "spawner data: %d addSpawn calls rewritten, %d files changed%s\n",
    $done, $files_changed, $dry ? ' (dry run)' : '';
print "  left for hand work: $_\n" for @skipped;
