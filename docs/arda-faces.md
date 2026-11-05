# Faces

Who wears what, how the renderer finds it, and which files under `textures/entity/` are which.

## How a named body gets its art

`HumanRender.textureFor` asks in one order:

1. **a keeper** at a built camp - `middle-earth/<slug>.png` if it is drawn, else `middle-earth/ranger.png`, else the front's giver cloth below;
2. **a giver** - `middle-earth/<slug>.png` if it is drawn, else the alliance's cloth from `GIVER_BY_ALLIANCE` (which logs a warning when missing), else the variant face;
3. **everyone else** - the variant face, and the winter one in a cold biome.

The named file is only asked for when the body is a giver (carries `quest_giver` and an alliance tag) or a keeper (`tolkienmobs:camp_keeper`), and only when it has a custom name. An ordinary villager is never looked up, however many files are drawn for the names it might have had.

The slug is the name lowercased, with accents flattened to the letter under them, spaces, dashes and underscores becoming `_`, and everything else dropped: `Glóin` is `gloin.png`, `The Rider` would be `the_rider.png`. A missing named file is passed over in silence - art arrives by degrees - but a missing alliance cloth is logged, because that one is the pack's own promise.

## The cast

| body | slug | where it stands |
| --- | --- | --- |
| Elrond | `elrond` | Rivendell: pages 1, 2, 5, 7, 12, 13, 15, and 2-1, 2-10, 2-11 |
| Glorfindel | `glorfindel` | Rivendell: pages 5, 6, 7 |
| Legolas | `legolas` | Rivendell: pages 13, 14 |
| Elladan | `elladan` | Thorenhad: pages 2, 4, 8 |
| Barachen | `barachen` | his fire: page 10 |
| Glóin | `gloin` | the pass camp: chapter two |
| Gimli | `gimli` | Gundabad: page 2-6 |
| Maelgrin | `maelgrin` | the West's herald, at a bench in every West hall |
| Guthlac | `guthlac` | the East's herald |
| Zarabi | `zarabi` | Harad's herald |
| (a camp keeper) | `ranger` | the body minding a pitched camp |

## The keeper's fallback

`ranger.png` is not a person. It is what the body minding a pitched camp wears when nothing has been drawn for its name; keepers are named from the giver pool (`QuestPool.giverName`), so they are different people in every world.

## The faction folders

- `faction_west/quest_giver_west.png`, `faction_east/quest_giver_east.png` and `faction_harad/quest_giver_harad.png` are `GIVER_BY_ALLIANCE`: the cloth every giver of that front wears, and what a herald drops back to when he has no file of his own.
- `faction_*/factionN.png` and `faction_trader/*.png` are the fronts' other sheets. The named lookup never reads them: it only ever looks under `middle-earth/`.
- **`maelgrin.png`, `guthlac.png` and `zarabi.png` are copies** of those three `quest_giver_*` files, taken byte for byte so each herald could have a file of his own to redraw. They do not follow the cloth: repaint `faction_west/quest_giver_west.png` and Maelgrin keeps the old face until `middle-earth/maelgrin.png` is copied across again. That is the point of copying - a herald can be made to look different from the givers of his front - but it is also the thing to remember.

## What can never have a named file

- **The Rider** - it is not a giver, so the renderer never reads its name.
- **the pool's givers and keepers** - named from the config or the eight shipped keys, so the same chair is a different person in another world.

## Adding a body

1. Stand it as a human (`ENTITY_TTM_HUMAN`, scaled when it is a dwarf) with `quest_giver`, its alliance's tag, and a custom name.
2. Draw `middle-earth/<slug>.png` at 64 by 64 - the sheet the human model reads.
3. Nothing else: no code, no registration.

## Checking the folder

A file whose name matches no body in the cast is dead weight - no body will ever ask for it - and a body with no file falls back to its alliance's cloth without saying so. Listing the folder beside the cast above is the whole audit.
