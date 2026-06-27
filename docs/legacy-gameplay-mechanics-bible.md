# Legacy Gameplay Mechanics Bible

This is a working summary of player-facing Astral Sorcery 1.16 gameplay, based on the Mondays tutorial playlist, the local `legacy-1.16.5` source tree, and current port observations.

It is not a statement of what the NeoForge port already implements. For current behavior, see `current-game-mechanics.md`.

## Design Read

Astral Sorcery is a staged magic mod about discovering and focusing starlight. It is not just a block-and-item progression. The feel comes from:

- finding ancient structures and papers before understanding the sky
- using sky exposure, night, altitude, and constellations as crafting constraints
- turning ordinary blocks into magical devices through starlight
- building multiblocks around altars, relays, rituals, and infusers
- making crystals personal by growing, cutting, combining, and attuning them
- visual feedback through beams, particles, sky overlays, constellation tracing, and the Astral Tome

## High-Level Progression

1. **Discovery**
   - Find Astral structures.
   - Collect constellation papers and marble.
   - Craft or obtain the Astral Tome and resonating wand.
   - Use a natural collector crystal to transform a crafting table into the luminous crafting table/discovery altar.
   - Find rock crystals and begin basic starlight crafting.

2. **Exploration**
   - Use the luminous crafting table to craft early devices.
   - Make a lightwell and liquid starlight.
   - Use relays to boost starlight crafting.
   - Start crystal growth and early crystal tools.
   - Upgrade the altar into the starlight crafting altar.

3. **Attunement**
   - Use the linking tool, lenses, and collector crystals to move starlight.
   - Transmute iron ore into starmetal ore.
   - Build and use the attunement altar.
   - Attune the player and crystals to bright constellations.
   - Unlock the perk tree.

4. **Constellation**
   - Use the starlight infuser.
   - Work with prisms, colored lenses, celestial crystals, and stronger crystal properties.
   - Discover dim and later faint constellations.

5. **Radiance / Late Game**
   - Use rituals, mantles, refraction table, advanced wands, chalice, fountain, and irradiant stars.
   - Faint constellation systems and perk manipulation become important.

## Discovery And Basics

### Structures

Legacy progression begins by finding Astral Sorcery structures, especially ancient shrines and desert-type shrines.

Player-facing behavior:

- Structures contain marble and/or chests with constellation papers.
- Large ancient shrines can contain a collector crystal.
- Shrine collector crystals are environmental progression objects, not just decorative blocks.
- The player can use the shrine crystal to transform a vanilla crafting table.

Implementation notes:

- Current port already has ancient, desert, and small shrine registered structures.
- Marker handling should stay faithful:
  - `crystal` creates a worldgen collector crystal.
  - shrine chest markers create loot chests.
- Structure generation should eventually preserve the "find a magical ruin" feeling, not only satisfy `/locate`.

### Constellation Papers And Tome

Player-facing behavior:

- Early constellation papers are initially found in shrine chests.
- Picking up a paper teaches/memorizes that constellation.
- The Astral Tome can store or display collected constellation knowledge.
- Early game exposes five bright constellations.
- Additional constellation tiers are gated by later progression.

Implementation notes:

- The current port has paper and tome items but no true knowledge system.
- Data components are the likely modern place for item state, but player knowledge should probably be saved on player attachments/data.
- The Tome is deferred, but its progression role is central. Temporary advancements/dev docs should not erase the need for a real guide later.

### Resonating Wand

Player-facing behavior:

- The resonating wand is one of the first required crafted items.
- It is used to activate altar crafts.
- It also helps locate rock crystals at night by revealing/shimmering starlight spots on the ground.

Implementation notes:

- Current port has the item and recipes, but not all behaviors.
- The "find shiny spots at night, dig down for rock crystal ore" loop is important and should be restored before discovery feels complete.

### Crafting Table To Luminous Crafting Table

Player-facing behavior:

- Place a vanilla crafting table near a natural collector crystal.
- The collector crystal must have direct sky access.
- The table transforms into the luminous crafting table/discovery altar.
- Opening sky access can make the transformation happen quickly.
- The effect should be obvious: crystal activity changes, a beam targets the table, and particles gather around the conversion.

Implementation notes:

- Legacy data defines this as a block transmutation recipe from `minecraft:crafting_table` to `astralsorcery:altar_discovery` with starlight cost `60`.
- Current port has a first-pass block entity conversion.
- Keep the directed beam visible while the crystal has sky access, but only progress conversion when the crystal is producing starlight.
- Visuals are still being tuned against the old game.

Test checklist:

