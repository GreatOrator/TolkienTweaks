package com.greatorator.tolkienmobs.world.components.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * One of the chapter's own trees, built as a structure and planted by the world generator.
 * <p>
 * A tree built by hand is a structure, not a growth: it is a template of blocks in a file, and the only way to stand
 * it in the world is to lay that template down. So each default tree is a feature of its own, named for the template
 * it places, and a wood that has no tree of its own is planted with them.
 * <p>
 * The ground is judged here rather than by a sapling. A sapling filter would only let the tree stand where a sapling
 * of some other species would survive, and these are the trees of whatever wood the country turned out to be; what
 * they need is soil under them and room over it, which is what this asks for.
 */
public class NaturalTreeFeature extends Feature<NoneFeatureConfiguration> {
    /** How much open air a trunk needs before the tree will stand: six blocks clears a sapling and its neighbours. */
    private static final int CLEARANCE = 6;

    private final Identifier template;

    public NaturalTreeFeature(Codec<NoneFeatureConfiguration> codec, Identifier template) {
        super(codec);
        this.template = template;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (!rooted(level, origin)) return false;
        StructureTemplate tree = tree(level);
        if (tree == null) return false;
        int wide = tree.getSize().getX();
        int deep = tree.getSize().getZ();
        if (wide <= 0 || deep <= 0 || tree.getSize().getY() <= 0) return false;
        // The template stands on its own corner, so the tree is set down around the column the heightmap named.
        BlockPos at = origin.offset(-wide / 2, 0, -deep / 2);
        tree.placeInWorld(level, at, at, new StructurePlaceSettings().setIgnoreEntities(true), context.random(), 2);
        return true;
    }

    /** The template this tree is built from, or null when the world has not got it. */
    private StructureTemplate tree(WorldGenLevel level) {
        ServerLevel server = level.getLevel();
        if (server == null) return null;
        StructureTemplateManager manager = server.getStructureManager();
        return manager.get(template).orElse(null);
    }

    /** Soil under the tree and open air over it: what a tree needs that a sapling would have answered for. */
    private static boolean rooted(WorldGenLevel level, BlockPos origin) {
        BlockState floor = level.getBlockState(origin.below());
        if (!floor.is(BlockTags.DIRT)) return false;
        for (int up = 0; up < CLEARANCE; up++) {
            if (!level.getBlockState(origin.above(up)).canBeReplaced()) return false;
        }
        return true;
    }
}
