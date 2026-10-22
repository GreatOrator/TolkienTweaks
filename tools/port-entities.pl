#!/usr/bin/perl
# Port TolkienEntities to the 1.21.4 registration API.
#
# 1.21.4 made EntityType.Builder#build take a ResourceKey<EntityType<?>> instead of a
# free-form description id, and removed NeoForge's DeferredSpawnEggItem: a spawn egg is
# now a plain SpawnEggItem whose two colours live in the item model definition JSON
# (see vanilla's ItemModelGenerators#generateSpawnEgg). The colours are kept here in
# SPAWN_EGG_COLORS so the model datagen can read them back.
use strict;
use warnings;

my $file = shift || 'src/main/java/com/greatorator/tolkienmobs/init/TolkienEntities.java';
open(my $in, '<', $file) or die "open $file: $!";
local $/;
my $src = <$in>;
close $in;

$src =~ s{import net\.neoforged\.neoforge\.common\.DeferredSpawnEggItem;\n}
         {import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
};

$src =~ s{(import net\.neoforged\.neoforge\.registries\.DeferredRegister;\n)}
         {$1
import java.util.LinkedHashMap;
import java.util.Map;
};

$src =~ s{(    public static final DeferredRegister\.Items SPAWN_EGGS = DeferredRegister\.createItems\(MODID\);\n)}
         {$1\n    /** Colours of each spawn egg; the 1.21.4 item model definition carries them as tints. */\n    public static final Map<DeferredItem<Item>, SpawnEggColors> SPAWN_EGG_COLORS = new LinkedHashMap<>();\n};

# Entity types now build against the key they are registered under.
my $entities = 0;
$entities += ($src =~ s{ENTITY_TYPES\.register\("([^"]+)",\s*\(\)\s*->\s*(EntityType\.Builder.*?)\.build\((?:[^()]*)\)\);}{registerEntity("$1", () -> $2);}gs);

# Spawn eggs: colours move out of the item and into SPAWN_EGG_COLORS.
my $eggs = 0;
$eggs += ($src =~ s{SPAWN_EGGS\.register\("([^"]+)",\s*\(\)\s*->\s*new DeferredSpawnEggItem\(([A-Za-z_0-9]+),\s*(-?\d+),\s*(-?\d+),\s*new Item\.Properties\(\)\)\);}{spawnEgg("$1", $2, $3, $4);}gs);

my $helpers = <<'JAVA';
    /** Builds an entity type against the key it is registered under, which 1.21.4 requires. */
    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, name));
        return ENTITY_TYPES.register(name, () -> builder.get().build(key));
    }

    /** A spawn egg whose entity type and tint colours are resolved lazily, as DeferredSpawnEggItem did. */
    private static DeferredItem<Item> spawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight) {
        DeferredItem<Item> egg = SPAWN_EGGS.register(name, () -> new SpawnEggItem(type.get(), new Item.Properties()));
        SPAWN_EGG_COLORS.put(egg, new SpawnEggColors(background, highlight));

        return egg;
    }

    public record SpawnEggColors(int background, int highlight) {
    }

JAVA

$src =~ s{(    public static void register\(IEventBus eventBus\))}{$helpers$1};

open(my $out, '>', $file) or die "write $file: $!";
print $out $src;
close $out;

print "$file: converted $entities entity types, $eggs spawn eggs\n";
