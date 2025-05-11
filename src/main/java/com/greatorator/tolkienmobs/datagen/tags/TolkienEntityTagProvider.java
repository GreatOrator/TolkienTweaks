package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEntityTagProvider extends EntityTypeTagsProvider {
    public TolkienEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, MODID, helper);
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
                .add(TolkienEntities.ENTITY_TTM_NAZGUL.get())
                .add(TolkienEntities.ENTITY_TTM_WITCHKING.get())
                .add(TolkienEntities.ENTITY_TTM_OATHBREAKER.get());
        this.tag(EntityTypeTags.AQUATIC)
                .add(TolkienEntities.ENTITY_TTM_FROG.get())
                .add(TolkienEntities.ENTITY_TTM_WATCHER.get());
        this.tag(EntityTypeTags.ARTHROPOD)
                .add(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get())
                .add(TolkienEntities.ENTITY_TTM_SHELOB.get());
        this.tag(TolkienTags.Entities.BOSSES)
                .add(TolkienEntities.ENTITY_TTM_BALROG.get())
                .add(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get())
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
