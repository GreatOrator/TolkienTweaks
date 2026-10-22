#!/usr/bin/perl
#
# 1.21.4 gave Item.Properties and BlockBehaviour.Properties a registry id, and the
# game reads it in the block/item constructor (drops, description id, item model).
# DeferredRegister can only stamp that id when the properties object is handed to
# it, so every `() -> new Thing(..., new Item.Properties())` registration has to
# become `(name, p -> new Thing(..., p), new Item.Properties())`.
#
# This moves the properties expression out of the factory argument and renames the
# call to the DeferredRegister helper that sets the id. Everything else in the call
# (the other constructor arguments, method chains after the constructor) is left
# byte-for-byte alone, and the properties text keeps its own line breaks.
#
#   tools/port-registry-ids.pl blocks src/main/java/.../TolkienBlocks.java
#   tools/port-registry-ids.pl items  src/main/java/.../TolkienItems.java
#
# Pass --dry-run to only report what it would do. It is *not* idempotent: it only
# rewrites calls whose factory still builds the properties, so a second run finds
# nothing left to do.

use strict;
use warnings;

my $mode = shift @ARGV or die "usage: $0 <blocks|items> <file> [--dry-run]\n";
my $file = shift @ARGV or die "usage: $0 <blocks|items> <file> [--dry-run]\n";
my $dry  = grep { $_ eq '--dry-run' } @ARGV;

die "mode must be blocks or items\n" unless $mode eq 'blocks' || $mode eq 'items';

my %callee_out = $mode eq 'blocks'
    ? (
        'registerBlock'                => 'registerBlock',
        'registerNoItem'               => 'registerNoItem',
        'registerBlockWithoutBlockItem' => 'registerBlockWithoutBlockItem',
        'BLOCKS.register'              => 'registerBlockWithoutBlockItem',
    )
    : ('ITEMS.register' => 'ITEMS.registerItem');

open my $fh, '<', $file or die "cannot read $file: $!\n";
my $text = do { local $/; <$fh> };
close $fh;

