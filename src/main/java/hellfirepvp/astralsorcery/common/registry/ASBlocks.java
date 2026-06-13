package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.DiscoveryAltarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ASBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AstralSorcery.MODID);

    public static final DeferredBlock<Block> MARBLE_RAW = stone("marble_raw");
    public static final DeferredBlock<Block> MARBLE_BRICKS = stone("marble_bricks");
    public static final DeferredBlock<Block> MARBLE_ARCH = stone("marble_arch");
    public static final DeferredBlock<Block> MARBLE_CHISELED = stone("marble_chiseled");
    public static final DeferredBlock<Block> MARBLE_ENGRAVED = stone("marble_engraved");
    public static final DeferredBlock<Block> MARBLE_PILLAR = stone("marble_pillar");
    public static final DeferredBlock<Block> MARBLE_RUNED = stone("marble_runed");
    public static final DeferredBlock<SlabBlock> MARBLE_SLAB = slab("marble_slab");
    public static final DeferredBlock<StairBlock> MARBLE_STAIRS = stairs("marble_stairs", MARBLE_BRICKS);

    public static final DeferredBlock<Block> BLACK_MARBLE_RAW = darkStone("black_marble_raw");
    public static final DeferredBlock<Block> BLACK_MARBLE_BRICKS = darkStone("black_marble_bricks");
    public static final DeferredBlock<Block> BLACK_MARBLE_ARCH = darkStone("black_marble_arch");
    public static final DeferredBlock<Block> BLACK_MARBLE_CHISELED = darkStone("black_marble_chiseled");
    public static final DeferredBlock<Block> BLACK_MARBLE_ENGRAVED = darkStone("black_marble_engraved");
    public static final DeferredBlock<Block> BLACK_MARBLE_PILLAR = darkStone("black_marble_pillar");
    public static final DeferredBlock<Block> BLACK_MARBLE_RUNED = darkStone("black_marble_runed");
    public static final DeferredBlock<SlabBlock> BLACK_MARBLE_SLAB = darkSlab("black_marble_slab");
    public static final DeferredBlock<StairBlock> BLACK_MARBLE_STAIRS = stairs("black_marble_stairs", BLACK_MARBLE_BRICKS);

    public static final DeferredBlock<Block> AQUAMARINE_SAND_ORE = BLOCKS.register("aquamarine_sand_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.7F)
                    .sound(SoundType.SAND)));
    public static final DeferredBlock<Block> ROCK_CRYSTAL_ORE = BLOCKS.register("rock_crystal_ore",
            () -> new Block(stoneProps().strength(4.0F, 6.0F)));
    public static final DeferredBlock<Block> STARMETAL = BLOCKS.register("starmetal",
            () -> new Block(stoneProps().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 8.0F)));
    public static final DeferredBlock<Block> STARMETAL_ORE = BLOCKS.register("starmetal_ore",
            () -> new Block(stoneProps().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 8.0F)));

    public static final DeferredBlock<Block> ALTAR_DISCOVERY = BLOCKS.register("altar_discovery",
            () -> new DiscoveryAltarBlock(stoneProps().strength(3.0F, 9.0F).lightLevel(state -> 4)));
    public static final DeferredBlock<Block> WELL = BLOCKS.register("well",
            () -> new Block(stoneProps().strength(3.0F, 9.0F)));

    public static final DeferredBlock<LiquidBlock> LIQUID_STARLIGHT = BLOCKS.register("liquid_starlight",
            () -> new LiquidBlock(ASFluids.LIQUID_STARLIGHT.get(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .replaceable()
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()
                    .lightLevel(state -> 7)));

    private ASBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    private static DeferredBlock<Block> stone(String name) {
        return BLOCKS.register(name, () -> new Block(stoneProps()));
    }

    private static DeferredBlock<Block> darkStone(String name) {
        return BLOCKS.register(name, () -> new Block(stoneProps().mapColor(MapColor.COLOR_BLACK)));
    }

    private static DeferredBlock<SlabBlock> slab(String name) {
        return BLOCKS.register(name, () -> new SlabBlock(stoneProps()));
    }

    private static DeferredBlock<SlabBlock> darkSlab(String name) {
        return BLOCKS.register(name, () -> new SlabBlock(stoneProps().mapColor(MapColor.COLOR_BLACK)));
    }

    private static DeferredBlock<StairBlock> stairs(String name, Supplier<? extends Block> base) {
        return BLOCKS.register(name, () -> new StairBlock(base.get().defaultBlockState(), stoneProps()));
    }

    private static BlockBehaviour.Properties stoneProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.QUARTZ)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F)
                .sound(SoundType.STONE);
    }
}
