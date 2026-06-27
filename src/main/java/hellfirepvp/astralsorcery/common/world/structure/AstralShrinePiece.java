package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.registry.ASStructureRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class AstralShrinePiece extends TemplateStructurePiece {
    private static final String ROTATION_TAG = "Rot";
    private static final String Y_OFFSET_TAG = "YOffset";
    private static final String SMOOTHING_MARGIN_TAG = "SmoothingMargin";
    private static final String SMOOTHING_FILL_LIMIT_TAG = "SmoothingFillLimit";
    private static final String SMOOTHING_CUT_LIMIT_TAG = "SmoothingCutLimit";
    private static final String SMOOTHING_Y_OFFSET_TAG = "SmoothingYOffset";
    private static final String SUPPORT_FILL_LIMIT_TAG = "SupportFillLimit";
    private static final String SUPPORT_Y_OFFSET_TAG = "SupportYOffset";
    private static final Heightmap.Types TERRAIN_HEIGHTMAP = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;

    private final int yOffset;
    private final int terrainSmoothingMargin;
    private final int terrainSmoothingFillLimit;
    private final int terrainSmoothingCutLimit;
    private final int terrainSmoothingYOffset;
    private final int terrainSupportFillLimit;
    private final int terrainSupportYOffset;

    public AstralShrinePiece(StructureTemplateManager templateManager, ResourceLocation templateId, BlockPos position, Rotation rotation, int yOffset, int terrainSmoothingMargin, int terrainSmoothingFillLimit, int terrainSmoothingCutLimit, int terrainSmoothingYOffset, int terrainSupportFillLimit, int terrainSupportYOffset) {
        super(ASStructureRegistries.SHRINE_PIECE.get(), 0, templateManager, templateId, templateId.toString(),
                AstralShrinePieces.settings(rotation), position);
        this.yOffset = yOffset;
        this.terrainSmoothingMargin = terrainSmoothingMargin;
        this.terrainSmoothingFillLimit = terrainSmoothingFillLimit;
        this.terrainSmoothingCutLimit = terrainSmoothingCutLimit;
        this.terrainSmoothingYOffset = terrainSmoothingYOffset;
        this.terrainSupportFillLimit = terrainSupportFillLimit;
        this.terrainSupportYOffset = terrainSupportYOffset;
    }

    public AstralShrinePiece(StructureTemplateManager templateManager, CompoundTag tag) {
        super(ASStructureRegistries.SHRINE_PIECE.get(), tag, templateManager,
                templateId -> AstralShrinePieces.settings(Rotation.valueOf(tag.getString(ROTATION_TAG))));
        this.yOffset = tag.getInt(Y_OFFSET_TAG);
        this.terrainSmoothingMargin = tag.getInt(SMOOTHING_MARGIN_TAG);
        this.terrainSmoothingFillLimit = tag.getInt(SMOOTHING_FILL_LIMIT_TAG);
        this.terrainSmoothingCutLimit = tag.getInt(SMOOTHING_CUT_LIMIT_TAG);
        this.terrainSmoothingYOffset = tag.getInt(SMOOTHING_Y_OFFSET_TAG);
        this.terrainSupportFillLimit = tag.getInt(SUPPORT_FILL_LIMIT_TAG);
        this.terrainSupportYOffset = tag.getInt(SUPPORT_Y_OFFSET_TAG);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION_TAG, placeSettings.getRotation().name());
        tag.putInt(Y_OFFSET_TAG, yOffset);
        tag.putInt(SMOOTHING_MARGIN_TAG, terrainSmoothingMargin);
        tag.putInt(SMOOTHING_FILL_LIMIT_TAG, terrainSmoothingFillLimit);
        tag.putInt(SMOOTHING_CUT_LIMIT_TAG, terrainSmoothingCutLimit);
        tag.putInt(SMOOTHING_Y_OFFSET_TAG, terrainSmoothingYOffset);
        tag.putInt(SUPPORT_FILL_LIMIT_TAG, terrainSupportFillLimit);
        tag.putInt(SUPPORT_Y_OFFSET_TAG, terrainSupportYOffset);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        super.postProcess(level, structureManager, chunkGenerator, random, box, chunkPos, pos);
        if (terrainSmoothingMargin > 0 && (terrainSmoothingFillLimit > 0 || terrainSmoothingCutLimit > 0)) {
            smoothTerrainEdges(level, box);
        }
        if (terrainSupportFillLimit > 0) {
            fillTerrainUnderFootprint(level, box);
        }
    }

    @Override
    protected void handleDataMarker(String marker, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
        AstralShrinePieces.handleMarker(marker, pos, level, random);
    }

    private void smoothTerrainEdges(WorldGenLevel level, BoundingBox chunkBox) {
        BoundingBox structureBox = getBoundingBox();
        int targetHeight = structureBox.minY() - yOffset + terrainSmoothingYOffset;
        int minX = structureBox.minX() - terrainSmoothingMargin;
        int maxX = structureBox.maxX() + terrainSmoothingMargin;
        int minZ = structureBox.minZ() - terrainSmoothingMargin;
        int maxZ = structureBox.maxZ() + terrainSmoothingMargin;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (isInsideStructureFootprint(structureBox, x, z) || !chunkBox.isInside(x, targetHeight, z)) {
                    continue;
                }
                smoothTerrainColumn(level, x, z, targetHeight);
            }
        }
    }

    private void fillTerrainUnderFootprint(WorldGenLevel level, BoundingBox chunkBox) {
        BoundingBox structureBox = getBoundingBox();
        int targetHeight = structureBox.minY() - yOffset + terrainSupportYOffset;

        for (int x = structureBox.minX(); x <= structureBox.maxX(); x++) {
            for (int z = structureBox.minZ(); z <= structureBox.maxZ(); z++) {
                if (!chunkBox.isInside(x, targetHeight, z) || !hasStructureBlockAbove(level, x, z, targetHeight)) {
                    continue;
                }
                int currentHeight = level.getHeight(TERRAIN_HEIGHTMAP, x, z);
                int delta = targetHeight - currentHeight;
                if (delta > 0 && delta <= terrainSupportFillLimit) {
                    fillTerrainColumn(level, x, z, currentHeight, targetHeight);
                }
            }
        }
    }

    private boolean hasStructureBlockAbove(WorldGenLevel level, int x, int z, int targetHeight) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int maxY = Math.min(targetHeight + 3, getBoundingBox().maxY());
        for (int y = targetHeight; y <= maxY; y++) {
            mutablePos.set(x, y, z);
            BlockState state = level.getBlockState(mutablePos);
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isInsideStructureFootprint(BoundingBox box, int x, int z) {
        return x >= box.minX() && x <= box.maxX() && z >= box.minZ() && z <= box.maxZ();
    }

    private void smoothTerrainColumn(WorldGenLevel level, int x, int z, int targetHeight) {
        int currentHeight = level.getHeight(TERRAIN_HEIGHTMAP, x, z);
        int delta = targetHeight - currentHeight;
        if (delta > 0 && delta <= terrainSmoothingFillLimit) {
            fillTerrainColumn(level, x, z, currentHeight, targetHeight);
        } else if (delta < 0 && -delta <= terrainSmoothingCutLimit) {
            cutTerrainColumn(level, x, z, targetHeight, currentHeight);
        }
    }

    private void fillTerrainColumn(WorldGenLevel level, int x, int z, int fromHeight, int targetHeight) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        BlockState topState = AstralShrinePieces.topBlockForBiome(level, mutablePos.set(x, targetHeight - 1, z));
        BlockState fillerState = terrainFillerFor(topState);
        for (int y = fromHeight; y < targetHeight; y++) {
            mutablePos.set(x, y, z);
            if (!level.ensureCanWrite(mutablePos)) {
                continue;
            }
            level.setBlock(mutablePos, y == targetHeight - 1 ? topState : fillerState, Block.UPDATE_ALL);
        }
    }

    private void cutTerrainColumn(WorldGenLevel level, int x, int z, int targetHeight, int currentHeight) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int y = targetHeight; y < currentHeight; y++) {
            mutablePos.set(x, y, z);
            if (level.ensureCanWrite(mutablePos)) {
                level.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        mutablePos.set(x, targetHeight - 1, z);
        if (level.ensureCanWrite(mutablePos)) {
            level.setBlock(mutablePos, AstralShrinePieces.topBlockForBiome(level, mutablePos), Block.UPDATE_ALL);
        }
    }

    private BlockState terrainFillerFor(BlockState topState) {
        if (topState.is(Blocks.SAND)) {
            return Blocks.SANDSTONE.defaultBlockState();
        }
        if (topState.is(Blocks.RED_SAND)) {
            return Blocks.RED_SANDSTONE.defaultBlockState();
        }
        return Blocks.DIRT.defaultBlockState();
    }
}
