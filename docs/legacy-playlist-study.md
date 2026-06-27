# Legacy Playlist Study

This document records the YouTube tutorial playlist being used as gameplay reference material for the Astral Sorcery Revival port.

The raw captions are not checked into the repository. Keep this file as the durable source map and keep `legacy-gameplay-mechanics-bible.md` as the summarized mechanics reference.

## Source

- Playlist: `How to: Astral Sorcery`
- Channel: `Mondays`
- Playlist URL: `https://www.youtube.com/playlist?list=PL0JWzZ1sLTB8JkztGqxYBXSivYYG0zmJc`
- Captions extracted: 2026-06-26
- Extraction tool: `yt-dlp 2026.06.09`
- Caption files were temporarily stored outside the repo at `%TEMP%\astral_sorcery_playlist_captions`.

## Videos

| # | Video | ID | Duration | Main Coverage |
|---|---|---|---:|---|
| 1 | How to: Astral Sorcery \| The Basics (Minecraft 1.16) | `lWqcR0cKtDI` | 39:15 | Discovery, shrines, tome, resonating wand, luminous crafting table, lightwell, liquid starlight, relays, early tools, altar upgrade |
| 2 | How to: Astral Sorcery \| Attunement Part 1 (Minecraft 1.16.5) | `PgYtjLEvipM` | 23:06 | Linking tool, starlight links, lenses, starmetal, cutting tool, crystal splitting, crystal tools, dimensional sky rules |
| 3 | How to: Astral Sorcery \| Constellation Part 1 (Minecraft 1.16.5) | `UtWyf3AdI_E` | 24:58 | Starlight infuser, crystal tool abilities, prisms, colored lenses, celestial crystals |
| 4 | How to: Astral Sorcery \| Attunement Part 2 (Attunement Altar) | `imYeV2YwvB4` | 37:51 | Attunement altar, relay point matching, player attunement, perk tree, gems, dim and faint constellation discovery |
| 5 | I Did All The Rituals In Astral Sorcery So You Don't Have Too! | `FAWAI1NpJ64` | 37:17 | Ritual pedestal, ritual effects, ritual range visualization, ritual boosting, faint/corrupted constellation rituals |
| 6 | How to: Astral Sorcery \| Mantle of the Stars (Minecraft 1.16.5) | `rmwoC4kziUw` | 21:28 | Mantle crafting, constellation mantle variants, Vicio flight perk interaction, repair |
| 7 | How to: Astral Sorcery \| Stella Refraction Table (Minecraft 1.16.5) | `74IkLIZtpfo` | 15:55 | Stellar refraction table, infused/etched glass, constellation-driven enchantments |
| 8 | How to: Astral Sorcery \| Wands & Radiance (Minecraft 1.16.5) | `c9Vp4Lr4amk` | 22:10 | Wands, traversal movement, chalice, ever-shifting fountain, irradiant stars, radiance endgame |

## Refresh Workflow

Use this only for local research. Do not commit raw subtitle files unless there is a deliberate reason.

```powershell
python -m pip install --user yt-dlp youtube-transcript-api

$tmp = Join-Path $env:TEMP 'astral_sorcery_playlist_captions'
New-Item -ItemType Directory -Force -Path $tmp | Out-Null

python -m yt_dlp `
  --skip-download `
  --write-auto-subs `
  --write-subs `
  --sub-lang "en.*" `
  --sub-format vtt `
  --output "$tmp\%(playlist_index)02d-%(id)s.%(ext)s" `
  "https://www.youtube.com/playlist?list=PL0JWzZ1sLTB8JkztGqxYBXSivYYG0zmJc"
```

## Study Caveats

- Captions are useful for progression, item names, requirements, and player-facing behavior.
- Captions do not reliably capture visual timing, particle shape, GUI layout, or exact block placement. Use the old 1.16.5 client, screenshots, clips, and legacy source for those.
- The playlist is tutorial-oriented, not a formal spec. Treat it as strong guidance and verify ambiguous mechanics against `legacy-1.16.5`.
- Some tutorial statements may be pack/version specific or based on the presenter's testing. Mark uncertain behavior in implementation notes until confirmed in code.

## How To Use This Source

For each ported mechanic:

1. Read the relevant section in `legacy-gameplay-mechanics-bible.md`.
2. Check the legacy code/data under `legacy-1.16.5`.
3. Use the user's old 1.16.5 instance or a short clip for visuals.
4. Implement the smallest faithful gameplay slice.
5. Update `current-game-mechanics.md` with what the 1.21.1 port actually does.
6. Keep unknowns in the "Open Questions" section of the mechanics bible until resolved.
