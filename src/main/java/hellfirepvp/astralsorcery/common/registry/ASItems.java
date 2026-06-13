package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public final class ASItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AstralSorcery.MODID);

    public static final DeferredItem<BlockItem> MARBLE_RAW = blockItem("marble_raw", ASBlocks.MARBLE_RAW);
    public static final DeferredItem<BlockItem> MARBLE_BRICKS = blockItem("marble_bricks", ASBlocks.MARBLE_BRICKS);
    public static final DeferredItem<BlockItem> MARBLE_ARCH = blockItem("marble_arch", ASBlocks.MARBLE_ARCH);
    public static final DeferredItem<BlockItem> MARBLE_CHISELED = blockItem("marble_chiseled", ASBlocks.MARBLE_CHISELED);
    public static final DeferredItem<BlockItem> MARBLE_ENGRAVED = blockItem("marble_engraved", ASBlocks.MARBLE_ENGRAVED);
    public static final DeferredItem<BlockItem> MARBLE_PILLAR = blockItem("marble_pillar", ASBlocks.MARBLE_PILLAR);
    public static final DeferredItem<BlockItem> MARBLE_RUNED = blockItem("marble_runed", ASBlocks.MARBLE_RUNED);
    public static final DeferredItem<BlockItem> MARBLE_SLAB = blockItem("marble_slab", ASBlocks.MARBLE_SLAB);
    public static final DeferredItem<BlockItem> MARBLE_STAIRS = blockItem("marble_stairs", ASBlocks.MARBLE_STAIRS);

    public static final DeferredItem<BlockItem> BLACK_MARBLE_RAW = blockItem("black_marble_raw", ASBlocks.BLACK_MARBLE_RAW);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_BRICKS = blockItem("black_marble_bricks", ASBlocks.BLACK_MARBLE_BRICKS);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_ARCH = blockItem("black_marble_arch", ASBlocks.BLACK_MARBLE_ARCH);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_CHISELED = blockItem("black_marble_chiseled", ASBlocks.BLACK_MARBLE_CHISELED);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_ENGRAVED = blockItem("black_marble_engraved", ASBlocks.BLACK_MARBLE_ENGRAVED);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_PILLAR = blockItem("black_marble_pillar", ASBlocks.BLACK_MARBLE_PILLAR);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_RUNED = blockItem("black_marble_runed", ASBlocks.BLACK_MARBLE_RUNED);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_SLAB = blockItem("black_marble_slab", ASBlocks.BLACK_MARBLE_SLAB);
    public static final DeferredItem<BlockItem> BLACK_MARBLE_STAIRS = blockItem("black_marble_stairs", ASBlocks.BLACK_MARBLE_STAIRS);

    public static final DeferredItem<BlockItem> AQUAMARINE_SAND_ORE = blockItem("aquamarine_sand_ore", ASBlocks.AQUAMARINE_SAND_ORE);
    public static final DeferredItem<BlockItem> ROCK_CRYSTAL_ORE = blockItem("rock_crystal_ore", ASBlocks.ROCK_CRYSTAL_ORE);
    public static final DeferredItem<BlockItem> STARMETAL = blockItem("starmetal", ASBlocks.STARMETAL);
    public static final DeferredItem<BlockItem> STARMETAL_ORE = blockItem("starmetal_ore", ASBlocks.STARMETAL_ORE);
    public static final DeferredItem<BlockItem> ALTAR_DISCOVERY = blockItem("altar_discovery", ASBlocks.ALTAR_DISCOVERY);
    public static final DeferredItem<BlockItem> WELL = blockItem("well", ASBlocks.WELL);

    public static final DeferredItem<Item> AQUAMARINE = simple("aquamarine");
    public static final DeferredItem<Item> ROCK_CRYSTAL = simple("rock_crystal", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> PARCHMENT = simple("parchment");
    public static final DeferredItem<Item> CONSTELLATION_PAPER = simple("constellation_paper", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> WAND = simple("wand", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> GLASS_LENS = simple("glass_lens");
    public static final DeferredItem<Item> STARDUST = simple("stardust");
    public static final DeferredItem<Item> STARMETAL_INGOT = simple("starmetal_ingot");
    public static final DeferredItem<Item> TOME = simple("tome", new Item.Properties().stacksTo(1));
    public static final DeferredItem<BucketItem> BUCKET_LIQUID_STARLIGHT = ITEMS.register("bucket_liquid_starlight",
            () -> new BucketItem(ASFluids.LIQUID_STARLIGHT.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final List<Supplier<? extends ItemLike>> CREATIVE_TAB_ITEMS = List.of(
            MARBLE_RAW, MARBLE_BRICKS, MARBLE_ARCH, MARBLE_CHISELED, MARBLE_ENGRAVED, MARBLE_PILLAR, MARBLE_RUNED,
            MARBLE_SLAB, MARBLE_STAIRS, BLACK_MARBLE_RAW, BLACK_MARBLE_BRICKS, BLACK_MARBLE_ARCH,
            BLACK_MARBLE_CHISELED, BLACK_MARBLE_ENGRAVED, BLACK_MARBLE_PILLAR, BLACK_MARBLE_RUNED,
            BLACK_MARBLE_SLAB, BLACK_MARBLE_STAIRS, AQUAMARINE_SAND_ORE, ROCK_CRYSTAL_ORE, STARMETAL,
            STARMETAL_ORE, ALTAR_DISCOVERY, WELL, AQUAMARINE, ROCK_CRYSTAL, PARCHMENT, CONSTELLATION_PAPER,
            WAND, GLASS_LENS, STARDUST, STARMETAL_INGOT, TOME, BUCKET_LIQUID_STARLIGHT);

    private ASItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<Item> simple(String name) {
        return simple(name, new Item.Properties());
    }

    private static DeferredItem<Item> simple(String name, Item.Properties properties) {
        return ITEMS.register(name, () -> new Item(properties));
    }

    private static DeferredItem<BlockItem> blockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