- Table near crystal, sky visible, night: converts.
- Table near crystal, sky blocked: beam/god rays off and no progress.
- Table near crystal, day: targeting can be visible, but progress should be zero or greatly reduced depending confirmed legacy behavior.
- Table out of line of sight: no conversion.
- Multiple tables: pick deterministic nearest/valid target or match legacy priority.

### Rock Crystals

Player-facing behavior:

- Rock crystal ore is found underground, with the resonating wand helping identify areas.
- Rock crystals have properties such as Size, Purity, Shape, and more.
- Some properties are hidden until the player has enough progression.
- Different crystals found in the world have different stats.
- Crystal properties matter for collector crystals, tools, rituals, lenses, and later systems.

Implementation notes:

- Current port stores legacy-shaped crystal attributes under `crystalProperties.attributes`.
- Known attribute language from legacy/current notes:
  - Size
  - Purity
  - Shape
  - Collector Rate
  - Focus/Constellation
  - Ritual Effect
  - Ritual Range
  - Lens Transfer
  - Lens Amount
  - Lens Effect
  - Tool Durability
  - Tool Efficiency
- Attribute caps differ by crystal type:
  - Rock crystals support fewer total tiers.
  - Celestial crystals support more total tiers.
- Use legacy code as authority before finalizing caps and generation.

## Exploration

### Luminous Crafting Table / Discovery Altar

Player-facing behavior:

- The table stores or receives starlight.
- Starlight depends on sky access, night, altitude, and local starlight strength.
- Recipes are placed into the table, then activated with the resonating wand.
- Crafting should have a visible lifecycle: particles, beams, sound, and completion animation.

Implementation notes:

- Current port has the block shell but not the full block entity, GUI, custom recipe type, or crafting lifecycle.
- The table should be the first real custom recipe system after the current collector crystal slice.

### Lightwell And Liquid Starlight

Player-facing behavior:

- A lightwell converts aquamarine or rock crystal into liquid starlight.
- Sky access matters.
- Liquid starlight can be bucketed or piped from the bottom.
- Standing in liquid starlight grants night vision.
- Liquid starlight has world interactions:
  - with water, it can create ice/packed ice behavior
  - with lava, it can create sand-like results
  - logs thrown into liquid starlight become infused wood

Implementation notes:

- Current port has placeholder fluid/bucket and lightwell block shell.
- Need implement tank/fluid handling through NeoForge fluid capabilities.
- Need verify exact fluid interaction outcomes in legacy code before implementing.

### Spectral Relays

Player-facing behavior:

- Spectral relays are small multiblocks that use glass lenses.
- They collect/redirect starlight to nearby luminous crafting tables.
- They are useful when the table does not have enough starlight for recipes.
- Relays have spacing rules; relays too close together reduce effectiveness.

Implementation notes:

- Requires the multiblock matcher and starlight availability system.
- Early version can directly scan blocks on place/tick before adding cached observers.

### Crystal Growth

Player-facing behavior:

- Rock crystals submerged in liquid starlight can grow/increase size over time.
- Max-size crystals can split, producing smaller crystals with possible purity changes.
- Growth is stochastic and slow enough to feel like a magical process, not instant crafting.

Implementation notes:

- Needs item entity monitoring around liquid starlight or fluid tick logic.
- Crystal property mutation should use data components in 1.21.1.

### Early Crystal Tools

Player-facing behavior:

- Crystal tools derive behavior from crystal properties.
- Durability and efficiency are early obvious properties.
- Shape and purity matter more later but should still be preserved early.

Implementation notes:

- Do not flatten crystal tools into ordinary static items.
- Tool construction should carry source crystal data forward.

## Linking, Lenses, And Starmetal

### Linking Tool

Player-facing behavior:

- Starlight links are source-first.
- Select a source collector crystal, then select the target.
- The player must deselect/reselect when changing source or target context.
- Linking mistakes are easy, so visual line feedback matters.

Implementation notes:

- Build this as the first player-facing starlight network interaction.
- Store links in `AstralSavedData` or a dedicated network saved-data object.
- Sync active links to clients for beam rendering.

Test checklist:

- Source to lens.
- Source to ore/transmutation target.
- Deselect/reselect behavior.
- Invalid source.
- Invalid target.
- Chunk unload/reload persistence.

### Starmetal

Player-facing behavior:

- Starmetal ore is not a normal generated ore in old Astral Sorcery.
- It is created by linking starlight from a collector crystal to iron ore.
- Starmetal then becomes ingots/dust and unlocks many midgame systems.

Implementation notes:

- Current port has starmetal placeholder content.
- Implement as a block transmutation recipe/system, not worldgen.

### Lenses And Prisms

