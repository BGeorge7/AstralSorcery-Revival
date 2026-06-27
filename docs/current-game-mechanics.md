# Current Game Mechanics

This file tracks what the NeoForge `1.21.1` port currently does in-game. Keep this focused on implemented behavior, not the full desired roadmap.

## Development Environment

- Minecraft `1.21.1` runs on NeoForge `21.1.233`.
- Java 21 is required.
- Curios is available at runtime but not meaningfully used by gameplay yet.
- JEI is available at runtime for recipe/item inspection.
- The dev client is muted by the Gradle `muteDevClientAudio` task before `runClient`.

## Creative Content

The Astral Sorcery creative tab currently exposes the early content shell:

- Marble and black marble block variants.
- Aquamarine shale ore and aquamarine item.
- Rock crystal ore, deepslate rock crystal ore, and rock crystal item.
- Starmetal block, starmetal ore, stardust, and starmetal ingot placeholders.
- Discovery altar.
- Lightwell block.
- Liquid starlight fluid and bucket.
- Parchment, constellation paper, glass lens, tome, and resonating wand.
- Rock collector crystal block.

## Worldgen

Implemented feature worldgen:

- Aquamarine shale ore.
- Marble.
- Rock crystal ore.
- Deepslate rock crystal ore through the rock crystal configured feature.

Implemented registered structures:

- `astralsorcery:ancient_shrine`
- `astralsorcery:desert_shrine`
- `astralsorcery:small_shrine`

Shrines are registered structures, so vanilla locate works:

```mcfunction
/locate structure astralsorcery:ancient_shrine
/locate structure astralsorcery:desert_shrine
/locate structure astralsorcery:small_shrine
```

Shrine templates are stored in `data/astralsorcery/structure`.

Registered shrine placement now applies the legacy vertical offsets:

- Ancient shrine: `-7`.
- Desert shrine: `-11`.
- Small shrine: `0`.

Ancient shrines also perform bounded terrain checks before generating. The structure tries up to 32 random positions in the candidate chunk, samples the rotated shrine footprint every 2 blocks, and only generates when the sampled base terrain height range is 5 blocks or less. It also samples a 10-block margin around the footprint and rejects sites where that surrounding height range is greater than 14 blocks. Water/lake candidates are rejected by comparing surface height against ocean-floor height.

Ancient shrines can lightly blend their outer edge into acceptable terrain. The current pass touches only the area within 5 blocks outside the shrine footprint, fills up to 4 blocks, and cuts up to 2 blocks. The smoothing target is one block below the shrine's surface anchor so the outer base layer sits on the ground instead of being buried by infill. Larger hills or mountain walls should be rejected instead of bulldozed.

Ancient shrines also support-fill below their own footprint to prevent floating corners. This support pass only fills under columns that have an actual non-fluid shrine block above the terrain target, uses local terrain top/filler blocks, and is limited to 6 blocks of fill.

## Shrine Behavior

Legacy NBT templates are used for the three early shrine types.

Structure block data markers are handled as follows:

- `crystal`: places a rock collector crystal block entity.
- `shrine_chest`: randomly places a shrine loot chest or air.
- `brick_shrine_chest`: randomly places a shrine loot chest or marble bricks.
- `random_top_block`: places biome-aware top cover most of the time or air. Desert uses sand, badlands variants use red sand, and other biomes currently fall back to grass.

Shrine chests use `data/astralsorcery/loot_table/shrine_chest.json`.

## Collector Crystals

Rock collector crystals currently:

- Exist as `astralsorcery:rock_collector_crystal`.
- Have a block entity.
- Store legacy-shaped crystal attribute NBT under `crystalProperties.attributes`.
- Worldgen shrine crystals currently receive Size 2, Purity 2, Shape 2, and Collector Rate 2.
- Worldgen shrine crystals initialize as non-player-made crystals.
- Worldgen crystals randomly choose one major constellation label from:
  - `aevitas`
  - `discidia`
  - `armara`
  - `vicio`
  - `evorsio`
- Tick once per second.
- Sample ambient starlight using `StarlightService`.
- Require sky visibility before they can produce starlight.
- Show their constellation, current starlight percentage, sky visibility, and attribute summary when right-clicked.
- Non-player-made crystals transmute nearby vanilla crafting tables into discovery altars after enough produced starlight accumulates.
- Faint client-side particles using legacy Astral Sorcery particle sprites appear around collector crystals.
- Brighter sky-exposure god rays render only while the collector crystal can see the sky and is producing starlight.
- During crafting table transmutation, a single continuous lightbeam renders from the collector crystal toward the target crafting table.
- When a crafting table finishes transforming, a brief burst of rock-crystal-tinted particles rises from the transformed block and fades out.

Known limitations:

- The table beam uses the old `lightbeam_transfer.png` texture through a modern block entity renderer.
- The god rays, table beam, and transmutation particle burst are still visual approximations and may need more tuning against legacy footage.
- No starlight network yet.
- No lens/prism/linking behavior yet.
- No true legacy crystal growth, purity, cutting, or enhancement behavior yet.

## Starlight

`StarlightService` provides a basic ambient starlight calculation.

Current factors:

- Time of day.
- Sky visibility.
- Dimension.
- Position.

This is enough for early block behavior and debug feedback, but it is not yet equivalent to old Astral Sorcery's collector network, relays, lenses, or fosic field behavior.

## Blocks And Rendering

Known model fixes already applied:

- Ore block textures were changed to solid versions so placed ore blocks are not transparent.
- Discovery altar uses the legacy non-full luminous crafting table shape and is not waterloggable. Lightwell uses non-full-block handling.
- Marble pillar and black marble pillar use non-full collision/selection shapes, are waterloggable, and use legacy-style stacked pillar states. A lone pillar or middle segment is a 12x12 column. A bottom segment gets a 4-pixel-tall full-width base flare, and a top segment gets a 4-pixel-tall full-width top flare.
- Rock collector crystals use the translucent render layer so their glass-like shell can blend instead of drawing as a hard cutout.

## Recipes

Basic vanilla-style recipes exist for early shell items such as:

- Marble and black marble variants, matching the old shaped crafting and stonecutting recipe set.
- Black marble raw.
- Parchment.
- Glass lens.
- Tome.
- Resonating wand.

Custom altar recipes are not implemented yet.

## Debug Commands

Available under `/astraldebug` with permission level 2:

```mcfunction
/astraldebug find_rock_crystal [radius]
/astraldebug count_worldgen [radius]
/astraldebug place_rock_crystal
/astraldebug place_shrine small
/astraldebug place_shrine ancient
/astraldebug place_shrine desert
/astraldebug find_collector_crystal [radius]
```

These are development aids and should not be considered final player-facing commands.

## Not Implemented Yet

- Full discovery altar GUI, inventory, recipe serializer, and crafting lifecycle.
- Astral Tome.
- Lightwell liquid production mechanics.
- Starlight network.
- Linking tool behavior.
- Spectral relays.
- Lenses and prisms.
- Constellation discovery and telescope behavior.
- Attunement.
- Rituals.
- Perks.
- Curios wearables.
- JEI/EMI integration for custom Astral recipes.
