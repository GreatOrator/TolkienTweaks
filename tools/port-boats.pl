#!/usr/bin/env perl
# 1.21.4 replaced BoatRenderer's Pair<ResourceLocation, ListModel<Boat>> lookup with an
# AbstractBoatRenderer that asks its subclass for a model and a render type. The mod's seven boat
# renderers all follow the old shape, so rebuild each from the names and textures already in it.
use strict;
use warnings;

for my $file (@ARGV) {
    open my $in, '<', $file or die "open $file: $!";
    local $/;
    my $src = <$in>;
    close $in;

    my $eol = $src =~ /\r\n/ ? "\r\n" : "\n";

    $src =~ /package\s+([\w.]+);/ or die "no package in $file";
    my $package = $1;
    $src =~ /public class (\w+) extends/ or die "no class in $file";
    my $class = $1;

    # the two texture constants, in declaration order (boat, then chest boat)
    my @textures = ($src =~ /private static final ResourceLocation (\w+) =\s*ResourceLocation\.fromNamespaceAndPath\(MODID,\s*"([^"]+)"\);/g);
    die "expected two textures in $file" unless @textures == 4;
    my ($boatConst, $boatPath, $chestConst, $chestPath) = @textures;

    # the model layers the constructor bakes; the chest one is built first
    my @layers = ($src =~ /TolkienModelLayers\.(\w+)/g);
    die "expected two model layers in $file" unless @layers == 2;
    my ($chestLayer, $boatLayer) = @layers;

    my $body = <<"BODY";
package $package;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class $class extends AbstractBoatRenderer {
    private static final ResourceLocation $boatConst = ResourceLocation.fromNamespaceAndPath(MODID, "$boatPath");
    private static final ResourceLocation $chestConst = ResourceLocation.fromNamespaceAndPath(MODID, "$chestPath");

    private final EntityModel<BoatRenderState> model;
    private final ResourceLocation texture;

    public $class(EntityRendererProvider.Context context, boolean chest) {
        super(context);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.$chestLayer : TolkienModelLayers.$boatLayer));
        this.texture = chest ? $chestConst : $boatConst;
    }

    \@Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    \@Override
    protected RenderType renderType() {
        return this.model.renderType(this.texture);
    }
}
BODY

    $body =~ s/\n/$eol/g;
    open my $out, '>', $file or die "write $file: $!";
    print $out $body;
    close $out;
    print "rewrote $class\n";
}
