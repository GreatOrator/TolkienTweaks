package com.greatorator.tolkienmobs.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.TolkienMobsMain.resLoc;

/**
 * A pack's say over what the trinket table repairs gear with, in place of the anvil's own rule.
 *
 * <p>An entry matches a tool - by item or by item tag - and names the material that repairs it. A
 * tool no entry matches keeps the anvil's rule, while an entry that does match replaces that rule
 * for the tool it covers, so a pack can both add materials and take them away.</p>
 *
 * <p>Entries are data pack files at {@code data/<pack>/tolkienmobs/repair_material/<name>.json} and
 * read like this:</p>
 *
 * <pre>{@code
 * {
 *   "tools": {"tag": "minecraft:pickaxes"},
 *   "material": {"item": "tolkienmobs:ingot_mithril"}
 * }
 * }</pre>
 *
 * <p>The mod ships no entries of its own, so an unmodified game repairs exactly as it did before
 * this registry existed and only a pack changes what the table takes.</p>
 */
@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public final class RepairMaterials {
    public static final ResourceKey<Registry<RepairMaterial>> REGISTRY_KEY = ResourceKey.createRegistryKey(resLoc("repair_material"));

    private RepairMaterials() {
    }

    public record RepairMaterial(Ingredient tools, Ingredient material) {
        public static final Codec<RepairMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("tools").forGetter(RepairMaterial::tools),
                Ingredient.CODEC.fieldOf("material").forGetter(RepairMaterial::material)
        ).apply(instance, RepairMaterial::new));
    }

    /** Takes the network codec too, so a client's slots judge a material the same way the server does. */
    @SubscribeEvent
    public static void register(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY_KEY, RepairMaterial.CODEC, RepairMaterial.CODEC);
    }

    /**
     * The final word on one tool and one material: an entry that covers the tool decides it, and a
     * tool no entry covers is left to the anvil.
     */
    public static boolean repairs(@Nullable LevelAccessor level, ItemStack tool, ItemStack material) {
        Boolean pack = packDecision(level, tool, material);
        return pack != null ? pack : tool.getItem().isValidRepairItem(tool, material);
    }

    /**
     * What the packs say, or null when none of them covers the tool. A covered tool is decided here
     * even when the material does not match, which is what lets an entry take a material away.
     */
    @Nullable
    private static Boolean packDecision(@Nullable LevelAccessor level, ItemStack tool, ItemStack material) {
        if (level == null) {
            return null;
        }
        Registry<RepairMaterial> registry = level.registryAccess().registry(REGISTRY_KEY).orElse(null);
        if (registry == null) {
            return null;
        }
        boolean covered = false;
        for (RepairMaterial entry : registry) {
            if (!entry.tools().test(tool)) {
                continue;
            }
            covered = true;
            if (entry.material().test(material)) {
                return Boolean.TRUE;
            }
        }
        return covered ? Boolean.FALSE : null;
    }
}
