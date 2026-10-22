#!/usr/bin/env perl
# GeckoLib 4.8 made GeoModel#getModelResource and #getTextureResource take the renderer that is
# asking. Every model in the mod overrides the old one-argument form.
#
# Usage: tools/port-geomodels.pl <files...>
use strict;
use warnings;

my $changed = 0;
for my $file (@ARGV) {
    open my $in, '<', $file or die "open $file: $!";
    local $/;
    my $src = <$in>;
    close $in;

    next unless $src =~ /extends\s+GeoModel</;

    my $orig = $src;
    for my $method ('getModelResource', 'getTextureResource') {
        $src =~ s{
            (public\s+ResourceLocation\s+\Q$method\E\(\s*([\w.]+)\s+(\w+)\s*)
        }{ "$1, GeoRenderer<$2> renderer)" }gex;
    }
    next if $src eq $orig;

    if ($src !~ /^import\s+software\.bernie\.geckolib\.renderer\.GeoRenderer;/m) {
        $src =~ s/(^import\s+software\.bernie\.geckolib\.model\.GeoModel;[^\n]*\n)/$1import software.bernie.geckolib.renderer.GeoRenderer;\n/m;
        if ($src !~ /^import\s+software\.bernie\.geckolib\.renderer\.GeoRenderer;/m) {
            $src =~ s/(^package[^\n]*\n)/$1\nimport software.bernie.geckolib.renderer.GeoRenderer;\n/m;
        }
    }

    open my $out, '>', $file or die "write $file: $!";
    print $out $src;
    close $out;
    $changed++;
}

print "rewrote $changed models\n";
