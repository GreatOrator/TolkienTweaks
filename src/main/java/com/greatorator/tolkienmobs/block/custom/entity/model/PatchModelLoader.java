package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;

/** Reads the {@code tolkienmobs:patch} model: one texture, optionally with shaggy edges. */
public final class PatchModelLoader implements UnbakedModelLoader<UnbakedPatchModel> {
	public static final PatchModelLoader INSTANCE = new PatchModelLoader();

	private PatchModelLoader() {
	}

	@Override
	public UnbakedPatchModel read(JsonObject object, JsonDeserializationContext deserializationContext) throws JsonParseException {
		if (!object.has("texture"))
			throw new JsonParseException("Patch model missing value for 'texture'.");

		return new UnbakedPatchModel(Identifier.parse(object.get("texture").getAsString()), GsonHelper.getAsBoolean(object, "shaggify", false));
	}
}
