package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.registry.ASBlocks;
import hellfirepvp.astralsorcery.common.tile.CollectorCrystalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Optional;

public final class AstralShrinePieces {
    static final ResourceKey<LootTable> SHRINE_CHEST_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(AstralSorcery.MODID, "shrine_chest"));

    private AstralShrinePieces() {
    }

    public static void addPiece(StructureTemplateManager templateManager, StructurePieceAccessor pieces, ResourceLocation templateId, BlockPos center, Rotation rotation) {
        Optional<StructureTemplate> optionalTemplate = templateManager.get(templateId);
        if (optionalTemplate.isEmpty()) {
            return;
        }

        StructureTemplate template = optionalTemplate.get();
        BlockPos placement = center.offset(-template.getSize().getX() / 2, 0, -template.getSize().getZ() / 2);
        pieces.addPiece(new AstralShrinePiece(templateManager, templateId, placement, rotation));
    }

    static StructurePlaceSettings settings(Rotation rotation) {
        return new StructurePlaceSettings()
                .setRotation(rotation)
                .setIgnoreEntities(true)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
    }

    public static void handleMarker(String marker, BlockPos pos, ServerLevelAccessor level, RandomSource random) {
        switch (marker) {
            case "crystal" -> makeCollectorCrystal(level, pos, random);
            case "shrine_chest" -> {
                if (random.nextBoolean()) {
                    makeChest(level, pos, random);
                } else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            case "brick_shrine_chest" -> {
                if (random.nextBoolean()) {
                    makeChest(level, pos, random);
                } else {
                    level.setBlock(pos, ASBlocks.MARBLE_BRICKS.get().defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            case "random_top_block" -> {
                if (random.nextFloat() < 0.7F) {
                    level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
                } else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            default -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private static void makeCollectorCrystal(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, ASBlocks.ROCK_COLLECTOR_CRYSTAL.get().defaultBlockState(), Block.UPDATE_ALL);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CollectorCrystalBlockEntity crystal) {
            crystal.initializeWorldgen(random);
        }
    }

    private static void makeChest(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState chest = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing);
        level.setBlock(pos, chest, Block.UPDATE_ALL);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RandomizableContainerBlockEntity container) {
            container.setLootTable(SHRINE_CHEST_LOOT, random.nextLong());
        }
    }
}
