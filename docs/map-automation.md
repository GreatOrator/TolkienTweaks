# How the map raises itself

The rule is in `AGENTS.md`: a build a player is meant to find reaches the world without a command. This is
the audit of every build the campaign has, the clock that raises it, and the commands that are left over as
development tools. It was walked by following each placing call back to whatever calls it, not by trusting
the names of things.

## What raises itself, and from where

| Build | What raises it | Clock |
| --- | --- | --- |
| Rivendell, band by band | `CityCommands.rouse` | `Dressing.onServerTick` |
| Rivendell's ground, shoulder and road | the `Dressing` job (`start`/`step`) | `Dressing.onServerTick` |
| Camps, the ruin, the four crags, the shelter camp, any pinned structure | `Sites.scenery` | `FactionEvents` -> `Places.tick` -> `Sites.tick` |
| A page's own site: Gundabad, Weathertop, Gloin's camp, the boss castle and arena, the wild tent | `Sites.plan` when the page is taken, then `Sites.build` on approach | `QuestLedger` -> the same `Sites.tick` |
| The goblin cave and its hall | `GoblinCave.raised` | `Campaign.tick` |
| The stone's three crags (a page asks in a hill of its own) | `Campaign.keepCaveOpen` -> `Sites.plan` | `Campaign.tick` |
| Roads out of every build | `RoadCorridor.onChunkLoad` -> `RoadCommands.pass`; `Sites.road` for a site's own door | chunk load + the `Sites` tick |
| Rivendell's people and the valley's folk | `Campaign.keepValleyStanding` -> `CityCommands.plantedCorner` | `Campaign.tick` |
| The chapter-one seam at the courtyard | `Departure.tick` | `Campaign.tick` |
| Keeper, steed, stone, named bodies | `Sites.dress`, `Sites.STANDS`, the `keep*` bodies | `Sites.tick`, `Campaign.tick` |

Two things are worth saying about the table. The first is that `plantedCorner` answers for a city the world
generated as well as one planted by hand (`wouldGenerate` plus `cutStandsAt`), so a generated world's people
stand without the marker a hand-planted city writes. The second is that a pinned structure needs no entry in
`Sites.SCENERY` to be raised: `sceneryFor` adds every pin whose key names a template, which is why the
shelter camp and the fourth crag stand with nothing but their pin.

## Commands that remain, and why they are not a gap

`/tmcave`, `/tmcity`, `/tmroad`, `/tmscene`, `/tmpin`, `/tmtp`, `/tmplace`, `/tmquest`, `/tmfaction` and
`/tmcourage` are development tools: for walking something a world has not made yet, for the report they
print, and for rehearsing a scene without spending a page. Nothing on the map needs any of them. Where a
command and the world do the same work they call the same code - `/tmcave cut` and `GoblinCave.raised` both
run `GoblinCave.cut`; `/tmcity drape` and the dressing job both run `Dressing.drape` - so a change to one is
a change to both.

## Open items this audit found

- **The chapter-two set is not placed by anything.** `ch2_approach`, `ch2_cave_a`, `ch2_cave_b`,
  `ch2_cistern`, `ch2_colonnade`, `ch2_hall`, `ch2_inner_a`, `ch2_inner_b`, `ch2_spiral` and `ch2_vault`
  are ten templates under `data/tolkienmobs/structure/` with no reference in the mod's Java and none in its
  datapack either. They are not a command away from standing; they are not placed at all. Raising them wants
  what Rivendell has: a cut table saying where each piece sits, or a pin per piece and a `Sites` entry, and
  then nothing else - the raise itself already exists in `Sites` and `CityCommands`.
- **Eight key-structure templates are also unreferenced:** `elven_banner`, `elven_gate`, `elven_house_1`,
  `elven_house_2`, `elven_lamp`, `elven_library`, `elven_square` and `snow_camp`. Same answer: they need
  ground before they need a clock.

Neither is a command-only build, so neither was automated here. Automating a build with no placement table
would mean inventing where it stands, which is a scouting decision rather than a code one.
