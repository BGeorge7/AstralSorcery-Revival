package hellfirepvp.astralsorcery.common.block;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.registry.ASBlockEntityTypes;
import hellfirepvp.astralsorcery.common.tile.CollectorCrystalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CollectorCrystalBlock extends BaseEntityBlock {
    public static final MapCodec<CollectorCrystalBlock> CODEC = simpleCodec(CollectorCrystalBlock::new);
    private static final VoxelShape SHAPE = box(4.5D, 0.0D, 4.5D, 11.5D, 16.0D, 11.5D);

    public CollectorCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CollectorCrystalBlockEntity crystal && crystal.isPlayerMade()) {
            return player.getDestroySpeed(state) / 4.0F / 30.0F;
        }
        return 0.0F;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CollectorCrystalBlockEntity crystal) {
            int percent = Math.round(crystal.getLastCollectedStarlight() * 100.0F);
            player.displayClientMessage(Component.translatable(
                    "astralsorcery.misc.collector.status",
                    crystal.getConstellationDisplayName(),
                    percent), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CollectorCrystalBlockEntity crystal) {
            crystal.initializePlayerMade();
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CollectorCrystalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ASBlockEntityTypes.COLLECTOR_CRYSTAL.get(), CollectorCrystalBlockEntity::tick);
    }
}
