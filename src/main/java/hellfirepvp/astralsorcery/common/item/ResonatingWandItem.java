package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.block.DiscoveryAltarBlock;
import hellfirepvp.astralsorcery.common.registry.ASBlocks;
import hellfirepvp.astralsorcery.common.registry.ASSounds;
import hellfirepvp.astralsorcery.common.starlight.StarlightService;
import hellfirepvp.astralsorcery.common.tile.DiscoveryAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResonatingWandItem extends Item {
    private static final float DISCOVERY_ALTAR_STARLIGHT_REQUIREMENT = 0.15F;

    public ResonatingWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (state.is(ASBlocks.ALTAR_DISCOVERY.get())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DiscoveryAltarBlockEntity altar) {
                return DiscoveryAltarBlock.showStarlight(context, altar);
            }
        }

        if (!state.is(Blocks.CRAFTING_TABLE)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        float starlight = StarlightService.getAmbientStarlight(level, pos);
        if (starlight < DISCOVERY_ALTAR_STARLIGHT_REQUIREMENT) {
            if (player != null) {
                player.displayClientMessage(Component.translatable("astralsorcery.misc.wand.not_enough_starlight"), true);
            }
            return InteractionResult.FAIL;
        }

        level.setBlockAndUpdate(pos, ASBlocks.ALTAR_DISCOVERY.get().defaultBlockState());
        level.playSound(null, pos, ASSounds.CRAFT_FINISH.get(), SoundSource.BLOCKS, 0.8F, 1.2F);
        if (player != null) {
            player.displayClientMessage(Component.translatable("astralsorcery.misc.wand.transmute_altar"), true);
        }
        return InteractionResult.SUCCESS;
    }
}
