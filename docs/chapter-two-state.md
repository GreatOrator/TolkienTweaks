# Chapter two — where the work stands

A handoff note, so the quest line can be put down for a while and picked up without re-reading the code.
The plan itself is `docs/chapter-two-last-refuge.md`; this is the state of it. Written 4 November 2026.

## In one line

All eleven pages of *Last Refuge* are written and wired into the West's line as pages 26–36, and the line has now
been **walked in game through page 32 (2-7)** - the Gundabad quests and the captain's hunt work as written. Four pages
are left to walk (33 to 36), and nothing after `2-11` is written; the finale stays shut behind `FINALE_OPEN = false` so
finishing the chapter does not open a road that is not there yet.

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
| 31 | Last Refuge I | `DIALOGUE` Gimli | Glóin → Gimli | 40 / 5 | Gimli's cave, east of the gate, on its dais |
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

``Glóin` and `Gimli` are keepers like the Elves: a tag, a name, a skin, and a body kept at one stand point so a
reload does not stack them. Glóin wears `gloin.png` on a human model scaled to a dwarf's height; Gimli stands in
**his own cave east of the gate**, on the room's own dais at `GundabadCave.post()` - the page that sends the player
below the hold is met under the mountain rather than beside the well. **Ulmug**, **Dugorim** and **Drugoth** are quarries counted
in prose — a goblin, a dwarf, a wight — because the pages name the fires and the room rather than tagging one
body each. The Dragon Wight is the pack's barrow wight, named in the last page's words; the chapter needs no
creature it does not have.

## Open decisions — deliberately not made

1. **The inner keep's stair is final, and it is open on purpose** (15 November 2026). The spiral at the hall's
   well has no door over it: a player can walk down into the keep below Gundabad whenever they find it, and that
   is the answer rather than an omission - the hold is a place to explore, not a lock, and nothing in the pages
   needs the descent to be held back. Page 29 was the beat a door would have paid for; it stays dialogue.
2. **The Pinnath Fenui is a bearing, not ground.** The notes put Ulmug's fires north-east of the camp; the
   page carries the direction and the prose carries the name. **Answered 12 November 2026:** it is a place
   now. `Places` already listed `goblins` and `GoblinCave.PIN` is that same string, so `Places.ensure` reads
   the cave's own pin rather than surveying - the name cannot drift from the cut. `Campaign.tick` names it
   beside `GoblinCave.raised`, so the name is written the first time a player is in Arda and the banner reads
   *The Goblin Fires* / *Ulmug's fires in the caves of the Pinnath Fenui* on the way down to the mouth, forty-
   eight blocks round the pin.
3. **The chapter-one seam is final (15 November 2026).** The scene between pages 25 and 26 was played in game
   and ran smoothly as staged - courtyard, company, Elrond's blessing, the walk out - so it needs nothing more.
   `docs/departure-scene.md` and the seam's own note below are records rather than tasks.
4. **Chinese is on hold until a full public release.** The rework's lines are translated when the mod is ready
   to go public rather than line by line, so nothing is owed while the chapters are still being walked.

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

1. Walk what is left in order in a fresh world - and not before the Carn Dûm ground is worked - watching four things: that the camp reads as a camp; that
   Gundabad's site search finds ground level enough for a 122-by-183 footprint in the Hithaeglir; that the
   stair reads as the way down; and that Carn Dûm's border camps are findable from the page's direction.
2. Nothing is left to decide at the hold: its stair is final and open, and the Pinnath Fenui stopped being a
   decision on 12 November 2026 - it is a place, named from the cave's own pin.
3. Only after that is worth spending: the cave tiles. The seam is finished as staged, and Chinese waits for a
   public release.

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

## The seam, staged - 11 November 2026, played and final - 15 November 2026



Open decision 3 is answered: between page 25 and page 26 there is now a scene. The courtyard is named,

the Company is spoken over and walked out along a marked line, Elrond reads his blessing off a scroll

that he then rolls up, and he and Bilbo go back down the road. The player keeps what they came for -

page 26 falls into the book as the courtyard empties - and `Campaign.sayWhereThePageGoes` says, once, the

one thing an unasked-for handover cannot: `Glóin holds the next page, at the dwarf camp at the east end

