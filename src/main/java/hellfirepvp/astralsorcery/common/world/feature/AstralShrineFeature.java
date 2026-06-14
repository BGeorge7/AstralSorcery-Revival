package hellfirepvp.astralsorcery.common.world.feature;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.world.structure.AstralShrinePieces;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class AstralShrineFeature extends Feature<NoneFeatureConfiguration> {
    private final ResourceLocation templateId;

    public AstralShrineFeature(String templateName) {
        super(Codec.unit(NoneFeatureConfiguration.INSTANCE));
        this.templateId = ResourceLocation.fromNamespaceAndPath(AstralSorcery.MODID, templateName);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return placeTemplate(context.level(), context.random(), context.origin(), templateId);
    }

    public static boolean placeTemplate(ServerLevelAccessor level, RandomSource random, BlockPos origin, ResourceLocation templateId) {
        Optional<StructureTemplate> optionalTemplate = level.getLevel().getStructureManager().get(templateId);
        if (optionalTemplate.isEmpty()) {
            return false;
        }

        StructureTemplate template = optionalTemplate.get();
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setIgnoreEntities(true)
                .setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING)
                .setFinalizeEntities(false)
                .setRandom(random);

        BlockPos placement = origin.offset(-template.getSize().getX() / 2, 0, -template.getSize().getZ() / 2);
        boolean placed = template.placeInWorld(level, placement, placement, settings, random, Block.UPDATE_ALL);
        if (placed) {
            handleMarkers(level, random, template.getBoundingBox(settings, placement));
        }
        return placed;
    }

    private static void handleMarkers(ServerLevelAccessor level, RandomSource random, BoundingBox bounds) {
        for (BlockPos pos : BlockPos.betweenClosed(
                bounds.minX(), bounds.minY(), bounds.minZ(),
                bounds.maxX(), bounds.maxY(), bounds.maxZ())) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(Blocks.STRUCTURE_BLOCK)) {
                continue;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof StructureBlockEntity structureBlock && structureBlock.getMode() == StructureMode.DATA) {
                AstralShrinePieces.handleMarker(structureBlock.getMetaData(), pos.immutable(), level, random);
            } else {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
}
