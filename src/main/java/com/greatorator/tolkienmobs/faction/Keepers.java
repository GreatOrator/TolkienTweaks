package com.greatorator.tolkienmobs.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The bodies the line has stood, written down by the tag each one wears.
 * <p>
 * A keeper is found by his tag, and a tag only reaches as far as the chunks the world has in memory: a body left in
 * an unloaded chunk is invisible, so the line stood a second one on the next visit and a third on the one after -
 * forty-five Glorfindels in one world. This record is the identity a tag cannot give, kept beside the campaign so a
 * keeper is found again after a reload, wherever he was left, and so the chunk he was left in can be read before the
 * line decides it is short a body.
 */
public final class Keepers extends SavedData {
    private static final String SAVE_DATA_NAME = "keepers";

    /** One body: the tag it wears, its identity, the world it belongs to, and the ground it was last on. */
    public record Post(String tag, UUID id, String dim, BlockPos at) {
        public static final Codec<Post> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("tag").forGetter(Post::tag),
                UUIDUtil.CODEC.fieldOf("id").forGetter(Post::id),
                Codec.STRING.fieldOf("dim").forGetter(Post::dim),
                BlockPos.CODEC.fieldOf("at").forGetter(Post::at)
        ).apply(instance, Post::new));
    }

    public static final Codec<Keepers> CODEC = Post.CODEC.listOf()
            .xmap(Keepers::fromPosts, Keepers::toPosts);

    public static final SavedDataType<Keepers> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Keepers::new, CODEC);

    private static final Map<String, Post> POSTS = new HashMap<>();
    private static final Keepers NO_SERVER = new Keepers(false);

    /**
     * The instance a world with no keeper file starts from. The clearing is the point: this map is static, so a
     * player who leaves one world and starts another in the same session would otherwise find the last world's
     * bodies still on its notes. A world that has a file is cleared again by {@link #fromPosts} before it is filled.
     */
    private Keepers() {
        this(true);
    }

    private Keepers(boolean fresh) {
        super();
        if (fresh) POSTS.clear();
    }

    private static Keepers fromPosts(List<Post> posts) {
        POSTS.clear();
        for (Post post : posts) POSTS.put(key(post.dim(), post.tag()), post);
        return new Keepers(false);
    }

    private static List<Post> toPosts(Keepers keepers) {
        return new ArrayList<>(POSTS.values());
    }

    private static String key(String dim, String tag) {
        return dim + "|" + tag;
    }

    private static String dimension(ServerLevel level) {
        return level.dimension().identifier().toString();
    }

    private static Keepers instance(ServerLevel level) {
        if (level.getServer() != null) {
            ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    /**
     * The body recorded for this tag in this world, if the world still has one: the entity itself while its chunk is
     * in memory, and otherwise the chunk he was left in is read once so the body is back before the line looks for
     * him. A record whose body is gone - killed, or dropped by the world - is forgotten, which is what lets a keeper
     * be stood again.
     */
    @Nullable
    static Entity keeper(ServerLevel level, String tag) {
        Keepers data = instance(level);
        String key = key(dimension(level), tag);
        Post post = POSTS.get(key);
        if (post == null) return null;
        Entity body = level.getEntity(post.id());
        if (body != null && !body.isRemoved()) return body;
        BlockPos at = post.at();
        if (!level.hasChunk(at.getX() >> 4, at.getZ() >> 4)) {
            level.getChunk(at.getX() >> 4, at.getZ() >> 4);
            body = level.getEntity(post.id());
            if (body != null && !body.isRemoved()) return body;
        }
        data.forget(key);
        return null;
    }

    /** Writes a body down, or moves the note he already has to the ground he is on now. */
    static void remember(ServerLevel level, String tag, Entity body) {
        instance(level).record(dimension(level), tag, body.getUUID(), body.blockPosition());
    }

    private void record(String dim, String tag, UUID id, BlockPos at) {
        if (new Post(tag, id, dim, at).equals(POSTS.put(key(dim, tag), new Post(tag, id, dim, at)))) return;
        setDirty();
    }

    private void forget(String key) {
        if (POSTS.remove(key) != null) setDirty();
    }
}
