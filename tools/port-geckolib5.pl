#!/usr/bin/perl
#
# GeckoLib 4 -> 5, the parts a script can do.
#
# GeckoLib 5 moved off software.bernie onto its own group and reshuffled its
# packages at the same time, so this rewrites every reference the mod has. It is
# idempotent: running it a second time changes nothing.
#
# What it does NOT do is fix signatures. Renderers and models changed shape in 5
# (the model is handed a GeoRenderState instead of the animatable, the renderer
# submits render tasks instead of drawing, controllers lose the animatable and
# take an AnimationTest), so each file still has to be converted by hand. This
# pass only makes sure that what the hand conversion reads is the right package.
#
# Usage: perl tools/port-geckolib5.pl [--check]

use strict;
use warnings;
use File::Find;

my $check = grep { $_ eq '--check' } @ARGV;

my @files;
find(
    sub {
        return unless /\.java$/;
        push @files, $File::Find::name;
    },
    'src/main/java'
);

# The v4 animation package held PlayState, AnimationState and AnimatableManager
# alongside the controller classes; v5 scatters them. A file importing the
# wildcard needs all of the ones that survived, so they are spelled out.
my @animation_wildcard = (
    'com.geckolib.animation.AnimationController',
    'com.geckolib.animation.RawAnimation',
    'com.geckolib.animation.object.PlayState',
    'com.geckolib.animatable.manager.AnimatableManager',
);

# Longest first: "animation.Animation" is a prefix of "animation.AnimationController",
# and "renderer.GeoRenderer" of nothing, but the lookaheads below are what actually
# stop a partial match from eating the longer name.
my @renames = (
    [ 'software\.bernie\.geckolib\.animation\.keyframe\.event\.builtin\.',
      'com.geckolib.animation.keyframehandler.' ],
    [ 'software\.bernie\.geckolib\.cache\.object\.',
      'com.geckolib.cache.model.' ],
    [ 'software\.bernie\.geckolib\.renderer\.layer\.BlockAndItemGeoLayer(?![A-Za-z0-9_])',
      'com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer' ],
    [ 'software\.bernie\.geckolib\.animation\.PlayState(?![A-Za-z0-9_])',
      'com.geckolib.animation.object.PlayState' ],
    [ 'software\.bernie\.geckolib\.animation\.AnimatableManager(?![A-Za-z0-9_])',
      'com.geckolib.animatable.manager.AnimatableManager' ],
    [ 'software\.bernie\.geckolib\.animation\.Animation(?![A-Za-z0-9_])',
      'com.geckolib.cache.animation.Animation' ],
    [ 'software\.bernie\.geckolib\.renderer\.GeoRenderer(?![A-Za-z0-9_])',
      'com.geckolib.renderer.base.GeoRenderer' ],
);

# Gone in 5 with no direct replacement. AnimationState became internal (handlers
# are handed an AnimationTest), and EntityModelData was replaced by the render
# state's own DataTickets (ENTITY_PITCH, ENTITY_YAW, ENTITY_BODY_YAW, PIP ...).
my @removed_imports = (
    'import software\.bernie\.geckolib\.animation\.AnimationState;\r?\n',
    'import software\.bernie\.geckolib\.model\.data\.EntityModelData;\r?\n',
    'import software\.bernie\.geckolib\.model\.data\.\*;\r?\n',
);

my %count;
my $changed = 0;

for my $file (@files) {
    open(my $in, '<:raw', $file) or die "read $file: $!";
    local $/;
    my $text = <$in>;
    close $in;

    my $original = $text;

    # Wildcard imports first, so the per-class renames below don't see them.
    if ($text =~ /^import software\.bernie\.geckolib\.animation\.\*;\r?\n/m) {
        my $eol = ($text =~ /\r\n/) ? "\r\n" : "\n";
        my $replacement = join('', map { "import $_;$eol" } @animation_wildcard);
        $text =~ s/^import software\.bernie\.geckolib\.animation\.\*;\r?\n/$replacement/mg;
        $count{'animation.* expanded'}++;
    }

    for my $rule (@renames) {
        my ($from, $to) = @$rule;
        my $hits = () = $text =~ /$from/g;
        next unless $hits;
        $text =~ s/$from/$to/g;
        $count{"$from -> $to"} += $hits;
    }

    for my $rule (@removed_imports) {
        my $hits = () = $text =~ /^$rule/mg;
        next unless $hits;
        $text =~ s/^$rule//mg;
        $count{'dropped import ' . $rule} += $hits;
    }

    # Everything left is a straight group move (GeoEntity, GeoModel, the renderers
    # that kept their package, GeckoLibUtil, DataTickets ...).
    my $leftover = () = $text =~ /software\.bernie\.geckolib\./g;
    if ($leftover) {
        $text =~ s/software\.bernie\.geckolib\./com.geckolib./g;
        $count{'group moved (remaining references)'} += $leftover;
    }

    # Asset ids lose their old roots: v5 reads models from
    # assets/<modid>/geckolib/models/ and animations from .../geckolib/animations/,
    # stripping both that root and the .geo/.animation suffix when it bakes, so the
    # id a model asks for is the bare path ("monster/rockgolem").
    my $geo = ($text =~ s/"geo\/([^"\/]+(?:\/[^"\/]+)*)\.geo\.json"/"$1"/g);
    my $anim = ($text =~ s/"animations\/([^"\/]+(?:\/[^"\/]+)*)\.animation\.json"/"$1"/g);
    $count{'model ids rebased'} += $geo if $geo;
    $count{'animation ids rebased'} += $anim if $anim;

    next if $text eq $original;

    $changed++;

    if ($check) {
        print "would rewrite $file\n";
        next;
    }

    open(my $out, '>:raw', $file) or die "write $file: $!";
    print $out $text;
    close $out;
}

print "\n" . ($check ? 'Would change' : 'Changed') . " $changed file(s)\n";
for my $key (sort keys %count) {
    print "  $count{$key}\t$key\n";
}
