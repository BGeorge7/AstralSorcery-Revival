# AI Porting Notes

This document is a handoff file for future AI sessions working on Astral Sorcery Revival. Keep it current when the porting strategy, workflow, or important discoveries change.

## Project Direction

- Target Minecraft version: `1.21.1`.
- Mod loader: NeoForge, currently `21.1.233`.
- Java target: 21.
- Main mod id: `astralsorcery`.
- Legacy source reference: `legacy-1.16.5`.
- Current active branch: `codex/neoforge-1.21.1-dev-env`.

The port should be faithful to old Astral Sorcery from the player perspective, but modernized internally. Do not mechanically wire the whole old source tree into the build.

## Workflow Notes

- The user may keep an old Minecraft instance open with the original mod installed as a behavior reference.
- Do not mass-kill `java`, `javaw`, or all Minecraft processes.
- If the NeoForge dev client must be restarted, prefer closing only the dev client window manually or identify the exact dev process first.
- After code changes are done and validation passes, run the NeoForge dev client so the user can inspect the result in-game.
- The user often keeps a separate Minecraft `1.16.5` instance open with the original Astral Sorcery mod as a reference. Do not close, kill, or restart that instance.
- `runClient` is configured to mute the dev client by updating `run/options.txt` before launch.
- Use `.\gradlew.bat --no-daemon build` as the baseline validation command.
- The user uses VS Code and the Codex extension.

## Dependency Decisions

- The port has pivoted from Forge to NeoForge.
- Curios is present as an optional development/runtime dependency and should stay isolated until wearable items are implemented.
- JEI is present for development/runtime convenience.
- ObserverLib should not be revived as a separate dependency right now.
- Replace ObserverLib behavior with small internal systems as needed.

See `docs/dependency-strategy.md` for more detail.

## Legacy Gameplay Study References

- `docs/legacy-playlist-study.md` records the YouTube playlist used as tutorial source material and how captions were extracted locally.
- `docs/legacy-gameplay-mechanics-bible.md` summarizes legacy player-facing mechanics by progression phase.
- `docs/starlight-visual-effects.md` documents the reusable god ray, directed beam, sparkle, and render type helpers. Use it before adding new Astral light effects.
- These docs describe intended/legacy behavior. `docs/current-game-mechanics.md` remains the source of truth for what the NeoForge port currently implements.

## Important 1.21.1 Migration Findings

- Structure template NBT files belong under `data/<namespace>/structure`, singular. The old `structures` folder caused template lookup failures.
- Registered structures use:
  - `data/<namespace>/worldgen/structure/*.json`
  - `data/<namespace>/worldgen/structure_set/*.json`
  - biome tags such as `data/<namespace>/tags/worldgen/biome/has_structure/<name>.json`
- Shrine structure JSONs can include `y_offset`. This mirrors the old template structure offsets; current values are ancient `-7`, desert `-11`, small `0`.
- Shrine structure JSONs can also include flatness controls:
  - `max_terrain_delta`: maximum allowed sampled height range across the template footprint. `-1` disables this check.
  - `max_surrounding_terrain_delta`: maximum allowed sampled height range across the template footprint plus surrounding margin. `-1` disables this check.
  - `max_surface_floor_delta`: maximum allowed sampled difference between the surface heightmap and ocean-floor heightmap. This is used to reject lake/ocean candidates while allowing thin snow layers.
  - `surrounding_terrain_margin`: extra horizontal blocks sampled around the template footprint.
  - `terrain_smoothing_margin`: outside-footprint margin that may be lightly blended after placement.
  - `terrain_smoothing_fill_limit`: maximum number of blocks a smoothing column may fill.
  - `terrain_smoothing_cut_limit`: maximum number of blocks a smoothing column may cut.
  - `terrain_smoothing_y_offset`: vertical offset from the sampled surface anchor used by the smoothing pass. Ancient shrine currently uses `-1` so surrounding infill stops below the outer base layer.
  - `terrain_support_fill_limit`: maximum number of blocks that may be filled below the structure footprint to prevent floating corners.
  - `terrain_support_y_offset`: vertical offset from the sampled surface anchor used by the under-footprint support pass.
  - `placement_attempts`: number of random positions to try in the candidate structure chunk.
  - `terrain_sample_step`: horizontal sample spacing in blocks.
