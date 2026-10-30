package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEntityTagProvider extends EntityTypeTagsProvider {
    public TolkienEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EntityTypeTags.ZOMBIES)
                .add(TolkienEntities.ENTITY_TTM_ROMIEWALKER.get());
        this.tag(EntityTypeTags.RAIDERS)
                .add(TolkienEntities.ENTITY_TTM_SWAMPHAG.get());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES)
                .add(TolkienEntities.AMMO_BOULDER.get())
                .add(TolkienEntities.AMMO_FELLBEAST_FIREBALL.get());
        this.tag(EntityTypeTags.UNDEAD)
                .add(TolkienEntities.ENTITY_TTM_BARROW.get())
                .add(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get())
                .add(TolkienEntities.ENTITY_TTM_ENRAGED_FELLSPIRIT.get())
                .add(TolkienEntities.ENTITY_TTM_NAZGUL.get())
                .add(TolkienEntities.ENTITY_TTM_WITCHKING.get())
                .add(TolkienEntities.ENTITY_TTM_OATHBREAKER.get());
        this.tag(EntityTypeTags.AQUATIC)
                .add(TolkienEntities.ENTITY_TTM_FROG.get())
                .add(TolkienEntities.ENTITY_TTM_WATCHER.get());
        this.tag(EntityTypeTags.ARTHROPOD)
                .add(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get())
                .add(TolkienEntities.ENTITY_TTM_SHELOB.get());
        // In 26.1 a saddle can only be worn by a kind of entity the saddle itself names, through the
        // tag its equippable component carries - the Saddleable interface the port removed is what
        // used to allow it. The mod's three mounts take a saddle again, which is what makes the bare
        // "this.isSaddled()" their jockeys' spawn code carried mean something: a jockey goat or steed
        // now arrives with the saddle its model draws, and a player can saddle one normally.
        this.tag(EntityTypeTags.CAN_EQUIP_SADDLE)
                .add(TolkienEntities.ENTITY_TTM_GOAT.get())
                .add(TolkienEntities.ENTITY_TTM_SHADOWFAX.get())
                .add(TolkienEntities.ENTITY_TTM_NAZGULSTEED.get());
        // Who fights for which alliance. A soldier's death raises the other two standings and costs
        // his own, so this table is the whole of where killing moves a reputation. The West's only
        // armed men in the wild are brigands - rough hands a hall turns a blind eye to - which is
        // why they are the one human mob that counts as a soldier.
        this.tag(TolkienTags.Factions.WEST_SOLDIERS)
                .add(TolkienEntities.ENTITY_TTM_BRIGAND.get());
        this.tag(TolkienTags.Factions.EAST_SOLDIERS)
                .add(TolkienEntities.ENTITY_TTM_MORDORORC.get())
                .add(TolkienEntities.ENTITY_TTM_URUKHAI.get())
                .add(TolkienEntities.ENTITY_TTM_GOBLIN.get())
                .add(TolkienEntities.ENTITY_TTM_DUERGAR.get())
                .add(TolkienEntities.ENTITY_TTM_WARG.get());
        this.tag(TolkienTags.Factions.HARAD_SOLDIERS)
                .add(TolkienEntities.ENTITY_TTM_HARADRIM.get());
        this.tag(TolkienTags.Entities.BOSSES)
                .add(TolkienEntities.ENTITY_TTM_BALROG.get())
                .add(TolkienEntities.ENTITY_TTM_ENRAGED_FELLSPIRIT.get())
                .add(TolkienEntities.ENTITY_TTM_GOBLINKING.get())
                .add(TolkienEntities.ENTITY_TTM_GWAHIR.get())
                .add(TolkienEntities.ENTITY_TTM_MITHRILGOLEM.get())
                .add(TolkienEntities.ENTITY_TTM_MORGULIRONGOLEM.get())
                .add(TolkienEntities.ENTITY_TTM_SHELOB.get())
                .add(TolkienEntities.ENTITY_TTM_WATCHER.get())
                .add(TolkienEntities.ENTITY_TTM_WITCHKING.get())
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER);
    }


        @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Entity Tags";
    }
}
