# Chapter two — where the work stands

A handoff note, so the quest line can be put down for a while and picked up without re-reading the code.
The plan itself is `docs/chapter-two-last-refuge.md`; this is the state of it. Written 4 November 2026.

## In one line

All eleven pages of *Last Refuge* are written, wired into the West's line as pages 26–36, and have never
been walked in game. Nothing after `2-11` is written, and the finale stays shut behind `FINALE_OPEN = false`
so finishing the chapter does not open a road that is not there yet. `./gradlew compileJava` is green; that
is the only verification any of it has had.

## The pages, as built

The West's line is one continuous list in `Campaign.PAGE_LINES` — pages 1–25 are chapter one, 26–36 are
chapter two — so `pages(Alliance.WEST)` is 36. Standing and courage are what the notes' rewards were scaled
to (a tenth); the purse is paid only where the notes put one.

| # | Page | Kind | Giver → turn in | Standing / courage | Ground |
|---|------|------|-----------------|--------------------|--------|
| 26 | Onward, Misty Mountains | `DIALOGUE` Glóin | Elrond → Glóin | 50 / 3 | Glóin's camp, east end of the High Pass |
| 27 | Troublesome Goblins | `HUNT` ×1 goblin | Glóin → Glóin | 50 / 3 | Ulmug's fires, the Pinnath Fenui |
| 28 | Return to Gundabad I | `LOCATION` | Glóin → Glóin | 50 / 4 | the hold, raised when the page is taken |
| 29 | Return to Gundabad II | `DIALOGUE` Glóin | Glóin → Glóin | 30 / 4 | the camp table |
| 30 | Key-Stone | `LOCATION` | Glóin → Glóin | 60 / 5 | the vault at the source of the Bruinen |
| 31 | Last Refuge I | `DIALOGUE` Gimli | Glóin → Gimli | 40 / 5 | the hold's stair, at the spiral's well |
| 32 | Last Refuge II | `HUNT` ×1 dwarf | Gimli → Glóin | 80 / 6 | the inner keep, below the stair |
| 33 | Steps of Evil I | `LOCATION` | Glóin → Glóin | 50 / 7 | the camps on Carn Dûm's borders |
| 34 | Steps of Evil II | `FETCH` Dark Sigil | Glóin → Glóin | 60 / 7 | the same borders |
| 35 | Evil Tidings | `DIALOGUE` Elrond | Glóin → Elrond | 60 / 8 | Elrond's library, the valley |
| 36 | Fire & Ice | `HUNT` ×1 wight | Elrond → Elrond | 120 / 10 | Carn Dûm |

Only `2-1` carries coin — five silver and thirty-three bronze, as the notes wrote it — and it pays no XP.
Page 36 is the front's last: settling it leaves the line finished rather than pointing at an unwritten page.

## The ground

- **Glóin's camp** — the old world's camp, captured as `key_structures/gloin_camp`, pitched as a site in
  `GLADDEN` and pinned to cell `{24, 10}`, the pass's mouth north of the valley. Glóin is stood at its door.
- **Gundabad** — `key_structures/gundabad`, 122 by 183 by 26, assembled from the six geomorph tiles by
  `tools/gundabad_keep.py`, with the six example structures as the inner keep a storey below. It is a site in
  `HITHAEGLIR` with **no pinned cell**: a building that size has to find its own level ground, so the search
  walks out from the country's middle. It is placed when `2-3` is taken and built as the player comes up to it.
- **The key-stone's vault** and **Carn Dûm** — places, not sites: the drawing already has that ground, so the
  pages walk to a place. `keystone` is canon'd to `TROLLSHAWS`, `carndum` to `ANGMAR`.
- **The two cave tiles** — `ch2_cave_a` and `ch2_cave_b`, 61 square, cut from set 4 by `tools/cave_tile.py`,
  are finished and held. No page places them; there is no cave beat in the chapter yet.

## The bodies

`Glóin` and `Gimli` are keepers like the Elves: a tag, a name, a skin, and a body kept at one stand point so
a reload does not stack them. Glóin wears `gloin.png` on a human model scaled to a dwarf's height; Gimli is
stood from the hold's own site beside the stair. **Ulmug**, **Dugorim** and **Drugoth** are quarries counted
in prose — a goblin, a dwarf, a wight — because the pages name the fires and the room rather than tagging one
body each. The Dragon Wight is the pack's barrow wight, named in the last page's words; the chapter needs no
creature it does not have.

## Open decisions — deliberately not made

1. **The inner keep is not gated.** The spiral's stair is open, so a player can walk down before the plans
   say they can. It wants a door that the plans open (page 29 is already the beat that would open it).
2. **The Pinnath Fenui is a bearing, not ground.** The notes put Ulmug's fires north-east of the camp; the
   page carries the direction and the prose carries the name. Naming it a place is still open.
3. **The chapter-one seam is not staged.** *Hasten Their Departure* is a walk back to the library; if the
   company is ever shown leaving, it belongs between pages 25 and 26.
4. **Chinese is deferred** for the lines the rework changed, as with chapter one — the last pass, not a
   running cost.

## Loose end worth checking before the next chapter

`Places` canon's the place `gloin` to `HITHAEGLIR` while the camp's *site* is in `GLADDEN`; the changelog
says the camp was moved to Gladden. Nothing uses `placeTarget("gloin")` today — `2-1` targets the person and
the site raises the camp — so it is harmless now, but a future page that points at the place would point into
the mountains rather than at the camp.

## What is not built

- **No chapter three onwards.** `FRONTS` holds `Alliance.WEST` alone: the East and the Harad keep their halls
  and heralds but hand nothing, and the West is the only line with pages.
- **The finale.** `FINALE_OPEN = false`; the Rider → castle → arena → Witch-king road opens only when every
  front in `FRONTS` is settled, which with one front means it stays closed by design.
- **The notes' job boards (quest 37).** Not started; `Contract` covers herald work already.

## Picking it up again

1. Walk 26 → 36 in order in a fresh world and watch four things: that the camp reads as a camp; that
   Gundabad's site search finds ground level enough for a 122-by-183 footprint in the Hithaeglir; that the
   stair reads as the way down; and that Carn Dûm's border camps are findable from the page's direction.
2. Then the two real decisions — gating the stair behind the plans, and naming the Pinnath Fenui.
3. Only after that is worth spending: the cave tiles, the seam scene, and Chinese.

## Where things live

| Thing | File |
|-------|------|
| The pages, givers, targets, rewards | `faction/Campaign.java` (`PAGE_LINES`, `Page`) |
| Sites and their cells | `faction/Sites.java` (`GUNDABAD`, `GLOIN_CAMP`) |
| Places and their countries | `faction/Places.java` (`gloin`, `gundabad`, `vault`, `keystone`, `carndum`) |
| The hold's assembly | `tools/gundabad_keep.py`, `key_structures/gundabad.nbt` |
| The plan and its reasoning | `docs/chapter-two-last-refuge.md` |
| Page text | `assets/tolkienmobs/lang/en_us.json` under `campaign.tolkienmobs.page.*` |
| Commands for poking at it | `docs/commands.md` (`/tmquest`) |
