package hellfirepvp.astralsorcery.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import hellfirepvp.astralsorcery.common.registry.ASBlocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ASDebugCommands {
    private static final int DEFAULT_RADIUS = 128;
    private static final int MIN_Y = -64;
    private static final int MAX_Y = 16;

    private ASDebugCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("astraldebug")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("find_rock_crystal")
                        .executes(context -> findRockCrystal(context.getSource(), DEFAULT_RADIUS))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 512))
                                .executes(context -> findRockCrystal(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))))
                .then(Commands.literal("place_rock_crystal")
                        .executes(context -> placeRockCrystal(context.getSource()))));
    }

    private static int findRockCrystal(CommandSourceStack source, int radius) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos origin = player.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        int minX = origin.getX() - radius;
        int maxX = origin.getX() + radius;
        int minZ = origin.getZ() - radius;
        int maxZ = origin.getZ() + radius;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = MIN_Y; y <= MAX_Y; y++) {
                    cursor.set(x, y, z);
                    if (!level.isLoaded(cursor)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(cursor);
                    if (!state.is(ASBlocks.ROCK_CRYSTAL_ORE.get())) {
                        continue;
                    }

                    double distance = cursor.distSqr(origin);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = cursor.immutable();
                    }
                }
            }
        }

        if (nearest == null) {
            source.sendFailure(Component.literal("No loaded rock crystal ore found within " + radius + " blocks from Y " + MIN_Y + " to " + MAX_Y + ". Try fresh chunks or a larger radius."));
            return 0;
        }

        int distance = (int) Math.round(Math.sqrt(nearestDistance));
        BlockPos found = nearest;
        source.sendSuccess(() -> Component.literal("Nearest loaded rock crystal ore: " + format(found) + " (" + distance + " blocks away)."), false);
        return 1;
    }

    private static int placeRockCrystal(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition().below();
        level.setBlockAndUpdate(pos, ASBlocks.ROCK_CRYSTAL_ORE.get().defaultBlockState());
        source.sendSuccess(() -> Component.literal("Placed rock crystal ore at " + format(pos) + "."), true);
        return 1;
    }

    private static String format(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }
}
