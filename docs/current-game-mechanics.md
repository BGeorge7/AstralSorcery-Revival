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

## Shrine Behavior

Legacy NBT templates are used for the three early shrine types.

Structure block data markers are handled as follows:

- `crystal`: places a rock collector crystal block entity.
- `shrine_chest`: randomly places a shrine loot chest or air.
- `brick_shrine_chest`: randomly places a shrine loot chest or marble bricks.
- `random_top_block`: currently places grass block most of the time or air.

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

Known limitations:

- Beam visuals use the old `lightbeam.png` texture through a modern block entity renderer.
- The god rays and table beam are first-pass approximations and still need tuning against legacy footage.
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
- Discovery altar and lightwell use non-full-block handling.
- Marble pillar and black marble pillar use `noOcclusion()` because their model is a 12x12 column inside a full block.
- Rock collector crystals use the translucent render layer so their glass-like shell can blend instead of drawing as a hard cutout.

## Recipes

Basic vanilla-style recipes exist for early shell items such as:

- Marble bricks.
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
