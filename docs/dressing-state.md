# Dressing: where it stands, and what is on the table

Written on 27 November 2026 so a session picking this up cold does not have to rediscover it. **Read this before
changing `Dressing.java` or `CutCommands.clearColumns`.** The running state of the campaign is `docs/next-up.md`; this
file is only the dressing - what the passes do today, what has been measured, the options that were put to the owner,
and what each of them is supposed to fix.

## The short of it

- The village is raised in four stages: **lay the cut, clear the country out of the footprint, dress the ground**. The
  dress stage is switched off by `dressShireRing` (default false) and only the ring outside the footprint is gone with
  it.
- The seam the owner photographs is **not one thing**. At the Shire's west and north edges the country stands *below*
  the village (82..85 outside against 91..103 inside): that edge is the build's own hill and no ground pass can smooth
  it without cutting the build. At the field edges *inside* the footprint the country stands four blocks *under* the
  village's fields, and the clear leaves that step one column wide - that one is dressing's to fix.
- The first option built is the **inward rim**: (a) the clear keeps a bank of country at the cut's own box line where
  the country stands over the build (a hillside cut), and (b) the inside pass grades the country **up** to the foot of
  the build's own ground over four to seven blocks, following the country's own top and wandering at its foot, so a
  field plate's edge is a bank and not a wall.
- The long-term target is one dressing rule that works for an above-ground site and an underground one (Gundabad)
  alike. Everything below is written with that in mind: the inward rule needs nothing outside the footprint, which is
  exactly what an underground site cannot give.
- Every bank's top course is grass, and **no grass is laid on a tree**: a bank whose cap lands inside a crown is laid
  under its own top instead (`7644ebc4`, 27 November). What is still open is the **tall faces** - the west face nine
  blocks over the country and the north nineteen - and the ranked options for them are the section of 27 November.

## What runs today, in the order it runs

1. **Lay** - `CutCommands.lay` places the cut's pieces, 48-block tiles, lowest band first, flags `2|16` (clients told,
   shape updates off). A cut is a folder of pieces plus a `.parts` table; `deck <n>` in the table says how high its own
   floor stands over the corner it is planted on (`CityCommands.DECK_LIFT = 2` when it says nothing).
2. **Clear** - `CutCommands.clearColumns(level, planted, box, shape, from, upto)`, a band of columns at a time:
   - the country off the top of every column with a build block: from `max(WORLD_SURFACE, box.highY()+16)` down to the
     cut's own top (`shape.top()`), which is why the sweep starts over the build's own sky rather than at the heightmap;
   - the country out of the cut's own hollows between its top and its bottom (`shape.solid`), so a hillside crossing a
     field is taken out;
   - `strays`: a trunk with air under it comes off, whole, up as far as it is wood - the art's own floating posts and the
     country's leftovers over the village both;
   - a column whose top is a tree **the cut itself draws** is kept whole (`standsOnOwnGround`): the village's party
     trees.
