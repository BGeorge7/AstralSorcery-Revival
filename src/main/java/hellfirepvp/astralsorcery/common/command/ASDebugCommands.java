package hellfirepvp.astralsorcery.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.registry.ASBlocks;
import hellfirepvp.astralsorcery.common.world.feature.AstralShrineFeature;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ASDebugCommands {
    private static final int DEFAULT_RADIUS = 128;
    private static final int MAX_SCAN_RADIUS = 256;
    private static final int MIN_Y = -64;
    private static final int MAX_Y = 16;
    private static final int STRUCTURE_SCAN_VERTICAL_RADIUS = 96;

    private ASDebugCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("astraldebug")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("find_rock_crystal")
                        .executes(context -> findRockCrystal(context.getSource(), DEFAULT_RADIUS))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, MAX_SCAN_RADIUS))
                                .executes(context -> findRockCrystal(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))))
                .then(Commands.literal("count_worldgen")
                        .executes(context -> countWorldgenBlocks(context.getSource(), DEFAULT_RADIUS))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, MAX_SCAN_RADIUS))
                                .executes(context -> countWorldgenBlocks(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))))
                .then(Commands.literal("place_rock_crystal")
                        .executes(context -> placeRockCrystal(context.getSource())))
                .then(Commands.literal("find_collector_crystal")
                        .executes(context -> findCollectorCrystal(context.getSource(), DEFAULT_RADIUS))
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, MAX_SCAN_RADIUS))
                                .executes(context -> findCollectorCrystal(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))))
                .then(Commands.literal("place_shrine")
                        .then(Commands.literal("small")
                                .executes(context -> placeShrine(context.getSource(), "small_shrine")))
                        .then(Commands.literal("ancient")
                                .executes(context -> placeShrine(context.getSource(), "ancient_shrine")))
                        .then(Commands.literal("desert")
                                .executes(context -> placeShrine(context.getSource(), "desert_shrine")))));
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
                    if (!isRockCrystalOre(state)) {
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

    private static int placeShrine(CommandSourceStack source, String templateName) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos origin = player.blockPosition().relative(player.getDirection(), 6);
        ResourceLocation templateId = ResourceLocation.fromNamespaceAndPath(AstralSorcery.MODID, templateName);
        boolean placed = AstralShrineFeature.placeTemplate(level, RandomSource.create(), origin, templateId);
        if (!placed) {
            source.sendFailure(Component.literal("Could not place shrine template " + templateId + ". Check that the structure NBT loaded."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Placed " + templateName + " near " + format(origin) + "."), true);
        return 1;
    }

    private static int findCollectorCrystal(CommandSourceStack source, int radius) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos origin = player.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int minY = Math.max(level.getMinBuildHeight(), origin.getY() - STRUCTURE_SCAN_VERTICAL_RADIUS);
        int maxY = Math.min(level.getMaxBuildHeight(), origin.getY() + STRUCTURE_SCAN_VERTICAL_RADIUS);
        for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
            for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                for (int y = minY; y < maxY; y++) {
                    cursor.set(x, y, z);
                    if (!level.isLoaded(cursor)) {
                        continue;
                    }
                    if (!level.getBlockState(cursor).is(ASBlocks.ROCK_COLLECTOR_CRYSTAL.get())) {
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
            source.sendFailure(Component.literal("No loaded collector crystal found within " + radius + " blocks."));
            return 0;
        }

        int distance = (int) Math.round(Math.sqrt(nearestDistance));
        BlockPos found = nearest;
        source.sendSuccess(() -> Component.literal("Nearest loaded collector crystal: " + format(found) + " (" + distance + " blocks away)."), false);
        return 1;
    }

    private static int countWorldgenBlocks(CommandSourceStack source, int radius) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos origin = player.blockPosition();

        int rockCrystal = 0;
        int aquamarine = 0;
        int marble = 0;
        int loadedPositions = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = origin.getX() - radius; x <= origin.getX() + radius; x++) {
            for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; z++) {
                for (int y = MIN_Y; y <= MAX_Y; y++) {
                    cursor.set(x, y, z);
                    if (!level.isLoaded(cursor)) {
                        continue;
                    }

                    loadedPositions++;
                    BlockState state = level.getBlockState(cursor);
                    if (isRockCrystalOre(state)) {
                        rockCrystal++;
                    } else if (state.is(ASBlocks.AQUAMARINE_SAND_ORE.get())) {
                        aquamarine++;
                    } else if (state.is(ASBlocks.MARBLE_RAW.get())) {
                        marble++;
                    }
                }
            }
        }

        int found = rockCrystal + aquamarine + marble;
        String message = "Loaded Astral worldgen blocks within " + radius + " blocks: rock_crystal=" + rockCrystal
                + ", aquamarine_shale=" + aquamarine + ", marble=" + marble + " across " + loadedPositions + " loaded positions.";
        source.sendSuccess(() -> Component.literal(message), false);
        return found;
    }

    private static String format(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }

    private static boolean isRockCrystalOre(BlockState state) {
        return state.is(ASBlocks.ROCK_CRYSTAL_ORE.get()) || state.is(ASBlocks.DEEPSLATE_ROCK_CRYSTAL_ORE.get());
    }
}
