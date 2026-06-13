package hellfirepvp.astralsorcery.common.data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AstralSavedData {
    private final Map<ResourceKey<Level>, Set<BlockPos>> starlightNetworkAnchors = new HashMap<>();

    public void rememberStarlightAnchor(Level level, BlockPos pos) {
        starlightNetworkAnchors
                .computeIfAbsent(level.dimension(), ignored -> new HashSet<>())
                .add(pos.immutable());
    }

    public Set<BlockPos> getStarlightAnchors(Level level) {
        return Set.copyOf(starlightNetworkAnchors.getOrDefault(level.dimension(), Set.of()));
    }
}
