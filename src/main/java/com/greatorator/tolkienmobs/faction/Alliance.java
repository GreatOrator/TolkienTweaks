package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.Supplier;

/**
 * The three alliances a player can stand with, and the only three. A standing is kept against each
 * one of them separately, so rising with the West is a decision rather than a score: the hall that
 * took your tokens has rivals, and they remember it.
 * <p>
 * An alliance owns two things. Its <em>countries</em> are the biomes it can raise a hall in and the
 * ground its guards patrol. Its <em>soldiers</em> are the mobs whose death moves standing - killing
 * one raises the two alliances that are not its own and costs the one that is. Both are tags, so a
 * pack can hand a biome or a mob to another alliance without touching this file.
 */
public enum Alliance {
    WEST("west", 0x4C7EBF, ChatFormatting.BLUE,
            TolkienTags.Factions.WEST_COUNTRIES, TolkienTags.Factions.WEST_SOLDIERS,
            TolkienItems.ITEM_TOKEN_WESTERN_ALLIANCE::get, TolkienEntities.ENTITY_TTM_FACTIONGUARD::get),
    EAST("east", 0x9B2C2C, ChatFormatting.DARK_RED,
            TolkienTags.Factions.EAST_COUNTRIES, TolkienTags.Factions.EAST_SOLDIERS,
            TolkienItems.ITEM_TOKEN_EASTERN_ALLIANCE::get, TolkienEntities.ENTITY_TTM_FACTIONGUARD::get),
    HARAD("harad", 0xD9A441, ChatFormatting.GOLD,
            TolkienTags.Factions.HARAD_COUNTRIES, TolkienTags.Factions.HARAD_SOLDIERS,
            TolkienItems.ITEM_TOKEN_HARAD_ALLIANCE::get, TolkienEntities.ENTITY_TTM_FACTIONGUARD::get);

    /** The scoreboard tag every hall's warden carries, beside the one that names his alliance. */
    public static final String ENVOY_TAG = "tolkienmobs:faction_envoy";

    private final String id;
    private final int colour;
    private final ChatFormatting chat;
    private final TagKey<Biome> countries;
    private final TagKey<EntityType<?>> soldiers;
    private final Supplier<? extends net.minecraft.world.item.Item> token;
    private final Supplier<? extends EntityType<?>> guard;

    Alliance(String id, int colour, ChatFormatting chat, TagKey<Biome> countries,
             TagKey<EntityType<?>> soldiers, Supplier<? extends net.minecraft.world.item.Item> token,
             Supplier<? extends EntityType<?>> guard) {
        this.id = id;
        this.colour = colour;
        this.chat = chat;
        this.countries = countries;
        this.soldiers = soldiers;
        this.token = token;
        this.guard = guard;
    }

    public String id() {
        return id;
    }

    /** The colour the ledger draws this alliance's bar in, as 0xRRGGBB. */
    public int colour() {
        return colour;
    }

    public ChatFormatting chat() {
        return chat;
    }

    public TagKey<Biome> countries() {
        return countries;
    }

    public TagKey<EntityType<?>> soldiers() {
        return soldiers;
    }

    public net.minecraft.world.item.Item token() {
        return token.get();
    }

    /** The soldier that musters at this alliance's hall and walks its country. */
    public EntityType<?> guard() {
        return guard.get();
    }

    /** The scoreboard tag the warden of this alliance's hall is placed with. */
    public String envoyTag() {
        return ENVOY_TAG + "/" + id;
    }

    /** The scoreboard tag a giver of this alliance's work carries, which decides the clothes it wears. */
    public String giverTag() {
        return "tolkienmobs:quest_giver/" + id;
    }

    public Component displayName() {
        return Component.translatable("faction.tolkienmobs." + id);
    }

    /** The two alliances this one is set against: the whole of the other two. */
    public List<Alliance> rivals() {
        return switch (this) {
            case WEST -> List.of(EAST, HARAD);
            case EAST -> List.of(WEST, HARAD);
            case HARAD -> List.of(WEST, EAST);
        };
    }

    public static Alliance byId(String id) {
        for (Alliance alliance : values()) {
            if (alliance.id.equals(id)) return alliance;
        }
        return WEST;
    }
}
