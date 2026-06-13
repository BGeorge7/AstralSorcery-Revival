package hellfirepvp.astralsorcery.common.util.tick;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ASTickBus {
    private static final List<ServerTickHandler> SERVER_HANDLERS = new CopyOnWriteArrayList<>();
    private static final List<LevelTickHandler> LEVEL_HANDLERS = new CopyOnWriteArrayList<>();

    private ASTickBus() {
    }

    public static void register(IEventBus gameEventBus) {
        gameEventBus.addListener(ASTickBus::onServerTick);
        gameEventBus.addListener(ASTickBus::onLevelTick);
    }

    public static void addServerHandler(ServerTickHandler handler) {
        SERVER_HANDLERS.add(handler);
    }

    public static void addLevelHandler(LevelTickHandler handler) {
        LEVEL_HANDLERS.add(handler);
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        SERVER_HANDLERS.forEach(handler -> handler.tick(server));
    }

    private static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        LEVEL_HANDLERS.forEach(handler -> handler.tick(level));
    }

    @FunctionalInterface
    public interface ServerTickHandler {
        void tick(MinecraftServer server);
    }

    @FunctionalInterface
    public interface LevelTickHandler {
        void tick(Level level);
    }
}
