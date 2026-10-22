#!/usr/bin/env perl
# GeckoLib calls preRender again for every render layer, with isReRender set. The old 1.21.1
# render(...) override scaled the pose stack once per entity, so the scale has to be skipped on
# those re-renders or a layer ends up scaled twice.
use strict;
use warnings;

my $changed = 0;
for my $file (@ARGV) {
    open my $in, '<', $file or die "open $file: $!";
    local $/;
    my $src = <$in>;
    close $in;

    next unless $src =~ /public void preRender\(/;
    next if $src =~ /if \(!isReRender\)/;

    my $eol = $src =~ /\r\n/ ? "\r\n" : "\n";
    my $orig = $src;

    $src =~ s{
        (public\s+void\s+preRender\([^)]*\)\s*\{\r?\n)   # signature line
        (.*?)                                           # body, up to the super call
        (\s*super\.preRender\([^;]*\);\r?\n)            # the super call itself
    }{
        my ($head, $body, $tail) = ($1, $2, $3);
        my @lines = split /(?<=\n)/, $body;
        s{^(\s*)\S}{"    $1" . substr($_, length($1), 1)}e for @lines;  # indent by four
        $head . "        if (!isReRender) {" . $eol
              . join('', @lines)
              . "        }" . $eol . $tail;
    }gexs;

    next if $src eq $orig;

    open my $out, '>', $file or die "write $file: $!";
    print $out $src;
    close $out;
    $changed++;
}

print "guarded $changed renderers\n";
