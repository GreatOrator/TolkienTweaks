#!/usr/bin/env perl
# Ports GeckoLib entity renderers from the 1.21.1 render(T, yaw, partial, pose, buffer, light)
# override to 1.21.4's preRender(...) hook. Overrides that only called super.render are deleted.
#
# Line based on purpose: the bodies contain nested braces, so a regex cannot find the end of the
# method without counting them.
use strict;
use warnings;

my $changed = 0;
for my $file (@ARGV) {
    open my $in, '<', $file or die "open $file: $!";
    my @lines = <$in>;
    close $in;

    my $eol = ($lines[0] // '') =~ /\r\n/ ? "\r\n" : "\n";

    my @out;
    my $i = 0;
    my $edited = 0;
    my $usesPreRender = 0;

    while ($i < @lines) {
        my $line = $lines[$i];
        if ($line =~ /public\s+void\s+render\(\s*(\w+)\s+entity,\s*float\s+entityYaw,\s*float\s+partialTicks,\s*PoseStack\s+poseStack,\s*MultiBufferSource\s+buffer,\s*int\s+packedLight\s*\)\s*\{/) {
            my $type = $1;

            # find the matching close brace
            my $depth = 0;
            my $j = $i;
            my $body = [];
            my $opened = 0;
            while ($j < @lines) {
                my $l = $lines[$j];
                my $stripped = $l;
                $stripped =~ s/[^{}]//g;
                my $opens = ($stripped =~ tr/{//);
                my $closes = ($stripped =~ tr/}//);
                if ($j > $i) {
                    if ($opened && $depth + $opens - $closes <= 0) {
                        last;    # this line closes the method
                    }
                    push @$body, $l;
                }
                $depth += $opens - $closes;
                $opened = 1;
                $j++;
            }

            # the @Override immediately above the method goes with it
            pop @out if @out && $out[-1] =~ /^\s*\@Override\s*$/;

            my @rest = grep { !/^\s*super\.render\(/ && !/^\s*$/ } @$body;
            if (!@rest) {
                # nothing but a super call: the override has no reason to exist
                $i = $j + 1;
                $edited = 1;
                next;
            }

            my @body = grep { !/^\s*super\.render\(/ } @$body;
            for (@body) { s/\bentity\b/animatable/g; }

            push @out, "    \@Override$eol";
            push @out, "    public void preRender(PoseStack poseStack, $type animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {$eol";
            push @out, @body;
            push @out, "        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);$eol";
            push @out, "    }$eol";
            $usesPreRender = 1;
            $edited = 1;
            $i = $j + 1;
            next;
        }
        push @out, $line;
        $i++;
    }

    next unless $edited;

    my $src = join '', @out;
    if ($eol eq "\r\n") {
        $src =~ s/(?<!\r)\n/\r\n/g;
    }
    $src .= $eol unless $src =~ /\n$/;

    if ($usesPreRender) {
        for my $fqcn ('com.mojang.blaze3d.vertex.VertexConsumer',
                      'software.bernie.geckolib.cache.object.BakedGeoModel') {
            next if $src =~ /^import\s+\Q$fqcn\E;/m;
            (my $simple = $fqcn) =~ s/.*\.//;
            $src =~ s/^import\s+[^\n;]*\.\Q$simple\E;/import $fqcn;/m;
            if ($src !~ /^import\s+\Q$fqcn\E;/m) {
                $src =~ s/(^package[^\n]*\n)/$1${eol}import $fqcn;$eol/m;
            }
        }
    }

    open my $out, '>', $file or die "write $file: $!";
    print $out $src;
    close $out;
    $changed++;
}

print "rewrote $changed files\n";
