# Chapter two — Last Refuge

The notes' Section II, *Last Refuge*: the pursuit of the Nazgûl out of the Trollshaws, through the goblins of
the Misty Mountains, into the Dourhand fortress of Gundabad, and on to the wight and the dragon's corpse at
Carn Dûm. It is the West's line continued, not a new front: the dwarves stand with the West, and every page
speaks to a body already in the pack's cast or one it can stand.

This is the plan the work is built against. It is written down so the pages can be checked against the notes
rather than against memory, the same way chapter one's are.

## The eleven pages (26–36 of the West's line)

| # | Notes | Page | Giver → turns in | Kind and ground |
|---|-------|------|------------------|-----------------|
| 26 | 2-1 | Onward, Misty Mountains | Elrond → Glóin | `DIALOGUE`, Glóin's camp at the east end of the pass |
| 27 | 2-2 | Troublesome Goblins | Glóin → Glóin | `HUNT`, Ulmug, in the goblin fires of the Pinnath Fenui |
| 28 | 2-3 | Return to Gundabad I | Glóin → Glóin | `LOCATION`, Gundabad's Vault (the cornerstone and the plans) |
| 29 | 2-4 | Return to Gundabad II | Glóin → Glóin | `DIALOGUE`, the plans read at the camp |
| 30 | 2-5 | Key-Stone | Glóin → Glóin | `LOCATION`, the dwarf-vault at the source of the Bruinen |
| 31 | 2-6 | Last Refuge I | Glóin → Gimli | `DIALOGUE`, the secret entrance to the inner keep |
| 32 | 2-7 | Last Refuge II | Gimli → Glóin | `HUNT`, Dugorim, inside the keep |
| 33 | 2-8 | Steps of Evil I | Glóin → Glóin | `LOCATION`, the camps on the borders of Carn Dûm |
| 34 | 2-9 | Steps of Evil II | Glóin → Glóin | `ITEM`, the Dark Sigil off a wight at the fortress' door |
| 35 | 2-10 | Evil Tidings | Glóin → Elrond | `DIALOGUE`, Elrond's library |
| 36 | 2-11 | Fire & Ice | Elrond → Elrond | `HUNT`, Drugoth and the Dragon Wight, in Carn Dûm |

Rewards follow the notes at the tenth the whole campaign is priced at: their 500 becomes 50 standing, 700
becomes 70, 1200 becomes 120. The coin the notes put on each page is paid as written, in silver and bronze.

## The ground

- **Glóin's camp** — a place at the east end of the High Pass (`gloin`), north of the valley, on the ground
  the pass drops into on the Anduin side (Gladden) and pinned to that pass's mouth. Captured from the old
  world's own camp rather than modelled: a fire, tents and a dwarf waiting at the door.
- **Gundabad and its vault** — places (`gundabad`, `vault`) in the same range, north of the camp. The
  fortress itself is the one real asset question left: the pack has no dwarf-fortress template, and the
  notes want an outer fortress, a vault and an inner keep.
- **The key-stone's vault** — a place (`keystone`) in the Trollshaws, at the source of the Bruinen: ruins
  with a vault still in them, and bears in the ruins.
- **Carn Dûm** — a place (`carndum`) in Angmar, which the drawing already has, and where the campaign's
  own castle and arena already stand. Chapter two ends in the same country the finale's war does.

## The bodies

- **Glóin** and **Gimli** are new keepers, stood like Elrond and Legolas are: a tag, a name, and a skin under
  `textures/entity/middle-earth/` if one is drawn for them.
- **Ulmug** (goblin), **Dugorim** (Dourhand dwarf) and **Drugoth** (Gaunt-lord) are named quarries, which is
  the power the campaign already has: a tagged body of an existing kind, named, and the kill counted for
  that body alone.
- **The Dragon Wight** is the notes' raised corpse and the pack has no such creature. It is either a named
  wight of an existing kind or the chapter needs an asset. Open question, and the last page's only one.

## The seam with chapter one

The notes end chapter one on *Hasten Their Departure*, and the old build staged it as a scene: the company
gathered before Elrond, then walking away. Nothing is staged today; the page is a walk back to the library.
If the scene is wanted it belongs here, between page 25 and page 26 — the Fellowship leaving is the same beat
as Elrond sending the player north, and a scene at the end of chapter one that is never seen again would be
the only one in the campaign.

