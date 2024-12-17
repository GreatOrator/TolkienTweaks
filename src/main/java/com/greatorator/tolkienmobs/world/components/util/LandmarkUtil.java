package com.greatorator.tolkienmobs.world.components.util;

@SuppressWarnings("OptionalIsPresent")
public final class LandmarkUtil {
//	public static Optional<StructureStart> locateNearestLandmarkStart(LevelAccessor level, int chunkX, int chunkZ) {
//		return locateNearestMatchingLandmark(level, StructureTagGenerator.LANDMARK, chunkX, chunkZ);
//	}
//
//	public static Optional<StructureStart> locateNearestMatchingLandmark(LevelAccessor level, TagKey<Structure> matching, int chunkX, int chunkZ) {
//		var structureRegistry = level.registryAccess().registry(Registries.STRUCTURE);
//		if (structureRegistry.isEmpty()) return Optional.empty();
//		var holders = structureRegistry.get().getTag(matching);
//		if (holders.isEmpty()) return Optional.empty();
//
//		return locateNearestMatchingLandmark(level, holders.get(), chunkX, chunkZ);
//	}
//
//	public static Optional<StructureStart> locateNearestMatchingLandmark(LevelAccessor level, HolderSet<Structure> matching, int chunkX, int chunkZ) {
//		Set<Structure> structures = matching.stream().map(Holder::value).collect(Collectors.toSet());
//		return locateNearestMatchingLandmark(level, structures::contains, chunkX, chunkZ, true);
//	}
//
//	public static Optional<StructureStart> locateNearestMatchingLandmark(LevelAccessor level, Predicate<Structure> filter, int chunkX, int chunkZ, boolean checkReady) {
//		BlockPos nearestFeature = LegacyLandmarkPlacements.getNearestCenterXZ(chunkX, chunkZ);
//		int centerX = SectionPos.blockToSectionCoord(nearestFeature.getX());
//		int centerZ = SectionPos.blockToSectionCoord(nearestFeature.getZ());
//
//		if (checkReady && !level.hasChunk(centerX, centerZ)) return Optional.empty();
//
//		ChunkAccess chunkAccess = level.getChunk(centerX, centerZ, ChunkStatus.STRUCTURE_STARTS);
//
//		for (Map.Entry<Structure, StructureStart> structureEntry : chunkAccess.getAllStarts().entrySet())
//			if (filter.test(structureEntry.getKey()))
//				return Optional.of(structureEntry.getValue());
//
//		return Optional.empty();
//	}
//
//	public static boolean isConquered(Level level, int blockX, int blockZ) {
//		Optional<StructureStart> start = locateNearestMatchingLandmark(level, s -> s instanceof CustomStructureData, blockX >> 4, blockZ >> 4, false);
//		return start.filter(structureStart -> structureStart instanceof TFStructureStart tfStructureStart && tfStructureStart.isConquered()).isPresent();
//	}
//
//	public static void markStructureConquered(Level level, EnforcedHomePoint mobHome, ResourceKey<Structure> structureKey, boolean conquered) {
//		markStructureConquered(level, mobHome.getRestrictionPoint(), structureKey, conquered);
//	}
//
//	public static void markStructureConquered(Level level, @Nullable GlobalPos pos, ResourceKey<Structure> structureKey, boolean conquered) {
//		if (pos != null && level.dimension() == pos.dimension()) {
//			Optional<StructureStart> nearStart = locateNearestLandmarkStart(level, structureKey, pos.pos());
//			if (nearStart.isEmpty() || !(nearStart.get() instanceof TFStructureStart twilightStart)) return;
//
//			twilightStart.setConquered(conquered, level);
//
//			for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos.pos()).inflate(32.0F))) {
//				TFAdvancements.STRUCTURE_CLEARED.get().trigger(player, structureKey);
//			}
//		}
//	}
//
//	@Nullable
//	public static Structure structureForKey(LevelReader level, ResourceKey<Structure> structureKey) {
//		Optional<Registry<Structure>> registry = level.registryAccess().registry(Registries.STRUCTURE);
//
//		return registry.isPresent() ? registry.get().get(structureKey) : null;
//	}
//
//	public static Optional<StructureStart> locateNearestLandmarkStart(LevelAccessor level, ResourceKey<Structure> structureKey, BlockPos pos) {
//		return locateNearestLandmarkStart(level, structureKey, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
//	}
//
//	public static Optional<StructureStart> locateNearestLandmarkStart(LevelAccessor level, ResourceKey<Structure> structureKey, int chunkX, int chunkZ) {
//		Structure structure = structureForKey(level, structureKey);
//
//		if (structure == null) return Optional.empty();
//
//		return locateNearestLandmarkStart(level, structure, chunkX, chunkZ);
//	}
//
//	public static Optional<StructureStart> locateNearestLandmarkStart(LevelAccessor level, Structure structure, int chunkX, int chunkZ) {
//		BlockPos nearestLandmark = LegacyLandmarkPlacements.getNearestCenterXZ(chunkX, chunkZ);
//		ChunkAccess chunkAccess = level.getChunk(SectionPos.blockToSectionCoord(nearestLandmark.getX()), SectionPos.blockToSectionCoord(nearestLandmark.getZ()), ChunkStatus.STRUCTURE_STARTS);
//
//		for (Long packedChunkPos : chunkAccess.getReferencesForStructure(structure)) {
//			int packedX = ChunkPos.getX(packedChunkPos);
//			int packedZ = ChunkPos.getZ(packedChunkPos);
//
//			if (level.hasChunk(packedX, packedZ)) {
//				StructureStart structureStart = level.getChunk(packedX, packedZ, ChunkStatus.STRUCTURE_STARTS).getStartForStructure(structure);
//
//				if (structureStart != null && structureStart.isValid()) return Optional.of(structureStart);
//			}
//		}
//
//		return Optional.empty();
//	}
//
//	public static boolean isProgressionEnforced(Level world) {
//		return world.getGameRules().getBoolean(TwilightForestMod.ENFORCED_PROGRESSION_RULE);
//	}
//
//	private LandmarkUtil() {
//	}
}
