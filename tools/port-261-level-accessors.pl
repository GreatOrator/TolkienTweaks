#!/usr/bin/perl
#
# 26.1 made Level.isClientSide and Level.random private/protected behind
# accessors, so every direct field read has to become a call:
#
#   level.isClientSide        -> level.isClientSide()
#   level.random.nextInt(4)   -> level.getRandom().nextInt(4)
#
# The first is safe across the whole tree - `.isClientSide` has no other meaning,
# and the lookahead leaves `isClientSide()` alone. The second is *not*: plenty of
# objects have a public `random` field of their own, so the `random` rewrite only
# touches the lines javac complained about.
#
#   tools/port-261-level-accessors.pl <javac.log>
#
# Run the tree-wide javac first, then this, then javac again. Idempotent: a second
# run finds `.isClientSide` followed by `(` and no flagged line left to change.

use strict;
use warnings;
use File::Find;

my $log = shift @ARGV or die "usage: $0 <javac.log>\n";

my $root = 'src/main/java';

# file -> { line => 1 } for the "random has protected access in Level" errors.
my %random_lines;
open my $lh, '<', $log or die "cannot read $log: $!\n";
while (my $l = <$lh>) {
    next unless $l =~ /^(src[^:]+):(\d+): error: random has protected access in Level/;
    # javac reports Windows paths; File::Find hands us the other separator.
    my ($f, $line) = ($1, $2);
    $f =~ s{\\}{/}g;
    $random_lines{$f}{$line} = 1;
}
close $lh;

my @files;
find( sub { push @files, $File::Find::name if /\.java$/ }, $root );

my $sided = 0;
my $rands = 0;
my $changed = 0;

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    local $/;
    my $c = <$in>;
    close $in;
    my $orig = $c;

    my $hits = () = $c =~ /\.isClientSide\b(?!\s*\()/g;
    $sided += $hits;
    $c =~ s/\.isClientSide\b(?!\s*\()/.isClientSide()/g;

    if (my $lines = $random_lines{$f}) {
        my @out;
        my $n = 0;
        for my $line (split /(?<=\n)/, $c) {
            $n++;
            if ($lines->{$n}) {
                my $h = () = $line =~ /\.random\b/g;
                $rands += $h;
                $line =~ s/\.random\b/.getRandom()/g;
            }
            push @out, $line;
        }
        $c = join '', @out;
    }

    if ( $c ne $orig ) {
        open my $out, '>:raw', $f or die "write $f: $!";
        print $out $c;
        close $out;
        $changed++;
    }
}

printf "level accessors: %d isClientSide() calls, %d getRandom() calls, %d files changed\n",
    $sided, $rands, $changed;