- NeoForge biome modifiers are still used for feature worldgen such as ores, but shrines are now registered structures so `/locate structure ...` works.
- Custom models that do not fill the full block need matching block registration behavior such as `noOcclusion()`. This fixed missing terrain/see-through rendering for discovery altar/well style blocks and marble pillars.
- Marble and black marble pillars use a custom waterloggable pillar block. Their legacy `pillartype` state is `middle`, `bottom`, or `top`: stacked bottom and top pieces flare to full width for 4 pixels, while middle/lone pieces remain 12x12 columns.
- The legacy marble and black marble shaped crafting and stonecutting recipe set has been ported under `data/astralsorcery/recipe/shaped/...` and `data/astralsorcery/recipe/stonecutting/...`. Keep future marble recipe edits aligned with `legacy-1.16.5`'s `VanillaTypedRecipeProvider`.
- Rock crystal ore needed solid ore textures for block models; item icons can look correct even when placed block models are wrong.
- Minecraft 1.21.1 has deepslate, so rock crystal ore now has a deepslate variant.

## Legacy Astral Sorcery Findings

- Old shrine templates used structure block data markers:
  - `crystal`: place a worldgen rock collector crystal.
  - `shrine_chest`: 50 percent chest with shrine loot, otherwise air.
  - `brick_shrine_chest`: 50 percent chest with shrine loot, otherwise marble bricks.
  - `random_top_block`: old code used the biome surface top state. The port maps desert to sand, badlands variants to red sand, and currently falls back to grass for other biomes because modern surface rules do not expose the old surface-builder config directly.
- Legacy worldgen shrine collector crystals used fixed properties equivalent to size 2, shape 2, purity 2, collection rate 2.
- Old shrine crystals were protected study tools, not intended to be harvested casually.
- Starmetal ore was not natural worldgen in old Astral Sorcery; it came from starlight/crystal transmutation mechanics.
- Crystal attribute NBT in legacy uses `crystalProperties.attributes[]` with entries like `{property:"astralsorcery:size", pLevel:2, discovered:true}`. Collector block entities also used keys such as `constellationType`, `constellationTrait`, `collectorType`, and optional `playerUUID`.
- The old crafting table to luminous crafting table behavior was a block transmutation recipe from `minecraft:crafting_table` to `astralsorcery:altar_discovery` with a starlight requirement of `60`.
- Legacy collector crystals only produce starlight when they can see the sky. Nighttime, constellation/distribution state, and crystal attributes affect how quickly starlight is produced.

## Current Implementation Shape

- `ASRegistries` wires blocks, items, fluids, sounds, data components, block entities, menus, recipe registries, feature registries, structure registries, and creative tabs.
- `ASStructureRegistries` registers the custom shrine structure type and piece type.
- `AstralShrineStructure`, `AstralShrinePiece`, and `AstralShrinePieces` implement registered shrine placement and legacy marker handling.
- `AstralShrineFeature` still exists mostly for manual debug placement compatibility. Natural shrine generation should use registered structures, not feature biome modifiers.
- `CollectorCrystalBlockEntity` stores legacy-shaped crystal attributes, a random major constellation label, player-made state, sky visibility, and last sampled ambient starlight.
- Non-player-made collector crystals scan for nearby crafting tables and slowly transmute them into discovery altars when the crystal is producing starlight.
- Collector crystal visuals now use reusable block entity render helpers for sky god rays, ambient motes, table transmutation beams, beam sparkles, and target-side transmutation motes. The ambient/target motes use the legacy `particle_small.png`; its octagonal alpha silhouette is baked into the asset and accepted for now.
- `StarlightService` currently provides basic ambient starlight from time of day, sky visibility, dimension, and position. It is not yet the full old starlight network.

## Useful Debug Commands

- `/astraldebug find_rock_crystal [radius]`
- `/astraldebug count_worldgen [radius]`
- `/astraldebug place_rock_crystal`
- `/astraldebug place_shrine small`
- `/astraldebug place_shrine ancient`
- `/astraldebug place_shrine desert`
- `/astraldebug find_collector_crystal [radius]`

Registered structure locate commands:

```mcfunction
/locate structure astralsorcery:ancient_shrine
/locate structure astralsorcery:desert_shrine
/locate structure astralsorcery:small_shrine
```

## Near-Term Risks

- Structure placement is a first-pass implementation. Verify generated shrines across flat terrain, slopes, water, trees, and chunk reloads.
- `random_top_block` marker behavior is simplified and may need biome-aware top material handling.
- Collector crystal visuals and gameplay are incomplete. It currently reports ambient starlight but does not participate in a real starlight network.
- Discovery altar block entity exists, but altar GUI/custom recipe/progression mechanics are not yet complete.
- Many late-game systems are still placeholders or registry shells.
