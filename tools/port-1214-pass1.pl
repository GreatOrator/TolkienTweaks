#!/usr/bin/env perl
# Pass 1 of the 1.21.4 port: renames that carry no behaviour change.
# One process for the whole tree - spawning a sed per file takes minutes here.
#
# Grounded in the decompiled 1.21.4 sources:
#   DirectionProperty                   -> EnumProperty<Direction>  (class removed)
#   MobSpawnType                        -> EntitySpawnReason        (renamed)
#   getMinBuildHeight/getMaxBuildHeight -> getMinY/getMaxY
#   ItemInteractionResult               -> InteractionResult        (merged)
#   InteractionResultHolder             -> InteractionResult        (merged)
use strict;
use warnings;
use File::Find;

my $root = 'src/main/java';
my @files;
find( sub { push @files, $File::Find::name if /\.java$/ }, $root );

my @needed = (
    [ 'InteractionResult',  'net.minecraft.world.InteractionResult' ],
    [ 'EnumProperty',       'net.minecraft.world.level.block.state.properties.EnumProperty' ],
    [ 'EntitySpawnReason',  'net.minecraft.world.entity.EntitySpawnReason' ],
    [ 'Direction',          'net.minecraft.core.Direction' ],
);

my $changed = 0;

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    local $/;
    my $c = <$in>;
    close $in;
    my $orig = $c;

    # InteractionResultHolder: the stack is mutated in place now, so only the
    # outcome survives; sidedSuccess(isClientSide) is a plain SUCCESS.
    $c =~ s/InteractionResultHolder<ItemStack>/InteractionResult/g;
    $c =~ s/InteractionResultHolder\.sidedSuccess\([^;]*\)/InteractionResult.SUCCESS/g;
    $c =~ s/InteractionResultHolder\.success\([^;]*\)/InteractionResult.SUCCESS/g;
    $c =~ s/InteractionResultHolder\.consume\([^;]*\)/InteractionResult.CONSUME/g;
    $c =~ s/InteractionResultHolder\.fail\([^;]*\)/InteractionResult.FAIL/g;
    $c =~ s/InteractionResultHolder\.pass\([^;]*\)/InteractionResult.PASS/g;

    # ItemInteractionResult was folded back into InteractionResult.
    $c =~ s/ItemInteractionResult\.PASS_TO_DEFAULT_BLOCK_INTERACTION/InteractionResult.TRY_WITH_EMPTY_HAND/g;
    $c =~ s/ItemInteractionResult\.SKIP_DEFAULT_BLOCK_INTERACTION/InteractionResult.SUCCESS/g;
    $c =~ s/ItemInteractionResult\.SUCCESS/InteractionResult.SUCCESS/g;
    $c =~ s/ItemInteractionResult\.CONSUME/InteractionResult.CONSUME/g;

    # Plain renames.
    $c =~ s/\bDirectionProperty\b/EnumProperty<Direction>/g;
    $c =~ s/\bMobSpawnType\b/EntitySpawnReason/g;
    $c =~ s/\bgetMinBuildHeight\(\)/getMinY()/g;
    $c =~ s/\bgetMaxBuildHeight\(\)/getMaxY()/g;

    # Explicit construction of a holder: keep only the outcome it carried.
    $c =~ s/new InteractionResultHolder<>\(\s*([^,]+?),\s*[^)]*\)/$1/g;

    # Whatever bare ItemInteractionResult is left is a type position or an import.
    $c =~ s/\bItemInteractionResult\b(?!\.)/InteractionResult/g;

    # Guard against the generic leaking into an import line.
    $c =~ s/^import ([^;\n]*)EnumProperty<Direction>;/import ${1}EnumProperty;/m;

    # Drop imports of classes that no longer exist.
    $c =~ s/^import net\.minecraft\.world\.(?:ItemInteractionResult|InteractionResultHolder);\r?\n//gm;
    $c =~ s/^import net\.minecraft\.world\.level\.block\.state\.properties\.DirectionProperty;\r?\n//gm;
    $c =~ s/^import net\.minecraft\.world\.entity\.MobSpawnType;\r?\n//gm;

    # Add the imports the renames now require.
    for my $pair (@needed) {
        my ( $sym, $fqcn ) = @$pair;
        next if $c =~ /^import \Q$fqcn\E;/m;
        my $body = $c;
        $body =~ s/^import [^\n]*\n//gm;
        next unless $body =~ /\b\Q$sym\E\b/;
        $c =~ s/^(package [^\n]*\n)/$1\nimport $fqcn;\n/m;
    }

    if ( $c ne $orig ) {
        open my $out, '>:raw', $f or die "write $f: $!";
        print $out $c;
        close $out;
        $changed++;
    }
}

print "pass 1 complete: $changed files changed\n";
