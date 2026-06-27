package hellfirepvp.astralsorcery.common.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.registry.ASStructureRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class AstralShrineStructure extends Structure {
    public static final MapCodec<AstralShrineStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            ResourceLocation.CODEC.fieldOf("template").forGetter(structure -> structure.templateId),
            Codec.INT.optionalFieldOf("y_offset", 0).forGetter(structure -> structure.yOffset),
            Codec.INT.optionalFieldOf("max_terrain_delta", -1).forGetter(structure -> structure.maxTerrainDelta),
            Codec.INT.optionalFieldOf("max_surrounding_terrain_delta", -1).forGetter(structure -> structure.maxSurroundingTerrainDelta),
            Codec.INT.optionalFieldOf("max_surface_floor_delta", -1).forGetter(structure -> structure.maxSurfaceFloorDelta),
            Codec.intRange(0, 32).optionalFieldOf("surrounding_terrain_margin", 0).forGetter(structure -> structure.surroundingTerrainMargin),
            Codec.intRange(0, 16).optionalFieldOf("terrain_smoothing_margin", 0).forGetter(structure -> structure.terrainSmoothingMargin),
            Codec.intRange(0, 8).optionalFieldOf("terrain_smoothing_fill_limit", 0).forGetter(structure -> structure.terrainSmoothingFillLimit),
            Codec.intRange(0, 8).optionalFieldOf("terrain_smoothing_cut_limit", 0).forGetter(structure -> structure.terrainSmoothingCutLimit),
            Codec.INT.optionalFieldOf("terrain_smoothing_y_offset", 0).forGetter(structure -> structure.terrainSmoothingYOffset),
            Codec.intRange(0, 16).optionalFieldOf("terrain_support_fill_limit", 0).forGetter(structure -> structure.terrainSupportFillLimit),
            Codec.INT.optionalFieldOf("terrain_support_y_offset", 0).forGetter(structure -> structure.terrainSupportYOffset),
            Codec.intRange(1, 64).optionalFieldOf("placement_attempts", 1).forGetter(structure -> structure.placementAttempts),
            Codec.intRange(1, 16).optionalFieldOf("terrain_sample_step", 4).forGetter(structure -> structure.terrainSampleStep)
    ).apply(instance, AstralShrineStructure::new));

    private final ResourceLocation templateId;
    private final int yOffset;
    private final int maxTerrainDelta;
    private final int maxSurroundingTerrainDelta;
    private final int maxSurfaceFloorDelta;
    private final int surroundingTerrainMargin;
    private final int terrainSmoothingMargin;
    private final int terrainSmoothingFillLimit;
    private final int terrainSmoothingCutLimit;
    private final int terrainSmoothingYOffset;
    private final int terrainSupportFillLimit;
    private final int terrainSupportYOffset;
    private final int placementAttempts;
    private final int terrainSampleStep;
    private static final Heightmap.Types TERRAIN_HEIGHTMAP = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

    public AstralShrineStructure(StructureSettings settings, ResourceLocation templateId, int yOffset, int maxTerrainDelta, int maxSurroundingTerrainDelta, int maxSurfaceFloorDelta, int surroundingTerrainMargin, int terrainSmoothingMargin, int terrainSmoothingFillLimit, int terrainSmoothingCutLimit, int terrainSmoothingYOffset, int terrainSupportFillLimit, int terrainSupportYOffset, int placementAttempts, int terrainSampleStep) {
        super(settings);
        this.templateId = templateId;
        this.yOffset = yOffset;
        this.maxTerrainDelta = maxTerrainDelta;
        this.maxSurroundingTerrainDelta = maxSurroundingTerrainDelta;
        this.maxSurfaceFloorDelta = maxSurfaceFloorDelta;
        this.surroundingTerrainMargin = surroundingTerrainMargin;
        this.terrainSmoothingMargin = terrainSmoothingMargin;
        this.terrainSmoothingFillLimit = terrainSmoothingFillLimit;
        this.terrainSmoothingCutLimit = terrainSmoothingCutLimit;
        this.terrainSmoothingYOffset = terrainSmoothingYOffset;
        this.terrainSupportFillLimit = terrainSupportFillLimit;
        this.terrainSupportYOffset = terrainSupportYOffset;
        this.placementAttempts = placementAttempts;
        this.terrainSampleStep = terrainSampleStep;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Optional<StructureTemplate> optionalTemplate = Optional.empty();
        if (maxTerrainDelta >= 0) {
            optionalTemplate = context.structureTemplateManager().get(templateId);
            if (optionalTemplate.isEmpty()) {
                return Optional.empty();
            }
        }

        ChunkPos chunkPos = context.chunkPos();
        for (int attempt = 0; attempt < placementAttempts; attempt++) {
            int x = chunkPos.getMinBlockX() + context.random().nextInt(16);
            int z = chunkPos.getMinBlockZ() + context.random().nextInt(16);
            int y = context.chunkGenerator().getFirstOccupiedHeight(
                    x,
                    z,
                    TERRAIN_HEIGHTMAP,
                    context.heightAccessor(),
                    context.randomState());
            Rotation rotation = Rotation.getRandom(context.random());

            if (optionalTemplate.isPresent()) {
                TerrainSample sample = sampleTerrain(context, optionalTemplate.get(), x, z, rotation, 0);
                if (sample.maxHeight() - sample.minHeight() > maxTerrainDelta) {
                    continue;
                }
                if (maxSurfaceFloorDelta >= 0 && sample.maxSurfaceFloorDelta() > maxSurfaceFloorDelta) {
                    continue;
                }
                if (maxSurroundingTerrainDelta >= 0 && surroundingTerrainMargin > 0) {
                    TerrainSample surroundingSample = sampleTerrain(context, optionalTemplate.get(), x, z, rotation, surroundingTerrainMargin);
                    if (surroundingSample.maxHeight() - surroundingSample.minHeight() > maxSurroundingTerrainDelta) {
                        continue;
                    }
                    if (maxSurfaceFloorDelta >= 0 && surroundingSample.maxSurfaceFloorDelta() > maxSurfaceFloorDelta) {
                        continue;
                    }
                }
                y = sample.averageHeight();
            }

            BlockPos position = new BlockPos(x, y + yOffset, z);
            return Optional.of(new GenerationStub(position, builder ->
                    AstralShrinePieces.addPiece(
                            context.structureTemplateManager(),
                            builder,
                            templateId,
                            position,
                            rotation,
                            yOffset,
                            terrainSmoothingMargin,
                            terrainSmoothingFillLimit,
                            terrainSmoothingCutLimit,
                            terrainSmoothingYOffset,
                            terrainSupportFillLimit,
                            terrainSupportYOffset)));
        }
        return Optional.empty();
    }

    @Override
    public StructureType<?> type() {
        return ASStructureRegistries.SHRINE.get();
    }

    private TerrainSample sampleTerrain(GenerationContext context, StructureTemplate template, int centerX, int centerZ, Rotation rotation, int margin) {
        Vec3i size = template.getSize();
        boolean quarterTurn = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
        int sizeX = (quarterTurn ? size.getZ() : size.getX()) + margin * 2;
        int sizeZ = (quarterTurn ? size.getX() : size.getZ()) + margin * 2;
        int minX = centerX - sizeX / 2;
        int minZ = centerZ - sizeZ / 2;

        TerrainAccumulator accumulator = new TerrainAccumulator();
        sampleTerrainGrid(context, accumulator, minX, minZ, sizeX, sizeZ);
        sampleTerrainPoint(context, accumulator, centerX, centerZ);
        sampleTerrainPoint(context, accumulator, minX, minZ);
        sampleTerrainPoint(context, accumulator, minX + sizeX - 1, minZ);
        sampleTerrainPoint(context, accumulator, minX, minZ + sizeZ - 1);
        sampleTerrainPoint(context, accumulator, minX + sizeX - 1, minZ + sizeZ - 1);
        return accumulator.toSample();
    }

    private void sampleTerrainGrid(GenerationContext context, TerrainAccumulator accumulator, int minX, int minZ, int sizeX, int sizeZ) {
        for (int dx = 0; dx < sizeX; dx += terrainSampleStep) {
            for (int dz = 0; dz < sizeZ; dz += terrainSampleStep) {
                sampleTerrainPoint(context, accumulator, minX + dx, minZ + dz);
            }
        }
    }

    private static void sampleTerrainPoint(GenerationContext context, TerrainAccumulator accumulator, int x, int z) {
        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                x,
                z,
                TERRAIN_HEIGHTMAP,
                context.heightAccessor(),
                context.randomState());
        int floorY = context.chunkGenerator().getFirstOccupiedHeight(
                x,
                z,
                Heightmap.Types.OCEAN_FLOOR_WG,
                context.heightAccessor(),
                context.randomState());
        accumulator.add(surfaceY, Math.max(0, surfaceY - floorY));
    }

    private static final class TerrainAccumulator {
        private int minHeight = Integer.MAX_VALUE;
        private int maxHeight = Integer.MIN_VALUE;
        private int maxSurfaceFloorDelta = 0;
        private int count = 0;
        private int totalHeight = 0;

        private void add(int height, int surfaceFloorDelta) {
            minHeight = Math.min(minHeight, height);
            maxHeight = Math.max(maxHeight, height);
            maxSurfaceFloorDelta = Math.max(maxSurfaceFloorDelta, surfaceFloorDelta);
            totalHeight += height;
            count++;
        }

        private TerrainSample toSample() {
            return new TerrainSample(minHeight, maxHeight, Math.round((float) totalHeight / count), maxSurfaceFloorDelta);
        }
    }

    private record TerrainSample(int minHeight, int maxHeight, int averageHeight, int maxSurfaceFloorDelta) {
    }
}