3. **Dress** - `Dressing.Job`, stepped by whichever build is raising itself:
   - `takePieces` reads the cut into `outline` (trees excluded, for the ring) and `whole` (trees included, for the inside
     cut), and `watered` (where the cut's own water stands);
   - `grow` fills each column's holes from its neighbours (two passes) and marks `channel` where the build's water is the
     top of the column;
   - `mapWater` reads every column's distance to the nearest water and that water's level, for the shore rule;
   - `dressChunks` walks the ring's chunks, two a tick, and calls `dressColumn` on each column of each chunk;
   - `dressColumn` sends a column inside the footprint to `dressInside` and an outside one to `join`;
   - then the road, if the build has one (none does: `Roads.NETWORK` is empty).
4. **Note** - `Hobbiton` writes `_stands.txt` beside the cut: dimension, corner, cut fingerprint, which dressing it got
   (`DRESS_VERSION`, now 14, or `UNDRESSED`, now 2), and seven probe columns. `Dressing` writes `_dressed.txt` with the
   drape and road bits. A world whose note does not match what this build would make raises the village again.

### The rules inside the drape, one line each

| Rule | Constant | What it does |
| --- | --- | --- |
| Join | `DRAPE_REACH` 60 | outside columns ease from the build's own edge height to the country's over 60 blocks, plain linear share |
| Bank | `BANK_BLOCKS` 3 | a column standing 3+ over the build's own ground is a face and is dressed; less is a groove and is left |
| Apron | `APRON_WIDTH` 4 / `APRON_CAP` 8 | a bank of 8 or less **inside** the walls is cut in quarters over 4 blocks instead of all at once |
| Soil | `SOIL_BLOCKS` 3 | a fill keeps the country's band of dirt under its grass |
| Shore | `NO_WATER`, `EDGE_WATER` 3 | no fill stands more than one block over its distance to water; a stray pool within 3 of the box edge is drained |
| Mouth | `OUTLET_LIFT` 24 / `MOUTH_DEPTH` 3 | a build's held-up water is carried out through the ground in front of it |
| Mountain | `HEM_*` (inset 4, reach 60, crest 3, drop 3, tall 4) | a hill planted over the build's high points, the outermost ring - the pass the owner rejected |
| Orphan | `COLUMN_DEPTH` 40 | a column with no ground within 40 blocks of its top has its tree taken off and nothing else done |
| One-block join | - | a join that would take or lay a single block is left alone: a step of one is a lip, not a shoulder |

## What has been measured

- **The site.** Planted corner 866, 70, -2917; footprint x 866..1424, z -2917..-2450 (551 by 468); deck 86, floor 70,
  village water 79, village grass 86; 143 pieces; fingerprint `143-fe676c68`. 13.1% of the footprint is drawn water
  (8,461 of 64,350 sampled columns); dry ground reads 79 / 82 / 87 at p10 / median / p90 against the village's 86.
  Ground east of the box (x 1420..1560, z -2600..-2500) reads 80..86 grass.
- **The `DoOver - 1` note** (bare, cleared, `1`): probe tops 107, 116, 89, 84, 82, 83, 85 - the first two are the
  village's own hill in the north-west, not the country.
- **The seam either side of the box line** (`build/tmp/seam.py`, mean ground, `DoOver - 1`):

  | Edge | Outside, 24 blocks | Inside, 24 blocks | Step at the line |
  | --- | --- | --- | --- |
  | west | 82 flat | 91..94 | **9 up** |
  | east | 82..83 | 83 at the line, 94 by +8 | 0, then the village's hill |
  | north | 84 flat | 103 flat | **19 up** |
  | south | 84..85 | 83..84 | 1 down |

  So three of the four edges are the **build standing over the country**, not the country over the build. That is why
  "leave the country standing at the box line" cannot be the whole answer here.
- **The old ring's leftovers** sit in that band in `DoOver - 1` (dressed stone at y95 from the previous ring), which is
  why it cannot be the baseline for an *outward* profile. The inside profile above is the build's own and is valid.
- **The ring band census** (3+ block drops) on `DoOver - 1` is dominated by crowns, not terrain: `oak_leaves` 16,761,
  `hardened_leaves_lebethron` 11,959, `hardened_leaves_culumalda` 5,882 - i.e. dropped crowns, a tree problem, and it
  was fixed as one (`36280c12`: 16,617 columns topped by country tree material over the cut's own top, to 0).
- **The bare raise** (`Bare 1`, before this work): 534 ticks, 26.7 s, 1050 chunks of ground, 143 pieces, thirteen of the
  folk, no drape line in the log at all.
- **The dressed raise** (`Dress 13`): `13264 columns cut down, 71838 filled up, 0 left for the water, 31088 left alone,
  0 cleared of a tree standing on nothing, 0 inside the walls cut down to the cut, 518 of the build carried under the
  mountain, 65016 of the ring on its flank`.

## The options, as put to the owner on 26 November

Ranked by what the measurements above say each one can actually fix. "Inward" means inside the cut's own footprint,
"outward" means the ring that was rejected.

1. **Inward rim roll-off** - the cut's own edge, not the country's. Where the country stands over the build at the box
   line, keep it standing and roll it down into the build over a band; where the country stands *under* the build's own
   ground, grade it up to that ground over a short reach. No blocks outside the footprint are read or written, so this
   is the only option an underground site can use at all. **Built first.**
2. **Contour-stepped, jittered falloff** - the band measures the country's own height at each column rather than a plane
   laid through the rectangle, so the bank follows the land's contour, steps one block at a time, and its foot wanders
   a column at a time instead of running parallel to the build. Cheap, and it is what makes 1 read as ground. **Built
   with 1.**
3. **Hedge banks** - on the edges that remain 2+ blocks of build over country (the west and north here), plant a hedge
   or bank of leaves two to three blocks high along the foot. Hides the wall without touching either side. Does not
   fire where the step is 1 or 0.
4. **Tree belt at the foot** - the same idea with the country's own trees instead of a hedge: a scattered belt just
   outside the line so the eye stops at the trunks. Costs chunks outside the footprint and reads as planting, not
   terrain.
5. **Lane and revetment where the north-west hill is real** - the hill is the build's own and stands 17..19 over the
   country; a revetment (a low wall of the build's own stone) at its foot plus a lane along it is what a real village
   does there. It is the one option that makes the hill read as *built* rather than attempted ground.
6. **Apron carried in the cut's own pieces** - a finished ground of the build's own blocks laid under and beside it by
   the piece that carries its wall, so there is no country to change at all. This is the cleanest answer to a permanent
   seam, and it is a change to the cut's table and its 143 pieces rather than to the drape - worth doing once the
   others are ruled in or out, because it is the only one that survives a build being dropped anywhere.

Rejected and not to be reopened without the owner: the **mountain ring** (a hill planted over the build's high points
and eased 60 blocks out). It is still in the code, reachable with `dressShireRing` on, and switched off by default.

## The options for the tall faces, as put to the owner on 27 November

The owner's verdict on the first build was that the ground close to the village's own level looks right - the join
smooth, the dithering fine - and that the tall faces are the question. `SKIRT_RISE` 6 refuses them: the west face
stands nine over the country and the north face nineteen. In the order they were put, with what each costs:

1. **A terraced skirt** - the same inward rule with the cap raised and the bank laid as steps: reach 8..12 columns, each
   terrace four blocks of rise or less with a one-block flat between, keeping the contour-following height and the 0..3
   wander and the country's own skin over its own dirt. Nine blocks is two terraces, nineteen is four or five. No chunks
   outside the footprint and nothing in it rewritten - about 10..15k filled columns at worst, against the old ring's
   24,417 - and it is the only one of the five an underground site can use unchanged. **The one to build first.**
2. **A revetment and lane at the foot** - a one-block wall of the country's own stone (the block already under the soil,
   so no palette to invent) two to three high with grass on top, and a two-wide lane along its foot. It finishes the
   face rather than smoothing it, which is what a Shire terrace is, and it is the honest answer for the nineteen-block
   north face where a terrace would be a green ramp five deep. A few hundred blocks, no terrain search; it does not
   translate to a shaft.
3. **Cutting the build's own hill back** - slope the village's own drawn ground off at about 1:2 over 6..10 columns.
   Cheap in blocks but it edits the art: it takes fields, fence lines and drawn trees off the cut's edge. Last resort.
4. **A short outward shoulder** - the ring's own direction at 10..14 blocks with the mountain still gated off. Chunks go
   1050 to about 1230 and 2..4k columns are filled. It is the only option that brings the country up to the village, and
   it fails the target: an underground site cannot give outward ground, and it is the direction that produced the
   plates. Fifth in line, kept only because it is the ring's own.
5. **A tree belt or hedge at the foot** - planting rather than ground, and nothing an underground site can do.

The pair worth shipping: **1 for the nine-block west face**, small enough to see the terraced look on, with **2 held for
north**. Verification for either: `seam.py`'s mean profile as before, plus two new numbers - the worst neighbouring
step between ground tops inside the footprint (should be two or less) and a skirt counter split by rise band, so the log
says how many columns were terraced and how many still refused.

## What was built first, and why

`RIM_*` in `CutCommands` and `SKIRT_*` in `Dressing`, plus the inside pass now running with the ring off:

- **`clearColumns`** keeps a bank of country at the cut's own box line where the country stands over the cut's own top:
  `RIM_FEATHER` 14 blocks in, wandered by 0..3 (`RIM_JITTER`), capped at `RIM_CAP` 12 so a build under a mountain is not
  buried, and left alone where the column is water. The bank's top course gets grass so it is ground and not a cut face.
- **`Dressing.skirt`** grades the country **up** to the foot of the build's own ground inside the footprint, over
  `SKIRT_REACH` 4 (plus the same 0..3 wander, so four to seven blocks), against edges of 3..`SKIRT_RISE` 6 blocks. It
  follows the country's own top and lays the country's own skin over its own band of dirt. Taller edges - the build's
  own hill - are left and counted as left alone. Water is left.
- **The inside pass runs with the ring off.** `Hobbiton.Standing.step` no longer returns before `dressStep`; the job is
  built with a reach of 0, so its chunk ring is the footprint alone and `dressColumn` never sees an outside column. The
  city and Bree keep the 60-block ring exactly as it was.
- **`Dressing.dressInside`** skips the band the clear already graded (`CutCommands.RIM_REACH`) so the two rules cannot
  fight over the same columns, and sends the ring's 60-block join away from empty tile columns when there is no ring,
  in favour of the short skirt.
- **Versions** - `DRESS_VERSION` 14, `UNDRESSED` 2, so a world holding either an old dressed village or an old bare one
  raises it again and gets the graded rim.

## What the first build actually did

`Seam 1` was the first fresh world (the code before the mountain's burial was gated off it), `Seam 2` the one the
counts below come from. Both are `run/Seam 1` and `run/Seam 2` - a headless server writes its world to `run/<level-name>`,
not to `run/saves/`.

- **Raise:** `Seam 2` stood the village in 866 ticks (43.3 s), 1050 chunks, 143 pieces, thirteen of the folk, under
  `its own rim graded and the ring left alone`.
- **The clear's bank fired: 8,885 columns** of the footprint's own edge kept a bank of the country. That is 3.4% of the
  257,868 columns - the south and south-east rim, where the land stands 2..6 over the build's own top.
- **The skirt fired on 0 columns.** Every column of the Shire's footprint is the build's own drawn ground (the cut is a
  fully grounded rectangle), so `whole` is never `MIN_VALUE` inside it and there is no country left for the bank to
  meet. This is the finding, not a failure: the inward rule has nothing to work on in a build that carries its own
  ground across the whole box, which is why the west and north faces need something else.
- **The mountain's burial is off a bare world.** Running the inside pass with no ring showed it firing on 518 columns
  (`Seam 1`); now gated to `reach > 0` and reading 0 (`Seam 2`).
- **The south rim is a slope now.** Mean ground, first eleven columns inside the line: `88, 88, 87, 87, 86, 85, 85, 84,
  84, 84, 83`, against a flat `83` in `DoOver - 1`. West line 92 against 91, north 103 unchanged, east 83 unchanged.
- The step histograms in `seam.py` print empty - the mean profile is the number that works; fix or ignore the buckets
  before relying on them.

What that says about the next step: the Shire's seam on the west is the village's own hill standing nine blocks over
the country and on the north nineteen, and no pass that may not rewrite the build's own blocks can grade those. The
options for them are 3 (hedge), 4 (tree belt), 5 (revetment and lane) or a **short outward shoulder** - the ring's own
direction, at fourteen blocks instead of sixty and with no mountain, which is the owner's call and the one thing in
this file nobody has tried.

## What the owner's fresh world showed, and the fix for it (27 November)

The owner raised a new world from the first build - `run/saves/Drape Test - 1`, whose log reads `the footprint is
cleared, and 8648 columns of its own edge kept a bank of the country` with every drape count 0 - and looked at it. The
ground close to the village's own level reads right, the join smooth and the dithering fine. The fault is that the rim
laid its grass **on top of trees**: plates of grass standing on the crowns along the village's edge.

Read out of that save with `build/tmp/leafgrass.py`, which asks for every grass block in the footprint whose own
footing is a leaf or a piece of timber:

- **2,409** grass blocks stand on a leaf or on timber, and **1,830** of them are within seventeen blocks of the box's
  edge - the clear's own rim band;
- **0** in a 226-block strip of country west of the box that no pass touched, which is what rules out the art and the
  land as the source;
- two columns whole: `866, -2519` is `grass_block` 85, `hardened_leaves_lebethron` 86..89, `grass_block` 90, and
  `870, -2560` is grass 86, one leaf at 87, grass 88.

The cause is the cap and not the height: `CutCommands.rim` measures the country with `MOTION_BLOCKING_NO_LEAVES`, which
is right for a contour and wrong for a cap laid on one - where the country stands in a tree the height the bank keeps
falls inside the tree, the sweep takes everything *over* that height, and the leaf at it stayed with the grass on top.
The fix, `7644ebc4`:

- the bank is laid **under its own top**: the cap is walked down through the tree to the ground the column stands on,
  the crown still inside the bank is filled with dirt, and the grass goes on the cap as before; a crown hanging over air
  or water gets nothing laid in it;
- `Dressing.skirt` now leaves a column with **anything standing on its ground** exactly as it stands - inside the village
  the trees are the village's own, so the bank stops short of a tree rather than climbing through it - and
  `dressInside` no longer lays its grass top on a cut whose top course is tree material;
- `DRESS_VERSION` and `UNDRESSED` are unchanged, because a column with no tree in it is laid exactly as it was, so this
  only shows in a world raised after the commit.

**Not seen in a world:** the fix is compiled and no world has been raised with it; `Drape Test - 1` was dressed before
it.

## What is still open

- **The tall faces** (west and north, 9..19 blocks over the country) are not dressing's to fix as the passes stand. They
  are the art's own terrain, and the ranked options are the section of 27 November; the terraced skirt is first.
- **The crown fix is unverified in a world.** The counts above are the fault it repairs, not the repair: nothing has
  been raised with `7644ebc4` in it, and `leafgrass.py` over a fresh world (expect 0, or only columns the clear never
  touched) is the check.
- **The skirt is untested in an underground site.** Its guard is `SKIRT_RISE` 6, so a column 40 blocks under the surface
  is left - which is the right answer for Gundabad's rock but has never been run there. Gundabad is not a cut at all
  (`GundabadKeep` places one template and dresses only the doorway mouth, `clearMouth`), so making it use this pass is a
  separate piece of work: it would need a cut table or a shape source, and the rim would have to become the shaft
  mouth's own rock rather than the country's sky.
- **`DoOver - 1` cannot be a before/after baseline** for anything outside the box (the old ring's stone is still in
  that band), and no clean same-world before/after is possible: a headless server does not take `stop` through its own
  stdin, a force-kill loses unflushed chunks, and an empty `level-seed` makes every fresh world different country.
- **The numbers above are reads of saved worlds and of the code**, never a client. Nothing in this file was seen by an
  eye at the screen.

## How to check a dressing change

1. `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache` must exit 0, and
   `git -c core.whitespace=cr-at-eol diff --check` must be clean.
2. A fresh headless world: `sed -i 's/^level-name=.*/level-name=<Name>/' run/server.properties`, `./gradlew runServer
   --console=plain`, poll `run/logs/latest.log` (boot about 2 min, the village's ground about 9, the raise about 45 s),
   then take the server down through its port (`netstat -ano | grep "0.0.0.0:25565"`, `taskkill //F //PID <pid>`).
3. `python build/tmp/seam.py --world "run/saves/<Name>" --box 866,1424,-2917,-2450` for the step profile across the
   four lines. `build/tmp/notch.py` for a small window as two character grids. `build/tmp/treestat.py` for tops and
   wood over a box. `build/tmp/edge.py` for the ring band's cliff census. `build/tmp/rim2.py` exists but reads
   misleadingly on `DoOver - 1` (see above). `python build/tmp/leafgrass.py --world "run/saves/<Name>" --box
   866,1424,-2917,-2450` counts the grass blocks standing on a leaf or on timber - 0 is the crown fix holding on a world
   raised after `7644ebc4`, and 2,409 is what `Drape Test - 1` reads. Its reader needs `grab_build.state_of(entry)[0]`
   for the block name: taking the pair for the name reads 0 grass in a world holding 207,359 of them.
4. Read the raise's own line out of the log: `... cut down, ... filled up, ... left for the water, ... left alone, ...
   cleared of a tree standing on nothing, ... inside the walls cut down to the cut, ... of the build's own edge banked
   up, ...`. The last counter is the skirt, and it is the one that says whether this pass did anything at all.