of the High Pass.`



So chapter two's first page no longer needs Elrond to be walked back to, and on 15 November 2026 the seam was played
in game and **ran smoothly as staged** - it is finished, not merely present. What chapter two still wants is the rest of
the walk: pages 33 to 36.

## Played - 15 November 2026

The line was walked in the test world from 2-1 through 2-7 and works as written: the seam between 25 and 26, Gloin's
camp, Ulmug's fires, the hold raised when its page is taken, the vault, Gimli at the well, and the captain below the
stair. Two things came out of it:

- **The captain of the Dourhands was stood again after every kill**, because his seat is filled from a page that stays
  in the book until Gloin takes it. He now wears a mark of his fall on the player who ends him - the same tag answer the
  wraith behind him has always had - and the fix was confirmed in game: felled once more, he did not come back. **The
  wraith was never broken**: its own mark was in place and it behaved as it should, killed once and left gone, so the
  captain was the only body of the two missing one.
- **The keep's placement is settled at `KEEP_FLOOR` 176 (18 November 2026).** A fresh world drew the keep exactly
  at its plan and still had no way in: the rooms stood on 200 with the gate's chamber on 189, because the template's
  rooms are lifted thirteen courses over its base and the pin had been read as 187. `9f88c9a0` dropped the eleven
  courses, so the pin is 176 and the rooms' floor 189, level with the passage. A walk from the gate to the rooms in a
  fresh world is the check still owed; `docs/gundabad-keep.md` carries the numbers.

**The rest of the chapter is paused (15 November 2026).** Pages 33 to 36 - 33 Steps of Evil I, 34 Steps of Evil II,
35 Evil Tidings, 36 Fire & Ice - are all Carn Dûm ground, and **Carn Dûm is what quests go to next**. They are not to
be walked until that ground is worked: the border camps and the wight's hall are the parts of the chapter nobody has
looked at yet.

## Where we stopped - 12 November 2026

The chapter two line itself is unchanged since 4 November: eleven pages, 26 to 36, written and wired - and as of
15 November 2026, walked in game through 2-7. What changed on the 12th was the ground those pages stand on, and one
lesson about templates that is now written down so it cannot cost another outcrop.

### Standing, and seen in a world

- **The shelter camp.** Its cut is `key_structures/shelter_cave`, pinned at 5595, 196, -3621 and seated on the
  pin itself - corner at the pin, height at the pin, no ground survey - with `Sites.SELF_DRESSED`: no underpin,
  no sweep, no keeper, no steed, no stone. It brings its own fire and benches, so the only ground it touches is
  the mouth: `Sites.clearMouth` clears eleven wide, six out and six up in front of the door, at the raise and on
  every approach after it. The player has walked up to it and reports the placement exact.
- **The goblin cave.** `GoblinCave.raised` runs from `Campaign.tick` within 96 blocks of the pin, so the hill
  the fires sit in is cut and dressed as the player comes near rather than by a command. The cut keeps the plan's
  own record of every open cell, `wear` weathers every chamber it opened (cobble and mossy cobble patches on
  floor and wall, the rare glowstone in a ceiling, only ever into `BASE_STONE_OVERWORLD`), and the hall's own box
  is dressed by `wearHall` after the template is laid so its floor course is not weathered with the rest.
- **The key, and page 27's count.** One hall goblin carries a key whose code is `DungeonKey`, stocked from the
  named table `tolkienmobs:chests/dungeonkey` - so the hall chest and any other chest set to that code open to
  the same key, three uses. Page 27 asks for the fires' own folk: `GOBLINS_GOAL` is `GoblinCave.FOLK` plus
  Ulmug, and only goblins tagged by the cave count, so six goblins met anywhere in the mountains are not the six
  the page wants.
- **The courtyard seam.** `Departure` is built and driven from `Campaign.tick`: page 26 falls into the book as
  the courtyard empties, and the campaign says once where that page goes. Open decision 3 is answered.

### Compiled, not seen

- The cave's wear, its hall dressing and the key's stocking; the camp's mouth clearing on any hill but the one
  it was tested on; the eleven pages themselves.

- **Glóin's camp** - it was written down by the survey before its pin existed, so the site held a guess 900
  blocks from the pin and never built, which is why no dwarf stood at the fire. A pin now outranks an unbuilt
  site both when the page is taken and on every look, and the camp has its own name on the ground (`gloin`).
  Compiled only.
- **The goblin cave's gate** - the mouth walled at the cut, the bronze key and the camp chest set to
  `CampStorage`, the ashen scroll opening the mouth within 16 blocks, and the wall going back up at settling unless
  another player still carries the page. Compiled only; nothing of it has been played.
- The palette cleanup of the whole template set (`3d8168e4`, 662 templates, 54 dead entries and 31 duplicated
  states rebuilt out of the states in use, 42,763,308 listed blocks and 614,650 air cells unchanged, so no cell
  moved). Unseen because it changes no block.

### The template lesson

A structure block never saves a structure void cell - it writes nothing at all - so a save with void in it comes
out **sparse**: the box is full size, and the void cells are simply missing. A cell that is not listed is never
placed, so the ground there stands as it was; a cell listed as air *is* placed and clears what stands there. The
shelter camp's first template was dense, and its box's air took the hillside away in a cube. It is now exactly
what the player saved, rotated back into the seated frame: 3,427 listed blocks and 344 air cells for the room,
with everything outside the camp left unwritten. No template in the tree lists a structure void cell; the audit
and the rule for new builds are in `docs/structure-void.md`.

### Open, in the order worth taking them

1. **Walk 26 to 36 in a fresh world.** Nothing in the chapter has been played end to end. Watch four things:
   that the camp reads as a camp; that Gundabad's site search finds level enough ground for a 122-by-183
   footprint in the Hithaeglir; that the spiral's stair reads as the way down; and that Carn Dum's border camps
   are findable from the page's direction.
2. **Gate the inner keep.** The stair is open, so the keep below it can be walked before the plans say it can.
   Page 29 is already the beat that would open it - a door the plans open is the whole of the work.
3. ~~Name the Pinnath Fenui.~~ Done on 12 November 2026, and the way it was done is worth keeping: the place
   id is the cave's pin key, so `Places.ensure` takes the pin instead of surveying and the name and the cut
   cannot come apart. The `/tmpin` checklist now lists the cave alongside the camps and the hold.
4. **Place the chapter-two set.** `ch2_approach`, `ch2_cave_a`, `ch2_cave_b`, `ch2_cistern`, `ch2_colonnade`,
   `ch2_hall`, `ch2_inner_a`, `ch2_inner_b`, `ch2_spiral` and `ch2_vault` are ten finished templates with no
   reference in the Java and none in the datapack. The two cave tiles were cut for a cave beat the chapter does
   not yet have. They want a cut table (as Rivendell has) or a pin per piece, and then nothing else - the raise
   already exists.
5. **Eight key templates still have no ground:** `elven_banner`, `elven_gate`, `elven_house_1`,
   `elven_house_2`, `elven_lamp`, `elven_library`, `elven_square` and `snow_camp`.
6. **Chinese is deferred** for every line the rework touched, as with chapter one - the last pass, not a running
   cost.
7. **The place `glo` is still canon'd to the Hithaeglir** while the camp's site is in the Gladden. Nothing
   targets `placeTarget("gloin")` today, so it is harmless; a page that points at the place would point into the
   mountains rather than at the camp.

Nothing after page 36 is written. `FINALE_OPEN = false` and `FRONTS` holds the West alone, so the Rider, the
castle, the arena and the Witch-king stay shut by design. The notes' job boards (quest 37) are not started;
`Contract` already covers herald work.
