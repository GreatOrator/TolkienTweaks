#!/usr/bin/perl
# 26.1 removed Player#displayClientMessage(Component, boolean). The flag it took
# chose between chat and the action bar, and the two are separate methods now:
# sendSystemMessage(Component) for chat, sendOverlayMessage(Component) for the
# action bar. 1.21.4 introduced displayClientMessage as the replacement for
# Player#sendSystemMessage, so this moves that pass onto the 26.1 pair.
#
# The message argument is matched with balanced parentheses, and the flag is the
# text after the last top-level comma. A call in a comment is left alone.
use strict;
use warnings;

my @files = @ARGV;
die "usage: port-261-chat-messages.pl <files...>\n" unless @files;

my $total = 0;
for my $file (@files) {
    open(my $in, '<', $file) or die "open $file: $!";
    local $/;
    my $src = <$in>;
    close $in;

    my $out = '';
    my $changed = 0;
    while ($src =~ /\.displayClientMessage\(/g) {
        my $start = pos($src) - length('.displayClientMessage(');

        # Take the text from the start of the line up to the match: if a line
        # comment opens in it, this call is commented out and stays as it is.
        my $lineStart = rindex($src, "\n", $start) + 1;
        my $prefix = substr($src, $lineStart, $start - $lineStart);
        if ($prefix =~ m{//}) {
            $out .= substr($src, 0, pos($src));
            $src = substr($src, pos($src));
            pos($src) = 0;
            next;
        }

        # Find the closing paren, and the last top-level comma inside it.
        my $depth = 1;
        my $i = pos($src);
        my $lastComma = -1;
        while ($depth > 0 && $i < length $src) {
            my $c = substr($src, $i, 1);
            if ($c eq '(') {
                $depth++;
            } elsif ($c eq ')') {
                $depth--;
            } elsif ($c eq ',' && $depth == 1) {
                $lastComma = $i;
            }
            $i++;
        }
        die "$file: unbalanced call at offset $start\n" if $depth != 0;
        die "$file: no flag argument at offset $start\n" if $lastComma < 0;

        my $arg = substr($src, pos($src), $lastComma - pos($src));
        $arg =~ s/^\s+|\s+$//g;
        my $flag = substr($src, $lastComma + 1, $i - 1 - $lastComma - 1);
        $flag =~ s/^\s+|\s+$//g;

        my $method;
        if ($flag eq 'true') {
            $method = 'sendOverlayMessage';
        } elsif ($flag eq 'false') {
            $method = 'sendSystemMessage';
        } elsif (length $flag) {
            die "$file: unknown displayClientMessage flag '$flag'\n";
        } else {
            die "$file: missing displayClientMessage flag at offset $start\n";
        }

        $out .= substr($src, 0, $start) . ".$method($arg)";
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
