#!/usr/bin/perl
# 1.21.4 keeps sendSystemMessage on ServerPlayer only, so the mod's `player.sendSystemMessage(text)`
# calls on a plain Player have to become `player.displayClientMessage(text, false)`.
# The argument is matched with balanced parentheses because it is usually a nested
# Component.translatable(...).withStyle(...) chain.
use strict;
use warnings;

my @files = @ARGV;
die "usage: port-chat-messages.pl <files...>\n" unless @files;

my $total = 0;
for my $file (@files) {
    open(my $in, '<', $file) or die "open $file: $!";
    local $/;
    my $src = <$in>;
    close $in;

    my $out = '';
    my $changed = 0;
    while ($src =~ /\.sendSystemMessage\(/g) {
        my $start = pos($src) - length('.sendSystemMessage(');
        my $depth = 1;
        my $i = pos($src);
        while ($depth > 0 && $i < length $src) {
            my $c = substr($src, $i, 1);
            $depth++ if $c eq '(';
            $depth-- if $c eq ')';
            $i++;
        }
        die "$file: unbalanced call at offset $start\n" if $depth != 0;
        my $arg = substr($src, pos($src), $i - pos($src) - 1);

        $out .= substr($src, 0, $start);
        $out .= ".displayClientMessage($arg, false)";
        $src = substr($src, $i);
        pos($src) = 0;
        $changed++;
    }
    $out .= $src;

    if ($changed) {
        open(my $fh, '>', $file) or die "write $file: $!";
        print $fh $out;
        close $fh;
        $total += $changed;
        print "$file: $changed\n";
    }
}

print "rewrote $total calls\n";
