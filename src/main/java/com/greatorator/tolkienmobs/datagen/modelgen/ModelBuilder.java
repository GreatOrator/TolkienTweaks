package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * Builds a block or item model as JSON.
 *
 * <p>This replaces NeoForge's {@code net.neoforged.neoforge.client.model.generators}
 * package, which was removed in 21.4. The method names and shapes deliberately match the
 * old API so the existing providers only needed their imports changed.</p>
 */
public abstract class ModelBuilder<T extends ModelBuilder<T>> {
    protected final JsonObject root = new JsonObject();
    protected final ResourceLocation location;
    protected final String name;

    protected ModelFile parent;
    protected ResourceLocation renderType;
    protected Boolean ambientOcclusion;
    protected final Map<String, String> textures = new LinkedHashMap<>();
    protected final JsonArray elements = new JsonArray();
    protected final JsonArray overrides = new JsonArray();

    protected ModelBuilder(ResourceLocation location, String name) {
        this.location = location;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public T parent(ModelFile parent) {
        this.parent = parent;
        return self();
    }

    public T parent(ResourceLocation parent) {
        return this.parent(new ModelFile.UncheckedModelFile(parent));
    }

    public T parent(String parent) {
        return this.parent(new ModelFile.UncheckedModelFile(parent));
    }

    public T renderType(ResourceLocation renderType) {
        this.renderType = renderType;
        return self();
    }

    public T renderType(String renderType) {
        return this.renderType(ResourceLocation.parse(renderType));
    }

    public T ao(boolean ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
        return self();
    }

    public T texture(String key, ResourceLocation texture) {
        this.textures.put(key, texture.toString());
        return self();
    }

    /** A texture value: either a {@code #reference} or a path relative to this model's namespace. */
    public T texture(String key, String texture) {
        if (!texture.isEmpty() && texture.charAt(0) == '#') {
            this.textures.put(key, texture);
            return self();
        }
        ResourceLocation asLoc = texture.contains(":")
                ? ResourceLocation.parse(texture)
                : ResourceLocation.fromNamespaceAndPath(this.location.getNamespace(), texture);
        return this.texture(key, asLoc);
    }

    public ElementBuilder element() {
        return new ElementBuilder();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (this.parent != null) {
            json.addProperty("parent", this.parent.getLocation().toString());
        }
        if (this.renderType != null) {
            json.addProperty("render_type", this.renderType.toString());
        }
        if (this.ambientOcclusion != null) {
            json.addProperty("ambientocclusion", this.ambientOcclusion);
        }
        if (!this.textures.isEmpty()) {
            JsonObject tex = new JsonObject();
            this.textures.forEach(tex::addProperty);
            json.add("textures", tex);
        }
        if (!this.elements.isEmpty()) {
            json.add("elements", this.elements);
        }
        if (!this.overrides.isEmpty()) {
            json.add("overrides", this.overrides);
        }
        return json;
    }

    public enum FaceRotation {
        ZERO,
        CLOCKWISE_90,
        UPSIDE_DOWN,
        COUNTERCLOCKWISE_90
    }

    public class ElementBuilder {
        private final JsonObject element = new JsonObject();
        private final Map<Direction, JsonObject> faces = new LinkedHashMap<>();
        private final RotationBuilder rotation = new RotationBuilder(this);

        public ElementBuilder from(float x, float y, float z) {
            this.element.add("from", vector(x, y, z));
            return this;
        }

        public ElementBuilder to(float x, float y, float z) {
            this.element.add("to", vector(x, y, z));
            return this;
        }

        public ElementBuilder shade(boolean shade) {
            this.element.addProperty("shade", shade);
            return this;
        }

        public RotationBuilder rotation() {
            return this.rotation;
        }

        public FaceBuilder face(Direction direction) {
            JsonObject face = this.faces.computeIfAbsent(direction, ignored -> new JsonObject());
            return new FaceBuilder(this, face);
        }

        public ElementBuilder allFaces(BiConsumer<Direction, FaceBuilder> action) {
            for (Direction direction : Direction.values()) {
                action.accept(direction, face(direction));
            }
            return this;
        }

        public ElementBuilder faces(BiConsumer<Direction, FaceBuilder> action) {
            return allFaces(action);
        }

        public ModelBuilder<T> end() {
            if (this.rotation.isSet()) {
                this.element.add("rotation", this.rotation.toJson());
            }
            if (!this.faces.isEmpty()) {
                JsonObject faceJson = new JsonObject();
                this.faces.forEach((direction, json) -> faceJson.add(direction.getSerializedName(), json));
                this.element.add("faces", faceJson);
            }
            ModelBuilder.this.elements.add(this.element);
            return ModelBuilder.this.self();
        }

        private static JsonArray vector(float x, float y, float z) {
            JsonArray array = new JsonArray();
            array.add(x);
            array.add(y);
            array.add(z);
            return array;
        }
    }

    public class RotationBuilder {
        private final ElementBuilder owner;
        private final JsonObject rotation = new JsonObject();
        private boolean originSet;
        private boolean set;

        private RotationBuilder(ElementBuilder owner) {
            this.owner = owner;
        }

        public RotationBuilder origin(float x, float y, float z) {
            this.rotation.add("origin", ElementBuilder.vector(x, y, z));
            this.originSet = true;
            this.set = true;
            return this;
        }

        public RotationBuilder axis(Direction.Axis axis) {
            this.rotation.addProperty("axis", axis.getSerializedName());
            this.set = true;
            return this;
        }

        public RotationBuilder angle(float angle) {
            this.rotation.addProperty("angle", angle);
            this.set = true;
            return this;
        }

        public RotationBuilder rescale(boolean rescale) {
            this.rotation.addProperty("rescale", rescale);
            this.set = true;
            return this;
        }

        public ElementBuilder end() {
            return this.owner;
        }

        private boolean isSet() {
            return this.set;
        }

        private JsonObject toJson() {
            return this.rotation;
        }
    }

    public class FaceBuilder {
        private final ElementBuilder owner;
        private final JsonObject face;

        private FaceBuilder(ElementBuilder owner, JsonObject face) {
            this.owner = owner;
            this.face = face;
        }

        public FaceBuilder uvs(float u1, float v1, float u2, float v2) {
            JsonArray uvs = new JsonArray();
            uvs.add(u1);
            uvs.add(v1);
            uvs.add(u2);
            uvs.add(v2);
            this.face.add("uv", uvs);
            return this;
        }

        public FaceBuilder texture(String texture) {
            this.face.addProperty("texture", texture);
            return this;
        }

        public FaceBuilder cullface(Direction cullface) {
            if (cullface == null) {
                this.face.remove("cullface");
            } else {
                this.face.addProperty("cullface", cullface.getSerializedName());
            }
            return this;
        }

        public FaceBuilder tintindex(int tintindex) {
            this.face.addProperty("tintindex", tintindex);
            return this;
        }

        /** NeoForge folds the two light values into one number when they are equal. */
        public FaceBuilder emissivity(int block, int sky) {
            if (block == sky) {
                this.face.addProperty("emissivity", block);
            } else {
                JsonObject emissivity = new JsonObject();
                emissivity.addProperty("block", block);
                emissivity.addProperty("sky", sky);
                this.face.add("emissivity", emissivity);
            }
            return this;
        }

        public FaceBuilder rotation(FaceRotation rotation) {
            if (rotation != FaceRotation.ZERO) {
                this.face.addProperty("rotation", switch (rotation) {
                    case CLOCKWISE_90 -> 90;
                    case UPSIDE_DOWN -> 180;
                    case COUNTERCLOCKWISE_90 -> 270;
                    default -> 0;
                });
            }
            return this;
        }

        public ElementBuilder end() {
            return this.owner;
        }
    }
}