Player-facing behavior:

- Lenses transmit or modify starlight beams.
- Crystal prisms split incoming starlight to multiple outgoing directions.
- Splitting decreases intensity per outgoing link.
- Colored lenses apply effects such as growth, damage, regeneration, breaking, and other utility behaviors.
- Line of sight matters; a lens can block or intercept another beam.

Implementation notes:

- This should sit on top of the starlight network once basic linking works.
- Start with uncolored lens transfer, then prism splitting, then colored effects.

## Attunement

### Attunement Altar

Player-facing behavior:

- The attunement altar is a multiblock/altar system used to attune players and crystals.
- The target bright constellation must be visible/in the sky.
- Holding a discovered constellation paper shows blue/purple point particles if the constellation is currently available.
- Astral relays are placed on the ground to match constellation points.
- Completing the relay pattern and activating the altar attunes the player or crystal.

Implementation notes:

- Needs:
  - constellation definitions
  - visibility rules by night/time
  - pattern placement/validation
  - player attunement saved data
  - crystal attunement data components
  - client particle guide

Test checklist:

- Correct constellation in sky.
- Correct relay pattern.
- Wrong relay placement.
- Missing relay.
- Daytime/no constellation.
- Player attunement.
- Crystal attunement.

### Perk Tree

Player-facing behavior:

- Player attunement unlocks the perk tree.
- Perks are arranged as nodes/paths.
- Leveling unlocks additional perk points.
- Major perks and gem sockets create meaningful build choices.
- Vicio mantle can interact with a specific perk to grant flight behavior.

Implementation notes:

- This is late enough to defer, but the data model should not block it.
- Player attachments/data should track attunement, level, unlocked nodes, and sockets.

### Gems

Player-facing behavior:

- Gems are grown similarly to celestial crystals.
- Rock crystal plus illumination powder in liquid starlight creates a gem cluster.
- Gems can be socketed into perk tree sockets.

Implementation notes:

- Shares infrastructure with item-in-fluid transformations and crystal cluster growth.

### Dim And Faint Constellations

Player-facing behavior:

- Bright constellations are discovered early.
- After attunement, dim constellations become discoverable from papers/sky tools.
- Faint constellations require later tools such as the observatory.
- Looking Glass/Telescope/Observatory let the player trace constellations by connecting stars.

Implementation notes:

- Need define constellation tiers:
  - Bright: early/player attunement roots.
  - Dim: later utility/ritual/tool systems.
  - Faint: late/radiance/corrupted ritual systems.
- Known dim constellation themes from tutorial:
  - Horologium: time
  - Lucerna: light
  - Bootes: herding
  - Fornax: heat
  - Pelotrio: convocation
  - Mineralis: minerals/ores
  - Octans: ocean/water

## Constellation And Infusion

### Starlight Infuser

Player-facing behavior:

- The starlight infuser is a multiblock crafting system.
- It uses liquid starlight around/within the structure.
- It unlocks stronger materials and late-midgame crafting.

Implementation notes:

- Requires multiblock validation and fluid consumption/rendering.
- Defer until lightwell/liquid starlight and altar crafting are stable.

### Celestial Crystals

Player-facing behavior:

- Throwing a rock crystal and stardust into liquid starlight starts celestial crystal cluster growth.
- The cluster grows over time.
- Night can accelerate growth.
- Mature clusters are harvested into celestial crystals.
- Celestial crystals have better attribute capacity than rock crystals.

Implementation notes:

- Item entity combination plus placed cluster block lifecycle.
- Needs persistence, random growth, and particle feedback.

## Rituals

### Ritual Pedestal

Player-facing behavior:

- A ritual pedestal uses an attuned crystal to project constellation effects into an area.
- The domic resonator can visualize the ritual area.
- Rituals act over time, not as instant spells.
- Effects depend on the attuned constellation.
- Higher placement/sky exposure can improve rituals.
- Collector crystals and linked lenses can boost ritual range/effect.

Known ritual themes from tutorial notes:

- Aevitas: plant/life growth style effects.
- Mineralis: changes stone into ores/minerals over time.
- Octans: water/ocean/fishing style effects.
- Horologium: time manipulation/freeze effects.
- Lucerna: light/illumination style effects.
- Fornax: heat/fire/smelting style effects.
- Bootes: herding/animal-related effects.
- Bright constellations provide their own combat/protection/movement/destruction/life themes.

Implementation notes:

- Rituals should be data-driven where possible, but do not invent a generic engine too early.
- Start with one simple ritual after attunement works, probably a harmless one like Aevitas or Lucerna.

### Faint / Corrupted Rituals

Player-facing behavior:

