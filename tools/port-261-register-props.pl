#!/usr/bin/perl
#
# 26.1 stopped taking a built Properties as the last argument of the DeferredRegister
# item/block helpers: the third argument is a Supplier<Properties> (or a
# UnaryOperator<Properties>) now, so the registry can build it and stamp the id itself.
#
#   registerItem("x", p -> new X(p), new Item.Properties())    // 1.21.4
#   registerItem("x", p -> new X(p))                            // 26.1, the common case
#   registerItem("x", p -> new X(p), () -> new Item.Properties().stacksTo(1))
#
# A plain `new Item.Properties()` is exactly what the two-argument overload supplies,
# so the argument is dropped; anything else is wrapped in a supplier, which builds
# the same object at the same point it was built before.
#
#   tools/port-261-register-props.pl [--dry-run]
#
# Idempotent: a second run finds no un-supplied Properties argument.

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
        if ($c eq '/' && substr($s, $i + 1, 1) eq '/') {
            my $e = index($s, "\n", $i);
            $i = $e < 0 ? $n : $e;
            next;
        }
        if ($c eq '/' && substr($s, $i + 1, 1) eq '*') {
            my $e = index($s, '*/', $i);
            $i = $e < 0 ? $n : $e + 1;
            next;
        }
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

my $calls = 0;
my $dropped = 0;
my $wrapped = 0;
my $files_changed = 0;

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    local $/;
    my $c = <$in>;
    close $in;
    my $orig = $c;

    my $pos = 0;
    while ($c =~ /\b(registerItem|registerSimpleItem|registerSimpleBlockItem|registerBlockItem|registerBlock)\s*\(/g) {
        my $cs    = $-[0];
        my $open  = index($c, '(', $+[1]);
        my $close = scan_to_close($c, $open);
        next if $open < 0 || $close < 0;
        next if $cs < $pos;

        my $inner = substr($c, $open + 1, $close - $open - 1);
        my @args  = split_args($inner);
        next if @args < 2;

        my ($ls, $le) = @{$args[-1]};
        my $last = substr($inner, $ls, $le - $ls);
        my $trim = $last;
        $trim =~ s/^\s+//;
        $trim =~ s/\s+$//;

        # Already supplied, an operator, or not a Properties expression at all.
        next unless $trim =~ /^new\s+(?:Item|BlockBehaviour)\.Properties\s*\(/;

        $calls++;

        my $kept   = substr($inner, 0, $ls);
        my $before = $kept;
        $before =~ s/,\s*\z//;

        if ($trim eq 'new Item.Properties()' || $trim eq 'new BlockBehaviour.Properties()') {
            # The two-argument overload builds exactly this.
            substr($c, $open + 1, $close - $open - 1, $before);
            $close = $open + 1 + length $before;
            $dropped++;
        } else {
            # `() -> ` goes after whatever separates it from the previous argument,
            # so an argument on its own line stays on its own line.
            my ($ws) = $last =~ /^(\s*)/;
            $ws = ' ' unless length $ws;
            my $repl = $kept . $ws . '() -> ' . $trim;
            substr($c, $open + 1, $close - $open - 1, $repl);
            $close = $open + 1 + length $repl;
            $wrapped++;
        }
        $pos    = $close + 1;
        pos($c) = $close + 1;
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

printf "register properties: %d calls (%d dropped, %d wrapped), %d files changed%s\n",
    $calls, $dropped, $wrapped, $files_changed, $dry ? ' (dry run)' : '';