## Deliberately not decided here

- Which build stands for **Gundabad**: the pack's own `boss_castle` raised in the mountains, or a new
  fortress template cut for it. The castle is the finale's, and using it twice is a cost paid in the last act
  rather than in this one.
- Whether the notes' **Pinnath Fenui** is named ground or a direction: the notes describe it as the hills
  north-east of the camp, which a compass bearing already covers.

## Source: the old world's Gundabad

The fortress chapter two needs still stands in the upgraded backup of the old world, and this is where to find
it again:

    G:\GitHub\MC1_21_x\TolkienTweaks\run\backups\2026-11-01_10-39-03_Arda\Arda     (overworld, region/)
    corners  14473,9,2275  ->  14701,9,2546     both taken on the lowest level of the build

Measured with `tools/grab_build.py`. The box is 229 x 272 and reaches from y 0 to the build limit, and it holds
7.9M blocks - of which 6.7M are the mountain itself: stone 6.66M, andesite 246k, diorite 255k, granite 247k,
dirt 203k. Only 195k blocks are built. So Gundabad is not a structure standing on ground; it is rooms cut into
a mountain, with a stair two blocks wide winding up the outside of it above y 50. The mountain is not portable
and is not worth carrying: what the chapter uses are the rooms at its feet.

| part | box | size | built blocks | tiles |
| --- | --- | --- | --- | --- |
| forge (anvils at y 3, nether brick) | 14570,0,2276 - 14604,16,2312 | 35x17x37 | 771 | 1 |
| treasury (emerald shelf at y 13) | 14590,4,2330 - 14620,20,2362 | 31x17x33 | 274 | 1 |
| goblin works (netherrack, hoppers) | 14596,24,2484 - 14644,52,2532 | 49x29x49 | 9,153 | 4 |
| obsidian ruin (cobble, fern) | 14528,20,2432 - 14576,44,2480 | 49x25x49 | 992 | 4 |

Cut with `--only` naming the masonry and the fixtures, so the hillside around a room becomes air and the room's
own walls, floor, stairs and shelf come across. The treasury is written and read back clean: one tile, 31 x 17
x 33, 274 solid blocks in a 41 KB file.

The old build's fixtures are worth knowing because they say what each room was for: 24 emerald blocks in a row
at y 13 (a treasury shelf), 14 anvils and nether brick slabs at y 3 (a forge), 32 hoppers and 480 gray
terracotta at y 39-50 (the goblins' works), 63 obsidian and 171 coal blocks at y 25-39 (a burnt-out place).

### The converted save

    G:\GitHub\MC1_21_x\TolkienTweaks\run\saves\Arda    (26.1.2, DataVersion 4790)

Reading it gives the same build to the block: 195,107 built blocks, 44 distinct names, and the emerald shelf,
the anvil row, the hoppers and the obsidian all at the same coordinates. The only difference is 12 extra lava
blocks, from lava that kept running while the save sat. Conversion cost nothing and bought nothing - for
extraction the two saves are the same world, so the 1.21.1 backup is the one to read.

One thing did change with the version: 26.1.2 no longer keeps the overworld in `region/`. It lives in
`dimensions/minecraft/overworld/region/`, and the reader now falls back to that when `region/` is missing.

### Gundabad is underground

Dwarven strongholds - Gundabad, Khazad-dum - were halls cut into rock, not castles on a hill. That reframes
what is worth carrying out of the old world. A room shell standing in air belongs on open ground; for Gundabad
the useful unit is the opposite: a plug of rock with the halls already inside it. Placed into a hillside its
stone merges and its air carves the passage, which is how the build worked in the first place - and it is why
the mountain inside the box is not waste. Only the tile budget limits that: 48 cubes a piece.

Tried it on the vault and the corridor leading to it. A 48-cube of that rock cuts as one tile: 86,697 stone,
2,639 stone brick, 232 slab, 209 stair and 12,740 air - 11.5% of the box is already hollow, so the passage is cut
into the rock and dropping it into a hillside reads as a dwarven hall rather than a room standing in the open.
Bedrock has to be dropped, since placing it would put unbreakable blocks in the world; `--drop` is for that, and
for snow that belonged to the old mountain's surface rather than to the halls. With both dropped the tile is 265
KB and 15.7% air.