sub lineno {
    my ($upto) = @_;
    my $pre = substr($text, 0, $upto);
    return ($pre =~ tr/\n//) + 1;
}

# Returns the index just past the closing quote of the string starting at $i.
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

# Returns the index of the bracket that closes the one at $open, or -1.
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

# Splits on top-level commas and returns [start, end] pairs relative to $s.
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

sub is_props {
    my ($arg) = @_;
    return $arg =~ /^\s*\(*new\s+Item\.Properties\s*\(/
        if $mode eq 'items';
    return $arg =~ /^\s*\(*(?:[A-Za-z0-9_]+\.)*Properties\s*\.\s*(?:of|ofFullCopy|ofLegacyCopy)\s*\(/;
}

my $out   = '';
my $pos   = 0;
my $done  = 0;
my @fails;

while ($text =~ /\b(registerBlockWithoutBlockItem|registerBlock|registerNoItem|BLOCKS\.register|ITEMS\.register)\s*\(\s*"/g) {
    my $callee = $1;
    my $cs     = $-[1];
    my $ce     = $+[1];
    my $line   = lineno($cs);

    next unless exists $callee_out{$callee};

    my $open = index($text, '(', $ce);
    my $close = $open < 0 ? -1 : scan_to_close($text, $open);
    if ($close < 0) { push @fails, "$line: unbalanced $callee call"; last; }

    my $inner = substr($text, $open + 1, $close - $open - 1);
    my @args  = split_args($inner);
    if (@args != 2) {
        push @fails, "$line: $callee has " . scalar(@args) . ' arguments: '
            . join(' | ', map { substr($inner, $_->[0], $_->[1] - $_->[0]) } @args);
        $pos = $close + 1;
        next;
    }

    my ($ns, $ne) = @{$args[0]};
    my $name_arg  = substr($inner, $ns, $ne - $ns);
    if ($name_arg !~ /^\s*"[a-z0-9_]+"\s*$/) {
        push @fails, "$line: $callee name argument is not a plain string";
        $pos = $close + 1;
        next;
    }

    my ($ls, $le) = @{$args[1]};
    my $lam = substr($inner, $ls, $le - $ls);
    my ($head) = $lam =~ /^(\s*\(\)\s*->\s*)/s;
    if (!defined $head) {
        push @fails, "$line: $callee factory is not a () -> lambda";
        $pos = $close + 1;
        next;
    }
    my $body = substr($lam, length $head);

    my ($cls) = $body =~ /^\s*new\s+([A-Za-z0-9_.]+)\s*\(/;
    if (!defined $cls) {
        push @fails, "$line: $callee factory does not start with a constructor call";
        $pos = $close + 1;
        next;
    }

    my $ctor_open  = index($body, '(');
    my $ctor_close = scan_to_close($body, $ctor_open);
    if ($ctor_close < 0) {
        push @fails, "$line: unbalanced constructor in $callee";
        $pos = $close + 1;
        next;
    }
    my $ctor_inner = substr($body, $ctor_open + 1, $ctor_close - $ctor_open - 1);
    my $suffix     = substr($body, $ctor_close + 1);
    my @ctor_args  = split_args($ctor_inner);

    my @props = grep {
        my ($s, $e) = @{$ctor_args[$_]};
        is_props(substr($ctor_inner, $s, $e - $s));
    } (0 .. $#ctor_args);

    if (@props != 1) {
        push @fails, "$line: $callee/$cls has " . scalar(@props) . ' properties arguments';
        $pos = $close + 1;
        next;
    }

    my ($ps, $pe) = @{$ctor_args[$props[0]]};
    my $props = substr($ctor_inner, $ps, $pe - $ps);
    $props =~ s/^\s+//;
    $props =~ s/\s+$//;
    # `(new Item.Properties()).stacksTo(1)` and `((new Item.Properties()))` only wrap the
    # constructor needlessly; the hoisted argument reads better without them.
    $props =~ s/^\((new\s+Item\.Properties\(\))\)/$1/;

    my $before = substr($ctor_inner, 0, $ps);
    my $after  = substr($ctor_inner, $pe);
    my $repl   = ($ps > 0 && $before =~ /,\s*$/) ? ' properties' : 'properties';
    my $new_ctor_inner = $before . $repl . $after;

    my $stmt_start = rindex($text, "\n", $cs) + 1;
    my ($stmt_indent) = substr($text, $stmt_start, $cs - $stmt_start) =~ /^(\s*)/;
    my $indent = $stmt_indent . '        ';

    my $callee_name = $callee_out{$callee};

    # A plain `new Item(props)` is what DeferredRegister.Items#registerSimpleItem is for.
    if ($mode eq 'items' && $cls eq 'Item' && @ctor_args == 1 && $suffix =~ /^\s*$/) {
        $out .= substr($text, $pos, $cs - $pos) . 'ITEMS.registerSimpleItem(' . $name_arg . ', ' . $props . ')';
        $pos  = $close + 1;
        $done++;
        next;
    }

    my $new_head = $head;
    $new_head =~ s/\(\)/properties/;
    my $new_body = substr($body, 0, $ctor_open + 1) . $new_ctor_inner . ')' . $suffix;
    my $prefix   = substr($inner, 0, $ls);
    my $new_inner = $prefix . $new_head . $new_body . ",\n" . $indent . $props;

    $out .= substr($text, $pos, $cs - $pos) . $callee_name . '(' . $new_inner . ')';
    $pos  = $close + 1;
    $done++;
}

$out .= substr($text, $pos);

print "$file: $done registration" . ($done == 1 ? '' : 's') . " rewritten\n";
if (@fails) {
    print "  left alone:\n";
    print "    $_\n" for @fails;
}

unless ($dry) {
    open my $wh, '>', $file or die "cannot write $file: $!\n";
    print $wh $out;
    close $wh;
}
