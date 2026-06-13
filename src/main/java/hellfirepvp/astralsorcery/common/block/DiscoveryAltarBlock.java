package hellfirepvp.astralsorcery.common.block;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.registry.ASBlockEntityTypes;
import hellfirepvp.astralsorcery.common.tile.DiscoveryAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DiscoveryAltarBlock extends BaseEntityBlock {
    public static final MapCodec<DiscoveryAltarBlock> CODEC = simpleCodec(DiscoveryAltarBlock::new);

    public DiscoveryAltarBlock(Properties properties) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DiscoveryAltarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ASBlockEntityTypes.DISCOVERY_ALTAR.get(), DiscoveryAltarBlockEntity::tick);
    }

    public static InteractionResult showStarlight(UseOnContext context, DiscoveryAltarBlockEntity altar) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            int percent = Math.round(altar.getLastAmbientStarlight() * 100.0F);
            player.displayClientMessage(Component.translatable("astralsorcery.misc.starlight.local", percent), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState();
    }
}