- Later systems unlock faint constellations and corrupted ritual variants.
- These can be stronger, stranger, or more dangerous.
- Tutorial examples include corrupted Mineralis placing mineral blocks/ores and corrupted Horologium freezing mobs/machines.

Implementation notes:

- Defer until normal rituals and faint constellation discovery are implemented.

## Mantle Of The Stars

Player-facing behavior:

- The Mantle of the Stars is a late-game wearable.
- It has constellation variants with distinct powers.
- Vicio can interact with the perk tree to replace/upgrade movement with flight.
- Mantles can be repaired with stardust in an anvil.

Implementation notes:

- This is where Curios integration becomes important.
- Keep Curios behind an adapter until wearable items are being implemented.
- Mantle visual/equipment behavior should be server-safe and optional-dependency aware.

## Stellar Refraction Table

Player-facing behavior:

- The stellar refraction table uses constellation combinations to create enchanted books/effects.
- Infused glass/etched glass has limited durability/uses.
- Each use can produce different enchantment outcomes.
- Constellations influence possible enchantments.
- Some Astral-specific enchantments can appear, such as Scorching Heat.

Implementation notes:

- Likely late because it depends on constellation discovery, liquid starlight, infused glass, and custom UI.
- Needs careful JEI/EMI integration later.

## Wands And Radiance

### Wands

Player-facing behavior:

- Later wands provide utility and movement.
- Traversal wand has blink and dash style modes.
- Grapple/movement style behavior has charge and range limits.

Implementation notes:

- These are player interaction/movement systems and need careful client/server sync.

### Chalice And Ever-Shifting Fountain

Player-facing behavior:

- Chalices store large amounts of fluid, with tutorial notes describing up to 64 buckets.
- The ever-shifting fountain uses liquid starlight and chalices to generate or move fluids.
- Fluid type/behavior can be manipulated with position/setup.

Implementation notes:

- Defer until the fluid system is robust.

### Irradiant Stars

Player-facing behavior:

- Irradiant stars reset perk allocations while keeping constellation attunement/level context.
- Recipes vary by constellation.

Implementation notes:

- Depends on full player attunement/perk model.

## Visual And UX Requirements

Important player-facing visual systems:

- Shrine collector crystal idle particles.
- Stronger god rays when the crystal has sky exposure.
- Directed collector-to-table beam during table conversion.
- Transmutation particles at the target block.
- Altar crafting beams/sounds/particles.
- Starlight link beams between crystals/lenses/prisms.
- Constellation sky overlays/tracing UI.
- Attunement altar ground point particles.
- Ritual area visualization.
- Fluid rendering and lightwell liquid level.
- Tome page navigation and progression unlock effects.

Implementation rule:

Do not treat visuals as optional polish for Astral Sorcery. In this mod, visuals are gameplay feedback.

Reusable renderer note:

- Current reusable starlight render helpers are documented in `starlight-visual-effects.md`.
- Use those helpers for collector-crystal god rays, ambient motes, directed starlight beams, beam motes, and target-side transmutation motes before creating new one-off render code.
- The current ambient mote implementation keeps the old `particle_small.png` texture. Its octagonal alpha edge is a known asset limitation, softened through layered/rotated sprites and accepted for now.

## Near-Term Port Priority From This Study

Recommended next implementation order:

1. Finish collector crystal table conversion visuals and behavior.
2. Implement real discovery altar/luminous crafting table block entity, inventory, GUI, and recipe type.
3. Implement resonating wand activation of altar recipes.
4. Implement lightwell mechanics and liquid starlight production.
5. Implement basic liquid starlight item-in-fluid transformations.
6. Implement spectral relay multiblock and starlight boost to altar.
7. Implement linking tool and minimal starlight network.
8. Implement starmetal ore transmutation from iron ore.
9. Implement lens transfer and later prism splitting.
10. Begin constellation discovery/looking glass once early crafting loop is playable.

## Open Questions To Verify

- Exact day/night and sky-exposure rules for crafting table conversion speed.
- Exact distance and line-of-sight rules for collector crystal table conversion.
- Exact starlight values from altitude, time, local distribution, collector properties, and constellation state.
- Exact crystal attribute caps and generation ranges in 1.16.5.
- Exact lightwell production rates and item durability/consumption behavior.
- Exact liquid starlight fluid interactions with water/lava/blocks.
- Exact spectral relay spacing and diminishing return formula.
- Exact lens/prism transfer loss formula.
- Exact ritual effect formulas and valid target selection.
- Exact mantle powers and Curios slot behavior.

When resolving one of these, update this file and `current-game-mechanics.md` if the port implementation changes.
