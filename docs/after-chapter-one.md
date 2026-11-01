# After chapter one — what the rest of the campaign needs

Chapter one (*Chasing Shadows*, the West's line) is built and waiting on a playtest. This is the list of
what the chapters after it will need, written down while the inventory is fresh so the next sitting starts
from a decision rather than from a survey. Nothing here is started; it is here to be argued with.

## What the campaign already has

- **A body for every beat.** The pack registers 57 mobs, and the notes' cast is covered by them: cave troll,
  tree-ent (the Giant), Nazgûl and steed, the Witch-king, warg, orc and goblin, Haradrim, mûmakil, brigands.
  No chapter needs a new creature to be written.
- **Ground to stand on.** Twelve `key_structures` templates (boss castle and arena, the tent, the crags,
  Weathertop, seven Rivendell pieces), the three wild ruin templates, and eleven named places in the
  catalogue — plus `ArdaMap`'s countries for the canon ones.
- **Three crags for the three caves.** `troll_cave`, `troll_cave_2` and `troll_cave_3`, all cut by
  `tools/make_troll_cave.py`; the stone's cave page walks the player to one per asking.
- **Coin, tokens and loot tables.** Silver and bronze, the cave-completion token, twenty chest tables
  including `desolate_ruins`, now wired into the campaign ruin.

## The gaps, in the order they will bite

1. **Chapter two (Gundabad) has nothing at all.** No place in the catalogue, no structure, no keep for the
   notes' *Last Refuge*. The boss-castle machinery would raise a fortress without new code, but a place name
   has to be added and canon'd to the right country, and the notes' *Steps of Evil* walks the Nazgûl to
   **Carn Dûm**, which the map already has as `ArdaMap.Country.ANGMAR` — the same country the finale's castle
   and arena stand in. Decision needed: does chapter two reuse that castle, or does it get a keep of its own?
2. **Chapter seven (Annúminas) is blocked on a country, not on art.** The drawing has no Arnor — that land is
   `ERIADOR` — and the pack has no city: the largest builds are the castle and the arena. Either Annúminas
   becomes a place canon'd inside `ERIADOR` and built out of the elven and human pieces, or its own country
   is drawn into `ArdaMap`. Both are real work; the second is the larger.
3. **Chapters five and six have biomes but no campaign ground.** Moria/Brimigak and Forochel exist as
   country, and neither has a place, a structure or a page yet.
4. **The fronts are lopsided.** The West has seventeen pages; the East and the Harad have three each. That is
   authoring rather than assets, but it is the biggest thing standing between the campaign and reading as one
   war: three fronts of seventeen pages is the shape the notes imply.
5. **The notes' job boards (quest 37) are unbuilt.** `Contract` and `MercenaryContracts` already cover
   herald work, so a board is a page source rather than new machinery.
6. **Skins are thin past the named few.** `middle-earth/` holds Elrond, Elladan, Glorfindel, Legolas and the
   Ranger. Barachen is fine as the Ranger — he is one — but any future named character needs a 64×64 skin
   dropped in that folder, and a keeper who has one now wears it.

## Deliberately deferred

- **Chinese.** The lines that changed in the rework were translated; the rest waits until the chapter is
  known to be right, which is the last pass rather than a running cost.
- **A fourth crag.** Matuna's lair reuses the first crag. If it reads as repetition in play, the generator
  takes another seed and the lair gets its own hill.
- **Playtest items.** Nothing in chapter one has been walked in game: tall ruins on slopes, the Elrond
  opening page, and whether three crags read as three places are the three things to look at first.
