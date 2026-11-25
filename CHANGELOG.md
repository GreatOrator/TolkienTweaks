# Tolkien Tweaks — Changelog

All notable changes to this project will be documented in this file.

- **The tools read the ground the world stands at, raggedness and all.** `tools/arda_ground.py` is `ArdaMap.groundHeight` in Python: the baked masks the game itself reads, its Catmull-Rom spline between the height pixels, its bilinear cover of the mountain mask, the mask's climb of sixty-four and `ragged`'s two folded octaves. `mask_probe.py` and `arda_scout.py` both answer heights through it now, and the probe prints the terms that made the number - what the drawing has, what the mask lifts, what the ridge noise moves. Two things only a check against the game finds: `ArdaMasks.coverage` reads its masks through `BufferedImage.getRGB`, so a grey pixel comes back sRGB-encoded rather than as the byte in the file (202 reads as 230, worth six blocks of climb), and `ragged`'s hash wraps its 64-bit products, which the port repeats with numpy's own wrap and arithmetic shift. Checked against `ardaSample`: the 81 Hithaeglir columns that read a mean 12 blocks low are 81 of 81 exact, the flank at 6518, -6461 at 266 and the summit at 6878, -6509 at 303, with the rock, the noise and the mapped ground each matching the game to the last digit; a 56-column grid across the Trollshaws is 54 exact, the two off by 12 at 5000, -3000 and 5000, -2800 where the game grades the Bruinen's bank down and the tools do not. Bank grading, the vale under the city, Bree's hill and the Gundabad pass are still not modelled, and a drawn sea still answers the waterline rather than its bed. `docs/arda-masks.md`'s probe paragraph and the two next-up entries it supersedes are corrected with it. **Not seen in a world: compared with the game's sampling task, not in a client.**

- **`tools/arda_scout.py` reads heights the same way `mask_probe` does now: black at 0, white at 318, and the mountain mask's own sixty-four.** It still had the pre-re-base pair - sea 63, crown 318, the sample blended up from sixty-three - and no mask lift at all, so its relief reports were out at both ends: a hill the game stands on at 138 came out at 174, and the summit the game puts at 303 came out at 258, forty-five low because the climb `ArdaMasks.raise` adds was missing from the number and from the shade. It is `game_height(raw, bright)` now, the drawing's span plus the mask's own share of `MASK_GAIN` 64 at the column's brightness, and every caller passes the mask with it: the window's height array, the footprint's own heights, and the candidate's middle. The hillshade steepens by a quarter with the span, and a mountain now shades at all, because its climb is part of the height the light is read from. Read against `ardaSample`: 4608, -3072 is 121 on both; 4352, -3840 is 138 on both; 6878, -6509 is 307 here and 303 there; 4995, -3311 is 117 here and 115 there, the two blocks being the game's own bank grading of the drawn channel. Across 81 columns of the Hithaeglir flank it reads a mean 12 blocks under the game with a spread of -23..+1, and that is `ragged` and nothing else: on land the game adds no other term between the drawing and its answer. `mask_probe.py`'s line calling the discrepancy 'a few blocks either way' is corrected with it, in both files' docstrings. **Not seen in a world: checked against the game's own sampling task and a rendered window, not in a client.**

- **`tools/mask_probe.py` reads heights the way the game does again: black at 0, white at 318, and the mountain mask's own climb on top.** The probe kept the reading from before the re-base - `SEA_LEVEL` 63 and `CROWN` 318 with every sample blended up from sixty-three - so everything it printed stood about fifty blocks high: at 455, 192 it answered 114 where the game answers 64, and at the brightest summit on the map it answered 258 where the game answers 303. It now reads the drawing between `BLACK_Y` 0 and `WHITE_Y` 318 and adds the mask's share of `MASK_GAIN` 64 the way `ArdaMasks.raise` adds it, splitting the answer into what the drawing has and what the mask lifts so a summit's number can be read either way; a column under the drawing's waterline of 25 is named as such rather than called sea, because `ArdaMap` holds a land column up to 25 and only a column with no ground in the drawing is water. Read against the game's own sampling (`ardaSample`, which calls `ArdaMap.sample` with no client): 400, 192 is 64 on both; 400, 792 is 73 on both; 400, 1392 is 58 on both; -200, 192 is 46 here and 47 there; the bright summit at 6878, -6509 is 307 (243 drawn plus 64 lifted) against the game's 303. The last few blocks are `ragged`'s noise, which the probe answers nothing for. `docs/arda-masks.md`'s description of the probe and its one `63` in the ground bullet are corrected with it; the censuses in that file are still the old reading and are left as they are. `tools/arda_scout.py` carries the same stale pair and is not touched here. **Not seen in a world: the numbers were checked against the game's sampling task, not in a client; nothing was read back from a chunk.**

- **The Brandywine stops being called the Lune: the river mask fuses the two rivers into one component, and the bake now divides that component between them.** The component is one network - the Lune's arm running south to the Gulf at -776, 64 and the Baranduin's loop out of Evendim at 650, -4700 coming down through the Shire at 455, -124 and on to the coast at -215, 1812, the two meeting near 400, -700 - and its middle sits 755 blocks from `lune`'s named point, so `tools/arda_river_names.py` gave the whole thing id 4: every column of the Brandywine, the Shire's eastern border and Sarn Ford included, read `river_lune` in the world. A component two rivers share is now divided instead of named by its middle: `SPLIT` lists points along each river's own course and every cell goes to the river whose nearest point is nearer, walked over the component's own cells eight-connected, so the border falls where the two courses actually part at the junction the drawing made. The Lune keeps 2,338 cells, x -1024..368 by z -792..528, and the Brandywine gets 10,647, x -264..1864 by z -4840..1864 - the loop out of Evendim, the Shire, Sarn Ford and the drawn mouth. The Brandywine is id 5, `ArdaMap.waterBiome` answers it with the new `TolkienBiomes.RIVER_BRANDYWINE` (water 0x2E6F72, sky 0x8AB2BC, fog 0xBFD6D8, temperature 0.5, so the tag provider drops it in the temperate list where the Lune's 0.45 sits in the cold one), and the biome is in `ArdaBiomeSource`'s overlays, the water-bed and sand surface rules, the en and zh lang, and the generated dimension list, overworld tag and temperate tag. `docs/arda-teleports.md`'s count of named rivers goes from four to five. **What this does to ground already generated:** nothing already written changes. `riverName` is read in one place, `ArdaBiomeSource.getNoiseBiome`, which is asked while a chunk is being generated and never afterwards, so the biome is settled the first time a chunk is made and kept in its palette for good. In `Generation Redux - 3` that means the whole Shire stretch of the Brandywine, Sarn Ford and the 274 already-generated chunks of the new reach down to z 1,247 still read `river_lune` - they keep the Lune's name and its water colour until those chunks are generated again, and a chunk generated again comes back `river_brandywine`, name, colour and bed together. The name reaches the world only where the ground is new: the 294 chunks of the new reach south of z 1,248 take it on their first generation, and so does the Lune's arm wherever it is generated from now on. **Not seen in a world, and one thing only the game can answer:** compiled only, so no column has been read back to see the name; and adding a registry entry is the one mechanism that could move the numeric ids a saved chunk carries, which no reading of the files here can settle - the first load of `Generation Redux - 3` should say `tolkienmobs:shire` over the Shire and `tolkienmobs:river_lune` over its Brandywine water, and anything else there is the registry's order rather than the mask.

- **The Brandywine is drawn to the sea: the reach that stopped inside the Shire now runs past Sarn Ford and south-west across Minhiriath to the coast north of the Greyflood's mouth.** The reference river layer's line ends at z 192 - 255 through z 190, 46 at 195 and nothing from 200 - which is why the water the owner stood over at 455, 195 stops dead against a dry bank, while the canon map in `Reference/middleearth.jpeg` (Jungels v7) carries the Baranduin on from Evendim past Sarn Ford at the Greenway, south-west through Minhiriath and into the sea just north of Eryn Vorn and north of the Greyflood's mouth at Lond Daer. That reach is now drawn on `ME_SimpleTopoTerrain_Rivers_albedo.png`: from the existing stroke's taper at 456, 160 through 415, 600 (Sarn Ford), 335, 900, 250, 1180, 180, 1420, 70, 1660 and 0, 1740 to a mouth at -215, 1812 - 1,780 blocks long, opening from the drawn channel's 50 blocks to a 100-block estuary - and the ground under it falls from 63 at the join to 26 at -175, 1815 and 19 at the mouth, under the sea's own 25, so the shore meets it. The line was drawn anti-aliased in canvas pixels the way the existing stroke is, at four subsamples, so the bank falls where the line was drawn; the pre-extension reference is kept at `Reference/work/rivers-before-brandywine.png`. **The bake:** `rivers.png` gains 1,969 water cells at the bake's 77, at x -264..480 by z 144..1864, and loses none - 2,168 cells of the asset change and not one cell has its water taken away. Only those cells were written, because the committed masks are behind the references they were cut from: a fresh bake of the same pre-extension reference differs from HEAD in 43,329 river cells, `lakes.png` in 2,820, and `height.png` in 2,866,503 of its 7,077,888 pixels, so a whole `tools/arda_masks.py` run would have carried unrelated ground into this commit; `lakes.png`, `mountains.png`, `height.png` and `bruinen.png` were restored to their committed bytes. `river-names.png` is deliberately not rebaked: the extension joins the one component that already carries the Lune, and its centre moves from 234 to 755 blocks from `lune`'s named point - still inside `REACH` 1500 - so a rebake would hand the Brandywine `river_lune` for its whole new length. Left as it is, the new water answers id 0 and the game's generic river biome while the Evendim to Shire stretch still reads `river_lune`; a name of its own (id 5, with `waterBiome`'s switch to match) is the next job. **What ground has to be regenerated to see it:** the new water touches 568 chunks, x 192..479 by z 144..1863. Of those, 274 are already generated in `Generation Redux - 3` - every chunk of the reach from z 144 to 1,247, in regions `r.0.0`, `r.0.1` and `r.0.2` - and they keep the dry ground they were made with, bed and banks included, because a channel is laid inside the raise and nothing retrofits one. The 294 chunks south of z 1,248 have never been generated and will draw the river on first visit. The banks are graded twelve blocks out and the river's level is read twenty-four, so one chunk either side is also affected: 1,206 chunks in all, 622 of them already generated. **Not seen in a world: the line is drawn and baked and no chunk has been generated from it, so the water in the owner's world still stops at 455, 195 until those chunks are made again.**

- **The village's worn ground is repaired across the whole of it, because the wear is the village's own and the village walks all of it.** `/hobbiton heal` asked for a circle of sixty-four blocks round the caller, and the world says what such a circle is worth: read out of `Generation Redux - 3` against the jar's `hobbiton` cut, 5,450 columns of the footprint hold plain `dirt` where `minecraft:grass_block` is drawn for them at the same height with nothing over it - all of them inside the cut, so the repair can see every one - and 5,446 of them lie further than sixty-four blocks from the spot behind the muster hall the screenshots are taken at, where the hand would stand. A command asked for there settles the four columns at its feet and leaves the green round them bare, and asked again from the next yard it settles a few more, while the village is two hundred and forty blocks of walking. The radius never bought a cheaper pass either: the cut's grid is 551 by 468 columns (257,868) and `Job` reads its grass height in every one of them before it writes anything, so `Dressing.repair` now takes no centre and `Job.heal` walks the whole grid - every column the world has worn where the cut draws grass, with nothing solid or wet over it, gets its grass back in one ask. The same reading says what the patchwork in the picture is not. The 25 bare columns under the camera, x -520..-478 and z -44..-18, all lie past the cut's own southern edge at z -45, outside the footprint, where the cut draws nothing and the repair never looks; and untouched country carries the same ground, 1.1% bare at x -820..-760 and 1.7% at x -520..-460 z -10..50, against the fringe's 3.4% - so the brown there is the Shire's own mottled top, with the extra at the fringe being the village's folk walking past its edge, which no cut covers. **Not seen in a world: compiled only; the widened pass has not been run on `Generation Redux - 3`, so the 5,450 columns still read as dirt and no column count has been read back after a run.**

- **The drape learns to put the village's own ground back where its folk have worn it to dirt, by a hand and never by itself.** The ground behind Hobbiton's muster hall reads as patchwork in the owner's screenshot - green terraces with scattered single blocks of bare dirt - and the ground read out of `Generation Redux - 3` says what it is: of the 595 columns at x -515..-480, z -55..-38, 385 are inside the cut and every one of them stands at the cut's own height, not one differs, 210 more are the country north of the footprint, and 68 hold plain `dirt` where `hobbiton_4_0_9` draws `grass_block` - one block swapped at the same y with nothing above it. It is not the hall, pinned at -529, 47, -74, whose 16x15x16 box ends five blocks short of the view; not the clear, which takes the country off above the cut and out of the cut's own hollows and writes no ground; and not `Dressing.dressInside`, which cuts what stands over the cut's own top and never writes at or below it, the rule that keeps a wall, a floor and a roof whole. It is the village's own folk: `Hobbiton`'s note beside `PROBES` has said since it was written that a body of the village walking turns a grass block to dirt, and 68 singles in a trail is a fortnight of hobbits crossing a yard. So `Dressing.repair` is the one repair that can be made without guessing: the cut is read for the height of its own grass in each column (`crownY`, by the palette name through `Dressing.grass`, so no registry read and no second idea of what the cut draws), and over the columns within 64 blocks of the caller a column whose world holds `dirt` or `coarse_dirt` at that height, with nothing solid or wet over it, gets its grass back. A floor laid at the cut's height is another block and is left, a garden the cut draws as dirt is not grass and is left, and a hole under the cut stays a hole. It is `/hobbiton heal`, asked for by a hand and run on the tick it is asked for; nothing runs it on a clock, because a repair that watches a village for a living is idle work in a world nobody is looking at, and the wear returns as soon as the folk walk it again. **Not seen in a world: compiled only, and the command has not been run against the save the ground was read from - the 68 columns behind the hall still hold their dirt until it is.**

- **The Bucklebury bridge is the span on the road the owner walked from Bree to the Shire.** The world says where it crosses: `Generation Redux - 3`'s plan store holds `planned_480_-6_381_-5_bridge_1`, a span from bank 456 to far 420 on z -49 at deck 65 (`bridgeBase` 63, `endBase` 64, `half` 4), planned with `bridge_end`/`bridge_middle` - and the standard pair is what stands there: oak planks at 65 across the channel, water at 60 to 62, piers drawn down. `Roads.BRIDGES` now names Bucklebury at 438, -49, that channel's middle block, with `bucklebury_end`/`bucklebury_middle` and `cobblestone_wall` as the support, because the pair's piers are cobblestone walls and the parapet above them is stone brick. Naming is what a span is stood with from here on: `pieces` asks the table before the section's own stored names, and the sweep before the pieces clears the five standard woods, so a pass over that span clears the oak pair and stands the stone one. **Nothing lifts the bridge already standing:** it is re-stood by `/tmroad build` with the caller at the crossing, which runs one pass over that section - and this span owes no note of its own, so a pass is planned for it. The pair was drawn for the Brandywine's fifty-four columns (17 + 20 + 17) and this crossing is thirty-seven (456 to 420), so the plan lays both ends and one middle and seventeen of that middle's columns stand inside the far abutment: the deck row is 65 either way, so the span stands, but it is not the fit the crossing it was drawn for had. **Not seen in a world: compiled only; the standing oak bridge has not been re-stood, and no span has been stood from the pair.**

- **The road from Bree to the Shire is in the network, recorded from the plan the owner walked it with.** He laid it himself on 25 November 2026 and the world's `road_plans` store holds the three stretches he planned: `planned_1031_-34_480_-6`, from Bree's hall front at 1031, -34 down to 480, -6 at the town's bands, whose own note records it laid from 1031, 76, -34 with 587 stations of dirt path; `planned_480_-6_381_-5`, the middle stretch that meets the Brandywine - dry to 472, -49, the span to 420, -49, and dry again to 381, -5; and `planned_381_-5_-149_-382`, which bends at 133, 60, -182 and arrives on the village's east side, so the whole leg runs 1031, -34 to -149, -382. All three are `Roads.NETWORK` entries now, in the order he walked them and with the kinds he gave them (the town's bands on the first, the country's three wide on the others), so a fresh world plans the same three stretches under the same ids. `Roads.TOWNS` names Bree's hall front at 1031, -34 under the first and the village gate at -149, -382 under the last - the two legs that table lost on the same day, named again because a road is cut into sections and only the section that reaches a town is graded on its ground. The middle stretch meets no town and is not in it. **`NETWORK_OPEN` is still off**, so this record lays nothing in any world today and his own world keeps the road he built; it is what a world made after the survey is reopened plans. **Not seen in a world: compiled only; no fresh world has planned the three stretches, and the ids a plan gives its own sections are the drawing's answer rather than the store's.**

- **The Last Bridge's pieces are in the table, and the crossing is all that is still missing.** The owner drew and added the pair on 25 November 2026 - `last_end` and `last_middle`, in `structure/bridges` beside the standard pair - for the Last Bridge, where the East-West Road crosses the Mitheithel: the abutment seventeen long and twelve high on its own bank, the span twenty, both nine columns across as `bridgePlan` needs, the deck on the same rows as Bucklebury's (the end's row 1 over the middles' row 2, which is the span's deck), and the piers `wall_cobbled_dwarven_stone` under a `wall_dwarven_stone_bricks` parapet - cobbled dwarven stone where the reservation note had guessed stone bricks, which is the art's answer and the block the entry names as `support`. `Roads.BRIDGES` now holds one entry, `Last Bridge`, with those two pieces and that support, and no place: the block on the water is what he still has to name, so the entry's `at` is null, `Roads.bridge` skips it and every span still stands from the standard pair. Naming the crossing is one block in that line, not a whole entry. The table is read the first time a span asks for it rather than at class load, because the support is a block this mod registers and a static initialiser is not guaranteed to run after the registry is full. The captures were cleaned before they were shipped - `tools/bridge_pieces.py` drops every cell that is not the bridge, 1346 of 1836 in the end and 610 of 1080 in the middle - because a listed air cell is placed, and a piece that cleared its own box would take the water under the span with it. The reserved names the notes had written down, `last_bridge_end`/`last_bridge_middle`, are not what the files are called: a piece is named by its leaf name, so the entry uses the names in the jar. **Not seen in a world: compiled only. Nothing has been stood from either piece, so no world holds a bridge that has to be lifted, and the pair will not be stood at all until its crossing is named - every span answers the standard pair until then.**

- **The two long roads are out of the network: nothing between the Shire and Bree, and nothing between Bree and Rivendell.** The owner's word on 25 November 2026, moving on from Hobbiton to the next build: he is laying that road himself, so the mod must not stand a second line beside his. `Roads.NETWORK` keeps the hold's road and the nine vale legs, and no longer holds `planned_1032_-34_-150_-382` (walked in `Progress Check 3`, five wide, Bree's hall front at 1032, -34 to the village's east side at -150, -382) or `planned_4648_-3316_1031_-36` (marked in `Progress Check 4`, five wide, the vale's junction at 4648, -3316 to the same hall front at 1031, -36); `Roads.TOWNS`, which named those two legs' gates, is empty. The ends are written down beside the table rather than the entries left behind `NETWORK_OPEN`, because that flag only decides whether the table is read - a session that opened the survey again after Rivendell's site is re-scouted would put both lines back on the ground he is building over - and an id is a road's two ends, so a leg walked again later is a new entry under a new pair. The four bridge pieces the two legs were to use stay in the jar for his road (`bucklebury_*`, `last_bridge_*`), and `Roads.BRIDGES` still names none. **Not seen in a world, and this changes nothing in the signed-off world:** the arda region of `Generation Redux - 3` holds no road on either line - the points probed read grass and drawn trees, and the line's Bree end is not generated - and its `road_plans` store is empty, so no world here had either leg to lose. What no table can take back is a road a save has already laid: nothing in the mod lifts one, a plan a hand wrote under either id is still restored by `RoadPlans` at every start, `/tmroad forget` standing on the line drops it, and the blocks it laid stay as ground.

- **The Arda re-base is signed off in a world: the drawing's ground, its water and its river joins all read right in a fresh world.** The owner fully quit the client, made a fresh world on 25 November 2026, and reports the rivers and the drape as they were, the Shire's party tree standing, and no country plinth left under a drawn crown in the fields - "perfection, issues cleared". That world closes the "not seen in a world" notes on the terrain bullets below it: the black 0, white 318, water 25 reading of the drawing and the flat tops it removed; the water lying over the Shire's fields and the fill and seal that stopped it; Arda's own noise settings; the village seven lower; the re-based pins and marks; the narrowed rivers and their gentler valleys; the mixed beds and banks; the ring's join to the river and the spring exception; the `whole` outline that kept the party tree; and the clear's own-ground rule. **Still unlooked at:** caves and the lava line under the new water table, Bree's join and Bree-hill, the camp pins, and the Shire's four edge columns with a drawn channel in them. A world raised before these fixes keeps its faults; `Generation - Redux 2` is that reference and the fresh world is the good one.

- **The clear no longer leaves the country standing under a tree: a tree is kept whole only where it stands on the build's own ground.** The newest world (`Generation - Redux 2`, raised from the classes that hold both earlier fixes) came up with a plinth of the country under every drawn crown in the Shire's fields. At -260, -216 the `hobbiton_3_0_8` piece draws rock to 41, dirt to 46, a brown mushroom block on 47 and one birch leaf at 70, and the world held all of that and then the country's own stone 48..63, dirt 64..67, grass 68, short grass 69 and a birch crown on top - twenty-two blocks of the hill left standing from the drawn ground to the drawn canopy, with the country's tree on it. `CutCommands.standsOnOwnGround` is why: it kept a column whose top is a tree when the block the tree stands on is at or under the cut's own top, and a column's top is a canopy wherever the build draws a tree, so the country's hill under that canopy answered the test and the whole column was left - both halves of the clear skipped at once. It now asks the cut's shape for the block the tree stands on: it has to be one the cut places at or under the top of the cut's own ground in that column (`Shape.foot`, the tree-excluded height that the clear had nowhere to read before), so the drawn party tree's grass keeps its column and the country's grass over the village does not. Read off that save, 837 columns in 72 clusters fail the new test - the plinths in the fields - and are cleared, while 11,309 tree-top columns still pass it (the drawn trees, and country trees grown on the village's own ground). **Seen in a world on 25 November 2026: a fresh world, made after fully quitting the client, came up with the fields clear - no country plinth under a drawn crown - which is the report the re-base was signed off on. A world raised before the fix keeps its plinths, because the clear runs inside the raise and nothing repairs one already raised.**

- **The ring's spring exception is gone: a column standing in the drawing's water is left alone whoever holds it up, so the river along the Shire's edge is not dressed as a pool.** The water guard added one turn ago exempted a "spring", because a spring is the build's own water and the mountain over its pool is the drape's to take. The exemption is a hole in the guard, and the river sits in it: `Dressing.dressColumn` reads its shape from the nearest wall column, so where the drawing's river runs along the footprint's edge that column answers with the river's own water - the column is `channel`, its `watered` is ten blocks over the village floor, which is past `OUTLET_LIFT`, so it is not the outflow and is taken for a spring - and every ring column beside it was then dressed: cut down to the pool's level and capped with the ground beneath it, which is the river's water taken out and a bank laid over the hole. The water test now runs first with no exemption, so a column whose top, or the block over its top, is a fluid is skipped whatever `spring` says; the build's own pool is untouched by the ring because it is inside the walls, which the ring never cuts, and a spring's land columns are unchanged - the mountain over a pool still comes off, and the cut still stops at the pool. **Seen in a world on 25 November 2026, together with the two fixes before it: the fresh world's edge keeps the drawn channel as the drawing has it and the fields beside it are dry. (The world these were found in was dressed by classes from before any of them: that session started at 15:29:36 on 24 November and `Dressing` was compiled at 15:56:20.)**

- **A build's own trees are the build, and the drape's inside cut no longer takes the Shire's party tree for the country.** The newest save answers it block by block: at -514, -100 the `hobbiton_3_0_8` piece draws dirt to 44, an oak trunk on 45 to 48 and a leaf at 49, and the world holds the dirt at 44 and nothing over it - so the party tree stood as sawn-off poles with its jack-o'-lanterns and cobwebs hanging where the crown had been, and every other drawn tree in the footprint went the same way. The clear is innocent and was already fixed: its shape is the cut's own blocks, a drawn tree is in it, and `standsOnOwnGround` leaves a tree standing on the build's own ground whole. The pass that took it was the drape's inside cut, which reads the same outline the ring reads - and that outline leaves trees out on purpose, because a ring filled up to the leaves stands a wall of stone beside every tree. So a party-tree column read as the course of grass at its foot, `dressInside` saw the country five blocks over the village there, and cut from the crown down to the grass. The job now keeps a second outline, `whole`, taken with the trees in (every block is written to it before the tree skip) and filled through the same `holes` pass as the ring's, and the inside cut asks that one: a drawn tree is the build's own top, while a hill genuinely standing over the village is still taken off down to the build's crown. The ring, the clear and `Drape` are untouched - `Drape` has always read its outline with the trees in (`Sites` passes false, so its inside cut never met the case). **Seen in a world on 25 November 2026: the party tree stands in a fresh world, crown and lanterns where the cut draws them. A tree a world has already lost stays lost - nothing plants it back - and `DRESS_VERSION` stays 2, because re-raising the village to reach the fix would lay and clear the cut again over whatever a hand has done inside.**

- **The drape no longer carves the river it crosses: a column standing in the drawing's water is left exactly as it stands.** The newest save has the river at the Shire's north-west carrying a straight trench walled with stone, its water three blocks lower, running through the water where the drape's ring meets it. It is `Dressing.dressColumn`: a height that counts fluids answers a river column with the water's own surface, so the ground search under it walked down to the bed as it would under a puddle, and the column was then cut and filled against the nearest wall's height like any land - the water taken out down to the cap, the bed laid back over the hole, the bank filled with stone. The ring reaches thirty-two blocks past the build and the Shire's north-west corner is the river, so the join was dug through the water. A column with water for its top, or in the block over its top, is now left exactly as it stands and counted with the columns left alone - the rule `Drape` has always kept through `wet` - while a spring the build itself holds up is still dressed, because that water is the build's and the mountain over its pool is the drape's to take. What it costs is a one-block puddle at the ring's edge being left as it stands instead of drained to the ground, which is worth it not to cut a channel through a river. **Seen in a world on 25 November 2026: the rivers are back to normal in a fresh world, the ring meeting the water instead of cutting a trench through it. A trench a save already holds stays in its blocks.**

- **Arda has its own noise settings now: the overworld's record with the sea level read as the drawing's twenty-five, so the aquifer and the carvers stop filling ground the drawing has as land.** The flood the last save showed was the vanilla aquifer's water table - sixty-three, the overworld's sea - left in the noise's voids under a Shire that stands at forty to sixty-two, and the previous fix plugged it after the fact. `TolkienDimensions.bootstrapNoise` now registers `tolkienmobs:arda`: `NoiseGeneratorSettings.overworld(context, false, false)`, the vanilla factory itself, so the router, the noise settings, the stone and water, the surface rule, the spawn target and the aquifer, ore-vein, mob and legacy-random switches are the overworld's exactly, rebuilt into a record with `ArdaMap.SEA_LEVEL` 25 in `sea_level`; the level stem points at it, and data generation writes the whole thing out as `data/tolkienmobs/worldgen/noise_settings/arda.json` (111,529 bytes, `sea_level` 25 at line 392) with `dimension/arda.json` reading `"settings": "tolkienmobs:arda"`. Read at data generation and shipped as data on purpose: the overworld's record cannot be read while the patch is being built - `context.lookup(...).getOrThrow(...).value()` threw out of `RegistrySetBuilder$RegistryStub.apply` on the first attempt - and the factory needs only holders, so the shipped entry is complete and nothing reads `minecraft:overworld` at run time. **What it changes for caves and deep lava: the water table is the drawing's.** A cave the carvers open in the drawing's land is now air rather than water, so the Shire's caves no longer fill to sixty-three; water in a carving appears only under twenty-five, which is under the sea and the sea's own beds, and the aquifer's own level-dependent noise (floodedness, barrier) reads twenty-five with it. Lava is untouched and deliberately so: its status stands at y -54 in `min(-54, seaLevel)`, which the drawing never moves, so carved rock below -54 opens onto lava exactly as it did, and no drawn ground is that low (`BLACK_Y` is 0). The reshape and seal plugs stay as the backstop they now are: a drawn column whose ground is under twenty-five is still the aquifer's water where the noise had a void, and a world made before this keeps `minecraft:overworld` in its `level.dat`, so those plugs are what keeps that world dry. **Not seen in a world: data generation ran to BUILD SUCCESSFUL and `compileJava` exits 0; no chunk has been generated with these settings, so neither a dry Shire cave nor a lava pocket has been looked at.**

- **The water lying over the Shire was the noise's own aquifer, whose water table is the overworld's sixty-three, and the reshape stepped over it: every fluid under the drawn top is rock now, and the drawn top is laid back after the carvers.** The newest save answers it column by column - over x -1200..200 by z -900..-200, 304,863 of 442,112 columns carried water where the drawing has land, and in the 163,072-column box inside that, 81,724 columns had water for a top block and 36,012 more had it buried under their ground: water tops of 44 to 62 in the Shire's own range of ground heights, 54,593 of them at exactly 62, which is one under the overworld's sea level of sixty-three and not a level the drawing has. Arda reuses `minecraft:overworld`'s noise settings, so the aquifer fills every void the noise leaves under y 63, and `reshape`'s fill under the drawn top wrote stone over air only: a column whose drawn top was aquifer water is not air, so the fill walked past it, the surface rules had nothing to dress, and the drawing's ground arrived as a sheet of water terraced at its own contours. The fill now writes stone over every fluid under the drawn top as well, and the map's own water is laid over that afterwards as it always was. `seal`, which runs after the carvers, lays the drawn top back down to the first rock under it (up to `SEAL_DEPTH`) for every column, since the carvers cut after the ground was drawn and the aquifer fills what they take out with the same sixty-three again - so a cave cannot open through a dry column's surface and stand there as water - and the plug under a drawn channel's bed now fills fluid as well as air, so a cave under a river cannot drain into it. The drawn top is read through `topAt`, so a road cut into a hillside is not given a lid. **Not seen in a world: compiled only, and no chunk has been generated with the fill in. The chunks of a save made before this keep their water - the sheet is in the blocks already written and nothing lifts it - so this needs a new world. Caves below the drawn ground are still filled to the aquifer's own sixty-three where a carver opened them, which reads as underground water and is not looked at; if that is wanted gone as well, the water table itself has to be Arda's twenty-five rather than the overworld's.**

- **The Shire's village comes down seven, and its water with it, because the cut stood seven over the ground it was cut into.** Walked in the newest save: the country round the footprint reads 47 to 51, while the village's grass line - sixteen over the pin - stood on 56 with its own water nine over the pin at 49, two blocks over the ground at the edge of the cut and free to run out over the fields. `HOBBITON_Y` is 33 and not 40, so the grass line is 49 and the village's water is 42: five under the fields rather than over them, and the build on the ground the village was walked on. The marks that are columns of the village went down with the cut - the milestone at -503 to 47, the crossing the portal arrives at to 43, the `hobbiton` pin to 44 and `hall:hobbiton` to 47 - and the branch's grade is 90 rather than 40, because the arm now leaves the village at 42 and the drawn channel it runs out to reads 25 at the fork 650 blocks on: at forty it would have arrived nine under the river it joins, a waterfall where the drawing has a confluence. `Marks.VISITED`'s Shire point stays at 67, the drawing's ground of its own column plus one. **Not seen in a world: the heights were read off the save that showed the fault and off the drawing, and no world has been generated at them; the water left on the fields beside the drawing's own channels has not been judged.**

- **The heights still written against the old scale are swept: the walked marks and the Trollshaws jump are re-read off the drawing, and Moria, the gate's own passage and the orc pieces are shown to still belong where they are.** `Marks` is the block a caller is put down on, so a mark above its ground is a `/tmtp` into the air and a board measuring to the wrong doorstep: the Shire's is 67 (the village's own frame, and what the drawing reads there), Thorenhad 117, Barachen's fire 120, the wood ruin 121, the three crags 115, 117 and 116, Matuna's lair 122, Gloin's camp 180 and the goblin fires 145 - was 114, 158, 159, 160, 155, 157, 157, 161, 224 and 196 - each read as the drawn ground of its own column, one over it. `TeleportCommands.LOCKED`'s Trollshaws point is 119, not 87, because a locked point is used as given and has to be a block a caller can stand in. Left where they were, with the measurement: Moria's floor 14 and roof 40 are a depth band in the rock rather than a reading of the country - sampled over the whole 5120..6144 by -4096..-2304 rectangle at 128 blocks, the ground runs 108 to 225 and not one column is under 41, so the chamber is in stone everywhere and the world's re-base moves no bedrock; Gundabad's own numbers (gate 187, keep 176, pass mouth 176, the door screen at 190..199) still agree with the drawing at 186 to 193 and 175, and the orc pieces' pins at 185 to 189 stand on the pass's carved floor of 187, which is the same floor they were scouted on; `Carrock.CORNER` is where the cut was taken from in the old overworld and places nothing; the Gundabad mark is the gate's ground and the goblin's is the country's. **Not seen in a world: compiled and sampled off the game. One chair is left unfixed on purpose: `Campaign.CASTLE_SEAT`, the Witch-king's seat, reads 13 blocks over the drawn ground at 3850, -7425, but it is a block inside a build that `Sites` seats by survey in every world - its x and z move with the castle as surely as its y - so no number read off the drawing puts it back on the chair. It wants either a walk of the new seat or a seat held as an offset from the castle's own corner, and the last act is not playable until then.**

- **The builds are re-based to the raised drawing - both cut villages come down forty-seven, the Carrock to the river it stands in, and every camp pin is read off the country instead of off the old world.** Nothing here was scouted by hand: the drawing is the ground now, so `tools/sampleprobe` answers any site off the game. The Shire was cut at 87 and plants at 40 - over its 551 by 468 footprint, 1,050 columns at 16 blocks, the drawing reads 36 to 71, a median of 56, so the village's grass line, sixteen over the cut's low corner, stands on 56 where it stood on 103. Its own marks moved by the cut's 47 - the milestone at -503 to 54, the crossing the Arda portal arrives at to 50, the `hobbiton` pin to 51 and `hall:hobbiton` to 54 - and the branch the village carries out to the drawn river goes down with it, because its water is `HOBBITON_Y + 9` and not a number of its own. Bree plants at 71 instead of 117: its site reads 48 to 140, a median of 76 with the tenth to ninetieth centiles at 62 to 82, so its grass line, five over the marker course, stands on 76, and `bree` and `hall:bree` came down the town's 46 to 77. The Carrock's pin is 156, not 187 - 24 of the 66 columns sampled across its 42 by 85 box are the drawn channel, its water at 154 and 155 and its bed 143 to 153, so the rock's low corner stands one course over the river it was cut out of. The goblin fires' door is 146 (ground 145) and the shelter camp's corner, one of the two buildings whose pin *is* its corner and so its base, is 143. Gundabad needed nothing: the gate's floor is 187 against 186 to 193 along its facade and the pass mouth 175 against 176. The campaign's camp pins were re-read the same way, because a pin above its ground is a keeper who never stands and a `/tmtp` that arrives under the country: Thorenhad 119, Barachen's fire 120, the ford 118, Elladan's camp 117, the wood ruin 121, Matuna's crag 122, the three crags 112, 116 and 115, Gloin's camp 181 with its end at 180, and the seven `.end`, `.body` and `.keeper` marks on the same ground. Rivendell's departure pins are untouched, the city and its plaza being off. **Not seen in a world: compiled and sampled off the game; no village, town, camp or rock has been generated at these heights. The figures are the drawing's own, and two sites are worth judging first - Bree carries a 140-block knob inside its footprint that the drape will cut to the roofline, and four columns of the Shire's footprint have a drawn channel in them at its edge.**

- **A land country's ground is held at the waterline, so a country is dry wherever no channel is drawn on it.** The reference's sea is painted black, and the soft edge of that black runs inland under cells the country grid still calls land; read through the drawing's ends those columns stand at nought to six blocks, under the world's own water, so the reshape laid twenty-five blocks of the sea over country the map had just called Shire - a flooded region with no channel in it and water twenty blocks over the ground. `ArdaMap.readDrawn` now holds every column of a land country at `SEA_LEVEL`, after the bank, the lake's shore and the valley's flank have all been graded, because the grading is what cuts under it and a land column carries the mask's rivers and lakes and nothing else. Measured with `tools/sampleprobe` over x -1200..300, z -800..400 at 32 blocks: 79 of 1,786 columns stood under the waterline and every one of them flooded; 10 stand under it now, each a drawn channel's own bed. Over the whole frame at 256 blocks, 48 land columns stand under 25 and none of them is without a channel on it. **Not seen in a world: compiled and sampled off the game. The world this was reported from answers y122 at Hobbiton where the current map answers y55, so its flooded chunks are an older terrain as well - a new world is what shows this.**



- **A drawn river is narrowed where the country is steep, allowed to wander where it is flat, and its valley's climb is read from the country again.** `ArdaMasks.river` narrowed a channel by a sixth of the drawn width on each bank - the same sixth down a gorge and across a floodplain, so every river in the world read at one width. The inset is now the drawing's sixth plus up to `STEEP_CUT` ten blocks a side where the ground falls, less up to `MEANDER` nine of that sixth where it does not: `steepness` reads the height raster over `SLOPE_RUN` eight blocks in the two directions a bank is measured along and holds the fall at nothing to one, full at `SLOPE_STEEP` three tenths of a block a block. The two banks are cut on their own, each from its own read of a 128-block noise, so the water's edge bulges and draws in instead of running parallel to the drawn line - at most down to one block inside the drawing, never past it, so a channel is still the mask's channel and no field is flooded. Sampled over 120,000 drawn channel cells, the fall reads p25 0.08, p50 0.15, p75 0.27 and p90 0.42: 32.5% of the channel's length takes the whole wander and 1.9% the whole gorge, worth about 4 blocks narrower on a 40-block channel through steep country and 7 wider across flat. And the valley's own climb was pinned: `ArdaMap.riverDepth` read `ArdaMasks.ground` raw there - a sixteen-bit raster value used as a block height - so the country always came out hundreds of blocks over the water and `rise` sat on `RIVER_STEEP_MAX` sixteen on every channel in the world, two blocks of climb per block of run. Read through the drawing's ends, a river five blocks under its country gradients at three a cell and one two hundred under it at nine. **Not seen in a world: compiled only, no river has been generated with either change. The width and the wander are the mask's; the meander does not move a channel's course, only the width of the water inside the drawn one, so a drawn straight reach is still straight at its middle. The gentler climb is the larger change and was not judged: every drawn river's valley is now shallower and wider than the one that stood there before, because the sixteen it was pinned to was never read from the ground.**



- **The beds under the water are mixed and no longer one depth, and the reeds stand beside the cane.** Three things left the drawing's water reading as a plate. A sea's floor was its country's own height plus five blocks of relief either way, so a whole bay was one depth from shore to shore; it now rolls on two octaves of the map's noise (`seabed`, six blocks over ninety-six and twenty-seven) held two under the waterline, so the floor cannot break the surface. A lake's bed was the coverage's own dome, cut to a depth per column and the same dome in every lake; `lakebed` adds four more blocks of roll at forty-eight and seventeen, the rim left to the coverage's ramp. And both beds and both banks were answered by the surface rules, which know only the biome: every channel came out in the same sand and every bank in the grass of its country. `dressWater` now lays the owner's two palettes per column - the bed mostly coarse dirt with cobble, mossy cobble, gravel, podzol and stone at the counts they gave (90/5/5/5/10/21/1/3 of 140), the bank coarse dirt and grass with gravel, stone, andesite and the odd block of dead coral (30/4/15/3/1/1/1/1/1/1 of 58) - drawn from a hash of the block's own coordinates, so the pattern belongs to the place and no seam runs down a chunk border, with the block under the top taking a second draw half the time. A sea's bed is dressed and its shore is not, and only a dry column of a land country with water in one of its four neighbours counts as a bank, so a drawn beach keeps its sand. The pass reads an eighteen by eighteen ring of columns a chunk - one past each edge, so a shore does not step at every sixteenth block - and that is its cost: 324 more height samples a chunk than before. The cane already stood at rivers and lakes (`PLACED_SHORE_REEDS`, twelve a chunk); `PLACED_RIVER_REEDS` now stands the mod's own reed among it at eight a chunk, with the same two tests - not a water column, and the reed itself would survive - and the water biome list carries both, so all ten water biomes get both. **Not seen in a world: compiled and the data generated (`river_reeds.json` and the ten biomes' feature lists), but no chunk has been made with the palettes or the rolling beds in it, so neither the mix nor the depth is looked at. The reed is one block tall because `plants/reeds` is a one-block column, so a stand of cane beside it will read taller; and the bank palette stops three blocks over the water, which is a number read off the drawing rather than chosen at a shore.**



- **The drawing is read with its black at y0, its white at y318 and its water at 25.** Two numbers decided what the map meant and neither was written down: `ArdaMap` read the height raster from `SEA_LEVEL` (63) up to the city's crown (318), and `ArdaMasks.lift` then blended the ground *toward* that crown in proportion to the mountain mask - `h + mask x (318 - h)` - so as the mask reached white the drawing's own relief was multiplied out and every bright column landed on the same block, one under a 384-tall world's sky. Read off the baked rasters with that formula, 1.75% of the world's land stood within three blocks of 318 and p99 of the ground was 317, while the heightmap alone tops at 267 and piles up nowhere: the flat tops were the mask, not the drawing. The ends are now named - `BLACK_Y` 0, `WHITE_Y` 318 - and `SEA_LEVEL` is 25, the black, white and water the map was proofed at in a terrain editor; the mask now `raise`s the ground by its own brightness instead of blending it (`ArdaMasks.raise`, `MASK_GAIN` 64 blocks, which is the climb the drawing's own top of 254 needs to reach the sky), so relief survives the climb. The same formula now answers p50 126, p90 192, p99 271, max 318, with 0.001% of land within three blocks of the sky. The three drawn sea beds follow the water (`SEA_LEVEL - 27 / -25 / -23` instead of 36/38/40) and Nurn's and Umbar's shore bands are `ArdaMap.SEA_LEVEL + 5` instead of a hardcoded 68. **Not seen in a world: compiled only; no world has been generated at the new waterline, so the coastline and the ranges are predicted from the rasters rather than looked at. A world made before this keeps its sea at sixty-three and its flat tops in every chunk already generated - this needs a new world, and `Scounting Rivendell`'s arda region cleared again if that save is the one used. The mountain mask's bake is unchanged (still eight blocks to the pixel, still blurred), and the rivers are untouched: a channel's width is still whatever the mask draws.**

- **The vale no longer pulls the rivers down.** Two rules still read the city's own floor with its ground off, and they are what the scouting save showed: `ArdaMap.vale(...)` - the blend that brings the country to the city's level - is also where every drawn channel's own level is read (`channelAt`), so a river within `VALLEY_VALE` of the old massif was still pulled to `VALLEY_FLOOR` 74 and climbed back out either side; and the dale, a 2,000-block follow of the drawn Bruinen out from the ford (`ArdaMasks.buildDale`), held that channel at `VALLEY_FLOOR + DALE_DEPTH` with `daleWater` while `daleBank` graded 256 blocks of ground to it - the large circular depression and the terraced walls. Both sat outside `PLANTED`. `vale` now answers the drawing's height when the city is off, and the dale run is not built, so `daleWater` falls back to the channel's own level and `daleBank` answers nothing: no channel is dropped to 74 and no bank is cut to it. The massif, the plaza, the raise, the stone and the roads are untouched by this and stay off. **Not seen in a world: compiled only. Chunks generated before this keep the canyon they were made with - the arda region of the scouting save was cleared for regeneration, and the band generated from here is the drawing's.**

- **Nothing of the city's own is drawn at the vale until its site is chosen.** The valley was laid by `ArdaMap.PLANTED`, but three passes belonged to the city rather than to the drawing and kept working with it off. The staging plaza (`Plaza`, centred 5117, -3409, twenty-seven blocks across) is drawn into the height, the surface and the chunk-load clearing like a road, so a world made after the switch still cut a levelled disc with a trodden floor into the vale; `CityCommands.rouse` reads the pin this world's pins file holds rather than the source table, so a world scouted before the pin was removed still raised the cut from the old corner; and `Sites.STANDING` still stood the city's milestone from `rivendell.stone`. All three now read `ArdaMap.planted()`, the same switch as the massif: the plaza answers no height, surfaces nothing and clears nothing, `rouse` forgets the raise instead of laying a band, and the standing stone table is empty. The plaza's numbers, the pins table's other entries and the cut are untouched, and each goes back on with the new site. **Not seen in a world: compiled only. A world whose chunks were already generated keeps the disc, the massif and the branch in those chunks - no code lifts blocks already made; chunks generated from here are the drawing's alone in that band.**

- **Rivendell is off until its site is re-scouted.** The city was planted against the drawing at 5200, 75, -3370 and its massif was laid there by `ArdaMap` - floor 74, crown 318, the rim 96 blocks out and the fall 200 beyond it. The owner's scouting found the ground there is a shelf, not a vale: the drawing reads 164 to 175 blocks with no mountain mask, and the river that ought to be crossed on the way from Bree runs at x 5000, between the vale's junction and the city, so the leg that ends at the junction never reaches it. `ArdaMap.PLANTED` is false now, which leaves the masks alone making the world; the `rivendell` assembly is out of `TolkienStructureProvider.ASSEMBLIES` and its two generated files are deleted (`worldgen/structure/rivendell.json` and `tags/worldgen/biome/has_structure/rivendell.json`), so no world generates a city there; and both pins - `rivendell` at 5200, 75, -3370 and `rivendell.stone` at 5499, 150, -3080 - are gone, so `Pins.at` answers null and `CityCommands.rouse` forgets the raise instead of laying one. The cut itself - 584 pieces, 554 by 512, 244 bands from 75 to 318 - stays in the jar: the new pin goes on the block the lowest band stands on, and the city occupies that y to y + 243. **Not seen in a world: compiled only. A world that already holds the city keeps it - the marker in the game directory and the chunks already generated still say it stands, and nothing in the mod lifts a city.**

- **The surveyed road network is paused while Rivendell moves.** Every leg in `Roads.NETWORK` was surveyed around the city as it stood, three of them starting from the city's own edges, so a new site makes those lines wrong rather than merely long. `Roads.NETWORK_OPEN` is false and `network()` answers with only the roads planned at a build this session; the thirteen entries stay in the table untouched, so opening it lays the old lines again, and a road re-surveyed from the new city is a new entry under the id its two ends give it. **Not seen in a world: compiled only; no world has been started with the pause in.**

- **A carried backpack no longer kills the tick at the top of the world.** `Progress Check 6` crashed at 17:40 on 23 November with `ArrayIndexOutOfBoundsException: Index 24 out of bounds for length 24`, thrown from the player tick: the player stood at 5785, 320, -3001 - the block above the overworld's last one, whose sections stop at 319 - with drops on the ground and a backpack carrying a magnet. The magnet reads a carried bag by building a copy of it that is not in the world (`BackpackBlockEntity.createHeld`), and that copy was put at `player.blockPosition()`. Neoforge writes an attachment read on a block entity down as a block change on its chunk, and the write indexes the chunk's sections by y, so the copy asked for section 24 of 24 and the exception came out of the tick. The copy is only ever read from and saved back to its stack, so it now takes the player's x and z with its y clamped into the level's own range, topmost block at most (`Mth.clamp`), and the read lands in a section the chunk has. Nothing else about a held bag changes: it is still made fresh from the stack on every scan, and no block in the world is touched. **Not seen in a world: the crash in the log is the only reproduction, the fix is compiled only, and no world has been stood at y 320 with a magnet bag since. The other two crash reports in `run/crash-reports` are not this: 22 November's 09:56 was a missing `Sites$Lines` class and 08:17 the watchdog after a sixty-second tick.**

- **The Shire leg now arrives on the village's east side, at -150, -382.** The leg ran from Bree's hall front to the village's `/tmtp` point at -463, -124 since 22 November, and the owner has moved its Hobbiton end to the east gate marked in `Progress Check 6` on 23 November, after seeing the road laid. An id is a road's two ends, so the leg is `planned_1032_-34_-150_-382` now: `Roads.NETWORK` carries the new pair, and `Roads.TOWNS` names -150, -382 as the ground the village end arrives on, so the `SHORT_OF_TOWN` 40-block hold is measured from the east edge and the ring it clears is the village's own. The block is on the footprint's own edge, so the grade stops 40 blocks short of it and the arrival waits for the village's note exactly as before. `Pins.SOURCE`'s `hobbiton` point is untouched: a pin is where the town's name stands, not where a road meets it, so `/tmtp` still puts a traveller at -463, 98, -124. The crossing the leg plans across the Brandywine moves with the line, and no bridge is named on it, so the standard pair is what stands. **Not seen in a world: compiled only; the re-ended leg has not been planned or laid. A world that walked the old line keeps it under `..._1032_-34_-463_-124` - the pass treats it as a different road - and the ground it laid stays, since nothing lifts a road.**

- **Bucklebury's bridge waits for the road to Bucklebury.** The drawn pair - `bucklebury_end` and `bucklebury_middle`, cobblestone-wall piers - was named on the Shire leg's own crossing at 445, 90, and the owner's call is that the road from the Shire to Bree keeps the standard `bridge_end`/`bridge_middle`: the drawn bridge belongs to the Hobbiton to Bucklebury road, and this is not that road. `Roads.BRIDGES` is empty again, so `Roads.bridge` answers null at every span and `RoadCommands.pieces` stands the standard pair, exactly as it did before the pair was drawn; the files stay in the jar's bridges folder beside the standard pair, and `tools/bridge_build.py` stays as the way the pair is drawn, so naming the bridge is one line in the table when the Bucklebury leg is walked. The span itself is unchanged - `_bridge_1`, 471, 90 to 418, 90, fifty-four columns of the Brandywine at deck 116 - and nothing has been stood from either piece in any world, so there is no ground holding a bridge that has to be lifted. **Not seen in a world: compiled only. The Shire leg's bridge stage has not run since (`Progress Check 6`'s `_bridge_1` began and never reached it), so the first span stood after this is what will lay the standard pair there.**

- **A road held at a town's edge is laid from the end the hand is standing on.** In `Progress Check 6` on 23 November the Shire leg's second dry stretch (`..._2`) began at the village gate at 16:11 and searched its way to Hobbiton, then stopped the `SHORT_OF_TOWN` 40 blocks clear of the village with 31 stations held for its note - and the grade never finished and the held arrival was never laid, neither in the thirteen minutes before the village stood at 16:12:41 nor after it. The hold turned every pass with a town still going up to grade from the other end, so the grade began at the far bank of the Brandywine at 418, 90, about nine hundred blocks east of the player, and the tick only steps a pass while the chunk under its head is loaded: the head stood in country nobody had loaded, the player was at the gate, and the road waited for somebody to walk to the bank. The Bree leg's arrival did not fare better - the hand's own build of it stopped 38 stations short at 16:28 and Bree stood at 16:32:53 with no arrival laid, because the player had gone back to the Shire at 16:28:46 and the waiting pass was never stepped. `alignDry` now grades from the end the pass was asked from, town or no town, and cuts the town's margin wherever it stands on that walk - from the start after the turn, or from the end as before - so the grade still begins on country the ring never touches and `held` is kept stop-to-gate either way. The direction is written into the pass's note (`heldFirst`) so a restart continues the same way; `joint` takes the grade's start height rather than its end when the arrival is in front of it, `join` puts the arrival first in the note with the line and the grade kept aligned, and a pass waiting at a town's edge is stepped on the arrival's own ground rather than the ground its grade got to, since the grade's last station can be a whole road away from the town whose note it waits for. **Not seen in a world: compiled only, and `Progress Check 6` was not replayed. The leg's three sections were forgotten at 16:25 on 23 November (`/tmroad forget`, 3 sections and 0 planned), so a new start is what puts the Shire leg back in the pass, and nothing lifts the ground the forgotten pass laid. The road still fills in as it is walked: standing at the gate now lays the stretch the loaded chunks cover, and the rest follows the walk.**

- **A tree standing on a build's own ground is not the country, and the clear leaves it whole.** The clear runs a cut's columns twice - everything over the cut's own top in that column comes off, then every position the cut leaves empty below that top is taken for the country's - and a village's party tree fell to the first of them: it grows in a garden whose cut is a course of grass, so its crown stands over that course with its trunk on it, and read as country the whole tree came off at the grass. A column is now asked what its top is before anything is taken: the column is read down from its surface through leaves and wood to the first block that is neither, which is the block the tree stands on. A tree standing at or under the cut's own top there stands on the build's ground and is the build's, so the column is left whole - leaves, trunk and ground, and the country's blocks in it untouched too, since the column is skipped before either run. A tree whose ground is above that top stands on the country over the build, a hill the clear is taking off, and still goes with the hill; a crown hanging over air has no ground of its own and still goes, so nothing is left standing on nothing. The count is in `/tmcut clear`'s answer, which now ends `N trees left standing on its own ground`; the village's and the town's own passes report no counts of their own, as they reported none before. `hobbiton` is not the only cut it reaches: `hobbiton`, `bree` and every `/tmcut clear` share the sweep, and a cut's own tree on its own ground was being taken there too. **Not seen in a world: the rule is compiled and no village or town has been cleared since, so the party tree stands until the Shire is raised in a world that does not hold it yet. A world whose village is already cleared keeps what it lost - nothing in the mod plants a tree back - and `DRESS_VERSION` is not bumped, because re-raising a village to reach the fix would lay it and clear it again over whatever a hand has done inside since.**

- **Bucklebury's bridge is drawn to the shape a span stands it in.** The owner's own cut of the crossing was the right bridge in the wrong box, so the pair the table stands is drawn from it rather than cut out of a world: `tools/bridge_build.py` writes `bucklebury_end` and `bucklebury_middle`, holds each to the four numbers `bridgePlan` reads - nine columns across, the end's box its own deck, the deck's course on the row the road arrives at, a support block in every pier column - and reads each file back cell for cell. Nine columns across is `HALF` 4 either side of the line, where the cut was seven inside an eleven-wide box: the near end is stood turned half round and its z is mirrored, so a seven-wide deck would cover `low` to `low + 6` at one bank and `low + 2` to `low + 8` at the other. The end is seventeen long with its deck's bank edge on local x 0 and the middle is twenty, so the Shire crossing's fifty-four columns (471 to 418) lay as 17 + 20 + 17 with nothing open and nothing doubled, where a twenty-long end left three columns of water before the first middle. The ends are stood at `endBase`, one row over the middles' `bridgeBase`, so the deck that is walked on is row 1 of the end and row 2 of the middle and both are at `deck` where the road arrives: the cut's deck was three courses thick and four rows over the water, which would have stood the bridge two blocks proud of the road at both bridgeheads. The parapet is `stone_brick_wall` and only the piers are `cobblestone_wall`, the block the table names as Bucklebury's support, because `pillarsColumns` takes the lowest support block it finds in a column and draws down from it - a cobblestone parapet running the length of the deck would have been found in every column it crossed and drawn to the bed as a curtain of pier under the edge. The end carries its piers at 4-5 and 12-13 of its length and the middle at 9-10, both on z 0 and 8 under the parapet; the middle carrying its own is the one deliberate change from the cut, whose middle carried none and would have hung its twenty columns over the water. The bank end has two stone-brick towers to row 9 with spruce plank caps, spruce slab seats and a torch each, the parapet starts clear of them at x 3 so the gate between the towers is five of the nine columns, and column x 0 is open deck so the road walks straight on. **Not seen in a world: nothing has been stood from either piece** - whether the towers read right at the bank and whether the deck meets the road flush are the first span's to say. The owner's cut is in this repository's history (`71e6270d`, `a6a7b30a`) if the drawn pair is not wanted.

- **Every bridge's pieces are in one folder of the jar, `structure/bridges`.** `bridge_end` and `bridge_middle` shipped loose in `data/tolkienmobs/structure/`, beside the cuts and the camps, and a bridge of the owner's own would have had to go there too - a nine-wide template in the same list as a city's five hundred and eighty-four pieces. Both have moved to `data/tolkienmobs/structure/bridges/`, and `RoadCommands.template` - which reads nothing but bridge pieces - and the `shipped` check behind a named bridge now read from `BRIDGE_PIECES`, so the folder is spelled once and every bridge's pieces, present and future, are one kind of file in one place. A piece's name is unchanged everywhere it is written down: `RoadPlan`'s `END_PIECE`/`MIDDLE_PIECE`, a line in `Roads.BRIDGES`, and the `endPiece`/`middlePiece` a world's `road_plans.dat` already holds are all leaf names, so no saved plan is renamed by the move and the standard pair is found under the new path exactly as it was under the old. **Not seen in a world: the resources moved and the code reads the new path, but no world has laid a span since; a span laid from a saved plan asks for the same two names and answers the same folder.**

- **A crossing can be stood with a bridge of its own, named by the water it crosses.** Every span the mod has laid is the standard pair - `bridge_end` and `bridge_middle` from the jar, nine wide, oak-log piers drawn down to the bed - and which crossing it is has never been the ground's business. `Roads.BRIDGES` now holds bridges by the water they cross: a name for the log, a block on the crossing, the two piece names, and the block the pieces' own piers are made of. `Roads.bridge` answers which span covers a named bridge the way `Roads.water` answers for a column - between the banks on the span's own axis and within half the deck plus `BRIDGE_LEEWAY` 8 of its line - and it is asked of the ground rather than of the span's section number, because `_bridge_<n>` counts from the end the plan walked from and moves the moment the plan meets another river before it. `RoadCommands.pieces` asks it once when the pass reaches the span, so the plan, the stand and `pillarsColumns` all take the same pair and the same material: a bridge of stone is drawn down to the bed in stone, and the standard bridge's oak log is what a span with no name of its own still gets. The sweep before the pieces are stood clears only the five standard woods, because clearing stone out of the box the abutments stand in would take the bank the span rests on; a stone bridge laid twice stands its pieces over the ones already there. **Bucklebury** is named - the Shire leg's `bridge_1` runs 471, 90 to 418, 90 across fifty-four columns of the Brandywine at deck 116, and the bridge is named at 445, 90 with pieces `bucklebury_end`/`bucklebury_middle` and oak-log piers. **The Last Bridge is not named yet, and the reason is in the world**: the leg to Bree, `planned_4648_-3316_1031_-36`, crosses no drawn water at all and plans as one dry section, which the world said in as many words when it laid it, "Nothing on the way is water, so nothing is bridged" (11:39 on 22 November). Its pieces are reserved as `last_bridge_end`/`last_bridge_middle` with stone-brick piers, and the table line goes in with a block on the water when that road has a river there. **Not seen in a world: neither pair of pieces is in the jar, so both crossings still fall back to the standard bridge - the pass logs which named bridge is missing - and nothing here has been stood.**

- **A road with one town still going up is laid up to that town's edge, and its arrival waits for the note.** The hold added on 22 November answered a leg between two towns by holding the whole road until both towns had written theirs, which is safe and slow: the Bree leg is four thousand blocks of country that neither town will ever touch, and none of it was laid while Bree's ring was still being dressed. `Roads.waits` now holds a leg whole only when a town at *both* of its ends is still going up; one town standing is enough for the pass to lay from it. `Roads.TOWNS` names the ground each town is met on - Bree's hall front at 1032, -34 and the village's gate at -463, -124 for the Shire's leg, the hall front at 1031, -36 for the Bree leg - and `Roads.hold` answers which end of a *section* arrives there, because a leg is cut into a section for every span and every dry stretch and only the arriving one is graded on the town's ground. A pass that finds one end held turns round if it has to, walks to `SHORT_OF_TOWN` 40 blocks clear of the town - past the `DRAPE_REACH` 32 the ring dresses - and keeps the rest of its line (`held`) in its own note, so a restart in the middle of the wait resumes at the edge rather than searching again; the grade it settles ends on the ground at the stop, and when the town writes its note the held stations are graded as a second profile from the height the road stopped at to the town's finished ground. The note a finished road leaves carries the two stretches as one line and one grade, so `RoadCommands.walked` still asks the ground for the arrival. **Not seen in a world: the stop and the second grade have not been walked, and neither leg was re-laid.**
- **A fingerpost already standing in its target's ring is no longer planted again.** The plinth is the post's marker and the standing check read a couple of blocks either way from a single column's height, but a column holding a post answers with the top of that post - five courses above its own plinth - and the ring a post is stood in reaches `CLEAR` 12 blocks past the target. Read that way the post just planted was invisible from the next sweep on, so the clock (`EVERY` 400 ticks) stood another beside it at the next free verge. `Progress Check 4` shows it: three posts at 4771, 4776 and 4781, -3068 on 22 November, five blocks apart at the same z, on a shoulder of the vale's junction with no crossing on it. `SignPosts.standing` now takes the radius of the ring the target's post would be given and reads every column in it from its own height - a shoulder can be a bank, so one height for the whole ring is not enough - walking a whole post down to its foot (`FOOT`, the three shafts and the cap, with slack for the heightmap). A post standing anywhere that ring could have put one answers the target, so a junction or a mark is settled once rather than once every twenty seconds. **Not seen in a world: the three posts already standing at 4771, 4776 and 4781, -3068 are left where they are, since nothing in the mod lifts a post; the fix stops the fourth.**

- **A road between two towns waits for both of them.** The Shire to Bree and Bree on to Rivendell are in `Roads.NETWORK` and both were laid in `Progress Check 4` on 22 November - and both arrived on ground that was still being made. The Shire's stretch arriving at the village (`..._2`) was begun at 11:23 while the village's pass was still going and never reported laid, and Bree's ring took the Bree leg's arrival out nineteen seconds after the whole 4049-block road reported laid, which is why the pass planned that leg again from 1031, -36. `Roads.waits` already held a road that starts from a build's own door (`BUILDS`), but these two start from one town's door and arrive at another's and were held by nothing. `Roads.TOWNS` now names the towns each leg runs between - hobbiton and bree for the Shire's leg, bree for the Bree leg - and `waits` answers for every town on it, so a leg is graded only once the towns at its ends have written their notes and their rings are dressed. A road's sections carry the road's id with a suffix, so a bridge or a later dry stretch waits with the leg it belongs to. **Not seen in a world: neither leg has been walked end to end with the hold in place, and the two towns re-raise at every start, so a start spends the town's pass before the road's.**

- **The road between Rivendell and Bree is in the network.** The owner marked it in `Progress Check 4` on 22 November: five wide (`WILD_WIDE`), `tolkienmobs:roads/planned_4648_-3316_1031_-36`, from the vale's road junction at 4648, -3316 - the block beside the join the three roads of the vale make at 4647, -3315 - to Bree's own hall front at 1031, -36. The pin was first given with the whole far end wrong - the town's x negated and the hall's height of 123 taken for its z, so the leg read `..._4648_-3316_-1031_123` and the search answered it with seven sections and three spans across country two thousand blocks out into Eriador; both numbers are corrected here, and because an id is a road's two ends the corrected leg is a different road rather than the same one moved. A road in a save is that save's and the network is what every world plans from, so the leg is written into `Roads.NETWORK` with the two blocks and the width the plan itself named, and a world made from scratch now plans and lays it under that id. `Progress Check 4` still holds the first line: its last stretch, -803, 143 to -1031, 123, is laid and the rest is at `SEARCH`, and `/tmroad forget` at that line drops its plan and progress so it is not re-laid beside the corrected road - the ground it did lay stays standing, because nothing in the mod lifts a road. **Not seen in a world: the pin is the owner's walk; the corrected leg has not been laid and the entry has not been played.**

- **The country standing over a city's own ground is taken off it.** The drape has always left everything inside the footprint alone, which is right for a cut that fills its columns to its own top and wrong for one that carves a valley: the cut's floors were left under the hill that was never taken off them, so a mountain could stand raw inside the city beside a ring dressed down to its skyline. A column inside the walls whose country stands three blocks or more (`BANK_BLOCKS`) over the top of the cut in that column now has the country cut off above that top - the cut's own top course stays, nothing below it is touched, so no wall, floor or roof can be taken, and a groove of a block or two is left as a groove. It is counted like everything else: `N inside the walls cut down to the cut`, in the log and in `/tmcity drape`'s answer. Checked against Rivendell's own pieces and the save, the city's inner columns already stand within a block or two of the cut's top where I sampled, so this is a rule for the case rather than a repair of that face. **Not seen in a world: the pass has not been run since the change.**

- **A crown hanging over nothing is taken off, and the drape says how much it left alone.** The city drape walks a column down from its top to the first ground, forty blocks at most - deeper than any canopy, shallower than the air over a cave - and a column that found no ground in that depth was simply left as it stood. That reading was written for a cave and it fails for a build's own carve: Rivendell's cut took the hill out from under its oaks, and those crowns hung ninety-four blocks over the city floor while every visit's pass walked past them, leaving a shoulder dressed to its skyline with a garden hanging above it and a raw face behind. Such a column now has its tree taken off - leaves, wood and trunk, by tag or by name - and nothing else done to it, because there is no ground to join to and rock built in the air is rock over a hole. The pass also counts the columns it chooses not to touch at all (no piece of the cut at the wall, nothing under the skin, a pool with no bed), and both numbers go where a person can see them without reading blocks: the log's `the ground is dressed` line and `/tmcity drape`'s answer now end `N left alone, M cleared of a tree standing on nothing`. **Not seen in a world: the pass has not been re-run on Rivendell, so the crowns at 5199, -3056 are still hanging until `/tmcity drape` is run there.**

- **Both towns' names stand where their own gates are.** The two `/tmtp` points were the middles of the cuts' footprints, which is a number about the drawing rather than about a door: the middle of the Shire's 551 by 468 village is a field a long walk from any way in, and Bree's 1272, 122, 80 is a fifth of the way across a 496 by 672 town from its west side. Both were walked in `Progress Check 3` and written down - Hobbiton at -463, 98, -124 (seven east and one over the block its cut leaves from) and Bree at 1059, 123, -31 (the town's arrival point at the head of the hall's front line) - so `Pins.SOURCE` now carries the gates, and everything that asks a town's name for a block gets the place a traveller is actually put down: a board's distance, a page's reach, a fingerpost's idea of which side of the wall it stands on. The builds are untouched, since a pin is where the name stands and not where a cut starts. **Not seen in a world: the points are the owner's own walk; the entries have not been played.**

- **The road between Bree and the Shire is part of the network, not one world's afternoon.** The owner walked it in `Progress Check 3` and it saved: `road_plans.dat` holds the leg's plan - `tolkienmobs:roads/planned_1032_-34_-150_114`, five wide (`WILD_WIDE`), dry, from Bree's hall front at 1032, -34 toward the Shire, with the four bends the search found - and `roads.dat` holds its progress, 1323 stations and the record marked down, which is the pass saying it reached the far end. A road in a save is that save's; `Roads.NETWORK` is what every world plans from, so the leg is written there with the two blocks and the kind the plan itself names, and a world made from scratch now plans and lays the same road under the same id - which also means the fingerposts at its ends are the ones the post clock asks for. The leg's first walk ended at -150, 114, which is the village's east edge line but a hundred and fifty blocks south of its footprint, in open wood: it read as the Shire end from the map and stopped short on the ground. An id is a road's two ends, so re-ending the leg at the Shire's gate - -463, -124, the block the village's own `/tmtp` point stands on - renames it to `..._1032_-34_-463_-124`, and the world that walked the first attempt holds that plan under the old id as a different road to the pass. To see the road arrive there in `Progress Check 3`: `/tmroad forget` on the old line (its northern bulge at 242, 227 is three hundred blocks from anything the new line crosses, so the command cannot pick the wrong road), which drops its plan and progress and stops it being re-laid; the re-ended leg then needs no command at all, since a named road is begun by the ground that crosses it and lays as its chunks load. What the world will not do is take the first attempt's gravel back: nothing in the mod lifts a laid road, so its bulge stands there as an older track until it is levelled by hand. **Not seen in a world: the road was walked by the owner; the table entry has not been played.**

- **The drape is one pass with a declaration, and a camp's sweep is the first build moved onto it.** The shoulder rule - cap the ground outside the walls at the build's own shape, ease back to the country by the far edge of the ring, leave water and a crown's air alone - was written out three times with three sets of numbers: the city's and the village's ring (`Dressing`, `DRAPE_REACH` 32), a camp's sweep (`Sites.sweep`, 12), and a crag's apron (128 at a 0.4 slope), which is why a camp's edge could read as a wall while the town beside it read as ground. `Drape` is that rule in one place, taking a `Declaration`: the build's box, its `Outline`, `reach`, `deck` (how high the floor stands over the box's low corner), `trees` (whether the build's own trees are not its shape), `fill` (whether the ring may build ground up as well as cut it down) and `version` (the rules it was declared under, which a build that keeps a note records). An `Outline` is read through a `Source`, and both readings exist: `Drape.outline` reads a structure template's own file the way the sweep always did, and `Drape.cut` reads a cut folder's pieces the way the city's ring does - the one thing the passes could never share was which format they were reading. `Sites.sweep` is now a four-line declaration (`reach` 12, no deck, no fill) over `Drape.dress`, and its helpers moved with it: the walk, the column, the inside cut, the hole filling and the offset key are the same code, line for line, with `corner.getY()` written as the box's base and a count returned instead of nothing - so a camp's ground comes out exactly as it did, and the next build to want a drape writes a declaration instead of a fourth copy. The towns are untouched: the city and the village keep their own stepping job, their mouths, springs and deck. **Not seen in a world.**

- **Bree's mustering hall has its ground, and waits for the town.** A hall is a pin in its town's own build, and Bree had none, so no hall was ever raised there - and had one been pinned early, the town's pass would have cleared the ground out from under it. The owner marked the spot in `Progress Check 3` with two blocks of ammolite at 1031 and 1032, 122, -32: the middle two blocks of the hall's 16-block front, which is the side its job board is on. That pair fixes the whole box for a 16 x 15 x 16 hall, so `Pins.SOURCE` now holds `hall:bree` at 1024, 123, -32 - laid turned half round from the pin, the board lands on the box's low-z face, so the hall runs 1024..1039 by -32..-17 with its floor course in the block the markers stand in, level with the grass round it. `TownHalls.waits` now answers for Bree as it does for the Shire and the valley, from `Bree.complete`, and `Bree` raises the hall after its pass writes the town's note - and on the next start for a world that already holds the town, the path the Shire's hall already takes. **Not seen in a world.**

- **The Arda crossing is held shut while a pass over the Shire is in hand.** The portal's arrival point is the Shire's own crossing, which stands in the village's footprint, and the village's pass clears that footprint as it begins: a traveller who went through while the village was going up had a doorway put down under them and swept away with the ground. `ArdaPortalBlock.getPortalDestination` now returns nothing while `Hobbiton.atWork()` - a pass planned and waiting, or at work - so the traveller stays in the doorway they lit and the crossing asks again every tick; it opens on the pass that writes the village's note. A world that never raises its village is not shut out: the gate is only held while a pass is in hand. **Not seen in a world.**

- **A camp's keeper and a ruin's steed are only stood where a body can stand, and only once the build's own blocks are there.** Elladan's and Barachen's pins were written from a scout's feet and sit a few blocks under the camp's own floor; `Sites.room` walked up from the pin looking for a standable column and, finding none within `BODY_LIFT`, answered with the pin itself - so the keeper was stood inside the hillside, which is the man the owner found under the ground or not at all. `room` now answers null rather than a buried column, falls back to the ground the column has, and `standKeeper` asks `Sites.standsAt` first so a man is never stood at a camp that is not laid yet; the next approach asks again, which is what the pass already does for every site. `standSteed` reads its spot the same way. **Not seen in a world.**

- **The two towns' milestones are asked for again until the ground will take them.** Both are planted by the world at a start, and at a start a new world's village is not raised yet: the stone was offered ground the village's own paving was about to cover, refused by `Stones.stone`, and never asked for again - a town with no stone in the travel list. `MilestoneHandler.plantTownStones` is now called at the start and again every 200 ticks until both stones stand (it stops asking once they do, and the clock does not generate chunks to ask), and the Shire's stone waits for `Hobbiton.complete` so it is only offered the paving that is really there. **Not seen in a world.**

- **Every laid road is posted within sight of both of its ends.** The network was posted at its junctions and every thousand blocks, which is right for a traveller already on the road and useless to the one who has just walked out of a town: the first board was a mile away. `SignPosts.spaced` now puts a mark 96 blocks along each road from each of its ends (`START`) as well as the thousand-block marks, so leaving Bree, the Shire or the valley a player meets a fingerpost inside a minute's walk that names the town the road goes to. A mark whose arms name no town still plants nothing, and a mark within `GUARD` of a junction is still that junction's post. **Not seen in a world.**

- **The drape fills the ground under a crown instead of leaving a bare hole at the join.** The ring read each column's top
from `MOTION_BLOCKING_NO_LEAVES`, and a height that counts leaves out answers with the highest block that is not a leaf -
which under a bough reaching out over air is the bough itself, one block over nothing. Read there, a column had its
crown cleared (the leaves over the bough stand above the top that was read), then found air under the bough, was taken
for a column with nothing solid under it, and was returned from untouched: the canopy gone and its ground left at the
country's level while every column round it was carried up to the join, which is the hole that looks like a tree the
drape removed and never filled. `Dressing.dressColumn` now reads the column from its blocks - from the top of whatever
stands, down through air, leaves and wood to the first ground (`Dressing.ground`, up to `COLUMN_DEPTH` 40 under the
top) - and leaves the column alone only when no ground is found in that depth, which is a cave mouth rather than a
crown; `Dressing.tree` also knows this mod's own `leaves_*`, `hardened_leaves_*`, `log_*` and `wood_*` names, because
its logs and wood are in no tag, so a trunk of its wood would otherwise read as ground. Measured by dressing a copy of
`Progress Check 2` headless with counters in the pass: 32,799 of the ring's 44,090 columns ended in that return, and the
columns at the south join read `surface 128 top 126 skin Air`. `DRESS_VERSION` stays 1, so `/bree drape` dresses an
existing world's ring again. **Cause measured; not re-seen in a world.**

- **Bree's cut now ships in the jar, so a new world raises the town with nothing dropped into its save first.** The 462 pieces had only ever been left in `Progress Check 1`'s `tolkienmobs/cuts/bree/`, and deleting that world took the town with it: the owner's new `Progress Check 2` answered `no bree cut to raise - nothing in ...\cuts\bree, and nothing in the jar beside it` and stood nothing, which is the warning the pass was given for exactly this. The cut is regenerated from the `Bree-Town` save it was taken from - 496 by 108 by 672 blocks, 462 pieces, 91 MB - and now lives in `src/main/resources/data/tolkienmobs/structure/bree/`, where `CutCommands` already looks before the save, so a copy in a save folder cannot win over it. No code changes: a world reads it on its next start, or on `/bree raise`. **Not seen in a world.**

- **`/bree drape` dresses the ground round the town again, and lays nothing of the town.** The raise was the only pass that wrote `_stands.txt`, so a town whose ring wanted dressing again - ground taken out and made anew, a rule changed, a shelf it left - had no way back but `/bree raise`, which lays all 462 pieces over whatever a hand has done inside since. `/bree drape` is the raise's last stage on its own: the cut's outline, then the ring at the town's own deck (`BREE_DECK_LIFT` 5) and with its trees out of its shape, at the same 5 ms a tick, and the note written when it finishes, so the world does not raise the town over it afterwards. No piece is laid and no country is cleared out of the footprint, and a raise the world has not begun yet - it waits 200 ticks and a player within 192 blocks - is replaced by it, so it can be typed at a start. `/tmcut drape bree` is not the same pass: a cut's table with no `deck` line is dressed to the city's 2, which reads the town's own paving as a roofline. **Not seen in a world.**

- **The drape reads the ground under a tree's crown instead of the gap in it, and never fills a column with nothing solid under it.** The ring took each column's height from `MOTION_BLOCKING`, whose answer over a wood is the top of the leaves, and then walked down while the block was a leaf or a log - which in a big crown is the air it hangs over. That air became the column's `skin`: the fill began five to ten blocks over the ground, and the cap's block had no soil to be, so `skin.isSolid() ? skin : STONE` laid it as stone. Every dressed crown therefore left a plate of bare stone standing in the air with the canopy still under it. Measured in `Progress Check 1`, the south band of Bree's ring (x 1000..1200, z 416..447) held 1,609 stone-topped columns, 483 of them with air under the stone; a slice of the town's own interior, which the drape never touches (x 1024..1080, z 340..419), held none. `Dressing.dressColumn` and `Dressing.openMouth` now read the ground with `MOTION_BLOCKING_NO_LEAVES`, walk down only a trunk from there, and leave a column with no solid block under its crown exactly as it stands - neither filled nor cut - so nothing is laid in the air a crown hangs over; the fill's top is the skin itself. The city and the Shire share the pass and get the same reading. Ground already dressed keeps its plates, because a re-dress reads a plate as ground, so `DRESS_VERSION` stays 1. **Seen in a world: the plates, by the owner, 22 November; not re-seen since the fix.**

- **A cut's parts table now carries each piece's own size, so a build's box is the build and not its tile grid.** The
cutter clipped its edge pieces - Bree's 496-wide build ends in a 16-wide tile - but the table said nothing, and a
reader takes 48 for a piece that does not say, so the box came out 528 wide and 144 tall: `Dressing`'s ring and the
clearing band were worked from country the town does not stand on, and on Bree's east side the ring found the cut's
own emptiness past its last column and did nothing where the hill meets it. The last three numbers are written now; a
four-number table still reads as it did. **Not seen in a world.**

- **Bree's pass now says which folder it read when there is no cut, and the notes name the save's folder instead of
`run/`.** A world asked to raise the town with no `bree` cut got an empty list back and stood nothing in silence -
ten minutes over the site read exactly like a pass nobody had asked for. `Bree.autospawn` warns with the folder it
looked in, and `docs/bree.md` and `docs/next-up.md` carry the cut at `<save>/tolkienmobs/cuts/bree/`, which is what
`CutCommands.folder` resolves, in place of the `run/tolkienmobs/cuts/` the earlier command wrote, where no world
looks. **Not seen in a world.**

- **A fingerpost near a chunk border no longer waits forever for the column across it.** The placer reads the
verge outward from its target in a ring, and gave up on the whole post when any column in that ring stood in a
chunk nobody had made - so a target a few blocks from a border answered the same nothing every twenty seconds,
however long the road around it had been loaded. The unread column is passed over now and the nearest loaded one
is planted; only a target whose whole ring is in unmade country is left for a later pass. **Not seen in a
world.**

- **The fingerposts' lettering now sits on the boards instead of half a board off them.** A world showed the double arm reading `Hobbiton` and `Rivendell` correctly but lettered past the far end of the plank and on one face only: a pose turned by `Axis.YP` and a model turned by a blockstate's `y` do not turn the same way for the same number, so writing the lettering's yaw as the model's put it a hundred and eighty degrees along the board. The yaw is the model's negation now - east 90, west 270 - and the words start at the shaft end again. **Not re-seen in a world.**

- **The fingerposts were fixed where the world found them wrong: one post per crossing, arms at the top, one town per arm, arrows pointing outward, and the post on the verge instead of the road.** The placer's first pass in a world stood two posts ten blocks apart at the Gundabad crossing, hung the boards at the bottom of the post, put Rivendell on the Gundabad arm, showed east and west arms pointing back at the shaft, and stood the post on the paving. A thousand-block mark within 32 blocks of a junction is that junction's post now and a join found from two pairs of ends is folded into one target; the arms go beside the top course, flush under the cap, and step down a course when a cell is taken; a town goes on the arm it lies most nearly down and on no other, so two roads meeting at a shallow angle no longer share a board; the two blockstates turned a south-drawn model with a north-drawn model's compass, so `facing=east` showed an arm facing west - east and west now turn the right way, the lettering and the block's shape with them; and the verge is the nearest column clear of every road in the network by the band's half-width and three blocks more, rather than a width guessed from the road's own kind.

- **The roads now post themselves: a fingerpost every thousand blocks and one at each junction, with the towns that way on their arms.** `SignPosts` is the placer the boards have been waiting for. It asks `RoadCommands.down` of every dry section, so only a road that is really laid is posted, turns each road into the line its own ends and bends run along, and marks it every 1000 blocks measured along that line - a switchback road is not posted twice on one bend and unposted on the next. Junctions are read from the network's own ends rather than from a list of junctions: an end against another road's end and an end against another road's line, both within 12 blocks, so the three roads that end on 4647, -3315 and the two on 8229, -3654 are found the same way as the Gundabad road's end on the old forest road's line. Each arm is turned on a way a road leaves the post, and names the nearest town within sixty degrees of it and further than 64 blocks away - the single arm for one, the double arm for two, whose boards carry a destination each and whose distances the arm block measures for itself. The post is the five blocks a fingerpost is (plinth, three shafts, cap), stood on the shoulder outside the band, with an arm in each cell that is free. A post is planted once and the plinth is the marker, so the placer may run on a clock (every 400 ticks), from the new `/tmsign place`, and in any world without stacking posts; a shoulder in a chunk nobody has made is left alone, so a road is posted as the country around it loads. Three limits are the design: a third town down one arm's road is left off, a post never stands on a bridge, and the distance on a board is the straight line the block entity always measured. **Not seen in a world.**

- **A road leg too steep for the grade is now planned as the switchbacks it needs.** `RoadPlan` leaves a mark every 320 blocks and the pass searches between two marks, and the pass settles a road's surface on half a block a station (`RoadCommands.MAX_STEP`), so nothing steeper than one block in two can be built at all - but the plan never asked whether the leg between two marks could be climbed, and country rising faster than that was handed over as one leg with a climb in it that no search can lay: the pass found a way round by luck, inside a 2.5x detour budget, or refused and the road went straight up the face. A leg whose two ends now stand further apart than 0.5 per block of travel is broken into traverses across the fall: `switchbacks` works out how many crossings the climb needs - each adds two traverse widths to the road's length - stands each mark `TRAVERSE` 24 blocks or more off the line, alternately either side, reads the drawing at each of them, and keeps the ones whose every leg, the two ends included, holds its climb within 0.5; a shoulder that makes a traverse steeper is answered by standing the marks further out, up to 96, and past that the leg is left exactly as it was, which is the plan that laid the road before this. Nothing in the pass changes - its grade still settles on half-slabs and still fillets a turn - and a leg that was walkable plans the same marks it did. Worked out offline against synthetic faces rather than in a world: a 1:1 slope of 100 blocks becomes four legs at 0.494, 0.273, 0.273 and 0.494 where it was one at 1.00; a climb of 60 over 40 becomes three legs at 0.473, 0.272 and 0.498; a 2:1 face of 240 over 120 becomes eight legs with 0.493 the steepest; a 3:10 slope is left alone. **Not seen in a world.**

- **The gate's hall now clears the passage it cuts, so the hold's own door can be reached.** `GundabadGate.extendHall` laid the floor, the roof and the two walls of the passage for the fifty-three blocks to the keep's wall but cleared the inside with `base.offset(x, y, z)`, in which the cell's own y and z are added to the gate's corner as well as to the cell: at the corner (5496, 187, -6877) that put the air about 377 courses up and seven thousand blocks north of the passage, so the mountain was never taken out of it, and a fresh world on 21 November showed the cost - the hold stood at its plan with the doorway the plan was written around at 5571..5575, the mouth dressed around it, and thirteen blocks of the mountain's rising ground, z -6910 to -6922, left between the hall and the hold. The inside is now built from the corner and the passage's own x, so the hall is a tunnel from the gate's rear face to the course before the keep's wall - the wall itself is never cleared, because the way in is the template's doorway in it and a hole the passage's width would take the door out on every start after the first - and the same double offset in `undressBack`'s strip behind the gate is fixed with it. Nothing in the plan moves; a world that already holds the keep has its passage cut on the next start, because the hall is carried on every pass.

- **A hall is no longer swept and laid again because one of its blocks was changed.** `TownHalls` guarded itself with
          `Sites.wholeAt` read turned - every sampled block, exactly - so a single block a player had moved inside the hall
          answered "not standing", and the next start took the whole box out and laid the hall again over their work. The
          way a hall was laid is now written down in this world's own file, `halls`, against the pin it was laid at: a
          hall recorded turned is returned from without a block being touched, whatever has happened to it since, and
          only a hall the record says was laid the other way is swept and laid again. A world made before the record
          existed has the way its hall stands read off its own blocks - the two orientations are compared by a majority
          through a new `Sites.standsAt` that can be asked turned, so one changed block is not an orientation - and then
          noted, which is how those worlds come in without anything being turned over. A hall pinned somewhere new has no
          record at that block and is laid as ground that has never held one, and `/tmpin hall remove` forgets the record
          with the pin.

- **A world that already holds the keep keeps it.** The hold's plan moved twelve east on 21 November so its own doorway
          would stand in the gate's chamber, and `GundabadKeep.autospawn` asked only for the plan this build writes - so a
          world laid at the older 5516 answered "no hold here" and was given a second one at 5528, 110 of its 122 columns
          over the first, the old south wall standing through the new rooms and two doorways facing the passage.
          `ArdaMap.keepOlderPlans()` is asked as well now: 5516 at `KEEP_FLOOR` 176, and 5516 at the 187 the plan used
          between 15 and 18 November. A world holding either keeps its hold and only has its mouth dressed, so the twelve
          east shift is what a world made after 21 November gets; it is not a migration.

- **A mustering hall stands turned half round, so its board faces the path.** The hall is one build with one front, and
          the owner walked it in the Shire and found the notice board at the end away from the path it stands on.
          `TownHalls` now lays the hall with `Rotation.CLOCKWISE_180` about its box's own middle: the pin, the floor and
          the footprint do not move, only the front does - the box is placed from its far corner and the template read
          back turned to match. A hall standing the other way is taken out before the new one goes down, because the
          file does not carry every cell of its 16x15x16 box (972 air, 1307 missing of 3840) and a turned lay over an
          unturned hall would leave the old walls standing where the file says nothing. Its own bottom course is
          complete, so the floor comes back with the lay, and the guard is `Sites.wholeAt` read turned, which leaves a
          hall that already stands turned alone.

- **The Shire is announced at the portal, which is where a traveller arrives.** `Places.standTowns` drew a town's
          circle round its mark, and the Shire's mark is (-150, 114, -382) - four hundred blocks from the crossing at
          (-470, 97, -123) and further than the town's own 280-block radius, so the first thing a player does in Arda,
          stepping out of that doorway, was the one place the village was never named. `Places` now has an `ANNOUNCE`
          point per town, read from `ArdaMap.hobbitonGate()` - the same constant the portal itself crosses at, so the
          banner and the doorway cannot drift - and a town given one is re-stood when the point moves, so a world that
          was told about the old centre is told about the new one on its next start.

- **The keep's own doorway now stands in the gate's chamber.** The owner walked `Work Verification` up the passage and
          found the hold's entrance at wall: the south wall reached the chamber with wall over twenty-six of its
          twenty-nine columns, and the doorway the plan was written around was twenty-nine blocks east of it. Read from
          the template, the wall carries four five-wide, seven-tall openings at local x 13..17, 43..47, 74..78 and
          104..108, two courses of wall behind them and twelve at local 58..63. The plan moves **twelve east**, `KEEP_X`
          5516 to 5528, which puts the 43..47 opening at world 5571..5575 inside the passage's 5563..5591;
          `GundabadKeep.DOOR_X` follows it (106 to 45) so `clearMouth` dresses the ground in front of the door the hold
          is entered by. **And the gate no longer cuts a passage through that wall:** `extendHall` cut a hole the
          passage's own width, fourteen courses deep, on every start - on a first start the template wrote over it and on
          any later start it took the doorway with it - so the passage now ends at the wall and the door is the keep's
          own. The box still fits the crown (x 5496..5656) with seven blocks to the east.

- **A road begins from the block a player is set down on, not only from within thirty-two blocks of its line.**
          `RoadCorridor.look` reached 32 blocks, and a road's end stands at a build's door while the build's own mark -
          the block `/tmtp` puts a caller on - can be much further inside it. Gundabad's road begins at the gate's
          courtyard, 5578, -6769, and the hold's mark is 5578, 190, -6833, sixty-four blocks away, so a player
          teleported to the hold stood outside the reach of its own road and nothing began; the log of `Work
          Verification` shows the road's first section begun at world start from 5704, -6680 and waiting on ground
          nobody was near, with its next sections only begun at 19:39 when a teleport to the Carrock loaded chunks of
          its line. The reach is 96 now, and the cost of the wider reach is a pass that waits for its head's ground,
          which is what it already did.

- **The Shire stands before anybody can reach the portal, and the pass keeps the server awake to do it.** The portal out
          of the overworld opens on the village's own crossing, -470, 97, -123, which is inside the footprint its clearing
          pass works over - x -700..-149 by z -512..-44 - so a village raised by somebody's arrival is a village that
          takes the traveller's doorway away: the pass begins on the first tick after the crossing is used and the
          ammolite doorway goes about ten seconds later. Raised at the start it stands about a minute into a fresh world,
          which is why `raiseShireOnStart` in the mod's server settings is now on by default. **And the start of a world
          is exactly when a dedicated server is empty**, so the pass lifts the server's own
          `pause-when-empty-seconds` while it runs and gives the number back the moment the village stands. That half was
          measured, not assumed: a fresh level with the property at 5 s and nobody online logs `Server empty for 5
          seconds, pausing` part way through the pass, and the pass never takes another step - the paused branch of
          `MinecraftServer.tickServer` returns before the tick event and before the level's own tick, so nothing wakes
          a pass the server has stopped ticking. The lift is `DedicatedServer.setPauseWhenEmptySeconds(0)` for the pass's
          length and the old number back on the way out: when the pass finishes, when the cut cannot be read, and when
          the server stops with the pass half done, so `server.properties` is written twice and ends as it began. A
          server killed outright mid-pass keeps the zero, which costs it its idle pause and nothing else - as it keeps
          the level's forced-chunk tickets until the next start re-runs the pass. A world that sets the switch to false
          keeps the older rule, the pass beginning when somebody comes within `REACH` 192 of the footprint, and
          `/hobbiton raise` still asks for it at any time in either world; a world that already holds the village is
          left alone. **Nothing here has been seen in a client session**: the pause lift is read out of a dedicated
          server's own log, and whether the village looks right from the crossing is still a walk to Hobbiton.

- **A road that meets one channel twice is walked again straight, so the planner no longer plans two spans over the
          same water.** The drawing probe at 6300, -3580 to 6800, -3580 planned five sections - a 45-wide span at
          6418, -3612, a road running back west to 6383, -3585 and a 56-wide span at 6399, -3585 - and a trace of the
          walk showed the line was the fault, not the span: the walk reads the drawing every sixteen blocks, and
          between two of them it stepped two lanes onto the one dry block at 6441, -3580, on the channel's ragged
          edge, and back into the lane beside it. Both readings were dry and nine dry blocks apart, so neither the
          `DRY_AFTER_CROSSING` rule nor the bank margin could see the water, while the staircase the plan is really
          read along passed through it twice. A plan now records that its line leaves water and meets water again
          inside `DRY_AFTER_CROSSING` lanes (64 blocks), and `RoadPlan.of` walks such a leg a second time with a lane
          change refused on every step that leaves water, so the crossing is straight and on the lane the road
          reached the bank on - which is also the only shape a span's banks and approaches can be read for. The
          first walk is left exactly as it was, so a leg whose own line was sound plans the line it planned before.
          **Measured with `tools/roadprobe`:** that leg is now three sections with one 45-wide span at 6424, -3627,
          deck 188, and nothing running back; all ten other legs probed - the seven roads in `Roads.NETWORK`, the
          Gundabad road, the 6398, -3654 pin and the 6420, -3560 bank leg - come out byte for byte as they did, so
          no road already laid in a world moves and the signed-off spans at 11649, -4080 and 8551, -3626 stay where
          they are. **Nothing has been laid from this:** it is a planning change, read against the drawing alone,
          and the next fresh world is where the one-span crossing shows.

- **A road no longer begins under a build that is still going up, so the strip the valley's dressing took out has
          something to fill it.** The ground begins every road that is the world's own - a chunk that the road passes through
          starts it, and so does a player standing within 32 blocks - and the road out of Rivendell is one of those, so it
          graded itself against the valley while the city was still being raised band by band. The dressing settles the ring
          of ground outside the walls *after* the last band, so the strip it cut and filled was the strip the road had just
          laid: Road Test 7's log has the road begun early and the small run from the first bridge to the road's own end
          gone, and a second laying does not put it back because the road's own note says it is down and the note is
          believed. **The road waits for its build.** `Roads.BUILDS` now names the build each of the valley's three roads
          leaves - the two gates' legs and the road to Elladan's camp, whose ids are the pins they were planned from, with a
          section's `_2` and `_bridge_<n>` suffixes read as the same road - and `Roads.waits` asks the town's own answer,
          `TownHalls.waits`: the valley is finished when `CityCommands.complete` says its last band is down, the Shire's
          village when `Hobbiton.complete` says its note was written. `RoadCorridor.mayBegin` refuses a road its build is
          still waiting on, and the ground asks again every second, so the road begins by itself the moment the build is
          finished and nothing has to be typed. Only a road that names its build is held: every other road is the country's
          and has nothing to wait for. **Nothing here has been seen in a game.** The gap already in Road Test 7's world is
          not repaired by this on its own - it needs `/tmroad forget` at the gap and one visit, which now re-lays from a
          build that is whole; if the pass refuses that stretch it says which leg and why, and that sentence is the next
          thing to read.

- **The twelve builds the owner has visited are marked at the block they should be arrived at, and the jump, the page's
          needle and the sign boards all read that one answer.** A pin is where a build is *planted* - a template's corner,
          a cut's low corner - and a corner is right for the hand laying it and wrong for anybody being sent there: an arrow
          at a corner points at a wall and a jump lands outside the door. `Marks` now holds the fifteen keys the commands ask
          with - a catalogue name where the name is the catalogue's, a template id where it is a build's, a build's last
          word matched the way a pin's is - and the blocks are the owner's own, walked in the test world: Rivendell's road
          end 5200, 135, -3034, Hobbiton -150, 114, -382, Thorenhad and Elladan's camp 4647, 158, -3315, Barachen's fire
          4819, 159, -2887, the wood ruin 4787, 160, -3059, the three crags 4515, 155, -2446 / 4624, 157, -2480 / 4562,
          157, -2398 / 4684, 161, -3050, Glóin's camp 8229, 224, -3654, the goblin fires 5605, 196, -3622 and Gundabad
          5578, 190, -6833. `/tmtp` answers a name with its mark before it asks the place or the pin, and `Waypoint` shows
          the mark for a site's page, a named place and the step after it - while a body the campaign stands keeps its own
          block, since a person is a person and not a doorway. **A mark never moves a build:** it is not a pin, so no
          template is seated anywhere new, and a world that already stands its camps gets the corrected door in front of
          the same camp. **Gundabad is listed as a town** - the owner's `<- town` line - so a board offers it, and a town
          with no place yet is now stood on its mark rather than on its pin, which is what the banner's circle and a
          board's distance are measured from. Nothing of this has been seen in a game: the jumps, the needles and the new
          board entry are all compiled only.

- **A road leg that ran beside a river gave the road up for water it never meant to cross, and now it steps inland along
          the bank.** The walk reads the drawing a lane at a time, and the line it leaves between two lanes is a staircase of
          single blocks, so a line that follows a bank clips the water's edge between two readings the walk never priced. The
          dense pass found those clips and measured them at the *pool* the road's own entry stood in rather than at the water
          the road crossed: planning `6420, -3560` to `6600, -3560`, both ends inside the mountains' wide bend, refused with
          `the water at 6420, -3560 is 110 columns wide, past the 96 this will bridge`, and none of that leg could be laid.
          **Two answers, both kept behind the refusal.** A refused leg is walked a second time with the bank priced: a dry
          lane within `BANK_MARGIN` (3 lanes, 48 blocks) of drawn water costs `BANK_COST` (400) a step, so the road keeps off
          the edge and the line steps inland along the bank - and only that second walk pays for it, so every leg that
          planned before plans the same line now. And water at a leg's own two ends is the bank rather than a crossing: where
          the road's first blocks are drawn as river the road begins on the first dry block of its own line, since a mark in
          the shallows has no dry ground behind it to stand an abutment on, with the same rule at the far end; a line that is
          water from end to end still refuses, and so does a leg too short for the walk, which the plan has always laid
          straight. **Planned against the drawing, no world:** `6420, -3560` to `6600, -3560` now plans one section, `6436,
          -3576` to `6589, -3560` - the pin in the water, the road beginning sixteen blocks inland of it. The geometry that
          was signed off is untouched: `11813, -3901` to `8229, -3654` still spans at 11649, -4080 (52 wide, deck 170) and
          8551, -3626 (46, deck 188), and `6398, -3654` to `8229, -3654` still spans 52 at 6668, deck 195 - the numbers the
          test world laid. **The doubled crossing is not covered by this and is still there:** `6300, -3580` to `6800, -3580`
          plans a 45-wide span at 6418, -3612, a section running back west to 6383, -3585, and a second 56-wide span at 6399,
          -3585 - the same water crossed twice, from a clip between the walk's own readings. That is the next thing in the
          planner. **The instrument is in the repo:** `tools/roadprobe` plans a leg against `ArdaMap` alone - no chunk, no
          world, no client - through `./gradlew -I tools/roadprobe.gradle roadProbe -PprobeArgs="leg WILD <x1> <z1> <x2>
          <z2>"`. Nothing here has been laid in a world, so what the pass makes of a section that begins on a bank rather
          than at the pin is unverified.

- **The raise laid one band and called the whole city finished, so a fresh world got a floor and a sky.** Road Test 6,
          walked with no commands at all: the raise started from the pin, band 1 of 6 (the floor) took its 132 pieces and one
          `out of made ground` line, and then bands 2, 3, 4, 5 and the whole city each reported "is down" **forty to two hundred**
          milliseconds apart - five bands and the finish in **720 ms**, having looked at not one piece of them. The save is what
          that means to the eye: at the pin's own column, 5200, -3033, `minecraft:stone` at **75** and **80** - the floor - and
          `minecraft:air` at **123** and **130**, where band 2 begins; the marker read `5200 75 -3370 6 0 tolkienmobs:arda`, the
          whole city down. Seventeen `Elf suffocated in a wall` lines between 09:21:13 and 09:21:26 are the other half of it: the
          floor went down over the valley's own city folk, and nothing ever came down on top of them. The cause was a band turn
          that moved the marker and forgot to invalidate the order it was carrying: `raisingBand` was set to the band just
          written while `raisingOrder` was cleared, so the next pass found the band already "in hand" and never built that band's
          order - and an empty order is every piece down, so the band closed on that pass and left the next one in the same state.
          Every band after the first closed in a single pass. **A band turn now leaves no band in hand** (`raisingBand = -1`, the
          state a corner starts in), so the pass that reads the marker builds that band's order and lays it piece by piece; and a
          band that nonetheless closes with nothing in hand warns by name - `band N of M closed with no pieces in hand` - instead
          of letting the marker move on in silence, because a city that reports itself finished with nothing in it is the report
          this whole raise exists to stop. **Two things the same run settled:** no `tolkienmobs:rivendell` structure start exists
          anywhere in the fresh world's `arda` region files, so the worldgen removal is doing what it was for and the raise is the
          only hand that plants a city; and the raise's own bands, ground-making and marker are sound up to the floor - the failure
          was never the laying, it was the hand that turned the page.

- **A structure start was taken for a standing city, so the world was handed Rivendell and built only a ring of it.**
          Road Test 5, walked with no commands: the raise said nothing at all, six roads laid, and the only Rivendell line in
          the log was the dresser's - `19728 columns cut down, 35361 filled up` over an empty valley. The save says why.
          Chunk 325, -211 held a `tolkienmobs:rivendell` start with all **584** pieces planned, and the blocks under it were
          a mountain. Vanilla carries references to a structure only in chunks within **eight chunks** of its start, so a
          city five hundred and fifty blocks across can only ever be placed in a ring round its anchor: chunks 325…333
          carried `References = [tolkienmobs:rivendell]`, 334 onward carried none, a top-band piece six chunks out matched
          its template **300/300** and one twenty-one chunks out matched **0/300**. The raise read that start as "the world
          built this one" and returned without a word - the branch that leaves a standing city alone - and `/tmcity summon`
          refused to plant over it, `/tmcity band` found nothing left to lay, and the dresser cut the mountain down round a
          city that was not there. **A start is now checked against the world's own blocks before it counts as a city:**
          the piece the cut is planted on - the lowest band's low corner, the block the pin names - has a dozen of its
          interior blocks looked for where the cut puts them, and stone where the valley has air is a city while air where
          the cut has stone is a plan with nothing under it. A majority answers, so a corner that has been walked on still
          counts, and only chunks the world already holds are read, because a probe that has to generate the ground it is
          asking about is only a slower guess. **And a cut is no longer generated at all:** `bootstrapSets` writes no
          structure set for an assembly, so `CityCommands` is the only hand that builds a city - it plants the corner at the
          pin and lays a band at a time - while the structure itself stays registered, so a world that already holds one of
          its starts still reads the chunk it is in. That is what a world made before tonight needs: the start stays and is
          ignored, and the raise plants the city over the top of it.

- **A city raised under a walking player stopped halfway, because a band waits for ground nobody walks on.**
          Rivendell's raise stands each piece on chunks the world already has and never makes ground for one, and that was
          right as far as it went: nine chunk columns of generation inside a tick, for a piece four hundred blocks away, is
          the raise's own worst cost. A band is a whole horizontal slice of a city five hundred and fifty blocks across,
          though, and no band advances until every piece of it is down - so the near pieces come down and the far ones wait
          for a walk that has no reason to happen. The Road Test 3 save shows it to the block: the city was planted at
          07:23:38 from the cut's corner, and band 0 is **132** pieces of which **23** lie inside a ten-chunk view of that
          corner - the median piece **341** blocks out and the furthest **624** - so **71** pieces of the band were never
          inside a view of anywhere the player stood. Band 0 then laid nothing for ten minutes and said nothing; all six
          bands went down in 300 ms the moment the hand's `/tmcity band` forced their chunks, and the server fell 418 ticks
          behind doing it. That is the same work, done all at once and by hand. Now a band that lays nothing for five passes
          in a row - a second to five seconds, on the cadence - makes the ground for the nearest piece still waiting, one
          piece a pass, and lays it. The cadence walks up when the world cannot take even that, so a stalled band costs what
          the player's own walking would have cost, a piece at a time, and the band goes on. It says once per band what it is
          waiting on - `Rivendell: band 1 of 6 is out of made ground - 96 pieces left, the nearest at 5672, -2870, 412
          blocks from the nearest player; its ground is made for it from here` - and the band's own "is down" line still
          says how it ended. A world the raise has already finished has nothing to redo.

- **The keep's rooms stood eleven courses over the gate's chamber, so the hold had no door.** A fresh world drew the
          keep at its plan to the block - x 5516…5637, z -7112…-6930, its south wall on -6930 with the gate's chamber carried
          to it and a doorway cut through where the wall stands - and the two floors still did not meet: the keep's rooms
          read **200** where the chamber's floor reads **189**, so the chamber opened into the void under the rooms and the
          keep's floor was the chamber's ceiling. The number at fault is `KEEP_FLOOR`, not the corner: the template's rooms
          are lifted thirteen courses over its base (the spiral's stair is cut under its own hall, so the five surface
          rooms are laid thirteen up to stand on one level with it) and its doorways are cut from course fourteen. The pin
          must put that floor on the passage's, so it is **176**, not 187 - which is what the first plan read before the
          move. The rooms' floor now lands on 189 and the walk on 190, level with the chamber to the block; the roof comes
          down from 212 to 201, which only deepens the rock over it; and the captain and the wraith follow
          `ArdaMap.keepThrone()` to (5546, 193, -7036). A world already holding the keep at 187 keeps it - a second one
          would stand eleven courses lower inside the same box - so this is a fresh-world change.
- **A leg pinned in a test world and never laid is out of the table.** `planned_4516_-2512_4562_-2398` was written into
          `Roads.NETWORK` with the high-pass legs and has no note in any save: no world has laid a section of it, so what
          it planned in a fresh world was ground nobody walked. The store is the test of a line, and the table now holds
          the store's roads and nothing else - eleven of them. Dropping it costs nothing a world had: a road with no note
          was never begun, and one that should be walked is pinned again with `/tmroad pin`.
- **The five roads a fresh world was missing are in the table, Gundabad's included.** The network was read off a test
          world's store before the vale's roads were walked, so a world made afterwards planned eleven sections and had no
          road out of Rivendell to the meeting plaza, none from the plaza on to Glóin's camp, and none from Rivendell down
          to Elladan's camp - the ground was there and no line was, which is a road missing in silence. The store was read
          back road by road: the five ids it holds that the table did not are now in `Roads.NETWORK` with the ends their
          own ids were made of (5105, -3413 to 5605, -3622; 5606, -3622 to 8229, -3654; 5200, -3033 to 4647, -3315;
          5111, -3399 to 5072, -3221), plus the hold's road, whose two ends are no longer null: 5578, -6769 at Gundabad's
          gate and 8583, -3617 where it meets the old forest road. Gundabad's road is named by the id its pins were given
          rather than `gundabad_road`, because a section's id is the road's two ends: a road named otherwise would lay the
          ground twice in the world that walked it and be a different road in a fresh one. A road left out of the table
          is not a road that waits - it is a road a new world does not have.
- **A road begins wherever a player stands beside it, not only when a chunk loads.** A road was begun by the load of a
          chunk it passes through, and a chunk loads once: ground brought in before the player came - by a structure being
          raised, by a city's bands, or by a save made in another session - never loads again, so a road whose ground was
          already there had nothing left to start it. That is what a fresh world showed: eleven sections planned, the
          player at Rivendell, nothing laid and nothing in the log. The corridor now looks at the players as well, once a
          second: every road a player stands within thirty-two blocks of the line of is begun from where they are, the
          same rule the chunk load has - the ground a road crosses begins it - with the ground being wherever a player has
          got to. The chunk still begins a road, so a world with nobody in it still dresses its own roads. A road whose
          ground refused it is kept refused for the run, or a player standing beside it would have the whole search run
          again every second; forgetting the road clears the refusal.
- **A world that lays no road now says which of the two it is.** Nothing in the log said whether a session held the
          roads at all: a world whose list was empty, one whose list was full and never touched by a loaded chunk, and
          one whose road was begun and refused all read the same - silence. The start now says how many sections are in
          hand beside how many came back from the save, and every pass says the ground it was begun from, once each.
          Found by a fresh world that laid nothing while the player waited at Rivendell: the logs had no road line in
          them beyond the save's own count, and the seven network roads are all 380 blocks or more from that city's
          west edge, so the corridor was never given a chunk of one to begin from.
- **The network the test world laid is the world's own.** Every road the test world has was pinned there with
          `/tmroad pin` and kept in that world's plan store, so a world made from scratch had none of them. `Roads.NETWORK`
          now names seven of them as the two ends each was pinned between, at the country's own three-wide track: the old
          forest road east of Mirkwood to Glóin's camp, 11813, -3901 to 8229, -3654, with its two spans at 11649, -4080
          and 8551, -3626; the high pass legs 4515, -2446 to 4705, -3169, 4516, -2512 to 4562, -2398, 4528, -2541 to
          4624, -2480, 4776, -3063 to 4684, -3050 and 4819, -2887 to 4647, -3315; and the short link at 4787, -3059 to
          4778, -3059. Each is planned from the drawing when the class loads, exactly as `/tmroad plan` plans it, and
          keeps the id its pins were given (`planned_<x>_<z>_<x>_<z>`), so a section made in a fresh world carries the
          same name and the same geometry as the one standing in the test world - eleven sections, seven of them a single
          dry leg and the old forest road five. The hold's road is the first entry of the same table and still waits for
          its two marks. `Roads.planned` now tells the two kinds of road apart where it matters: a named road is part of
          the world and the ground it crosses begins it, while a section planned at a build this session still waits for
          the command that asked for it.
- **A channel is crossed once, not waded into and out of.** The old forest road to the far side of the river east of
          Mirkwood planned two bridges side by side and a section that doubled back between them: `bridge_1` at 11759,
          -3882 to 11710, -3882, then a dry stretch running *east* from 11694, -3882 to 11755, -3908, then `bridge_2` at
          11739, -3908 to 11685, -3908 - the same channel, twenty-six blocks apart, crossed twice. The lane walk priced
          water per sixteen-block step, so touching the channel's edge cost one toll and wading back in two lanes later
          cost another, and a dip in and out was cheaper than staying on one bank. The walk now carries the count of dry
          lanes since the water as part of its state: a lane whose ground is dry moves that count on, a wet lane resets it,
          and water met again before the road has been dry for `DRY_AFTER_CROSSING` (four lanes, sixty-four blocks) is
          priced at `CROSS_AGAIN` (twelve thousand a step) on top of the toll. It is priced rather than forbidden, because
          a road with no way round still has to be laid and a walk that cannot finish is a road given up altogether - but
          nothing a shifted lane or a followed bank can cost competes with it, so the crossing happens once. Checked
          against a port of the same walk over a drawn channel with an island that splits a straight crossing: the old
          rule takes two wet runs there, the new one a single run from bank to bank.
- **A road is forgotten as a road, and a plan waits for its command.** The old forest road planned twice in the
          mountains stood twice: seven sections and three bridges on the first attempt, seven and two on the second, both
          of them laying at once. Two things made that happen. `/tmroad forget` cleared the note of the one section under
          the hand, so a road that crosses water - the first of its sections named after the road, the rest `_2`, `_3` and
          `_bridge_<n>` - kept the notes of every other section, and the next start resumed them all; and a plan is put
          back in `Roads.ROUTES` by `RoadPlans.load`, which meant a chunk coming into view began laying a road nobody had
          asked for a second time. Forgetting now takes the whole road: `Roads.roadOf` finds the road a section belongs to
          from the sections themselves (a road's own name ends in a number too, so trimming the suffix cannot work),
          `Roads.forget` and `RoadPlans.forget` drop every section that begins with it, its passes are stopped and its
          notes cleared, and the reply names the road and counts what went. And `RoadCorridor.start` no longer begins a
          planned section that has no note: a plan is a decision waiting for a command, while a road begun and not finished
          still resumes where it stopped after a logout. Compiles; the double lay has not been re-run.
- **A span is decked whichever way the road crosses it.** The first planned bridge stood its two abutments and left
          fifty columns of open water between them: `bridgePlan` walked the deck from the near bank towards the *low*
          coordinate only, which is the way the old ford happened to be crossed, and the mountains' span runs the other
          way - bank at 6668, -3799, far bank at 6668, -3748 - so the run of middles began behind its own abutment and
          stopped at the first step. The gap is read from both ends now: the first column clear of the near abutment and
          the last one still in front of the far abutment, with the middles laid from one to the other in the span's own
          direction, turned about (counterclockwise and clockwise along z, none and half a circle along x) so the deck
          covers the band on either heading. The far end is still stood last and closes over the last middle's overlap,
          so no gap is left at the far abutment, and the ford's own fifty columns come out piece for piece as they did.
          Neither heading has been re-run in a world yet.
- **A crossing is read across the water, not down it.** The span's axis was taken from the two ends of the run of water
          the planned line met - `dx = exit - entry` - and a run is a handful of blocks: a road crossing a north-south
          channel almost square still comes out with a block or two of travel up the channel in it once the line is
          rounded to blocks, so the axis came out along the river and both banks were then searched down its length. In
          the mountains that cost two refusals in one session. The channel at 6471, -3645 is forty columns across and over
          a hundred along its own course into the wide sheet north of it, so the plan said `no dry ground within 96 blocks`
          where a bridge forty wide was standing ready; the leg before it was refused as `112 columns wide` at 6422, -3616
          for the same reason, walking north-west up the same channel. `RoadPlan.cut` now reads the water both ways and
          `pick` stands the span on the narrower reading, which is the shortest way across the channel and the one the
          pieces are stood on; the road's own way through decides only when the two readings come to the same width, as a
          channel crossed at forty-five degrees. A reading that found both banks is the one a refusal is spoken from, since
          it can say how wide the water is. Compiles; neither refusal has been re-run in a world yet.
- **A river is found a block at a time, so a road crosses it on a bridge rather than under it.** The first road laid
          in the world went beneath a river more than four blocks deep with no span over it. The plan is priced at steps of
          sixteen blocks - `LANE` - and the water it cuts spans from was read at those steps too, one sample per step of
          each lane: a channel a road crosses on a diagonal can therefore fall between two dry samples, and a plan that
          never saw the water is a plan with one dry section from end to end. The pass then grades that section as if the
          water were not there, and a causeway is laid with the river standing over it. The line is now read a block at a
          time before it is cut into sections (`RoadPlan.dense`), so every block the road is actually paved on is asked
          what it is, and the runs of water are found where they really are - one block of submerged bed at the least, not
          sixteen. Water is a column the drawing gives a bed below its own surface (`sample.river() && water > height`),
          which covers the rivers and the lakes both and never mistakes dry ground that merely stands under the sea's
          level for water; the banks still have to be dry ground at or over the surface. The span, its deck two blocks
          over the highest water, and the two dry sections that meet it on its own approach and exit are otherwise
          unchanged. A leg that runs along the water's edge rather than across it now refuses by the width it would have
          to bridge, which is the true answer, where before it could slip past the edge and grade a road into it. And a
          section planned again at the same two ends replaces the one the world held (`Roads.remember`) rather than being
          dropped in favour of it, so a leg re-pinned after the ground is read differently is the leg that is laid.
- **Both ends of a leg can be named, and a leg that lays nothing says so in the chat.** `/tmroad build <x> <z>` reads
          the start from the block underfoot, which is a rule a hand can forget between walking to one end and typing: run
          it at the far end with that end's own numbers and the road is one block long, so nothing appears where the road
          should be and nothing was said about it. `/tmroad pin <x> <z> <x> <z> [kind]` names both ends, so a leg is pinned
          by the ground it is meant to cross and the same command gives the same road from anywhere; it is a literal of
          its own rather than a longer `build` because a width word after the first pair would read a mistyped third number
          as a word and lay a seven-wide road without a word said about it. Two ends that are the same block are refused
          out loud, naming the block and telling the hand which of the two forms it was, instead of planning a road of
          nothing. And a leg the country refuses now answers in the chat of every player within 128 blocks of it, not only
          in the log - a refusal is the one thing a player sees as nothing at all, so it says `No road was laid:` and the
          leg, its ends and the reason. A pass whose step could not run at all was swallowed by the tick that carries it;
          it is now a warning in the log rather than a road that never appears for no stated reason.
- **A planned road outlives the session that planned it, and a leg the ground will not carry lays nothing.** Two halves of
          the same promise: a road made at a build has to be a road, and a road that cannot be walked has to say so rather
          than be laid anyway. The plan is now written into the world as it is laid, in a store of its own beside the road
          notes (`road_plans`, a sibling of `Progress`, because a note is how far a pass has got and is thrown away when a
          road is forgotten, where a plan is the road itself and is kept) and read back when the server starts, so after a
          restart a player standing on a road they laid themselves can find it, forget it and have it re-laid. What is kept
          is what the plan decided rather than what was laid: each section's name, its kind, whether the bands decide its
          width, its two ends, its span's banks, deck and pieces, and the marks it is bent through - the whole of what
          `RoadPlan` says, so the road read back is the road that was planned. The class's tables are the world's now too:
          before anything is read in, the previous world's planned roads are taken back out of `Roads.ROUTES`, so swapping
          one save for another does not open the last world's roads beside them. The second half is the search. A leg whose
          walk found nothing used to be answered with one straight line through everything the search had just refused -
          the fallback that made every long road need marks every forty blocks, and made a road with a bad bend a road cut
          through whatever stood between its ends instead. It now gives the whole road up: nothing is graded, no note is left
          saying the road is down (so it can be planned again over better ground), the pass says `nothing laid` in the log
          with the leg, its two ends and the reason - a window too small for the leg is a route that wants another mark in
          it, ground with no way through it is country the road cannot cross - and `/tmroad build` answers the hand with the
          same sentence. `Descent.why` is where the two faults are told apart.
- **A road takes its width from the ground it leaves.** Two towns stand and everything else is a camp, a ruin or a road
          between them, so `/tmroad build <x> <z>` with no word after the coordinates now reads the ground it starts on: a
          town's own ground - or the approach out of it, `Places.towns` and the place's own radius plus the transition band -
          takes the three bands and narrows seven to five to three as it leaves; anywhere else is a wild track three wide,
          which is what a camp's door wants. The word still overrides it - `wild`, `wide`, `main`, `urban`, `town` - and is
          the one thing about a road's width that is ever chosen by hand: the hold's road is `main` in `Roads.withGundabad`.
          Bree is a town the catalogue names and has not stood yet, so a road begun there is a wild track until it does.
- **A refused crossing lays no road at all, and a span takes its banks from the ends of the run it met.** Where the plan
          would not bridge - no dry ground within ninety-six blocks, water wider than ninety-six columns, or water running
          the wrong way across the road - it used to hand the route back as one straight line, which is a paved ford nobody
          chose with the country either side graded down to the riverbed on the way to it. It now answers with nothing and
          the reason: `/tmroad plan` and `/tmroad build` say it to the hand that asked, and the log says it for a road the
          world asks for. The banks are no longer found by walking out from the middle of the run either. A run a few lanes
          long and a little diagonal has its middle over water either side of where the road actually crosses, so a span
          stood there lies at an angle to the road it carries and both approaches have to be forced onto a line the road
          never travelled; the near bank is now the last dry column behind the lane the road first meets water on, and the
          far one the first dry column past the lane it leaves by - the water the road found, and nothing else. A run that
          turns back on itself is refused rather than given two abutments in the same water. And the three bands a build's
          road is laid by are now thirty-two and ninety-six blocks from its walls: seven wide of the town's own paving,
          five of cobble and path through the approach, three of the country's own track beyond - measured from the build's
          footprint where the road carries one, and from the block it leaves by where it does not, which is the case a
          planned road is. `/tmroad build <x> <z> town` asks for the bands; `wild`, `wide` and `main` stay one width the
          whole way.
- **Plan a road from its two ends instead of walking a hundred marks between them.** A route's way is now worked out
          from the drawing: `RoadPlan` reads `ArdaMap.sample` - the same function the chunk generator builds the ground
          from, and one that answers for country no chunk has made - at sixteen blocks a lane across a corridor a hundred
          and ninety-two blocks either side of the straight line, and every step pays for the rise it takes over a grade
          of 0.35, for the turn it makes and for each lane of drawn river it would cross. Nothing in that reads a block or
          generates a chunk, so the old forest road's three and a half thousand blocks cost about seven thousand noise
          reads - milliseconds - where the pass's own search for that length is a bounding box of ten million candidates
          against a budget of six hundred thousand. Past the budget the walk gives up and lays the straight line, which is
          why a long road could not be walked at all without marks every forty blocks: the plan leaves a bend every three
          hundred and twenty, which is what keeps each leg short enough for the pass's own search to refine it against the
          ground the world actually has, and it is the only thing the ammolite pins were doing. Water comes out of the same
          read: a run of river on the chosen line becomes a `Route.Crossing` whose axis is the way the road is travelling
          there, whose banks are the first dry columns either side, whose water top is the highest the run is given and
          whose deck stands two over it - the rule both walked spans were built to - and the road comes out as the sections
          it is made of, dry, span, dry, each of which the pass already knows how to lay. A span is refused past ninety-six
          columns, and the log names the stretch and its width rather than raising a viaduct nobody surveyed. `Roads.wander`
          gives a planned road a thirty-two block search window against a walked one's ninety-six, so a leg reads the hem
          either side of it - about ninety chunks - instead of the four hundred the old window did. `/tmroad plan <x> <z>`
          says what a road would be and lays nothing; `/tmroad build <x> <z>` starts a pass for each section, stepped by the
          world as its chunks load, with `wild`, `wide` or `main` after the coordinates for the width.
- **The walked network is erased, and the hold's road is the first that will be planned.** Every route written out by
          hand is gone from `Roads`: the valley's road and its crossing, Elladan's branch, the Thorenhad road, Barachen's
          camp, the wood ruin and its link, the mountain way, the ford way, the three cave roads, the high pass road, its
          bridge and the climb, Glóin's road, its stream crossing and the road east, the old forest road with both of its
          fords - and the bend table, the kinds, the cuttings and the banded list that went with them, four hundred lines of
          surveyed ground whose numbers a plan answers for now. The builds are still standing, so the network is rebuilt by
          revisiting them: one block to leave from, one to arrive at, and the country between the two read from the map.
          `Roads.ROUTES` is therefore empty in a fresh world, and a road is in it because it was planned - named in the
          code, as Gundabad's is, or planned at a build and remembered for the run. Gundabad's route keeps its two null
          marks: with either unset the plan returns nothing and the road never appears, which is the silence the route had
          before, and those two blocks are still the whole of the survey it needs.
- **Write the Gundabad road's code side, with its two ends left for the scout's marks.** The hold's road now has a name -
          `tolkienmobs:roads/gundabad_road` - a route, a slot in the bend map and a kind, so that the walk down out of the
          valley has something to be written against and the road can be judged on foot the moment it exists. The two ends
          are `GUNDABAD_GATE` and `GUNDABAD_JOIN`, both null, and the route only joins `Roads.ROUTES` once both are
          pinned, because a route is two ends and two ends invented are a road laid across country nobody surveyed: with
          either null, `withGundabad` hands back the list it was given and nothing anywhere sees a new road. They are
          declared above `ROUTES` and not below it for the reason this file has bitten itself twice already - the list is
          built once at class load, so a mark written under it would still be null at that moment and the road would never
          appear and never say why. Its bends slot is in the map with an empty list, one leg per mark as the walk is walked,
          and its footprint is the four noughts every road with no build behind it carries until the hold's own cut is
          measured and the box goes in to stop the grading at the gate. The kind is `WILD_WIDE`: five wide, of the
          country's own gravel and dirt, which is how every walked line here is laid so the line can be judged before it is
          given a width of its own. What it is meant to be is a hold's road, `Kind.MAIN` - seven wide of the same country
          surface, a road rather than a track - and naming it in `KINDS` is what makes that a one-line change once the walk
          holds up.
- **Ask a finished road's own note where its road is, station by station, instead of trusting one block.** The Old Forest
          Road's note said it was down for all 4,022 of its stations, and the first 559 of them - from Glóin's camp east
          past the Old Ford to the edge of the next region file - held no road at all: no gravel, path, coarse dirt or
          cobblestone anywhere in a forty-one block window either side of the line, while everything from x 8,702 to the
          far end at 11,842 was complete and paved. The saved note, the region files and the logs agree on how it got
          that way: the pass writes its own cursor down every eight steps and at the turn of every stage, the world
          saves its chunks on their own schedule, and a stretch laid before a save that did not carry it is a stretch
          the cursor has already walked past - 17 Nov's headless pass carried a cursor of 942 stations out of the
          morning's session and finished the remaining 3,080 in 110 steps, and the 559 the earlier session had claimed
          were not on disk. Nothing ever looked: `standing` compared a single block, the grass under the road's first
          station at the camp gate, and natural ground satisfies that, so `down` kept answering yes, `pass` kept
          returning null, and the gap could never be walked again. The note now carries the alignment and the grade, so
          a dry road is asked for its own road: twenty-three stations - the first eight, the last eight, and seven
          across the middle - are each read at the grade the note gives them, in the three middle columns, for the block
          `surface` would have laid there. Stations inside a build's own footprint and stations over surveyed water are
          the build's and the crossing's and are not counted either way, and up to a fifth of the rest may be missing
          before the note is doubted, so a road with a block knocked out of it is still a road. A note that disagrees is
          walked once more, and only once per run, so a station a pass leaves open by design cannot plan a road on every
          chunk load of it.
- **The Carrock's pin is in the source table now, and the pass says when it cannot stand the rock.** Two rounds of "it did
          not stand" came to the same shape: the comment was written and the statement under it never was. `Pins.SOURCE`
          carried the Carrock's explanation - its corner at 8307, 187, -4165 - without the `Map.entry` itself, so
          `Carrock.autospawn` found no pin and returned in silence, which is exactly what the log showed: the start hook
          ran (the Shire's milestone line proves the listener was in), and nothing about the Carrock followed. The entry is
          there now, and the pass no longer fails quietly: a server with no Arda level, and a world with no pin under the
          name, each log a warning naming what is missing.
- **Bed the Carrock into the river with stone, and only where the water runs straight to the bed.** `Carrock.seat` reads
          down every column that carries one of the build's own blocks on its base course: courses of water are counted, the
          first block that is not water ends the read, and that whole water column is filled with stone only when the block
          it ends on is ground and at least one course of water was passed. A column already standing on stone needs no
          bed, and one hanging over a cave or over open air is not the river's to fill, so both are left exactly as they
          are - nothing is bedded to a guess. The stone is plain rather than the build's own rock, because what it stands
          for is the river's bed and not a piece of the Carrock, and it is written without neighbour updates so the river
          is not asked for one. The read stops sixty-four courses under the base, so a column over something that is not a
          river is never filled to the world's floor. It runs with the autospawn and on every start after it, since it
          writes only where water stands under the rock: a world that has held the Carrock since before there was a bed
          gets one the next time it comes up.
- **The Carrock stands itself on the first start of a world.** `Carrock.autospawn` runs in `MilestoneHandler` after the
          gate, the keep and the cave: it takes the pin under `Carrock.NAME` - 8307, 187, -4165, the cut's low north-west
          corner - lays the three pieces of the cut shipped in the jar, and writes a note beside the cut in the save's own
          folder, the dimension, the corner and the table's own fingerprint, so every start after it reads that note and
          does nothing. A world whose note is missing, or whose pin has been moved, is laid again rather than cut about:
          the pieces carry no air, so a second laying writes the rock's own blocks and leaves whatever else is standing
          exactly as it is. The planting takes the table's lowest offsets off the pin, the rule `/tmcut place` already
          uses, so the world's pass and a hand's command cannot disagree about which block the corner is; `/tmpin set
          carrock ...` still overrules the shipped pin, and a cut that has changed since the world was laid answers with a
          different fingerprint and is laid afresh.
- **Cut the Carrock's picked-up base plate off and pin the rock at 8307, 187, -4165.** Standing in the owner's world the
          first cut read as rock plus terrain nobody had built: the riverbed and the banks it was boxed through, a plate no
          more than six blocks deep that spread over the whole box and came out of the water at the south end as three
          tongues of bare dirt. Every column whose highest block stood at or under that plate's top (y 5) is now cut away -
          2,860 blocks in 1,189 columns, nothing above them touched and the rock's own foot left whole - so the pieces hold
          36,079 blocks in ten kinds and the spruce leaning off the west side keeps its fourteen hanging leaf columns. The
          box, the name and the anchor are unchanged, so the trimmed build still stands on its low north-west corner;
          `Pins.SOURCE` now carries that corner for this world under `Carrock.NAME`, which is the entry the cut will be
          raised or generated from, and `/tmcut place carrock 8307 187 -4165` lays the very box the owner pinned until one
          of those is written.
- **Ship the Carrock as a named cut with its anchor corner fixed.** The rock the owner shaped in the Anduin is cut and in
          the jar at `data/tolkienmobs/structure/carrock/` - three 48-cube pieces and a `carrock.parts` table, 42 x 57 x 85
          from its own low corner, 38,939 blocks in eleven kinds - so `/tmcut table carrock` and `/tmcut place carrock
          <corner>` work in any world with nothing copied into a save. The river's water, the air and the four ammolite
          markers the box was measured with are all left out of the pieces. `Carrock` declares the one name (`carrock`),
          the size and the anchor: the cut's own low north-west corner, which is the block a placement names, so the spot
          still to be chosen is one coordinate rather than a box, and the parts table is zero-based so a pin and a
          `/tmcut` corner mean the same block. How it comes to stand is deliberately left undecided - worldgen and a raise
          at a pin would both ask for it by that name, and this cut does not change either way; nothing generates or
          raises it yet.
- **Keep Hobbiton's hall pin in the table every world reads.** The hall was pinned by hand in the owner's own world,
          where `checklist` and every other world could not see it, so a new world would have had no Shire hall until
          somebody stood in the village again; the pin it was taken at - -529, 101, -74, read back out of that world's
          `pins.dat` once the hall had been raised from it and looked at - is in `Pins.SOURCE` now, under
          `TownHalls.key("hobbiton")` rather than a typed string, so the table and `/tmpin hall` cannot drift apart
          about what a town's hall is called. The hall is left exactly where it stands: it was called perfect as it was
          laid, so its seat, its height and its front as the template was cut are all kept.
- **Cut the bank at a build's edge instead of leaving it as a cliff.** The drape never cut the ground outside a deck,
          because cutting it round a face the build leaves open opens a trench where the country was; over the Shire's
          village that rule reads the other way round. The village sits at the foot of its own country, so the ground
          along its south edge stood eleven blocks over its paving with a raw face between them - measured in the
          owner's `Progress Check 1` world at x -430..-438, z -45, where the country stands at 108 and the village's own
          ground at 98 - which is the wall in his screenshot, taken from (-504, 101, -46). `Dressing.BANK_BLOCKS` (3) is
          the line between the two cases: a column that stands three blocks or more over the build's own floor is dressed
          exactly as every other column is, cut to the join at the wall and eased back into the country over the reach,
          while the shallow grooves round a city's open faces are left as they were, and a column that is already at or
          below the floor is still never cut. Nothing else about the drape moved: water at an outflow, a spring held up
          in the build and the fill that underpins a slope are all as they were. `Hobbiton.DRESS_VERSION` is 2, so a
          world holding the village re-raises and re-dresses it on the next start rather than being left with the cliff.
          Not looked at: the new rule has not been run in a world.
- **Stand each town's hall at its pin once the town is finished, rather than waiting for a hand.** The pin said where a
          hall belonged and nothing read it, so a town's hall was still a template and a pair of coordinates; it stands
          itself now (`TownHalls.raise`). Where is the pin, which is the block the builder stood in - the hall's own ground
          course is laid in the block they stood on, so its floor comes up into the paving rather than sitting a course
          above it - and which build is the town's alliance, as before. When is the part that needed a rule: a town's pass
          lays its cut, clears the footprint out of the country and dresses the ring round it, so a hall stood there early
          would be taken away by the ground it was meant to stand on. `TownHalls.waits` asks the town itself - the Shire's
          village by its note (`Hobbiton.complete`), the valley by the bands its marker has laid (`CityCommands.complete`)
          - and the hall goes up at the end of the village's own pass, before its folk are stood so nobody is placed inside
          a wall; on the next start for a world whose town already stands; and on the spot when `/tmpin hall add` pins one
          into a town that is finished. The chunks the hall's box touches are asked for first, since a hall pinned in
          country nobody has visited has no ground to stand on, and a hall already standing at the corner is left exactly
          as it is. Laid unturned, because a template has one front and which way it should face is the town's business.
          Not looked at: no hall has stood in a town in game yet, so its seat and its front are unverified.
- **Put the marks the world draws behind one client switch, on by default.** The mark over a notice board was drawn for
          every player whether they wanted a label hanging over the wood or not, and the only way to be without it was to
          change the mod; it now reads `questBadge`, the switch the scroll over a giver waiting for a page back already
          answered to, so one setting clears both - the two marks that stand in the world rather than on a nameplate.
          What the switch leaves alone is the plate: a player who has turned the world's marks off still reads the work in
          front of him, and the boards keep their drawers and their paper untouched. It ships on now (`questBadge`,
          client, default true) where it shipped off while the scroll was being held up against the text `?`; a file
          already written with `false` keeps its answer, since an existing config is never rewritten.
- **Stop scattering the mustering halls and pin one in each town instead.** A hall generated by a spread stood in a
          field before the town that owns it existed, and one per alliance for a dozen towns to come would only get worse;
          the three definitions are gone from the structure table, along with the generated structure, structure set,
          template pool and `has_structure` tag files, and the templates stay in the pack for hand placement. `/tmpin hall
          add <town>` now pins a town's hall where the caller stands - the key is the town's, `hall:<town>`, the template
          is its alliance's, and the town list comes from the catalogue (`Places.towns()`: Hobbiton, Bree, Rivendell) so
          a name cannot be mistyped - and `/tmpin hall remove <town>` forgets it, both printing the `Pins.SOURCE` line so
          a pin found once can be kept for every world. Nothing raises a hall at its pin yet; that is the town build's
          job, and the pin is the block it will read.
- **Say the door in red and stand a mark over the notice boards in green.** Two inks the sheet did not have, cut from
          the drawn gold art by re-reading its three shades as a ramp, so the outline and the light are still the artist's:
          red for a hall's own page while the player is below `STRANGER` (where the plate used to say nothing, which a
          player cannot tell from a hall with no work left), and green for the boards. The notice board had no state a mark
          could hang on, because the paper lives in the player's pack and a blockstate is the same for every reader, so it
          grew an empty block entity whose only job is to carry a renderer: `NoticeBoardMarkRenderer` submits the mark as a
          nameplate through `submitNameTag`, which gives it the billboard, the distance scaling and the ground occlusion of
          every plate in the world, and `BoardMarks` answers from the reader's own inventory - no contract is green `!`, a
          finished one is green `?`, work still in hand is nothing. Cyan keeps its old meaning and is now read as the
          wandering traveller's errand, which is what a giver always was.
- **Draw the quest marks as the artwork's icons instead of as punctuation.** The owner's sheet - two shapes by three
          inks - is cut into six icons on its own pixel grid (a 4x11 `!` and an 8x11 `?`, black outline and three shades of
          fill) and each one is a bitmap glyph of a mod font, `assets/tolkienmobs/font/quest_marks.json`. The plate names a
          glyph and puts the mark font on it, so the mark is still a letter on the nameplate - no render pass, no texture
          pass, nothing to cull - while white says work in hand, gold the mainline and cyan an errand, and the shape keeps
          saying which state it is. White `?` has no state to wear and ships with its code reserved. How the icons sit on a
          real plate (11px tall, seated a pixel below the baseline) is not yet looked at.
- **A badge over a giver's head, for the turn-in state, behind a switch.** The cheap half of the item-over-head idea: a
          scroll (`item_scroll`) hung on the giver's own head bone, drawn by the `BlockAndItemGeoLayer` the hand item already
          uses, and only while the plate is asking for a page back. It is a second entry in that layer's list rather than a
          layer of its own, because every `BlockAndItemGeoLayer` shares one `CONTENTS` data ticket - two of them on one
          render state would each read the other's list - and `submitItemStackRender` tells the two apart by bone name:
          `head` lifts the badge 1.15 and draws it at 0.65 scale, while the hand keeps its own offset. `questBadge` in the
          client settings (default false) turns it on so it can be compared with the text mark, and the state comes from
          `QuestMarkRenderer.asksForReturn`, the same call the plate uses, so a body cannot wear a scroll and no question
          mark at once. Bone-locked on purpose: it turns and ducks with the head and is hidden by the ground the way the
          giver is. Where it sits over a man's and a hobbit's head is not yet looked at - no client has run it.
- **The portal between the worlds is a place at both ends, and the Shire has its milestone.** A portal lit anywhere in
          the overworld now opens onto the village's own crossing in Hobbiton, at `ArdaMap.hobbitonGate()`
          (-470, 97, -123) - the gravel the owner walked to - and the way back opens on the overworld's spawn point
          (`ServerLevel.getRespawnData().pos()`), so a traveller never has to remember where they came in to get home.
          Both ends keep the portal's own behaviour: a doorway standing within 16 blocks of the crossing is used, and if
          there is none one is built and lit on the ground there rather than dropping anyone out of the air. The village's
          travel stone stands at the paving the owner marked, (-503, 101, -314), laid with the hold's stone on every start
          (`MilestoneHandler.onServerStarted`); the mark there is gravel rather than ammolite, so the stone is tried in the
          spot and then on top of it and the paving is not dug up for it.
- **The village's folk are somebody: thirteen hobbits, each with a name and a face of their own.** The old pack's clone
          database holds twenty-two hobbits and fifteen skins under `customskins/hobbits`, thirteen of them modern 64x64
          art that sits on this model; the two legacy 64x32 ones are left out rather than drawn wrong. The village now
          keeps thirteen - one per face - stood by `Hobbiton.inhabit` with a name from the pack (Belba Butcher, Gararic
          Rumblebelly, Gomatrudis Proudmead, Ingunde Silentfoot, Isembard Underlake, Leufroy Swiftfoot, Marcus Hairyfoot,
          Nick Greenhill, Godomar Barrowes, Amanda Brockhouse, Melissa Headstrong, Regnetrudis Fallohide, and Hal Cotton,
          the one name that is ours because the pack's seventh male body has a skin and none), and the name is what finds
          the face: `HobbitRender` already looks for `middle-earth/<slug>.png` for a named hobbit, so the thirteen files
          were copied in beside Frodo's and Bilbo's and no renderer changed. A name already in the village is not stood
          twice, so a village raised again over itself keeps its people, and the log line now says who stands where.
- **The village stops killing the hobbits the Shire spawns in it, and stands eight of its own once it is dressed.**
          The chunks under the village are the Shire's own, so the game populated them as it generated them - on ground the
          pass then cleared and laid again - and the first measured raise logged **eighteen** `Hobbit hit the ground too
          hard` deaths, bodies falling with the hill they stood on. A spawn rule is the wrong gate: a chunk-generation spawn
          is finalised before it has a position, so the body is refused as it joins the level (`EntityJoinLevelEvent`)
          while the pass holds the footprint, which is the first moment both the body and its ground are known. The same
          raise now logs **zero** deaths, and runs a little quicker for not carrying them (920 ticks, 46.0 s, against 1242
          and 62.1 s). With the ground settled, `Hobbiton.inhabit` stands **eight** of the Shire's folk on the deck the cut
          was dressed to - columns whose top is within a block of the cut's own floor, with room to stand and something to
          stand on, so roofs, tree crowns and the hillside outside the walls are passed over - persistent, so coming back
          finds the same village, and topped up rather than doubled when a village is raised again over itself. Arriving
          at the Shire is arriving somewhere inhabited.
- **The note beside a standing village says what stood it, and checks the ground is still there.** `_stands.txt` was a
          dimension and a corner, which only says "done": a re-cut, a fix to the drape or a world trimmed under it all read
          as a village that is fine. It now carries the cut's fingerprint - the piece count and a CRC of the cut's table,
          `143-fe676c68`, which lists every piece, its corner, its size and the `deck` line, so a re-cut, a moved piece or
          another floor all read differently, and a block changed inside a piece is what a re-ship covers - a
          `DRESS_VERSION` for the drape's own rules, which a hand bumps when the ring's settling changes, and seven probes
          of the ground: the top of a column and the block it stands on, at the corners of the footprint, its middle and
          two more. A start that finds a different fingerprint or version raises the village again, and so does one that
          finds most of the probes moved - the height moved more than six blocks, or the block is not the one written -
          so a trimmed or replaced world re-raises instead of staying half-missing, while one grown tree or one trampled
          grass block does not. The probes read only chunks the level already holds, never asking for one, because a start
          that generated a chunk to measure it would be the very stall the pass exists to avoid.

- **The village raises itself with nobody online, counts its own ticks, and a world holding a stale one raises it again.**
          The ground stage that asks for its chunks a chunk at a time had never been measured, and could not be: the pass
          waits for a player within 192 blocks, and a `runServer` console does not reach the world. So there is now a
          switch and a script. `raiseShireOnStart` in the mod's server settings (`TolkienMobsConfig`) takes the gate off at
          start, and `tools/raise_shire.sh` boots a fresh level with that switch set, waits for the village to stand,
          prints the two lines that matter, stops the server and restores `server.properties` and the config. The pass
          counts its own ticks to make the line worth reading: **1200 ticks (60.0 s)** - 1210 (60.5 s) on the run before
          it - to stand the village on a fresh level with nobody in the world - 1326 chunks of ground, 143 pieces, 1290 bands, 701 dress steps against a 5 ms
          budget, and no tick anywhere near the watchdog. That is the first measurement of the ground asked for and taken
          in the same tick, and it says the shape holds. The note beside the cut (`_stands.txt`) now says what stood the
          village and not only where: the cut's fingerprint - its piece count and a CRC of its table, so a re-cut, an added or
          moved piece or another floor all read differently - plus a `DRESS_VERSION` for the drape's own rules, so a ring fixed in code reaches a world that already holds the village rather than only worlds made after
          the fix. It also records the ground at seven columns across the footprint, its top and the block it stands on, and a start
          that finds most of them moved raises the village over what is there, which is the case of chunks trimmed or a world replaced under a
          note that would otherwise keep half a village. The probe never asks the level for a chunk - it reads only the
          chunks the level holds, and leaves the question to the first tick a player is near enough to make it free - so
          the start stays free of chunk generation, which is the one rule this pass has.
- **The new Shire code, read again with fresh eyes and cut where it was doing the same thing twice.** `Dressing` had
          a `drape` overload with a `trees` flag that nothing called but its own two forwarders, so those go straight to
          the deck-aware one; `CutCommands.deck` returned the city's answer from three places and now falls through to it
          once; `Places` stood its towns with a radius every caller passed as `TOWN_RADIUS`, so `standTowns(level)` stands
          all of them - the list of towns and the list of calls can no longer drift - and the radius is private to
          `Places`; the board's `destinations` de-duplicated a list that `towns` cannot repeat; `MilestoneHandler` made
          the same call twice. `Hobbiton` carried three statics where one would do (`began` and `found` live on the pass
          itself), walked the whole chunk rectangle to find the nth chunk of it, and had a separate readiness sweep over
          1326 chunks that, measured, only moved the engine's forty-three-second drain from the first piece to the
          sweep. The ground is now asked for and taken a chunk a tick, so nothing is ever pending for the region as a
          whole - **unmeasured**; see the note in `docs/next-up.md`.
- **The village's pass is the size of what it does and no more.** Measuring the rise left its instruments behind -
          a nanosecond counter per stage, a progress line every ten seconds and a two-line report at the end: fields and
          helpers that stood the village no better than the one line saying where it stands. They are gone, with the
          duplicate javadoc on `watched` that still claimed an empty world raises the village, the per-piece ground check
          the readiness sweep already makes for every chunk, `Places.seen` (nothing asked), `Places.CODEC`'s public door
          and `ArdaMap.Channel`'s `place` label, which no reader used. `/hobbiton raise`, `/tmriver at|along`, the towns
          and the boards are untouched.
- **What raising the Shire costs is now a number, and a player's first start does not pay it in one tick.** The pass
          was measured on a dedicated server against a fresh level: 1326 chunks of ground, 143 pieces laid in 9.8 s, 1290
          bands of 200 columns - 257,868 columns and 1.99 million blocks - cleared in 9.5 s, 701 dress steps in 3.3 s, the
          whole rise about 60 s of wall clock over 1200 ticks at a 5 ms budget. Two ways of asking for the ground were
          measured and dropped: `setChunkForced` per chunk generated 1326 chunks on the server thread and held a tick for
          49 s, and `getChunkFuture` parks the main thread on the worker pool and held one for the watchdog's 60. The one
          long tick left is a single `getChunk` finishing the region's 1326 new chunks at once, 43.2 s, which is why the
          village now waits for a player within `REACH` rather than raising itself in an empty world - a chunk nobody is
          ticking is a chunk that cannot be finished cheaply - with `/hobbiton raise` for the rest.
- **A cut is laid without shape updates.** `CutCommands.lay` planted the piece with flags `2`, so every block ran a
          shape update and asked the level for the neighbour it stood against - a chunk generation per block at the edge of
          an ungenerated build, and the sixty-second stall a server watchdog found when the village was first raised. Flags
          `2|16` tell the clients and leave the shapes alone: the same measured cut lays in 9.8 s where it took 11.0 s,
          and nothing in a build needs the update.
- **The clear is cut into bands a budget can hold.** `COLUMNS_PER_UNIT` was 3000, which measured about 100 ms a band
          against a 5 ms budget; it is 200, so the clear is 1290 bands of about 7 ms rather than 86 of a tenth of a second.
- **The conversation payload is registered on the client only.** Its handler opens `ConversationScreen`, and the
          registration line loads it: a dedicated server could not start the mod at all
          (`NoClassDefFoundError: net/minecraft/client/gui/screens/Screen`). Guarded by the dist until the screen moves
          behind a client-only handler, which is how `SpawnFallenLeafFromPacket` already does it.
- **The drape reads its deck line from the build, not from the city.** `DECK_LIFT` was a constant of Rivendell's - a
          floor two blocks over the planted corner - and the Shire's village stands sixteen over its base, so nothing
          outside its walls was ever read as a deck and its whole ring was settled to the tops of the walls. `Dressing`
          now takes the number from the cut being dressed: a `deck <n>` line in the cut's own table, with the city's two
          kept as the answer for a cut that says nothing. `hobbiton.parts` says `deck 16`.
- **A drawn channel's level belongs to its build, not to the Shire.** `SHIRE_WATER`/`SHIRE_GRADE`/`SHIRE_REACH` and
          `shireBranch` are now one entry in `ArdaMap.CHANNELS`: a `Channel` record holding the level a build's water
          leaves at, the grade it falls at, the reach of its corridor and the course the mask drew, keyed by place.
          Hobbiton's level is its own pin plus the nine its water stands over it, so the numbers are the village's rather
          than a constant of the code's, and a second build with a channel out of it is one more entry. The levels
          themselves are unchanged - 96 at the wall, one block in forty of run, 80 at the Y.
- **The Shire stands itself in a new world.** `Hobbiton.autospawn` raises the village on the first start: the cut
          shipped in the jar at `data/tolkienmobs/structure/hobbiton/`, laid at `ArdaMap.hobbitonPlan()`, the country
          cleared out of it, and the ring dressed by the city's own `Dressing` job stepped a band at a time. A note
          beside the cut keeps the next start from doing it again, and the three `/tmcut` commands still work for a
          re-cut. The build no longer depends on a hand typing three commands into the right world in the right order.
- **Boards point at towns, and the towns stand.** `Places.TOWNS` is Hobbiton, Bree and Rivendell; `Places.towns(level)`
          offers only those a world has actually stood, so a sign board cycles the blank and the towns instead of every
          planted name in the catalogue. Rivendell and Hobbiton are stood at their pins on the first start
          (`Places.stand`) with a `TOWN_RADIUS` of 280 rather than a camp's 32, and the village's pin is the middle of
          its footprint. Bree keeps its catalogue entry and has no ground yet.
- **`/tmriver` says what the drawing makes of a column.** `at` reads the column under the hand and `along <from> <to>
          [step]` walks a line and reads one every few blocks: ground, water, the valley's ceiling and how much the
          valley takes off, straight out of `ArdaMap.column` rather than re-derived in a script, which had none of the
          bilinear read, the roll or the rounding the generator actually uses.
- **A mouth is filled with its own water, not just cut.** Taking the bar out of the drape's ring left the ring's bed
          standing dry between the mouth's water and the drawn channel, so the Shire's north branch left the city and
          stopped against air over a bed three blocks down. `Dressing.openMouth` now lays water over what it takes out, up
          to the level the mouth carries the water out at and never above it, so what the cut leaves is the channel's own
          water rather than a dry strip. **This is a drape change: run the drape again in an existing world.**
- **Every drawn river the ground stands well over is a valley now, not a slot.** `ArdaMap`'s bank grading had only
          ever been given to the dale and, since the last change, to the Shire's branch; the same trench is cut wherever the
          mountain mask lifts the ground over a channel, because the water keeps the drawing's level and the hillside keeps
          the mask's. `ArdaMap.buildRiverBanks` now grows one raster for the whole world from every cell drawn as river
          water - lakes left to `SHORE_CUT`, the dale's own water left to `daleBank`, and the lower of the two ceilings
          taken where they overlap so nothing is graded twice near Rivendell - rising at a rate read per channel from the
          tallest country `RIVER_SAMPLE` (96) blocks round its water, between 3 and 16 blocks a cell, and reaching
          `RIVER_BANK` (192) blocks out, far enough that the deepest crossing in the world closes before its rim.
          `shireBank` and the branch's own half-width constants are gone; the Shire's level rule and the dale are
          untouched. Ordinary drawn rivers measure 1 to 10 blocks under the ceiling on both banks and are not moved at
          all. **This is world generation: chunks made before it keep the old ground.**
- **The orc pieces stand on stone, not grass.** All nine templates in `structure/orcs` had their `grass_block` palette
          entry changed to `minecraft:stone` - the courtyard's own block - with the grass block's `snowy` property dropped
          rather than carried onto a block that has none. They were cut in grass country and read as green squares set down
          on Gundabad's paving. Block counts, footprints and the pieces' air are untouched; the templates were rewritten
          through a reader that round-trips every one of them byte for byte before it is allowed to write.
- **The Key-stone's vault is pinned where the chapter says it is.** The name `keystone` had no pin, so it was surveyed
          out of the middle of the country the map paints; it is now pinned to `GundabadCave.post()` - the cave's own
          dais, which is the block Gimli stands on - and page 2-5 names the place again rather than a bare point, so the
          needle carries "the Key-stone's Vault" and the walk is measured around the room it is in. `Places.ensure`
          stands the name every tick, the way Glóin's camp and the goblin fires are stood.

- **Where the work stands is now a file.** `docs/next-up.md` records the state of chapter two, everything committed but
          never seen in game, the chapter-three plan's next step and the roads from Glóin's camp to Gundabad that are
          still to be laid, and `AGENTS.md` sends a fresh session to it first.
- **Chapter three, written up.** `docs/chapter-three.md` lays out *Fires in the South* from the pack's own chapter -
          quests 56 and 60 to 69, dialogs 200 to 228 - as eleven pages (`3-1` to `3-11`), with the kind, giver and
          turn-in of each, the prose each book and leaf will print, the standing and courage on the ladder, and the
          ground, bodies and objects the chapter needs before the first page can be handed over. Nothing of it is in
          `Campaign` yet: it is the plan the implementation follows.
- **The Key-Stone's vault is in the cave beside Gundabad.** Page 2-5 named a ruin at the source of the Bruinen and walked
          to a place the map owns; it now points at `GundabadCave.post()` - the cave east of the gate, the one Gimli stands
          in - as a point with a reach of twelve, and the cave's mouth opens for that page as it does for the hold's own.
          Walking into the vault hands the stone over, so the page pays the object it is named for.

- **Glóin takes the Key-stone back.** The page that opened the stair is the last thing the stone is lent for: when he
          takes page 2-7 at the camp he takes the stone out of the pack and keeps it, with a line of his own.
- **Dugorim gets a boss bar and a titled nameplate.** The bar is kept in `DugorimFight` - red, notched ten, named
          `boss.tolkienmobs.dugorim` and filled from his health, shown within thirty-two blocks and taken down with him -
          because the mod's other bosses own a `ServerBossEvent` as a field and his body is the pack's dwarf. His plate
          now reads "Dugorim, Captain of the Dourhands", and `DwarfRender` asks for the words before the first comma when
          it looks for art, so a title does not cost a named dwarf his face.
- **Dugorim fights back.** He is armed with an iron axe and given a captain's forty health, and his side of the fight is
          written in `DugorimFight` rather than in a goal list - the pack's dwarf is a `Villager`, whose movement belongs
          to its brain, so an attack goal added beside it would be walked away from. The driver runs after every body has
          ticked, wakes for a player within twenty-four blocks of the throne, closes to arm's length and swings once a
          second for six with a shove behind it, and a sixteen-block home area keeps him in the hall between fights. He
          stays killable, which is the whole point of him.
- **Dugorim keeps the throne, a Nazgul stands in front of it, and the key-stone lifts the gate.** The captain is the
          stage's boss now rather than a body in the cave: he stands on the keep's own seat (5501, 193, -7024), facing
          the hall, with one of the Nine four paces down the carpet in front of him - a real `entityttmnazgul`, given a
          home area and left to fight. Page 2-7 is not paid for the dwarf alone: Gloin refuses it while the wraith
          still stands, and the mark of the fall is saved on the player, so a second wraith is never stood for somebody
          who has already killed one. Gimli hands over the `item_keystone` with the page, and the coal lattice across
          the hall's mouth (5575-5581, y 190-199, z -6856) lifts a course every two ticks, bottom first, while a
          player carrying the stone is within twelve blocks - and settles back the same way, never on somebody
          standing in the opening.
- **Gimli hands over the Key-stone with page 2-7.** The dwarven captain is the one who carries it up the stair ahead of
          the player, so the stone changes hands at the moment the page that opens the way is given rather than being
          found in a vault - `Campaign.noteHanded` puts `item_keystone` in the pack the way the beryl is put there for
          1-5, and a player already carrying one is left alone so a page re-handed by a reloaded world cannot double it.
- **The two Last Refuge pages are spoken, in the old chapter's own words.** Both are in `Campaign.SPOKEN`, and the leaf
          is voiced by whoever hands the page over - which is Glóin for 2-6 and Gimli for 2-7, and so is exactly where
          the pack's four dialog nodes belong: `184`'s two halves and `185` are Glóin sending the player to the stair,
          `186` and `187` are Gimli sending them at Dugorim and wishing them a short, glorious battle. The replies are
          the pack's own two options - *I am ready now. What do I need to do?* and *Let me come back when I am better
          prepared.* - and the closings were already written, so they are untouched.
- **Dugorim wears his own face, and stands in the cave's room while his page is in hand.** The hunt on page 2-7 is a
          hunt of the pack's dwarves, so it never had a named body to be about: the Dourhands' captain is now stood at
          the back of the room - three blocks behind the dais, facing the tunnel - while 2-7 is the page carried, and
          found by his own tag afterwards so a body already felled stays down. He is deliberately not a giver, since a
          giver is made invulnerable on the way into a world and a hunt needs something that can fall. His art is the
          old pack's `dwarfwarrior 4` copied in as `middle-earth/dugorim.png` - a named dwarf's skin is looked for by
          the name he wears, so the face and the nameplate are the same fact.
- **The cave's keeper turns to face whoever comes in, the way a hall's staff do.** Gimli is planted where he is stood
          (`QuestGivers.plant`: no AI, a home area the width of the room), which is the state the halls' own givers are
          given on the way into a world, and the hall's gaze - six degrees every two ticks toward the nearest player -
          now reaches him from **twenty-four** blocks rather than twelve: his dais stands behind a six-block tunnel, so
          a player coming in through the mouth finds the dwarf already facing them instead of the wall. A hall's giver
          keeps its twelve, which is the width of a room with the bench at its middle.
- **The dev tool opens the cave's mouth, and Gimli stands inside it.** The pack's dev tool (`item_dev_tool`, either
          hand) counts as a page for the mouth's own rule, so the owner can walk in and out of a cave whose door is a
          chapter without asking the book for the page each time. And the dwarf the chapter sends below now keeps the
          cave rather than the hold's well: his post is the room's dais - `GundabadCave.post()`, (5703, 188, -6676) -
          reached from either the cave or the hold's middle, so a world that already holds him at the well has him
          recalled down rather than a second Gimli stood, and no new body is placed until a player with page 2-6 is at
          the cave itself.
- **The cave's mouth stands shut until somebody the chapter has sent walks up to it.** The way in is a wall of the
          hillside's own stone rather than a door: `GundabadCave.seal` fills the mouth's own two-by-two cells two courses
          deep, so it reads as rock from outside with the tunnel's hollow left behind it, and `open` takes it out again -
          the pair the goblin cave's ashen scroll drives. What opens it is a player within **64 blocks** carrying one of
          the hold's pages (the walk to it, Gimli's stair, the captain below it - `Campaign.onHoldPage`); the rule is
          asked once a second within twice that reach, so the mouth is shut again before the last player is gone, and it
          never shuts on somebody within twelve blocks of the room's middle. A fresh cut is sealed as it is cut, and a
          world that existed before the cave gets it on the first approach, when `keep` cuts the hollow if the mountain
          has none behind the mark.
- **The cave east of the gate is cut, on the second mark the owner laid.** (5693, 186, -6676) stands on the shelf
          where the mountain begins to climb, and `GundabadCave` cuts a way in facing west: a mouth two wide and two up
          with its floor on the shelf's own ground course, a six-block tunnel, and a room seven across by nine deep -
          x 5699-5707, z -6679 to -6673 - five courses clear with the ceiling ragged a course or two higher column by
          column, the same uneven cut the gate's rock and the goblin halls carry. The dais is three by three, one
          course up, of `angainor_bricks`, on the middle of the floor at x 5702-5704, z -6677 to -6675. Nothing else
          is placed: the mouth, the walls and the roof are the mountain's own stone. The cut is 412 blocks and all of
          it is inside chunk (356, -418), with at least eight courses of rock over the highest part of the ceiling - the
          tunnel is six blocks rather than three because the shelf's foot runs diagonally and a room starting three
          blocks in broke out along its southern corner, where the surface is 186 and the ceiling 193. It goes up with
          the hold on every start and is left alone once it stands; `/tmgate cave` re-cuts it on demand.
- **The hold has a travel stone, on the mark set into the gate's own facade.** The owner put an ammolite at
          (5584, 190, -6830) - the gate's facade plane, three courses up its wall - and `Stones` now stands one there,
          named **Gundabad**, on every server start: on the mark rather than with the gate, so a world whose gate has
          stood for weeks still gets its stone, and a stone of the same name already standing twenty-four blocks away is
          left alone. It is the gate's stone rather than the keep's because the facade is what a player walks up to -
          the hold is a doorway in a chamber behind it. Standing it at server start needed one change under it:
          `MilestoneHandler.updateMilestone` now sends the travel list to a player rather than requiring one, so a
          stone the world itself lays is registered in the save data and the list before anybody has logged in.
          Without that it existed as a block and not as a destination until someone right-clicked it.
- **The keep is final: one plan, one autospawn, one pin, and one dwarf at the well.** The hold's location is settled at
          (5471, 176, -7100), unturned - doorway x 5575-5579 centred on 5577 at courses 190-196, its floor course 189,
          its south wall on z -6918, the plane the ammolite was walked on. It lays itself on the first start of a world,
          after the gate and before the orc pieces, loads its own chunks and dresses its own doorway; every start after
          that leaves it alone. `Pins.SOURCE` now carries the plan, so `Pins.at(level, Sites.GUNDABAD)` answers the corner
          in every world instead of leaving a page free to survey a second hold out of the massif; `Sites.ON_THE_PIN`
          holds it, because a box 122 by 183 cannot be seated by measuring the mountain under it; and it is
          `SELF_DRESSED`, because the mountain is its roof and the sweep that carries a camp's ground to its roofline
          would open the massif over it. Gimli's post is lifted thirteen courses to the hold's own floor - the plan is
          the build's bottom corner, so the middle of its footprint is the course it starts at - which puts him at
          (5570, 190, -6948), local (99, 152) beside the well at the head of the spiral stair, instead of thirteen
          courses under the room `floorOf` could not reach.
- **The keep's doorway has a mouth again, and it is dressed on every pass.** With the wall whole the door was still
          unusable: south of the wall the chamber's ground stands a course high - course 190 is solid across the
          passage's whole width - so at the height a player walks the five-wide doorway read as a one-block slot at its
          middle column, and east of it the mountain's rock comes down from 196 over the other half. `GundabadKeep`'s
          own dressing, `clearMouth`, takes out a box around the door: the doorway's five columns and four more either
          side, eight courses out from the wall, from the floor's course up to the top of the opening. The floor
          course is never touched, so the apron comes out flush with the hold's floor rather than as a trench, and the
          wall is south of every cell it looks at. On the world this was measured in the box is 728 cells and 378 come
          out of it - 283 stone, 84 snow, 6 ice and 5 rockpile - leaving 13 wide and 7 tall clear at every course of
          the mouth. It runs at server start whether the hold was just laid or has stood for weeks, and `/tmgate keep`
          runs it too and reports the count, so a door re-cut by a later gate is dressed again.
- **The keep's own chunks are loaded by hand, and `/tmgate keep` lays it on demand.** The autospawn was written and it
          did not fire in a live world: the hold's box begins at chunk x 341, z -444, further north and west than the
          spawn chunks reach, so every chunk it needed was unloaded on the first start of a world - `hasChunkAt` answered
          no and `place` returned nothing, silently, which reads exactly like a keep that had always been standing. The
          box's chunks are now asked for by hand before the template is placed, which loads them and draws them where
          they are new ground. `/tmgate keep` is the same laying as a command: no position and no facing, because there
          is one box the hold fits - the plan the chamber's marked floor was measured into - and it reports the
          doorway's floor at 189, x 5577, z -6918 so there is somewhere to stand and look through it.
- **The keep behind the gate now stands, and its way in is the floor you marked.** The dwarven hold was written down
          in `ArdaMap` as a plan - 122 by 26 by 183 at (5516, 187, -7112), unturned - but nothing ever laid it; only the
          campaign's page pitched it. It is now stood up at server start, after the gate, and the plan is re-read off the
          mark the owner walked: an ammolite on the chamber's floor at (5577, 189, -6918), at the foot of the hold's own
          doorway. The template carries its floor as rock - fourteen courses of deepslate whose top course is the floor a
          player stands on, with the rooms over it and the doorways cut from course fourteen up - so the mark's course is
          that top course, local y 13, and the base is 176: the doorway's floor lands on 189 and the rooms fill the
          chamber's own section, 190 to 200, to the block. The doorway taken is the far one of the south wall, local
          x 104-108 with its middle at 106, and the wall's own course is local z 182, so the box moves to **5471, 176,
          -7100**: the doorway's middle lands on the mark's x, the wall on the mark's z, and the body runs the rest of
          its 183 courses west and north into the mountain. Every corner of that box is solid stone at 176 to 201 under
          244 to 315 of rock, so the hold is buried as designed and only its doorway shows. The order is the other half
          of it - the gate's hall is carried north to the keep's wall and cleared fourteen courses through where that
          wall will stand, so the gate lays first and the keep writes its own wall and its own five-wide doorway whole
          over the clearing; laid the other way round the hall opens the wall across the chamber's whole width and the
          hold has no door. `GundabadKeep` tests the doorway's own floor cell, so a keep already standing is left
          exactly as it is.
- **The Misty Mountains and Mount Gundabad are clear while the gate is photographed.** Both ranges close their fog at thirty-eight and forty-four blocks, which is what a mist is and also what hides a mountain from a screenshot taken outside it, so `BiomeMaker.CLEAR_RANGE_FOG` is on and both biomes read a near plane of 512 and a far plane of 1024 instead. It is temporary and it is one switch: set it false and the two mists come back exactly as they were, since their own numbers are still written at the two calls. Nothing generated depends on it - a fog is a look, not a world rule - and every other biome in the pack keeps the fog it had.
- **A capture's air is written, and it is the other half of a flat cut face.** Placing a structure writes the air it carries, so a build cut out of a hillside erases ground wherever it was cut with sky: the gate's eight files hold 489,812 air cells against 132,268 blocks, and the top course's east end - `gundabad_gate_3_1_0.nbt`, sixteen by thirty-three by forty-eight - is 25,344 cells and every one of them air, so that file alone clears a box the size of itself out of the mountain. The two upper pieces beside it are 94 to 99 per cent air. So a flat face left after a build is placed has two possible causes - ground not shaped to the build, or the build clearing ground it does not need - and the second is fixed at the capture: `tools/gate_capture.py --trim-sky` drops the air above the highest block of each column, and a piece that holds nothing but air can be dropped whole. Both are written into `AGENTS.md` as the rule for any build, with the warning that a passage's own air is load-bearing and only the box's sky is fair game. Nothing about the gate changes yet: its air is below the height of its wings and the pass's floor already leaves it nothing to clear inside the box.
- **The outside camp is final, and no floating glowstone is left standing anywhere.** All twenty-eight pieces outside
          the keep are settled: eight towers, four huts, the farm, the pit, the two rams and the twelve tents, with the hall
          recorded as the one piece placed by hand whose position was only ever found by matching its fourteen wall torches
          against the world. The cut tool's residue is gone too - it left a glowstone floating in the corner of every
          template's box, one block with nothing beside it - and the tent's (12, 11, 0) and the hut's (19, 18, 0) are cut out
          of their files as the tower's, the farm's and the ram's were. Those two templates' pieces were already standing, so
          `OrcPieces` now carries the corners and **sweeps** them at every raise: the cell is cleared only where it still
          holds glowstone, so a piece the world has built around keeps what it has. Each template keeps the glowstone set into
          its own base course; only the one hanging in the sky goes.
- **Twelve tents stand in front of the gate, the first pieces in the table no builder marked.** They are placed from a
          survey rather than a mark: the gate's front was counted for open ground - surface within two courses of level, four
          clear courses over it, nothing already standing - which comes to 5860 cells over x 5492-5656 by z -6829..-6766,
          thirty-two tent footprints of raw area, and 3087 of those cells in the 59-wide stretch in front of the hall's
          doorway. Packed 13 by 14 with four blocks between neighbours, twelve tents fit across the front and seven of them
          in the doorway's stretch, with the eight-wide corridor in front of that doorway left empty; each pad is set on the
          ground under its own footprint and the turns are mixed so it reads as a camp rather than a grid. In front of the
          hall itself nothing fits: it is a covered courtyard, floor 188 to 190 under rock at 200 to 203, ten clear courses
          where a tent is twelve tall, and only twelve blocks of floor between its open south face and the far tower row.
          That count is written into `docs/arda-pins.md` beside the marks, so the numbers can be argued with later.
- **The pit and the two rams are settled as final.** No geometry changed and nothing was moved: the pit stands with its
          base course on its own mark at 186 and its mouth at 200, and the two rams stand unturned with the faces that carry
          their corner glowstones at -6806 on bases of 186. What changed is the record: `docs/arda-pins.md` now says beside
          the farm's own note that all three pieces have been judged on the ground and are final - the farm on its first
          laying, the rams after two goes at their depth and one at their place, the pit after three at its height - so
          none of them is to be moved again on a guess.
- **The rams move six courses south, and the pit's base is set on its own mark at 186.** Both were judged from the
          ground. The two rams now stand with the face that carries their corner glowstone - the box's south face - at
          **-6806**, the block the builder pointed at, their boxes running north to -6823, so the push is 15 and only the
          push changed: same marks, same `NONE` turn, same base 186. The pit is the other half of the lesson: its mark at
          186 is not only its centre but its **base course** - the template's first course is its own ground, 22 by 22 of
          grass and dirt round 21 cells of lava, and everything above it is a one-block perimeter wall round a hollow
          shaft - so it is laid with no drop at all, its floor on the mark and its mouth at **200**. Both arrangements it
          has stood in before are in `FORMER`, so the low pit and the six-courses-back rams are taken down as the new ones
          go up; the rams' first grass pad stays in the courtyard behind them, the take-down sparing every piece its own
          base course.
- **The rams are dropped two courses and turned to face the gate, and the pit is lifted to the courtyard floor.**
          Both were judged from the ground and both kept their marks. The rams went up turned `CLOCKWISE_180` with a base
          of 188; they are laid `NONE` now - a ram's front is the template's low-z end, which unturned is the box's north
          edge, on the gate - and dropped two to a base of **186**, so their 18-deep boxes, x 5606-5615 and x 5613-5622
          by z -6829..-6812, stand as they did and only the piece in them turned and sank. The pit's rim at 186 sat below
          the courtyard floor it was cut into and read as a hole rather than a floor round a hole, so it is lifted two:
          **base 174, rim 188**, the level of twenty-four of the cells around its footprint. Both are in `FORMER` with the
          arrangements they first went up in, and the take-down's floor is now the higher of the new piece's base and the
          course above the old one's, so dropping a piece no longer scrapes the ground out from under its own pad - the
          pit's buried lava at 172 and the rams' first grass pads stay where they are, under the new work. The farm is
          settled as it stood and was not touched.
- **The pit, the two rams and the farm go up on their marks.** Six ammolite blocks the builder left at Gundabad: one
          alone at (5578, 186, -6813) for `orc_lava_pit`, and two pairs on the gate's face line for the rams and the farm.
          Three of them are not footprint middles - a ten-wide ram's front edge and the farm's back have no middle block -
          so `OrcPieces.Piece` gained a **push**, written per piece beside the mark, that moves the footprint off the mark
          once it has been centred on it. The pit is centred on x 5568-5589 by z -6823..-6802 with its rim level at 186,
          base 172 and the lava seventeen courses under the lip; both rams are turned `CLOCKWISE_180` so the template's
          cobblestone head faces the gate, pushed 9 south to put their front edges at -6829, a course clear of the wall
          (x 5606-5615 and x 5613-5622, z -6829..-6812, base 188); the farm is pushed 14 north, its back on the mark at
          -6860 and its gated wall looking at the huts (x 5607-5622, z -6887..-6860, base 188). The floating glowstones
          the cut tool left at a corner of `orc_farm` (15, 15, 0) and `orc_battle_ram` (9, 6, 0) are cut out, as
          `orc_tower`'s (11, 21, 0) was.
- **The lava pit joins the orc pieces, and its footprint is 22 by 15 by 22.** `orc_lava_pit` is the ninth and
          last piece: `Sites.ORC_LAVA_PIT`, on `ON_THE_PIN`, `FRONT_CORNER` and `SELF_DRESSED` with the other orc pieces, and
          named **`pit`**, **`lavapit`** and **`lava`** in `PinCommands.BUILDINGS`, so `/tmpin build pit` and
          `/tmplace build pit` both know it. Measured out of the file: the **whole 22 by 15 by 22 box is filled** - 1120
          blocks of stone, grass, dirt and gravel round a sunk floor of **21 cells of lava at local x 8-12, z 9-13, on the
          bottom course**, with 42 cells of water standing over it at y 3-4, 60 blocks of iron, 48 of stone brick, and
          nine skulls at the rim. It is the one piece that **carries air** - 6140 of its 7260 entries - because a pit is a
          hole, so laying it takes its whole box out first: nothing that stands inside that box, a hut or a tower, survives
          it being placed.
- **The three huts east of the hall stand a course higher and look south at the gate.** The builder marked
          their doorways with ammolite - **(5593, 187, -6898)**, **(5607, 187, -6898)** and **(5621, 187, -6898)**, the west
          edge of each body, where a player stands to walk in - and judged them a course low and facing west, so all three
          are now `Rotation.NONE` (a hut's one opening is in the template's south face, and the gate's front is south of
          them) with a **drop of 0**: their marks were placed in the plaza's own surface course rather than in the air over
          it, so their base sits on the mark, flush with the ground, where a tower's sits one course below its own. That is
          what `Piece` carries now - a template, a mark, a turn and a drop - because the marks were not all made the same
          way. A piece whose arrangement changes fails `Sites.wholeAt`, so before it goes up again its **former**
          arrangement is taken off the ground: `OrcPieces.FORMER` records the three huts as they were first laid (a quarter
          clockwise, a course lower) and `clear` removes each of the piece's own blocks by name, read out of the template's
          own file, only from the new base up - which leaves the old base course as the ground under the lifted hut instead
          of taking it out from under it. The hut west of the hall is untouched: the builder did not mark it, so it keeps
          its east-facing door and its course.
- **The four huts are spread across the plaza instead of laid on top of each other.** The builder's marks were 10
          to 19 blocks apart and a hut body is 13 wide, so all four stood inside one another. The open strip is measured now -
          the hall runs **x 5563-5591**, the mountain's foot closes it at **5538** west and **5633** east, and the tower row takes
          **z -6888** south, leaving x 5538-5633 by z -6889 to -6909 - and the four marks are set across it: **(5548, 187,
          -6899)** west of the hall and **(5599, 187, -6899)**, **(5613, 187, -6899)**, **(5627, 187, -6899)** east of it, 14
          blocks apart so each 13-wide body stands as its own building with one block of air between neighbours; a two-block gap
          needs two more blocks than the east strip has. The hut west of the hall is turned a quarter anticlockwise so its door
          looks east and the three east of it a quarter clockwise so theirs look west, which puts every opening on the plaza's
          middle at (5576, -6893).
- **The marked pieces rise with the server, and four huts join them on the plaza.** `OrcPieces.raise` is called
          from `MilestoneHandler.onServerStarted` after the gate, so the eight towers and the four huts stand as a world is
          logged into and nothing has to be asked for; the chunks under a piece are opened first, since a piece asked about
          from a closed chunk reads as air. A piece also carries a **turn** now: `orc_hut`'s one opening is a single column two
          courses high in its **south face**, and the huts' marks - **(5604, 187, -6899)**, **(5612, 187, -6892)**, **(5615, 187,
          -6905)** and **(5627, 187, -6903)** - are all west of the plaza's middle (5576, -6893), so each is turned
          `Rotation.CLOCKWISE_90` and its door looks west at that middle. `Sites.wholeAt` takes the turn too and turns the
          sampled blocks with it, so a turned piece still answers "already standing" correctly instead of being laid again on
          every start. The four hut boxes overlap each other by up to 16 by 7 blocks - the marks are 10 to 19 apart and a hut
          fills 19 by 20 - but `orc_hut` places no air, so they interpenetrate rather than cut one another.
- **The orc tower's cut marker is gone from the template.** The tower carried two blocks of glowstone, put there
          when the build was cut out of the world after it was built: one at a bottom corner against the base, one **floating
          at the top corner (11, 21, 0)** with air on all six sides. The floating one is removed - 729 blocks to 728, size,
          palette and DataVersion untouched - and the corner at the base is left, since it sits against the tower's own ground
          and reads as part of it. A world that already has towers standing keeps the floating block where it was laid; this
          changes what is laid next.
- **The gate's own marker is read from the box's corner, so it no longer re-lays itself every start.**
          `autospawn`'s guard added the marker offsets to `gatePlan()` — the middle of the box's north row — while every piece's
          position is counted from the box's corner `base(plan, Rotation.NONE)`. Eighty blocks east the same three numbers land
          in the middle of the gate, where the answer is always stone, so the guard said "not standing" on **every server
          start** and laid the whole gate again: its template's air took the half of anything standing on the footprint that was
          inside the box, which cut three of the four near towers in half at the facade every time the world was logged into.
          The guard now reads **(5513, 188, -6852)**, which is the netherrack it was always meant to read.
- **A marked piece that is not whole is laid again.** `Sites.standsAt` answers whether a building is there, and a
          majority is enough for a site - but a marker fixes a piece's place exactly, so a piece cut in half is damage, not a
          piece. `Sites.wholeAt` is the strict reading - every sampled block, over the same band of lifts - and `OrcPieces`
          asks it instead, so `/tmplace orcs` rebuilds a marked piece that something has laid over. The occasion: laying the
          gate again writes its template's air, and the near rank straddles the gate's south face, so the half of each tower
          inside the gate's footprint was cut away and the command then counted the survivors as the piece standing. It now
          raises the whole piece over the gate's own blocks.
- **The gate is closed from behind by a second rank of towers.** All eight ammolite marks by the gate are towers,
          not four with four leftovers: the far rank stands five blocks north of the gate's back face at **z -6882** - marks
          **(5539, 187, -6882)**, **(5558, 187, -6882)**, **(5598, 187, -6882)** and **(5628, 187, -6882)**, two either side of the
          passage's mouth - on ground one course lower than the courtyard, so each base sits at 186 and each box runs y 186-207
          and z -6888 to -6876. They go in the same `OrcPieces` table as the near rank, so `/tmplace orcs` raises all eight and
          `Sites.standsAt` leaves the four already standing alone.
- **A marked spot is enough to place a piece.** `OrcPieces` records the spots a builder marked with
          `tolkienmobs:block_ammolite`, and `/tmplace orcs` raises them: the middle of the piece's footprint goes on the mark
          and its base course one block below, the same rule the orc pins follow, and `Sites.standsAt` leaves a piece that is
          already there alone. Its first entries are the **four orc towers on the gate's own footprint** - marks
          **(5539, 188, -6831)**, **(5557, 188, -6831)**, **(5598, 188, -6830)** and **(5628, 188, -6830)**, each tower 12 by 22
          by 13 with its base at 187. No template in the pack carries ammolite - gate, keep and all eight orc pieces were
          checked - so every ammolite block in a save is a mark and the scan cannot be confused by a build.
- **The orc pieces bring no draping.** They join `Sites.SELF_DRESSED`, so the world lays one and leaves the ground
          alone around it: no sweep out to its rooflines, no cut inside its footprint, no underpinning under its base - the
          yard a builder pinned it on stays the yard they chose. The same list means no keeper, no steed and no travel stone
          at them either, which suits pieces being tried rather than places the campaign sends anyone to; a piece that ever
          becomes one comes off the list and gets its own dressing. Every other building keeps its ground pass.
- **A pinned orc building sits one course below its pin.** The front-corner rule took the pin's own height, which put
          the base level with the block the builder stands in; it now drops **one course** (`FRONT_DROP = -1`), so the base
          takes the ground under their feet. The owner's by-hand `orc_hall` is therefore one course above where the rule
          places it, which is the rule they asked for. The corner - the front's south-west, back by the piece's own depth -
          is unchanged, and so is every building not on the orc list.
- **A pinned orc building lands face-first on its pin.** The orc pieces are now on two lists in `Sites` -
          `FRONT_CORNER` and `ON_THE_PIN` - so a pin marks the **front's south-west corner** rather than the north-west
          corner, the piece runs back from the pin by its own depth, and it stands on the **pin's own y** instead of being
          seated on the ground. Both halves were needed, and both were read off the owner's own placing: an `orc_hall`
          pinned at **(5533, 188, -6891)** was laid by hand at **(5533, 188, -6912)** - the pin 21 north, the hall's depth of
          22 less one, x and y untouched - while the same pin laid by the world had put it at **(5533, 201, -6891)**: the
          corner right, the height seated 13 blocks up on the courtyard's surface. A full template match against the saved
          world put the by-hand hall at 93.2 % of its 3,033 blocks at y 188, against 25.6 % at 187 and 25.5 % at 189, which
          is what settles the y rather than the arithmetic. Every other building is untouched and still hangs off its
          north-west corner, so the camps, crags and the ruin stand exactly where they did.
- **The passage reaches the keep, and the doorway is cut.** The chamber out of the gate used to stop in the rock
          where the mountain first stood over its roof, three blocks on - 34 blocks, a tunnel ending in a face because there
          was nothing for it to reach. It now runs **53 blocks**, its last course on the dwarven keep's own south wall at **z
          -6930**, and the end is the plan's rather than the rock's: `ArdaMap.keepWall()` reads it from the new
          `KEEP_X 5516 / KEEP_Z -7112 / KEEP_WIDE 122 / KEEP_TALL 26 / KEEP_DEEP 183` beside the gate's, because the rock's
          answer changes once the hall is standing - its own roof reads as ground over the roof - and an end measured that
          way would walk back towards the gate on every pass. A **29 wide, ten tall doorway** is cut through the wall, and
          it is cut **fourteen courses deep**, the deepest the keep's south wall goes across the passage's own columns (one
          course at the face, one behind most of the eastern half, three to nine on the west, fourteen where an inner mass
          stands just inside it). The doorway is cleared every time the hall is laid, so raising the keep and laying the gate
          again opens it; until a keep stands there it is a pocket in the rock and nothing else. The keep's plan is also what
          settles its pin - a fixed piece of the location needs no measurement by hand.
- **The keep is underground, and the plan says where.** `docs/gundabad-keep.md` is rewritten for the owner's
          intent - the chamber out of the gate is the keep's approach, not a way up to a building - and it lines the two up
          to the block: the passage's floor's top is **189** and its interior starts at **190**; the `gundabad` template's
          floor is three courses, so its pin's **y is 187** and its interior also starts at 190. The pin is **(5516, 187,
          -7112)**, its own north-west corner, giving a box of **x 5516-5637 by z -7112-6930** with the roof course at 212;
          the passage has to run **19 blocks further** (34 to 53) to reach the south wall, and the doorway - 29 wide and ten
          tall - has to be cut, because **all four faces of the template are wall at floor level** and it has no door on any
          of them. The box was sampled at y 190 to 211 across its whole footprint: **0 % air**, so it is solid rock the
          template's own air will carve, with 18 blocks of rock over the south wall and 55 under the crown at 267. And two
          passes in `Sites.build` would wreck it as written: the seating reads the surface height (267) and would lay the
          keep on the crown, and `sweep` would cut the whole 122 by 183 footprint down to 187 - so the keep needs
          `ON_THE_PIN` and `SELF_DRESSED`, the two lists the shelter camp already uses.
- **The keep's site is worked out, and it is a shelf rather than a mountain.** `docs/gundabad-keep.md` records what
          the ground behind the gate actually is - the courtyard at 187, the cliff from z -6908 up to 267 at 2.4 blocks per
          block, and above it a level terrace **160 wide and 168 deep at exactly 267**, held there by the pass's own
          `Math.min(shaped, PASS_EDGE_TOP)` cap, walled by the drawn country at 298-299 on three sides and by the cliff on the
          fourth - and places the keep on it: the shipped `gundabad` template is **122 wide, 26 tall, 183 deep**, so it fits
          the shelf with nineteen blocks to spare each side, its front five blocks in from the lip and its back fifteen blocks
          into the higher ground. It also says where the eight orc pieces belong - the guard at the stair head on the lip, the
          camp on the courtyard at the gate's foot - and which three things are still open: the stair up the seventy-eight
          blocks of cliff, the height of `orc_hall` over the keep's roofline, and the width of the shelf's margins.
- **One table of building names, not two.** The orc pieces went into the catalogue `/tmpin build` already carries, and
          `/tmplace build` reads that same table rather than keeping a second one beside it: a name known to one command is a
          name the other knows, the suggestion list is the same list, and the orc tent is asked for as `orctent` because `tent`
          is the quest's own wild tent. Worth saying plainly, since the commands are many: **nothing else in the pack lays a
          build on demand**. `tmgate`, `tmcave`, `tmcity` and `tmroad` each lay their own build and do more around it, and
          `tmpin build` only writes a spot down - a site is raised by the page that names it - so laying a template here and now
          had no home at all, and `/tmplace build` is that home.
- **The orc buildings can be raised where you stand.** `/tmplace build <name>` puts one of the pack's eight orc
          pieces under the caller's feet - `ram`, `tent`, `tower`, `hut`, `farm`, `butcher`, `hall`, `hall2`, or any full
          template id - turned to face the way they face, its own floor at their feet and its middle on their middle. This is
          the builder's hand and not the campaign's: `/tmplace add` still names ground that a page is built on later, and this
          lays a template here and now so a piece can be walked round, measured against the ground and judged before it is
          placed for good.
- **The gate stands by itself at its own site.** `GundabadGate.autospawn` runs as the server comes up: if the gate is not
          there it lays the eight pieces, beds the floor to its own top, dresses the seam and carries the passage north into the
          mountain, and if it is there it does nothing at all. The question is answered by one cell - netherrack at world
          **(5513, 188, -6852)**, a course inside the west wing with the gate's own rock on all four sides and beneath, and a block
          the country never makes - so a world that has the gate pays nothing for the check and a world that does not gets the
          gate without a command. `/tmgate place` and `/tmgate clear` are still there for laying it again, and the ground it was
          shaped into is unchanged.
- **The box is the chamber's own passage, not the width of the building.** The measurements settled what the chamber
          is: at the rear face the rock closes to **x 5563-5591** with the floor's top at 189 and the first course of rock above
          at 200-203, so the chamber is a passage twenty-nine wide and ten tall in the middle of the face, and everything either
          side of it, and above it, is open. The hall is rebuilt to exactly that: its sides read off the rock either hand, its
          ceiling off the first course overhead, and that box - floor, two walls, three courses of roughened roof - carried north
          until the rock stands over the roof and three blocks into it. Measured against the world it runs **34 blocks in all
          twenty-nine columns**, so the courtyard between the gate and the rock stays open to the sky and the passage meets the
          mountain square on.
- **The hall's roof and walls are built to a thickness, and roughened.** A single course of roof read as a lid, so the
          roof is three courses (`HALL_ROOF` 3, 227 to 229) and each of the two walls two (`HALL_WALL` 2), with every column
          carrying 0-2 courses more above its nominal top from a hash of its own x and z (`rough`), and each wall course a course
          lower than the one inboard of it - so the outside is a bank of rock with a broken top rather than a wall with a flat
          lid. The rock is the gate's own mix, stone five parts in ten, cobblestone three and mossy cobblestone two, taken from
          the proportions the capture actually holds, so the new work does not read as one block repeated beside the old.
- **The chamber is carried north into the mountain as one hall.** The rear face is not one opening but three - west x
          5523-5554, centre 5565-5591, east 5607-5634, each sixteen rows tall - and above y 206 the pillars between them stop, so
          all three open into a single room 138 wide (world x 5517-5654, y 190-226). That room is what `extendHall` reads back off
          the placed build and carries north: floor at 187-189, a wall at each of its two ends, and a roof at 227, the room's own
          ceiling, out to where the rock stands over that roof and three blocks further into it. Because the mountain arrives at a
          different distance in every column the far end is ragged - three blocks north at the east end where the rock is already
          there, fifty at the middle of the width, fifty-four at the furthest. Nothing is ever taken away: every block is written
          only into a cell that is air, so the mountain keeps its rock and the hall closes onto it, which is what should make it read
          from outside as a way cut through a mass of rock rather than a tunnel built in the open.
- **The seam is dressed again, at six blocks, so the build stays proud.** Both ends come out of the mountain about six
          blocks proud - not at the same height or the same place on the two sides, which is the look the owner wants kept - so
          what the dressing has to close is the gap underneath that proud edge, not the proud edge itself. `DRAPE_REACH` is 6 and
          the drape is still fill-only: it walks the ring, reads the top of the gate at the nearest column and fills the ground up
          to it with the stone that is there, capped with the surface that was there, two blocks lower for every block further out.
          It never cuts, so a wall of rock standing above the gate is left exactly as it is, and `/tmgate place` now reports both
          numbers - bedded to the floor, and dressed into the seam.
- **The floor beds the ground beside it to its own top.** The floor stands two courses, so anywhere the ground beside
          the footprint sat a course or two below it there was a lip right at the build's edge - the last thing standing between
          the gate and the country. `dressFloor` walks the ring six blocks out (`FLOOR_REACH` 6) and fills up to `base + 1`, the
          floor's top at y 188, with the stone that is already there and the surface that was there laid on top. It only ever
          adds, and only where the ground is below the floor: at the ends, where the rock already stands at 282 against the
          gate's own 249-267, it leaves the rock exactly as it is. Runs as part of the placing again, and the message says how
          many blocks it bedded.
- **The gate's end tiles carry no air, and the drape is off again at placement.** The four end tiles - `0_0_0`,
          `0_1_0`, `3_0_0`, `3_1_0` - have had every air cell taken out of them: the west end keeps 52,525 blocks of rock and
          the east end 23,611, its upper tile having been air throughout and now holding nothing. The point is the seam: a
          capture's air is written, so at the ends it cut the mountain back to the gate's own silhouette and left the overhang
          standing where the build does not reach, and with the air gone the ground stays as the pass made it and the gate is
          absorbed into the rock instead of sitting in a trench of its own making. The middle tiles keep their air, so the
          plaza is still cleared. `GundabadGate.DRESS_ON_PLACEMENT` is `false` again so the ground can be judged bare, and the
          drape itself is unchanged and still runs when that is turned back on.
- **The drape is back on the placement.** It was turned off while the pass's own ground was being judged; the ground is
          settled now, so `/tmgate place` dresses the seam again and says how many blocks it added. Nothing else about the drape
          changed - it still walks the ring around the footprint, reads the top of the gate at the nearest column and fills up to
          it, and it still only ever adds - and no world needs regenerating for it: placing the gate in the world that is already
          generated runs it.
- **The owner has asked to be asked.** When a request contradicts something already settled, or one word could mean two different mechanisms, or a number has two possible reference points, the rule now written in `AGENTS.md` is to ask one short question with the options spelled out rather than pick one and build it - because a wrong guess costs a regeneration, a chunk trim and a playtest, and the owner pays that. Routine choices (which file, which constant, how to word a comment) are still made and mentioned rather than asked about.
- **The opening is narrower by 39 blocks on the west and 24 on the east.** The slope's shape is left exactly as it is - 
          floor 187 at its foot, two and a half blocks up per block out, capped by the drawn massif - and only where the climb 
          begin moves: `PASS_FOOT_WEST` 39 and `PASS_FOOT_EAST` 24 inboard of the footprint, so the gap the gate stands in is 
          that much narrower and the mountain arrives at the build's ends. West of the gate's middle the climb starts at 
          out + 39, east of it at out + 24, so the west end is into rock that is already 286 a block out while the east's foot 
          is at 246 and climbs to the drawn 293 by twenty-four blocks. Inside the footprint the ground is held to 267, the 
          build's own roof, because anything above the build's cells would survive the placing and sit on its parapet; outside 
          it the mountain may stand over the roof. The floor, the courtyard, the plaza and the rear mountain are unchanged.
- **The side mountains are a slope again, and the seam is left undressed so the ground can be judged on its own.** The rear mountain was right and the sides were flat from the ground, and the end lift was the reason: it is shared out by distance, so behind the plaza it is zero and the back kept its slope, while at the sides it stood a sixty-block step at the footprint's edge and left only twenty blocks of slope before the drawn plateau's three hundred. `PASS_SINK` is 0, so the foot is the floor on every side and the climb runs its full length out of it - 187 at the footprint, the roof's 267 by about thirty-two blocks, the drawn massif by about forty-five - which is the slope that was called right. And `/tmgate place` no longer calls `GundabadGate.drape`: the seam is left as the ground made it, so what is judged next is the pass and not a skirt over it. The drape stays in the pack and is called again once the ground is settled; the corner rounding, the 2.5-per-block rate, the cap at the drawn surface, the plaza and the gate's unturned placement are unchanged.
- **The courtyard floor is the pass's floor again, and the foot comes in as height rather than as a shifted climb.** The regenerated world came back with its courtyard at **63**, water and ice in it, and walls round it: the corner fix had started counting "ground south of the box" as outside the footprint, so the courtyard stopped reaching the pass's own early return and fell through to `Math.min(drawn, ...)` - and the drawn ground in the pass is the map's 63-level valley. Shifting the whole climb inboard by twenty-four blocks did the same thing for a second reason (the floor test was being given a distance that had already been moved) and additionally left the slope six blocks to reach a three-hundred-block plateau, which is the wall that came back. So: the pass floor and the plaza are recognized by the corridor again, the mountain's distance is measured from the footprint with the corners rounded but **not** counting the courtyard, and the foot comes in as height instead - `PASS_SINK` is 60, exactly the twenty-four blocks of climb it replaces, so the ends meet the rock at 249 and the roof's 267 by about eight blocks while the slope keeps its full run to the drawn massif. The lift fades out over thirty-two blocks south of the box so the courtyard's sides still climb out of the floor and the way in stays open.
- **The climb now begins twenty-four blocks inside the footprint.** The shape is right and the mountain is left
          exactly as drawn; the slope is simply started `PASS_SOONER` (24) blocks closer than the footprint, so the ground
          at the build's own edge already stands where the slope would have been twenty-four blocks out. At the ends that
          is 279 - floor 187, end lift 32, climb 24 x 2.5 - which is twelve blocks above the gate's roof, so the ends go
          into the rock and the slot between the build and the mountain closes. The steep part of the climb now falls
          inside the footprint, where it is the build's own ground and is laid over when the gate is placed; everything
          outside the footprint is the same mountain as before, arriving sooner.
- **The mountain comes up onto the gate's ends, and the gate faces the approach.** Two things were wrong where the build's ends met the rock. The ground met the gate at its lowest course and then climbed away, which left a slot between the build's own rock and the mountain - the ends stood in the open with the rock leaning away from them - so the foot now stands thirty-two blocks up beside the ends (`PASS_SINK`): 219 within a block of the footprint, the roof's 267 by about sixteen. The lift is shared out by which distance is doing the work, `outX / (outX + outZ)`, so the ends get all of it and the plaza behind the gate none of it, because the plaza has to stay level for the camp. And the site was laying the gate turned half round: `Rotation.CLOCKWISE_180` fills the same box but turns the build so its facade faces **north**, into the mountain behind the plaza, with its back to the approach - so the courtyard was behind the gate instead of in front of it. It is laid with `Rotation.NONE` from `ArdaMap.gatePlan()`, the middle of the box's north row, which fills the identical box (x 5496 to 5655, z -6877 to -6830) with the facade south.
- **The mountain is one flank, not two ramps meeting at a corner.** The climb was measured from one side at a time - the distance across beside the box, the distance north behind the plaza - so where a side and the back met the two profiles butted into each other and left a crease through the rock, which the owner photographed from both ends as a gap between the sides and the back. The distance is now measured from the footprint as a whole: `hypot(across, max(north behind the plaza, south down the pass))`, so the climb rounds the corners and the side and the back are one continuous flank. The rate is unchanged at two and a half blocks per block, the floor, the plaza, the courtyard and the cap at the drawn surface are all unchanged - only the corners are taken together instead of one axis at a time.
- **The mountain climbs out of the floor at two and a half blocks per block and stops where the drawing does.** Read out of the saved world round the placed gate: the ground beside the box was 293 west and 319 east against a courtyard floor of 184, a hundred and ten to a hundred and thirty-five blocks of step over a handful of blocks, and the drawn massif west of the courtyard is 317 to 319 right up to within sixty blocks of the floor - so the walls were half ours and half the drawing's own cliff. The peak-and-crest profile is gone. The ground now climbs out of the floor at once at `PASS_STEP` - two and a half blocks up for every block out, a stair of two or three each step - and the climb is capped by the country's own drawn surface, `Math.min(drawn, floor + distance * 2.5)`, so it reaches the massif about fifty blocks out and everything past that is the drawing, sloping away as it always did. Nothing is stood up above the drawn country any more, so there is no crest to end in a face: 202 at six blocks, 247 at twenty-four, the roof's own 267 by about thirty-two - the gate's ends are into the rock inside thirty blocks. The plaza behind the gate, the floor at 187, the courtyard in front and the drape's ring are unchanged.
- **The gate is measured to the block, placed at its own coordinates, and the valley comes down to its floor.** The eight templates were read cell by cell with the box anchored at the site's corner: **132,268 solid cells** against 489,812 of air, x 5496 to 5655 - 160 wide - y 187 to 267 - 81 tall - and z -6877 to -6830, 48 deep; every column's lowest solid is the floor course, so the ground under the gate has to be 187 across the whole footprint; the two central towers at x 5560 to 5592 and the west end at x 5496 reach the full 267, the wings stand 12 to 47 above the floor, and between them the footprint is only the floor course, so the gate is open there. Those are the numbers the terrain is drawn to. `/tmgate place` now lays the gate at its own site - the middle of its facade on `ArdaMap.gateFront()`, facade south - instead of where the player stands, because a gate that follows the walker makes every judgement of the ground a judgement of a different place; `here` is the dev tool that still lays it at the player's feet, and `clear` now takes the site's box down (`clear here` for a box at the player). And the mountains now rise **out of the floor**: their foot is 187 at the build's own edge, no lip and no shoulder, climbing over ninety-six blocks to the 330 peak and falling into the country beyond, so before the gate is placed there is a valley whose sides come down to the floor and no sheer wall standing where the build will go. `PASS_SINK` is 0; eighteen would bury the ends again and put the wall back.
- **The gate is the only way into the keep, so the ground behind it is a plaza.** The gate stands in a pass at Gundabad, which means what is behind it cannot be rock against its back: it is a floor an army can stand on, so the pass now lays thirty-two blocks of level plaza north of the box's back (`PASS_PLAZA`) and only then lets the mountain climb - out of the plaza, over fifty-six blocks (`PASS_BACK_RISE`), to the same peak of 330 the sides reach, and away into the country beyond that crest. The plaza is the pass's own width, so the mountain closes round it on three sides and the gate is the one way in: the camp behind it is enclosed, and the keep beyond is reached through the gate or by climbing the massif. The floor, the courtyard in front, the sunk ends and the drape's ring are unchanged.
- **The ground round the gate is a mountain drawn to the gate's footprint.** After a lip, a shoulder, a bank and a shaved cut all failed in their own way, the shape that was asked for is the obvious one: a mountain round the spot the gate stands in. At the build's own edge the ground already stands eighteen blocks above the top of its wings, so the ends of the gate are sunk into rock rather than meeting it flush; over twenty-eight blocks it climbs to a peak of 330, sixty-three above the roof, so the massif stands over the build rather than beside it; the crest is held for sixteen blocks and then falls at six in sixteen until the country's own ground is higher than the slope and takes over, about a hundred blocks out, where the drawn massif was already three hundred or more. The surface carries the map's folded noise at fourteen blocks either way and crests that wander eighteen along the pass, held off the build's own edge so the gate still meets the ground to the block where its ends go in. The front is untouched: the courtyard floor stays open, its walls stay the country's, and the peak is multiplied by how closed the pass still is, so it has come down to the lip by the time the pass opens. The floor, the rock behind the back and the drape's ring are unchanged.
- **The pass only ever cuts, and the mountains beside the gate fall back into themselves.** Standing ground up beside the gate was the wrong shape twice: a lip held at the height of the wings, and then a shoulder and a bank above it, each of which read as a face with the sky behind it once the gate was placed, because the country drawn round the courtyard is a basin lower than any lip worth choosing. What bounds the pass now is the drawing's own ground, shaved only where it stands above a slope that begins at the height of the gate's wings and runs back at one in one - about forty-five degrees - so the rock beside the gate falls away into the mountain and whatever the massif already holds below that slope is left exactly as it was drawn (`PASS_CUT_RISE`). Nothing is raised, so there is no face to be left standing when the gate clears its own box, and there is no rim to have to fill behind either. The cut keeps the map's folded noise, ten blocks either way at twenty-eight and eleven, and crests that wander eighteen along the pass on a ninety-six block scale, held off the build's own edge so the gate still meets the ground to the block. The floor, the wing top at its edge and the rock behind the back are unchanged, and so is the drape's ring. Unverified in game again - the ground has to be made again for it.
- **The bank behind the gate's walls holds the mountain up where the drawing would drop it.** Read out of the save again, at four heights on all four sides of the gate's box and across the courtyard: the ground east of the build arrives at 267 two blocks out and 280 at eight, north of the back it climbs 189 to 302 in sixteen, and the courtyard floor is 184 as far as the wall - but the wall itself was a rim. At z -6700 the foot is 249 and the crest 282 at x 5400, and by x 5360 it is back down to 255, because the country drawn west of the courtyard is a basin only two hundred and fifty up: a wall that rises above the ground behind it and then falls away is a curved face with the sky over it, which is exactly what the screenshots showed, and no amount of detail on the face changes it. The ground behind the shoulder now runs back at one in four - `PASS_BANK_FALL` - with the drawing's own ground wherever that stands higher, so the flank beside the gate has mass behind it instead of ending in a rim, and the crest wanders eighteen blocks either way on a ninety-six block scale (`PASS_SPUR`) so the bank carries spurs and the gullies between them rather than being a shell's edge. What the gate's own footprint sits on is untouched: the floor, the wing top at the wall's foot and the rock behind the back are the same, and the drape has the same ring to close.
- **The walls beside the Gundabad gate are the build's own scale, and broken like rock.** The first world generated with the pass was read back out of the save column by column, and it showed the pass doing exactly what it was told and the shape still wrong: the floor was level at 186-187 across the gate's whole box and the wall foot arrived at 267 at both of its edges, but the rock behind the foot was not the same on the two sides - 272 at four blocks west and 288 at ten, against 274 at four blocks east and 313 at ten - so a wall that climbed straight back into the drawing read as a mountain too small for the build on the left and one that would swallow its whole flank on the right. Neither is the drawing's fault: it is a massif, and the gate happens to sit nearer its eastern shoulder. The near wall is now the build's own scale instead - the wing top at the gate's edge, a shoulder of 302 by sixteen blocks, the same on both sides whatever the drawing says there, and the country's own heights back over another forty-eight blocks so the mountain is the massif again sixty paces out. The wall also carries the map's folded noise now, at ten blocks either way and at a scale of twenty-eight and eleven, held off the build's own edge so the gate still meets the ground exactly: an eased blend of eighty blocks lays down one-block treads of even width and reads as a quarry, which is the flat look the first world's screenshots showed on both sides. Unverified in game - the ground has to be generated again for it.
- **The ground has a house style now, and it is written down.** `docs/terrain-style.md` sets out how 1.18+ Minecraft builds mountains - multi-noise controls read at every column, ridged noise made by folding a value with `1 - abs(noise)` and squaring it, splines that spike a height instead of multiplying it, and a 3D density field that lets cliffs bulge - and measures `ArdaMap` against each: the heights here are drawn on a 512-block grid over a 16-bit heightmap mask and lifted by the mountain mask, the folded ridges are already `ragged` at 128 and 32 blocks weighted by the square of the rock, the curves are `smooth()` blends in each site's own shaping, and the third is the one thing this generator cannot do at all, because `ArdaChunkGenerator.reshape` retargets every column to a single height, so Arda's ground will never overhang and its caves are only ever the carvers'. From that follows the checklist a new pass, valley, notch or hillside is measured against: heights that depend on the column alone or chunk borders disagree, folded noise rather than raw, no cones, smoothstep blends with runs chosen on purpose - a face ten to twenty blocks, a hillside a hundred, a courtyard's easing two to four hundred - a floor level to the block where a build stands on it, faces dressed by `SurfaceRules.steep()` rather than by hand, and the shaping applied after `ragged` so it cuts crags and drawing alike. `AGENTS.md` carries the same rules beside the worldgen section, so the note is read before the next change to the ground rather than after it.
- **The gate's handoff note is written, so the work can be put down.** `docs/gundabad-gate.md` holds what the gate is - eight templates, 160 by 81 by 48, every cell of the capture carried including the mountain's own stone and gravel - the box it stands in at x 5496..5655 by z -6877..-6830 with its floor at 187, the pass cut to that box with its numbers measured off the drawing, the four decisions already taken, and the six things still open: the gate has no pin and is reached by `/tmgate` alone, the keep's pin is inside the new east wall, the sky over the wings is carried as air until the capture is trimmed, nothing has been seen in game with the new ground, the test world's chunks are the old ones, and `place` still lays at a player's feet where `site` lays at the box. `docs/HANDOFF.md` points at it from the end of the file.
- **Gundabad's pass is cut to the gate, so the mountain meets the build's own rock.** The massif's drawing is a plateau three hundred blocks up with thirty blocks of movement on it and no face anywhere a gate eighty-one tall could be set into, and the first pass cut through the country left the gate in a floor five hundred and sixty blocks wide with nothing within reach of it. `ArdaMap` now draws the notch to the gate's own box: the gate is a hundred and sixty wide, forty-eight deep and eighty-one tall, its facade south and its back in the rock, and the ground is measured to that - the walls either side stand at the gate's own edges at 267, the height of the build's wings, and climb to the drawn rock over sixteen blocks, so the mountain the gate carries runs on into the mountain the world grows instead of leaving the build in a trench of its own making. Behind the back the drawn plateau climbs out of the floor in fourteen blocks: 200 at three paces, 231 at six, 301 by fourteen, so the rock stands at the gate's own height as close behind it as its box allows. South of the gate the pass opens - the floor widens from the gate's hundred and sixty into a courtyard of five hundred and forty within sixty blocks and seven hundred and fifty at the mouth, and the walls ease from a cliff into a hillside - falling from the gate's own 187 to 176 where it joins the Hithaeglir's low ground; the floor rolls two blocks either way past the gate - and not under it, where it is level to the block, because a roll that lifted a floor course off the ground by one is a gap under the whole build. `/tmgate site` lays the gate at that box, facade south, and every laying is followed by `GundabadGate.drape`, the seam pass the city's own drape taught: it walks the ring around the footprint, reads the top of the gate at the nearest column of it and fills the ground up to that with the stone it already has, capped with the surface that was there, so a block out here and a course there are closed without a step. It only ever adds - the mountain over the gate is the mountain, and a drape that cut would lower the very walls the pass was shaped for. Measured off the drawing: 186 to 187 across the gate's own footprint, 267 at the wall foot on both sides and 277 to 301 eight blocks further out, and a gap exactly the gate's width for its depth and sixty blocks beyond it. The box is `ArdaMap.GATE_WEST`, `GATE_EAST`, `GATE_NORTH`, `GATE_SOUTH` and `GATE_FLOOR`, which is the one place the gate can go while the ground is drawn to it - and which means the keep's pin at 5655, 189, -6877, four blocks east of that box, now stands in the new east wall and wants walking again.
- **The Gundabad gate exists twice, so the two answers can be judged side by side.** The gatehouse scouted in the pass before the fortress - 160 wide, 81 tall and 48 deep, four 48-cubes across, two up and one in - is a build of eight templates, and it is not pinned or placed by anything yet. The capture keeps every cell the box holds - the gate's own 78,000 blocks, the 19,300 air cells inside it and the mountain's 54,000 of stone and gravel around it - because the rock is the build and not a wrapper around it. The gate was dug and dressed into a mountainside, so its skirts and retaining walls are that mountain, and a cut that dropped the stone stood the gate in the air with nothing under it, which is what the first trial in game showed. The sky above each wing comes with it too, as air as tall as the gate, and a `--trim-sky` pass on the capture tool cuts that back to the building's own top when the voids are adjusted. `GundabadGate` lays the eight by their offsets, turning the whole assembly to face any of the four ways (checked against the turned box: 8 pieces, no gaps, no overlaps in any of the four turns), and `/tmgate place` lays it at the player's feet with `/tmgate clear` taking the box out again so a spot tried is a spot left as it was found. The procedural cut it was judged against - a passage arched out of the hillside, twelve courses of jamb and eight of arch, dressed from the stone that was there - has been taken back out of the pack: the build lays fast enough that nothing is gained by cutting, and the cut could never carry the hand-dressed detail, so the eight templates are the gate.
- **Mount Gundabad has a biome and a country of its own.** The massif the notes call the orcs' northern capital was more of the Misty Mountains, so a player standing on it read the range's own biome and the name `gundabad` surveyed anywhere along a country 4,600 blocks long. It is its own country now: the generator paints it over the drawn range's own cells after the mountains' line - columns 20 and 21 of rows 5 and 6, x 5,120 to 6,143 and z -7,680 to -6,145 - at 195 blocks with 32 of relief, so three peaks over one massif stand where the map puts the mountain, north of the Ettenmoors and level with Angmar. `tolkienmobs:gundabad` is colder than the range it sits in (temperature -0.3, so snow lies all year and the weather is snowflakes rather than rain), closes the fog to 44 blocks against the Misty Mountains' 38, lays snow over bare rock through `gundabadSurface`, and grows the mountain's trees, rubble and rockpiles with no fallen leaves. Its people are the ones the pack already has: goblins and orcs in army numbers, the wargs of the slopes as the notes' Gundabad wargs, the duergar of the deep halls standing for the dwarves that lost the mountain, crebain overhead and goats on the rock - no new mobs. The name `gundabad` and the keep's site both survey in that country now, so page 2-3's fortress is looked for on the massif rather than anywhere along the range (the site search still walks out of it for level ground, since a building 122 by 183 cannot stand on three peaks). It is Arda's ground alone: the new country is in neither `TolkienClimate` list, which is what keeps a hand-painted massif out of the vanilla overworld's climate grid.
- **The wall is carried up to the hillside, so the trench behind it is not sky.** The ramp's first steps are cut down into the hill and the hill falls away above them, so the bore's own head is already below the surface a few courses in: a wall built to that head left the trench behind it open to the sky, which is the hole over the plug. The wall now fills the air above the bore up to the ground beside it - the higher of the two flank columns, capped eight blocks over the head - so the sky over the trench becomes hillside and the bank itself is untouched. Only air is filled, and opening clears the bore alone, so the fill stays and the doorway is exactly the bore's height. (The cut also pins its own doorway now - `goblins.door.<heading>`, the door's block with the measured sill in its y - because the pin's y is where the scout stood and the ground at the door is gone once the wall is over it.)
- **The cut writes its own doorway down, and the wall is built on it.** The sill of the mouth is the ground at the door column, but the pin's y is only where the scout stood - five blocks low in the scouted world - so a wall measured from the pin stood under the bore and left the mouth open above it, which is how a player walked into a cave that had never been opened. Nothing is left to measure after the cut either: the wall becomes the top of that column and the ground read there climbs seven blocks, which is why the scroll cleared the sky over the wall rather than the wall. The cut now pins its doorway before it takes a block out - the door's block and sill under `goblins.door.<heading>` - and both the wall and the opening are built from it, so the two are the same shape in the same place whatever the pin's y happened to be.
- **The wall is the hole's shape, and it opens all the way.** Two faults in the seal: it covered only the door's three courses, so the ramp's first steps - which the cut opens above the ground, behind the mouth - stayed a trench you could see over the plug into, and it measured its sill from the ground at the pin, which by then was the plug's own roof, so reading the scroll cleared the sky above the wall and left the wall standing with its top course gone. The wall and the opening now walk the cells `carve` itself opens, taken from the same arithmetic, and the sill is the pin's own height - written down before the wall existed and unmoved by it. The scroll is still spent on the wall, so the key's stores are made good while the page is carried: a key with no scroll in its slots is given one, since the wall is laid again for the next walk of the page.
- **The goblin king can be spawned, so Ulmug can stand.** `GoblinKingEntity` built its snowball goal in a field initialiser, and a mob's goals are registered from the middle of `Mob`'s own constructor - before any field initialiser on the class that is being constructed - so the goal added to the selector was null and the selector crashed hashing it. The first thing that ever spawned a king was the cave standing Ulmug, so every player entering the fires crashed the server on the tick the king was made. The goal is made in `registerGoals` now, where the selector is given it, and the weapon-change re-add asks for it first; the king's snowballs were the same null goal, so they come back with it.
- **The key comes with page 2-2 now.** The gate that hands it over asked `carriedCampaignPage`, and that is not the page in a player's hand: it is the Rider's page alone, since a campaign page is the wraith hunt and nothing else - so the question was never true for Glóin's page and no key was ever given. It asks the book for the page itself now, and the same reading is what tells the reseal whether somebody else is still carrying it, which was quietly answering no to every player. The hand-over is one moment per session rather than every look, and a player already holding a key of that code is not given a second - while a reload asks afresh, which is how a key lost in a river is replaced.
- **Glóin's camp is built where it was pinned, and its name stands there.** The site was written down by the survey before the pin was walked, so the saved site held a guess nine hundred blocks east of the pin, the build's own reach was measured to that guess, and the camp therefore never rose - which is why no dwarf was at either spot. A pin now outranks the guess for a site that is not built, both when the page is taken and on every look at it, so a world already carrying the guess re-seats itself instead of waiting to be re-taken; a camp already standing is left where it stands, the rule the world's own buildings follow. The camp also has its own name on the ground at last - `gloin`, pinned at the middle of the footprint the camp is laid from - so a page that names it has a bearing rather than a hillside the drawing guessed at.
- **Glóin's account of the goblin fires is kept as the hand-over, not as the closing line.** The page's closing is the key line now, so the Ulmug paragraph - Ulmug's fire, the pass's traffic, the road north - moved to `campaign.tolkienmobs.page.goblins.tale` and is spoken once, when the key is handed over; the book carries the key line for the player to read while they walk it.
- **The goblin cave is walled until the ashen scroll is read at its mouth.** A cave the player can simply walk into is not page 2-2's cave - Glóin sends them to put the fires out, not to find the door - so the cut now ends by sealing the mouth: the same five-by-seven, three-course rectangle it opens at the surface, filled with the hill's own stone read off the flank, so the place reads as unbroken hillside. The page hands over a bronze key coded `CampStorage` with zero uses and no mode, which is the one setting `KeyItem` never spends, and its own fifteen slots carry the camp's stores - four cram and the ashen scroll - because a chest opened with the right key opens the key's inventory rather than the chest's. The shelter camp's chest is set to the same code where it is laid and on every look at one already standing; reading the scroll within sixteen blocks of the cave pin walls it open, spends the scroll and says so; and settling the page walls it up again for the next player, unless somebody else in the world is still carrying it.
- **The Pinnath Fenui is a place, not a bearing.** Page 27's ground had a name in `Places` - `goblins`, the fires of the Pinnath Fenui - but nothing wrote it down, so the page still carried a direction rather than a point a needle could find. The cave's pin key and the place id are one string, which is the whole of the trick: `Places.ensure` asks `Pins.at` for the id and takes the pin ahead of any survey, so the name stands on whatever ground the cave was scouted on and the two cannot drift apart. `Campaign.tick` now asks for it beside `GoblinCave.raised`, forty-eight blocks round the pin, so a player walking up to the mouth is introduced to the fires on arrival, and the `/tmpin` checklist lists the cave with the camps and the hold.
- **Chapter two's handover note is brought up to date.** `docs/chapter-two-state.md` gains a 12 November section - the shelter camp seated on its pin and seen in game, the goblin cave that cuts and dresses itself within reach of its pin, the key stocked from its named table, and the courtyard seam built - and sets the open list in the order worth taking it: the eleven pages never walked end to end, Gundabad's inner keep ungated under an open stair, the Pinnath Fenui still a bearing rather than ground, the ten `ch2_*` templates and the eight `elven_*` and `snow_camp` templates with nothing that places them, Chinese still deferred, and the place `glo` canon'd to the Hithaeglir while its camp's site is in the Gladden. `docs/HANDOFF.md` points at it from the end of the file, so the next sitting starts from the notes rather than the transcript.
- **Structure void is audited out of every template, and the mechanic is written down.** A structure void cell is never saved by a structure block - the file has no entry for it at all - so a cell a template does not list is a cell whose ground is left alone, while a listed air cell is a cell that is cleared, which is the wiki's own worked example and the reason the shelter camp's dense box took the hill with it. A listed `minecraft:structure_void` is therefore a hand edit rather than a save, and no template should carry one. The audit read all 662 templates under `src/main/resources/data`: 400 are sparse, fewer entries than their own box, 42,763,308 blocks are listed and 614,650 of them are air. Fifteen templates carried a structure-void palette entry and not one had a cell pointing at it, so the tool rebuilt each palette from the states actually in use, renumbered the blocks with it and dropped the dead and duplicated entries, reading every rewritten file back cell by cell - name, properties, position and block entity data - against what it was. `docs/structure-void.md` records the mechanic, the trap and the convention for new builds, and `tools/structure_void_audit.py` re-runs the check on demand.
- **The shelter camp keeps the hill by leaving its air unwritten, not by void.** A structure-block save writes a structure void cell as nothing at all, so the file cannot carry void to the world: a cell that is not listed in the template is never placed and the ground there stands as it was, while a cell listed as air is cleared. That is how this mod's own tent, faction hall and lamppost templates already read, and it is why the earlier pass of 3,625 structure-void cells never did what it promised. The camp's saved file - taken from the other side of the outcrop, so a quarter turn round from the frame the camp is seated in - is now rotated back to 24 x 14 x 21 with its mouth on the -z face, and is otherwise exactly what was saved: 3,427 listed blocks with every bench, bag, stair, lantern, fire and chest where they were, and 344 air cells for the room itself, with the hill outside simply absent instead of described. Sites.clearMouth still opens the mouth, eleven wide by six out by six up, at the raise and on every approach.
- **The shelter cave stops clearing the hill it stands in.** Its template carried the air around the outcrop as air, and a structure's air is placed like any other block - which is why the hill came away in a cube around the camp. All 3,625 of those outside cells are now minecraft:structure_void, which a structure placer skips, so the ground that is already there is left exactly as it is; the camp's own 348 air cells - the hollow from the mouth to the back wall, where the fire, the benches, the rack and the chest stand - are still air. The box, the palette (one entry longer) and the two block entities are otherwise untouched, the file is still 24 x 14 x 21 at DataVersion 4790, and the original is kept at run/structure-backup/shelter_cave.before_void.nbt. The template's front face is void too, so the way in is opened by Sites.clearMouth rather than by the file itself.
- **A self-dressed build still gets its way in.** The shelter camp keeps the hill it was cut into - no plinth under it, no ground swept off its shell, no stone by the door - but a camp the hill has closed over is no camp at all, so Sites.clearMouth takes the ground out of the doorway itself: a box in front of the door's own column, at the sill and the six courses above it, five blocks either side, cleared to air. It starts one block out from the template and one course over the sill, so the camp's own blocks are never touched and the ground a player walks in on is left standing; the rest of the outcrop stays exactly as the file cut it, which is what makes it read as part of the hillside rather than as something placed on it. The doorway is cleared again on every later approach too, so a world that raised the camp before this pass existed is walked into rather than walked round.
- **The shelter camp is left exactly as it was cut.** Sites now keeps a SELF_DRESSED list of buildings whose template is the whole of what they are meant to be, and lays them without the passes that tidy every other build: no dirt filled under the floor course, no hillside swept off the roofline, nobody stood at it and no stone put by its door - on the build and on every later approach alike. The shelter camp is the first name on it, because it was cut as a rock outcrop with the camp already inside it, and each pass that makes a placed building readable for the world was making this one read as placed rather than found: the plinth under it, the ground cut away above it and the stone beside it all took away from a shell that is meant to be part of the hill.
- **The shelter camp stands exactly on its pin.** Sites now keeps a list of buildings that are put where the pin is and nowhere else, with no sampling of the footprint and no sinking into the ground under it, and the shelter camp is the first name on it: its pin was walked on the ground its floor belongs at, so the seating arithmetic had nothing to add but error - measured from its highest column it stood proud of the hill, and measured from its mouth the drop was still a second guess at ground a scout had already stood on. A pinned building on the list takes the pin as its corner and the pin's own height, so the camp is where it was walked and at the level it was walked at; the pin is the measurement. The doorway seat stays for the dressing pass, which still needs to know which face the camp opens on.
- **The whole cave wears, and the shelter camp sits down into its hill.** GoblinCave's wear now keeps every cell the plan opens rather than only the ones the cut had to break, so a cell the hill was already hollow at - a flank chamber a natural cave ran through, a corner the world had left open - is dressed with the rest, and a second pass after the hall is laid gives the hall's own walls the same cobblestone and moss, leaving its floor course exactly as the template saved it. The shelter camp also gets a DOORWAYS seat at last: measured from the mouth's own column on the file's north face, its sill comes down level with the ground a player walks up to it on, where seated by its highest column it stood a course or two proud of the hill and had to be climbed onto. Both changes take effect when the cave and camp are laid, so an already-cut world wants its chunks taking out again to see them.
- **The caves wear their age.** GoblinCave.wear runs after a cut, over the cells it has just opened but before the hall is laid in them, and gives the hill's own stone a weathered face: patches of cobblestone where a floor has broken and mossy cobblestone where it has been wet, and the odd block of glowstone set into a ceiling so the caves behind the hall can be walked without a torch. Every roll comes off the cell's own hash rather than the world's random, so the same plan wears the same way in every world and a re-cut is the same cave, and only BlockTags.BASE_STONE_OVERWORLD is ever changed - anything else is somebody's work rather than the hill's. The hall's own box is skipped entirely, because its floor belongs to the template.
- **The map audit: every build raises itself, and the leftovers are written down.** docs/map-automation.md follows each placing call back to the clock that owns it - Rivendell through Dressing.onServerTick to CityCommands.rouse, the camps and crags and shelter camp through FactionEvents to Places.tick to Sites.scenery, a page's own site through QuestLedger to Sites.plan, the goblin cave through Campaign.tick to GoblinCave.raised, roads through RoadCorridor's chunk load, the valley's people through keepValleyStanding, the chapter-one seam through Departure.tick - and records that the commands left over, /tmcave through /tmcourage, are development tools that nothing on the map needs, sharing their code with the world's own raise rather than holding a second copy of it. It also records what the audit turned up and could not automate: ch2_approach through ch2_vault, ten chapter-two templates with no reference in the mod's Java or its datapack, and eight key-structure templates beside them - elven_banner, elven_gate, elven_house_1, elven_house_2, elven_lamp, elven_library, elven_square and snow_camp - none of which any command places either, because they want a placement table or a pin before they want a clock.
- **The goblin cave is cut and dressed by the world, not by a command.** GoblinCave.raised runs from the campaign clock for every player in Arda: within ninety-six blocks of the goblin pin and with the chunk loaded, it cuts the cave and lays the hall in it - the same cut /tmcave makes, the command left as a tool for walking a cave a world has not made yet. The hall's own log bench is the record that the room is already there, so a world cut by hand before this existed is never cut twice, and the pin is remembered for the session in RAISED so a player walking the hill pays for one cut rather than one a tick. Nothing is said in chat: the map's rule is that everything built for it - Rivendell, the camps, this cave - stands on its own, because the players who walk it have no commands. That rule is now written down in AGENTS.md, with the repo's checking and committing conventions beside it.
- **The goblin's key carries the hoard as well as the lock.** A key's code now doubles as a loot table's name: when the carrier drops the key, GoblinCave.stock reads the code - DungeonKey names tolkienmobs:chests/dungeonkey, and a full id names a table anywhere in the pack - and rolls that table into the key's own fifteen slots, so the key the goblin gives up is stocked the moment it is made. That is also what makes one key serve several chests: chests set to the same code are one lock, and the table the code names is one hoard behind them. The roll happens at the drop rather than at the chest because a dropped key is one moment and needs no marker to stop it rolling twice, and a code that names no table leaves the key empty with the lock still working. A starter dungeonkey table ships with it - replace it and every key cut to that code follows.
- **One of the hall's goblins carries the key to its chest.** The hall's five are one body's key rather than a purse each: one of them is tagged as the carrier as the hall stands them, and when that one falls, GoblinCave.dropKey reads the code off the hall's own lockable chest - DungeonKey in the shipped template - and cuts a master_key to it with three uses and the reusable mode. Reading the chest rather than writing the code down is the point: a hall furnished and saved again moves both together and the key still opens the chest it was taken from. A hall that has given its key up arms no second carrier in that session, a hall with no chest in it gives none up, and a goblin met anywhere else in the world asks and is told no.
- **Page 27's count runs into the king's name.** The story leaf now ends the page's own line in a comma when a line naming somebody follows it, so 2-2 reads "Slay 6 Goblin," and then "Ulmug, king of the fires: kill him too, and the fires are out" as one sentence rather than two statements with a gap between them. The comma is added by the leaf and not by the page: the same objective is printed in chat when the work is taken, with no name under it there, and a page that counts nobody keeps its line exactly as it was.
- **Page 27's book entry names its king.** The story leaf now draws a gold line under the count - "Ulmug, king of the fires: kill him too, and the fires are out" - from Campaign.aside, so a page that counts six bodies tells the player whose fires they are before they walk into them. It is a line and not a second counter: the six are one count and the king is one of them, and a leaf showing five goblins and a king apart would be asking two things where the chapter asks one. Every other page draws exactly what it drew before.
- **Page 27's fires are counted as six, king included.** The hunt now asks for the hall's five goblins and Ulmug himself rather than one goblin of the kind: GOBLINS_GOAL is GoblinCave.FOLK plus one, Ulmug counts by the tag the hall puts on him because the page names a goblin and he is a goblin king, and a kill only pays the page when the body wears the fires' tag - a goblin met in the wild is a goblin, not the fires. So the king cannot be walked past on the way to a number, and the five cannot be six elsewhere: kill him first and the page still wants the folk, clear the folk first and it wants him. The hall's dead are topped up while the page is in hand, so the six are always there to be finished.
- **The goblin fires are stood in the hall, and the camp beside the door is pinned.** The shelter camp and the cave's own door are in Pins.SOURCE now, so every world raises the camp 33 blocks west of the way in and /tmtp goblins finds the door the mountain rises south of. GoblinCave.keep runs beside the other keepers and stands page 27 its quarry: while the hunt is in hand and a player is within 64 blocks of the door, Ulmug - the pack's goblin king, named - stands beside the hall's log bench, found by walking the room rather than by a saved offset, and five goblins are stood on the floor once each, found again by tag so a second look tops up the dead rather than multiplying the living. The page counts a goblin and not the king, so any one of the five settles it and Ulmug's name stays in the prose, which is how the chapter was written.
- **The goblin hall is furnished, and the cut lays it as built.** The hall's structure now carries the decorated room rather than a bare hewn one - 15 palette entries where it had two, with benches and shelves in spruce and dark oak, cobwebs, the log bench, the cooking rack and a lit soul campfire - and it carries its block entities rather than dropping them, so the lockable chest arrives with its chest code and the campfire with its cooking times. The box is unchanged at 19 by 14 by 43 with the same stone and the same bottom course, and the placement now reads the file's own width rather than the room's figure, so a hall saved back a course wider or narrower still lands centred on the way in.
- **The goblin cave's hall is laid by the cut.** The carve can only take rock out, so the hall came out a bare hewn hole and the hall's own structure had nothing that put it into a world. GoblinCave.dress now places goblin_hall.nbt into the hollow the cut leaves, turned to face whichever way the hill rises so it lands over the room rather than beside it, which gives the hall the floor course the file carries and the worn walls with it. /tmcave cut reports the corner it was laid from, and /tmcave dress lays it again without cutting the cave at all - that is the way a hall decorated by hand and saved back over the same file reaches every world's cave.
- **The goblin cave's camp is turned to face north.** The shelter was cut opening to the west - the room ran straight out to that edge while rock closed in three to five blocks the other three ways - so a structure block placed at the pin put the camp's back to the way the cave is walked up to. Every block is turned one quarter clockwise: the size, all 7,056 positions and the palette's facing and axis values, so the beds and chest face north and the stairs and fire with them, while the campfire's block-entity payload is copied through byte for byte. The way in is on the north face now, and the footprint is 24 by 14 by 21.
- **The goblin cave's camp can be pinned.** The shelter cut into the rock beside the cave is a template like the other camps, but nothing surveys for it and nothing plants it: it stands only where a scout stood. `/tmpin build sheltercave` (or `shelter`) writes that block down and prints the `Pins.SOURCE` line to keep for every world, and `/tmtp` then has a name for the ground. It is kept out of the scenery the world raises, so a camp nobody has placed is never planted in whatever spot the drawing guessed at.
- **The cooking rack sits down over its fire instead of floating above it.** The model was drawn with the block's own floor as its base, so a rack placed over a campfire stopped at the top of its block and read as hanging in the air. The whole drawing is six pixels - a little under half a block - lower now: every element and every rotation origin moved down, so the poles run from fifteen pixels below the block's floor to nine above its roof, and the traced outline moved with them, which means the rack is picked and broken where it is drawn rather than inside the block's own cube. Six is the limit rather than eight, because a model element may not reach below -16 and the poles already hang at -9 - a full half block broke the model outright and it rendered as a missing model. Its display transforms are unchanged, so the item sits lower in the hand and in menus than it used to.
- **The mod's blocks and items are in the shape tags vanilla expects.** The hand-written tag providers had drifted: the whole mountain stone outfit - stairs, slabs, buttons and pressure plates - plus the mithril and morguliron slabs, buttons and pressure plates, the mithril and morguliron bars, one wall sign and one fence gate were in no shape tag at all, and on the item side almost none of the families were tagged, which is what a vanilla recipe or another mod looks at. Both providers are filled in from the same rule per family now: every stairs, slab, wall, button, pressure plate, fence, fence gate, door, trapdoor, plank, bars and sign the mod registers is in `minecraft:`'s tag for what it is, wooden families in the wooden tag as well.
- **The bench's back can be one of the mod's own trapdoors.** The recipe asks for `minecraft:wooden_trapdoors`, and the mod wrote no `wooden_trapdoors` item tag at all: its seven wooden trapdoors were listed in the block tag, and a block tag does not carry across to the item side by itself, so a bench could not be backed with mallorn, mirkwood, culumalda, lebethron, fangorn oak, deadwood or dwarven maple. All seven are in the item tag now.
- **The bench's back is part of its recipe.** A trapdoor sits across the back of the seat now, behind three logs with a stick under each end - and any wooden trapdoor will do, since the recipe asks for the tag rather than for oak, so a bench can be built to match whichever wood it stands among.
- **The log bench can be crafted.** Three logs in a row for the seat and a stick under each end for the legs make one bench, in decorations since it is furniture; it unlocks off logs, and data generation wrote both the recipe and its advancement.
- **The hall's structure carries its own floor.** The file was the room alone, its bottom course air, on the reasoning that the cut leaves the hill's own floor under the hall and a structure block's corner stands on it - which meant that loaded onto anything but that floor it built a room with nothing under it. It is now one course taller, 19 wide, 14 tall, 43 deep, 11,438 cells - 8,278 air and 3,160 stone - with stone from wall to wall along the bottom. The room's own courses are unchanged and sit one level higher in the file, so a structure block's corner goes at y 156 at the test pin rather than 157 and the hall it builds is the same hall in the same place.
- **The cooking rack can be crafted.** Three sticks leaning together for the poles, an iron bar across the middle for the grate and an ingot beneath it for the hook - `S`, then `SBS`, then `I` - make one rack, in decorations rather than building blocks since it is furniture for a fire. It unlocks off iron bars, and data generation wrote both the recipe and its advancement.
- **The log bench's model and texture are in the repository.** The bench itself, its blockstate, its item model and its loot table were committed while the model and texture they name were left untracked in resources, so a clone had `log_bench` registered against a model it did not have and drew the bench as a missing model. Both are tracked now, and the model still names the texture where it lives, `block/custom/log_bench`.
- **The cooking rack stands over a campfire.** `cooking_rack` is registered against the model and texture already in resources - wood sound, no occlusion, and deliberately no support and no collision. The model's three poles are drawn from nine pixels below the block's floor to fifteen above its roof, where they meet at the rope lashing, so the block is meant to stand with the fire in the space beneath it and its legs reaching down around the flame: a rack that asked for something solid to sit on would refuse to go over a campfire at all and would pop off the moment the ground under it was dug away, and one with a collision shape would turn the hanging grate and cage into a wall around the fire. Its outline is traced from the model's own parts - the three poles where they cross the block, the grate, the cage and the three hooks - so the crosshair finds the rack rather than a cube of air. Two things in the supplied model also had to be fixed before it would load: its parent `item_models/base` exists nowhere in the mod, and its bare `block/...` texture names resolved inside this mod rather than to the vanilla textures they meant, while the rope it does own sits at `block/custom/rope`. Data generation wrote the blockstate, the item model, a loot table that drops the rack itself, and both names ("Cooking Rack", "烹饪架").
- **A bench's seat no longer crashes the client.** The seat entity was registered with no renderer, and 26.1's `EntityRenderDispatcher.shouldRender` dereferences the renderer *before* asking whether to draw anything, so the first frame with somebody sitting on a bench died in a `NullPointerException` (`crash-2026-11-11_07.27.12-client.txt`) - the block worked and the seat existed, and the client fell over merely because it had one. The seat is now registered against vanilla's `NoopRenderer`, which is a renderer that draws nothing, which is the whole of what a seat needs.
- **The log bench is in the game, and it is a seat.** `log_bench` is registered against the model and texture already in resources - `HorizontalDirectionalBlock`, wood sound, no occlusion - so it turns with the player who places it and its back sits behind the seat. Right-clicking it puts the player on an invisible entity, `entityttmseat`, at the height of the model's own seat planks (seven pixels up) rather than on the block, so the sitter's feet land on the bench; a second click finds the seat already standing there and rides that one, so a bench only ever holds one, and crouching passes the click through to whatever the player meant to place against it. The seat is the least an entity can be - no model, no save, nothing synched - and it is killed the moment its rider gets off, so a bench never leaves one standing in the world and a save never has to be repaired after a crash. Data generation wrote the four facings, the item model, a loot table that drops the bench itself, an empty one for the seat, and both names ("Log Bench", "原木长椅").
- **The goblin hall is 43 blocks long, and hewn rather than machined.** The room in the middle of the caves was 18 long and a clean box: it is now 19 wide, 43 long and 13 tall, its four corners cut at 45 degrees three blocks in, its ceiling vaulted - set in two blocks from every wall above the eighth course - and its faces worn a block either way on the same cell hash the caves use, so it stays squared in plan while reading as rock somebody squared off rather than as a box somebody built. The ways out of it are tunnels now as well: the 7-wide passages out of its sides and the one out of its back wall are strings of overlapping balls wandering three to five blocks across, the way the flank tunnels already run, so every chamber is reached through rock. Standing rock is held two blocks clear of the hall, so it is a room with rock round it and the only thing that pierces its wall is the round head of a tunnel. The plan is 99,320 cells with none dropped by the roof rule, and `GoblinCave` and `tools/cave_section.py` moved together again.
- **The hall can be decorated by hand: it is written out as a structure.** `tools/hall_structure.py` reads the same `hall()` the cut uses and writes `goblin_hall.nbt` - 19 wide, 13 tall, 43 deep, gzipped vanilla structure NBT at `DataVersion` 4790, the version the shipped `ch2_hall.nbt` carries. It is the room rather than an empty box: the hollow is air and the other 2,343 cells of its box are stone, so loading it in any world builds a hall to walk into and furnish - an all-air file would have changed nothing in a carved world and could not have been decorated. Both the project's own reader and a separate checker read it back to its last byte, and the file's first attempt did not: list elements were being written with a tag id and a name that a list element must not carry, three junk bytes on every block. The floor is deliberately left out, because the cut leaves the hill's floor under the hall and a structure block's corner stands on it - and the file's x axis runs the way the world's does, since the cut reaches `x = doorX - s`, so loading it unrotated at the hall's own near corner builds the room that was cut rather than its mirror image.
 The corridor that ran out of the hall's far wall into the chamber behind it is gone: that wall is solid rock now, and each flank chamber trails a winding tunnel into the back one instead - eight small overlapping balls whose centres wander and whose radii change between three and five blocks, so it reads as a tunnel somebody followed rather than a passage somebody drove. Both tunnels are the same string of balls, mirrored. Flood filling the plan from its mouth reaches 95,539 of its 95,616 cells; the 77 it cannot reach are pockets the noise shut inside the rock, with no opening on any side and nothing to see from the cave. Seven cells are still dropped by the roof rule, all of them at the mouth where a 7-tall way in comes within 3 blocks of the ground.
- **The way in is five wide and seven tall.** The mouth was three by four and the ramp under it the same, which meant the largest cave in the campaign was entered through a doorway you had to stoop into and walk down single file. Both are now five wide and seven tall, so the mouth, the ramp, the landing and the hall read as one passage rather than as three sizes of door. The mouth's own cells are still the only ones cut through the surface, and the cut's top bound follows the mouth up, so the plan and the cut still stop at the same block and the drawing stays evidence.
- **The goblin cave is three chambers now, and bigger.** The hall - the entry chamber - is 17 blocks wide and 13 tall against 15 by 10, and the chamber either side is built from four overlapping lobes rather than one ellipse, about 63 blocks along the way in by 44 across, so its outline has shoulders and recesses instead of one clean sweep. A third chamber sits behind the hall's far wall, reached by a 7-wide passage, and is the largest of the three at about 76 by 69 and 20 tall. The whole cave is 220 blocks long and 120 wide and comes to 93,716 cells, six times the two-chamber cave, with none dropped where the hill was too shallow to roof it. Both sides moved together: `GoblinCave` and `tools/cave_section.py` carry the same lobes and the same three sine terms, and drawing the whole plan now takes Python 39 seconds, so a lobe is turned away by its bounding box before the trigonometry runs.
- **The wrapper is on Gradle 9.9.1.** 9.9.1 is the patch release for 9.9.0 and carries three security fixes - the worst of them critical, code execution through version-catalog accessors - so the wrapper now points at it and its own jar and scripts were regenerated from it rather than left at the old version. `compileJava` is green on it. Nothing else needed moving: the build already asks for the Java 25 toolchain and the IDE's project SDK is temurin-25, while the wrapper itself runs on the JRE 17 that `JAVA_HOME` names.
- **A cut can be taken back out of a world.** `tools/chunk_trim.py` removes just the chunks a cut generated - the six by nine the cave's own plan reaches, plus whatever they touch in the save's `region`, `entities` and `poi` folders - so the world makes that ground again from the seed. Whole region files are deliberately not the unit: the four the cave's test cut lands in hold 4,096 chunks between them, so deleting those files would take a 1,024 block square of somebody's world to get rid of fifty-four chunks. The tool reports before it writes anything, refuses to write while the world is open (Minecraft holds `session.lock`, and would write its own chunks back over the top), copies each file to `run/chunk-trim-backup/<stamp>/` first, and reads every rewritten file back - every chunk's sectors inside the file, every length inside its sectors, no two chunks sharing a sector - before touching the next. The test cut came to 79 chunks across the three folders.
- **The goblin cave's caverns are rough now instead of swept.** Their walls wander off their own reach by two hash terms - a wobble over four blocks and a finer one over two - the floors carry rubble, and about one column in eight keeps rock standing in it: a stalactite hanging from the ceiling or a stalagmite rising off the floor, up to five blocks. Nothing is placed and no material is picked, because the cut only ever takes rock out, so the hillside's own stone is what stays. The hash is an integer hash of the cell, computed the same way in `GoblinCave` and `tools/cave_section.py`, so the spurs sit in the same place on every cut and the drawing still shows what the cut leaves behind. The hall and the landing are untouched - they are the worked rooms. The test cut went from 15,960 cells to 15,350.
- **The goblin cave has a handoff note of its own.** `docs/goblin-cave.md` writes down why the cave is cut rather than placed - the WorldPainter export measures 1022 by 79 by 1022 and 92.9% stone, so it is a kilometre of painted world with a cave in it, an order of magnitude past the Gundabad box that was already too big - along with the pin workflow that puts the door where somebody stands, every number of the shape, the two implementations that have to keep agreeing, what is still missing (the camp outside the door, Ulmug, a road north), and the two traps: a design made smaller leaves its old air standing, and a world generated before the cave shows nothing in it until `/tmcave cut` is run.
- **The caves behind the goblin hall are caverns now rather than squared rooms.** The two chambers either side were boxes ten by nine; they are caves - a flat floor, a wall that swells and pinches round the compass, a ceiling that domes from a little under half way up, and an alcove or two off each - twenty-four blocks out to either side and thirteen tall, which is about ten times the volume. Nothing random is involved: the swell is two sine terms on the angle, so the same door always cuts the same cave, and the hall stays squared because it is the room somebody made and the caves behind it are not. `GoblinCave` and `tools/cave_section.py` use the same two sines, so what is drawn is what is cut.
- **The section drawing reads as much hillside as the cave is long.** It read a fixed forty-eight blocks, so once the way in fell forty over eighty the ramp's tail, the landing, the hall and both chambers were all beyond the ground it had looked at and were quietly left out of the drawing - a cut of five hundred and eighty-eight cells where the plan is thousands. The read now follows the plan's own far wall, so what the drawing shows is the cave that would be cut.
- **The goblin cave's way in falls forty blocks instead of eight, so the cave sits deep in the hill.** The ramp keeps its walk - one block down in two, so eighty blocks of tunnel - and the rooms no longer take their height from the depth: the hall is ten blocks of headroom and the chambers six whether the way in falls eight or forty, which is what a deeper cut needed, since a hall that grew with the drop would have been forty blocks tall. The drawing says what it took to fit on the page rather than being cut off at the edge: a slice longer than the page is drawn every second block across and down, and says so.
- **The goblin cave can be cut at its door and walked.** `/tmcave cut` takes the cave out of the hill behind the world's `goblins` pin: a mouth on the pinned block, a ramp of one in two down into the hill, a landing that runs into the hall's own back wall, a hall ten blocks tall with a chamber out to either side through a three-wide passage, and every cell held three blocks under the ground above it so the mouth is the one place it opens. Which way the hill rises is read off the ground rather than given, so the same pin cuts the same cave in any direction the hill happens to face, and the shape is the one `tools/cave_section.py` draws - the ramp is the same integer division in both, so the drawing and the world agree block for block.
- **A pinned door is cut and drawn before any of it is carved.** `tools/cave_section.py` reads the `goblins` pin out of the world, reads the ground around it block by block out of the same save, cuts the cave the generator will cut - a mouth on the pinned block, a ramp of one in two down into the hill, a landing that runs into the hall's own back wall, a hall and two chambers out to either side, and every cell held three blocks under the ground above it so the mouth is the only place the cave opens - and then draws it as two vertical sections and a plan, with the cells the hill was too shallow to roof counted rather than hidden. The pins reader was also looking only at the world's own `data` folder, where the store is not: it lives under the overworld's dimensions folder, so it found nothing to read.
- **The goblin caves have a name, so their door can be pinned where it will be cut.** Page 27's ground was the one place in the two chapters the catalogue had nothing for, which meant `/tmpin` had no name to write a scouted spot down under. It is `goblins` now - a name in the Hithaeglir beside Glóin's camp's, announced as "The Goblin Fires", over "Ulmug's fires in the caves of the Pinnath Fenui" - so `/tmpin place goblins` writes the feet of whoever is standing at the door, and `tools/pins.py` reads that spot back out of the world's own pins file instead of off a debug screen.
- **The old world's goblin caves are measured before any of them is turned into a template.** They arrived as a legacy `.schematic` - 786 KB on disk and 166 MB uncompressed - and `tools/schematic_probe.py` reads that layout outright: MCEdit's own block order, the capture's air and rock taken as its background, and the placed blocks and the thickness of the air as two maps. Judge by them and the goblin caves are 1022 x 79 x 1022 and 92.9% stone, with 110,908 cobblestone and 8,315 glowstone placed inside them: a kilometre of old world with a cave in it rather than a cave with a hillside round it, so the capture is a source for a carve and not a structure to ship.
- **A marked body stands on the ground under its mark, not on the height the mark was read at.** The plaza's marker blocks were taken up after the positions were written down, which left the cast a block in the air on the holes they left. A pin is now treated as a column and the ground read fresh every time the cast is stood, so a floor that has changed puts the bodies on the floor that is there - one block lower on the plaza as it stands, and right again if the holes are filled.
- **Chapter two opens where chapter one ends.** The scene hands over page 26 - *Onward, Misty Mountains* - and now also says the one thing a page nobody asked for cannot: who takes it, and where they keep their fire. "Glóin holds the next page, at the dwarf camp at the east end of the High Pass." One line, said as the Company goes, so the first thing chapter two asks is a walk north rather than a walk back to Elrond.
- **Elrond rolls the scroll up before he puts it away.** The pack already draws both states - the tied tube and the open page with its seal - so the roll-up is a swap of what he is holding rather than an animation: he reads from the open page, and six ticks after the reading pose has come down to his side the scroll becomes the rolled one, held there for a second and a half before it goes. No new art, and no new item: the two sprites that exist are the two states.
- **Elrond's scroll is closed at his side instead of vanishing out of his hand.** It used to go the instant his last line was said, while the arm holding it was still up. It now goes just after the reading pose has finished coming down, so the scroll is lowered and then put away rather than plucked from mid-air.
- **The speaking window is measured off the right end of the script.** The first correction asked whether a line's worth of ticks had passed *since the line before*, which is the span before a line rather than after it - so an arm would have risen a line early, and never for the last one. The test is now the script's own: a line is being said until the clock reaches the next line's turn, so the last line's window closes a line's worth after it lands.
- **A body with a scroll in its hand reads rather than greets.** The arm that comes up while a body speaks was one gesture for everybody: eighty degrees, out in front of the shoulder, which is a greeting. A body holding the pack's scroll now lifts forty-four degrees and turns the arm twenty across the body, and its head drops twelve degrees towards the paper, so Elrond's blessing is read off the scroll in his hand instead of delivered to the air.
- **Elrond carries the scroll all through the scene and puts it away on his last line.** It used to arrive with his first line; it is now in his hand from the moment the courtyard fills, and stowed as "Let us go back, Adventurer" is said, so he walks in empty-handed.
- **A speaking body's arm actually comes up, and comes down when its line is over.** The window a line counts as being said in was written against the wrong side of the clock, so it never opened and no arm ever rose. It opens on the tick a line lands and closes one line's worth of ticks later, measured on the script's own clock so the pause before the parting words holds the arm with it - which is what brings Elrond's arm down as the blessing ends.
- **Elrond speaks his blessing off a scroll.** It comes into his hand on his first line rather than being carried all through the scene, and stays there - one appearance and no flicker - so the last thing the courtyard hears is read from something rather than remembered.
- **The cast come into the courtyard armed: Aragorn and Boromir with swords, Gimli with a mithril axe, Legolas with the elven bow.** They are given their gear as they are stood rather than at the walk, so the whole scene has them carrying it - and with the folk renderers' item layer in place, it is now actually drawn in their hands.
- **Gandalf leaves holding the arda staff.** A scene's bodies are stood out of nothing, and the three folk renderers had no layer to draw a held item with, so an item in a villager's hand was in the entity and seen by nobody. The humans, hobbits and dwarves now carry the same `BlockAndItemGeoLayer` the guards and wights use, and Gandalf is handed the staff as he turns for the road.
- **Bilbo puts a hand up while he watches them go.** The arm-raise that a speaking body gets is now held up for as long as a body is waiting to walk rather than talking - so Bilbo's four seconds at the courtyard's edge are spent with a hand raised in farewell, and it comes down as he turns for the door. One gesture, two reasons.
- **Bilbo watches the Company go before he turns in.** Elrond sets off for the door the moment the courtyard is empty; Bilbo holds where he stands for four seconds, facing the way they went, and only then follows him - so the last thing the courtyard sees is somebody watching them out of sight.
- **The Company walks rather than glides.** A body leaving the courtyard is moved by the scene rather than navigating, so nothing about its motion reached the client and the legs never moved. A synched walking flag now says a body is on its way, and the renderer swings hips and arms off that body's own age - so nine of them are never in step - and the walk reads as a walk instead of a slide.
- **The Company walks the route that was marked for it.** Ten marks north of the courtyard, read out of the save and pinned as `rivendell.scene.walk.1` to `.10`, nearest first: the nine line up on the first and walk the line out round the shoulder of the hill, each a little to its own side of it, and the side is taken across the line so a bend turns the formation with the road. A body is taken away at the last mark, which is out of sight, rather than after a fixed forty-eight blocks. An unmarked plaza still walks straight out of the courtyard's open side.
- **Elrond and Bilbo go twenty-four blocks back down the road and vanish there.** They used to walk seven blocks the other way and go while still in the open courtyard. They now go the way the Company did not - back down the road towards the valley - and are taken away well down it, so the courtyard is left empty without anyone disappearing in view.
- **The nine walk out in the column the ammolite marks laid down.** Gandalf alone in the middle and furthest out, then four pairs abreast, each body on the side of the column it stood on in the courtyard. The walk reads a pin per body now, so the courtyard's shape is carried out of it rather than spaced from the bearing - a plaza nobody has marked still empties abreast, since the bearing is the fallback.
- **Elrond and Bilbo walk in off the road once the Company is gone.** They used to stand in the courtyard until the scene took them away, which left the last thing on screen two people watching an empty plaza. Once the nine are gone they turn the other way - the courtyard's open side is where the company went, so they go back through the standing side, pair abreast - and the scene closes on the courtyard empty.
- **Elrond's parting words wait for the Company to be away.** The script used to run to its last line with everybody standing in the courtyard. It now lets the nine go on the twenty-third line and holds there until they are eight blocks off the plaza - the hold is added to the clock rather than to the count, so nothing is said early when the pause ends - and then says "And so the Company departs" and "Let us go back, Adventurer" over their backs. The walk itself is twice as long, forty-eight blocks, so both lines are spoken while the Company is still in sight.
- **The walk out's own marks can be read: `/tmscene route`.** Ammolite laid along the route is read in one pass, numbered outwards from the courtyard, pinned as `rivendell.scene.walk.<n>` and printed mark by mark. A body's standing spot is skipped rather than counted - the same stone can be both, since one of the plaza's cast marks is ammolite too - so the numbers are the road's and nobody's else's.
- **The courtyard names itself from further out, so the banner lands before anybody speaks.** Forty blocks rather than the twenty-four that begin the scene: the title comes up as the plaza comes into view, holds through the walk in, and is fading as Gandalf opens, instead of sitting on top of his first line.
- **The courtyard announces itself: "Meeting of The Fellowship", over "The Gathering".** Coming into the plaza with the note unanswered puts the campaign's own banner on screen - the packets, the ink and the timing the places already use, so it reads as one more named place rather than as a scene starting - and only on that step of the line: it is sent on the reach that begins the scene, and a tag keeps it to one showing, cleared by `/tmscene forget` with the rest of the seam.
- **Nobody leaves until everybody has spoken.** The nine who go used to turn on Gandalf's sending, which put them walking out while Elrond was still speaking his five closing lines. They now wait for the script to finish - the whole Company standing through Elrond's close - and only then turn and go, and the scene is not over until they are off the plaza: Elrond and Bilbo are taken away with it rather than left in an emptying courtyard. A line also only counts as being said for the three seconds before the next one lands, so a raised arm comes down on its own when the script moves on instead of staying up through the walk out.
- **Gandalf goes with the Company, and only Elrond and Bilbo stay behind.** The walk out was written to the old pack's eight walk scenes, which left Gandalf speaking the send-off from a courtyard he then stayed in. He now turns and leaves with the rest - nine out and two left - so the last five lines are Elrond's own, with Bilbo for company, which is how Rivendell is left in the story.
- **A body of the departure holds its ground and turns its head, and lifts an arm while it talks.** The turn to the player used to be the whole body swinging, which is a thing people do not do to look at somebody across a courtyard. The scene now sets one synched flag per body - this is a scene body, and this body's line is the one being said - and the renderer does the rest on the bone: the head turns up to seventy degrees toward the player from wherever the body was stood, eased so it is never a snap, and the right arm comes up while that body is talking. The rigs were asked what they hold (the head is `head`, the arm `rightArm`, the arm's rest pose no rotation at all), so the raise is a rotation of its own. Nothing is animated in the models themselves: the head and arm are placed from the render state, which is what the pack already does with the head.
- **The courtyard looks into itself, and the speaker turns to the player.** Elrond and Bilbo stood with their backs to the Company because every body was turned to the gathering mark - and Elrond's own mark *is* that mark, so he faced out of his own scene. Everybody now turns into the middle of the courtyard instead, which puts those two among the rest. Bodies also breathe: a slow four-degree sway on each one's own beat, so eleven of them are never in time. And the body whose line is being said turns to the player for the length of it - no further than seventy degrees, so nobody spins when the player walks behind him - and sways back when the line passes. Both are made on the body's own yaw, which every rig of the pack draws and the heads follow; a head that moves on its own, or an arm raised, is work inside each model and comes next.
- **The Company walks out of the courtyard once the talking is done.** Gandalf's sending is the twentieth line, and there the courtyard empties: the eight who go - Aragorn, Boromir, Legolas, Gimli, Frodo, Samwise, Merry and Pippin - turn and walk off the plaza in lanes of their own, abreast across the bearing the ring itself gives, while Elrond speaks the last five lines. Elrond, Gandalf and Bilbo stay until the scene ends. The walk is driven by the scene rather than navigated by the bodies - a body in a cutscene has no AI worth asking, and nothing of its own should take it somewhere the courtyard did not say - stepped at a walking player's pace and re-grounded on every step, and a body is taken away on arrival or after thirty seconds, so no scene leaves one standing about. It ends where the plaza does: twenty-four blocks out on the open side, until a road mark says where the road actually runs.
- **The plaza's ground is written into the build, so no map needs the marker blocks.** The eleven blocks laid on the staging plaza were read once, at 5117, -3409 - Elrond at 5117, -3407 with the ten round him, all at y=77 - and the spots they gave are now the build's own pins: a map that has never held a mithril block or a plank of culumalda still stands the Company in exactly that courtyard, and a world that lays its own survey still overrules it. The blocks were the reading, not the furniture.
- **The road out begins where the plaza's marks say it does.** The Company's walk needs somewhere to set off from, and the courtyard already says where: Elrond is at the middle and the ten fill the half-ring in front of him, so the open side of that ring is the way out. `/tmscene marks` now reads the bearing from Elrond through the Company, steps out to just past the outermost body and reports the ground there - so moving a mark moves the road's start with it, and no twelfth block has to be laid down or remembered.
- **Elrond stands in the departure in a body of his own, and keeps his post in the valley for everything else.** The scene spoke his five closing lines over a courtyard with no Elrond in it, because the only Elrond in the world is the campaign's keeper, stood where the campaign keeps him. He is now one of the scene's own eleven: spawned at his mark with the rest, named, and taken away when the last line is said - so the library keeps its keeper for the audience and the tidings carried back, and the plaza gets an Elrond for the departure. Both can be in the valley at once, which is cheaper than moving the campaign's own about.
- **The plaza the Company waits in is read off the blocks set down for it.** Eleven marker blocks - Morgul iron for Gandalf, mithril for Elrond, ammolite for Aragorn, dwarven stone for Gimli, elven marble for Legolas, mountain stone for Boromir, Angainor stone for Bilbo, culumalda planks for Frodo, lebethron planks for Samwise, deadwood planks for Pippin and dwarven maple planks for Merry - are read by `/tmscene marks` into `rivendell.scene.<name>` pins, the nearest of each kind within the radius it is given, so the layout is the owner's placing rather than the old pack's half-ring. A marked body stands on its own block and turns to face the gathering mark, and Elrond's mithril mark is the gathering mark itself: the ground the note's scene begins at, where `/tmscene play` rehearses, and where his keeper is now stood in the valley.
- **The seam can be put on and taken off by hand, and rehearsed on ground nobody has pinned.** `/tmscene` reports where the seam stands for the caller, hands over the summons as the end of chapter one would, gathers the Company round the caller's own feet for a rehearsal that answers no note and opens no page, takes the cast away again, clears the note so the seam can be walked from its start, and skips the scene outright for a showing that will not finish. It answers to gamemaster-level operators and to players in creative, as the other arrangement commands do.
- **The four hobbits of the seam now wear their own drawn faces.** Hobbit bodies had no named-skin lookup - only the tall folk and the dwarves did - so Frodo, Samwise, Merry and Pippin were wearing whichever of the sixteen village hobbit skins their variant picked. They now ask for `middle-earth/<name>.png` first, exactly as named humans and dwarves already do, so the four files that have landed are worn and a hobbit nobody has drawn keeps the clothes of his folk.
- **The end of chapter one leaves a note, and the Company leaving the courtyard is what opens chapter two.** The last page of the West's line no longer hands the next one over: settling it puts Elrond's summons in the player's hand - one of the pack's letters, named and lored for the errand - and the line's next page stays shut while the note is unanswered. Walking to the courtyard gathers the ten who wait there and speaks the old pack's departure in order, three seconds a line, from Gandalf's opening to Elrond's five closing lines; when the last line is said the bodies are taken away, the note is answered, and page 26 falls into the book without a walk back to Elrond. The gathering reads a `rivendell.scene` pin when one is set and otherwise Elrond's own stand in the valley, the cast is placed in the old pack's own half-ring, and the four hobbits stand on the hobbit body rather than as scaled men. Reloading is safe: the note and the having-seen are player tags, so a half-watched scene begins again at the same courtyard.
- **The chapter-one seam cutscene is recovered from the old pack, script and cast and all.** It was never a camera scene - the old world has no camera or cinematic anywhere in it - but a customNPCs scene: `FellowshipDepart1` spawns Elrond, Bilbo, Gandalf, Aragorn, Boromir, Legolas, Gimli, Frodo, Samwise, Merry and Pippin in Elrond's courtyard, speaks the whole departure over 1138 ticks, sends the eight walking to the road and hands over to `ElrondBye`, which says the last five lines and moves the player on. The beat sheet, the eleven lines of cast, the tick numbers and the eight walk scenes are written down in `docs/departure-scene.md`, so the scene is not scanned for a second time; nothing of it is built yet. The audit also found that eight of the eleven faces are not in the project - `bilbo`, `gandalf`, `aragorn`, `boromir`, `frodo`, `samwise`, `merry` and `pippin` - and that every skin in `CustomSkins` is the old 64x32 layout while every model here samples 64x64.
- **Gloin's camp is pinned at the High Pass's east mouth, and the road east of the pass is walked to it.** The scout marked the camp's ground at 8215, -3674 - the corner the 28 by 22 camp is laid from, its doorway in the middle of the south wall on 8229, -3652 and Gloin's own stand inside at 8233, -3664 - so the camp now stands there in every world instead of being surveyed out from the middle of the Gladden, and he is stood at the block the scout walked rather than six south of the camp's middle. East of the climb the road is three sections: `GLOIN_ROAD` from the pass's last mark at 5187, -3658 to the stream, `GLOIN_BRIDGE` over it on the four bank markers at 6434 and 6477 - forty-three columns, water 185 and 186, deck 188 - and `GLOIN_ROAD_EAST` on from the far abutment through the marks at 8124, 8177, 8201 and 8211 to the end point at 8234, -3590; the camp's own track leaves its door at 8229, -3652 and comes up onto the road at the junction on 8211, -3594.
- **A road with no build no longer has a footprint to be shut in by.** The high pass climb gave up on its first leg with one candidate settled and then laid 268 blocks of straight line through the country, and the high pass bridge's approach had been refused the same way. A route's box is the build's own ground, which its walk may not cross and has to leave square to a wall; the high pass routes were given a small box round their own start instead, so the very block the walk begins on was refused as build ground and every neighbour with it. A route with an empty footprint now has no wall to leave and nothing to keep off, and its walk begins on the mark the scout stood on - the three high pass routes are empty-boxed rather than box-round-their-start.
- **The high pass stops at the staging plaza, and the climb above it is laid as a road of its own.** The scout's new mark at 5124, -3401 ends the first section on the disc's own edge, and the second walk now leaves from the mark on its north rim at 5119, -3422 and climbs to 5187, -3658 as `HIGH_PASS_CLIMB`. A plaza is drawn ground, level across the disc and eased into the country at the rim, and it answers for its columns before any road does - a road run through it was graded by the pass and then taken back by the terrain the next time the chunk rose, which is what left the old end-point with no way on. The twenty-one blocks between the two ends are the staging floor, and `/tmroad build` now answers with the end a hand is nearest, so the plaza's edge finds the road down the vale and its rim finds the climb.
- **The high pass road is walked on north out of the plaza and up the pass.** The thirty-five markers a scout laid beyond 5124, -3399 carry it past the staging ground and up the climb to 5187, -3658: out of the vale at 78, north-west round the shoulder to 95, then east across the face and back west and east again in three switchbacks to 161. Thirty-two of them are pins - the three on the dead-straight runs stand on the line between their neighbours - and the route now ends at the last mark.
- **A staging plaza is drawn at the north end of the high pass road.** The block a scout marked at 5117, -3409 is the middle of a thirteen-block disc of level ground, generated as part of the terrain so it stands in every world: the columns inside take the drawn ground under the middle, the last four blocks of the rim ease that back into the country so the disc meets the wooded bank instead of standing on it, the floor is the wild mix trodden earthier in the middle, and its chunk clears the wood out of the clearing when it loads.
- **A span is closed all the way across.** The plan stopped a piece short whenever a span was not nine, eight and nine repeated exactly: the last middle was only placed if there was room for the far end behind it as well, and a turned middle was anchored a piece's length short of where it stands. The ford's fifty-three columns were three short of the loop's fifty and the bridge over the Buinen was eight short, both leaving open water in front of the far abutment. Middles now run until the far end has its own columns to stand on, and the far end, stood last, closes over the overlap.
- **A crossing on a named road builds itself.** Only a city's bridge waited for the city, because its abutments need the city's ground; a crossing on a road with no build behind it now starts from the first chunk it passes through and steps on the tick, the way every dry section does - so the high pass bridge needs no command.
- **The span can lie on either axis, and the high pass road has its bridge.** A `Crossing` was two bank x's and one z, which is the shape of the ford and not of the river: north of it the Bruinen turns east across the scout's new road, so the same two pieces are stood a quarter turn and the sweep, the plan, the pillars and both approaches walk the span along whichever axis its banks share. The four blocks the scout laid on that line (the shore at -3070, the water at -3077 and -3090, the far side at -3105) are read as the crossing they were surveyed for: banks at 5065/-3073 and 5065/-3098, twenty-six columns, the near end, one middle and the far end exactly, deck 79 over water at 77. The road north of it keeps the pins from -3138 on.
- **The road north out of the crossing towards the high pass is walked.** The twenty markers a scout laid from the ford's east bank at 5067, -3062 up the shoulder above the Bruinen and on to 5124, -3399 are the route's pins now, and the section is a `Route.Riverless` of its own laid five wide: it climbs twelve blocks over the first two hundred and fifty, then falls away down the vale's north side.
- **A drawn river's bed is plugged against the caves under it.** The water was laid on the drawn bed and its floor made stone, but only that one block of it: a carver that opened the bed from below left a throat into the aquifer, and the channel ran out through it - the water gone, the bed standing dry and stepped in the open, and a pool of the aquifer's water sixteen blocks underneath. Eight blocks of rock are now put back under every column the map floods, so the channel stands on stone and a cave beneath it stays a cave.
- **A drawn river's water is level across its channel, not stepped across it.** The masks paint their rivers as bands thirty blocks wide and more, but a river column looked only twelve blocks out for the lowest water of its own channel - inside one bank rather than across the channel - so the surface took each column's own blend of the vale's apron and the drawn country, and that blend falls over the channel's width. North of Rivendell that came out as a bowl: fifteen blocks of fall across forty of channel, the river lying in a stair of pools with its bed terraced under each one and the terraces showing through the water. The search now reaches twenty-four blocks, wider than the widest band, so a channel answers with one level across it and the only fall its water has is the one along its course.
- A trunk standing in the lane of a walked road is felled to its own crown rather than cut off at the tunnel's roof, which would have left a bare pole on the road with the crown it carries hanging above it.
- A road through a wood is cleared as a tunnel rather than to the sky: five blocks of headroom over the middle and the first column out, four at the second and three at the edge, with the wood left standing above the roof - and the pass and the generated-road clearing now share that one profile, so both kinds of road keep the same way through. A trunk standing in the lane is still taken whole, since a tree cut off at the roof leaves its crown hanging over the road.
- A recorded bend now carries the level its mark stands at: a mark under the grade cuts the road down to itself, within what that road may cut of the ground under it, and the stations either side walk down to meet it - so a switchback's arms reach the shelves they were pinned on instead of running on above them with a shelf and no way down. A mark above the road still lifts nothing.
- The switchback on the massif's west fall is walked through the scout's new pins: two lines 28 apart at z -2678 and z -2706, arms running the width of that band and dropping two to five blocks each, from 4867, -2688 to 4907, -2706 - nine turns where there were seventeen, so the grade has a long run into every turn instead of a terrace at each.
- The main road is walked from its west reach at 4560, -3360 through the pins at 4588, -3355 and the junction at 4638, -3355 where Elladan's camp comes down onto it, so the stretch west of the camp belongs to the Thorenhad line rather than to a second road; its path end moved to the new reach, which is also what lets the world re-lay the road without a forget.
- The mountain way's south end is folded onto the junction pin at 4818, -3118 rather than the 4818, -3115 it was recorded at, so the Thorenhad road, the mountain way and the wood ruin's link all finish on the one block the scout marked, with no three-block gap between three ends.
- The mountain way's north end joins the north road at last: the road now begins on the pinned junction at 4820, -2708, where the north road's line passes between 4793, -2688 and 4835, -2717, and runs through its first bend at 4810, -2720 down the flank as before. Those two doubled pins are the junctions at either end of the piece, which was the last length the network was waiting on.
- The Thorenhad road is walked on from its old end at 4745, -3355 to the junction at 4818, -3118: north along the shelf to 4748, -3292 and then south-east down its long slope through eleven pins, so it meets the mountain way's southern end and the wood ruin's way back there on one block rather than stopping in sight of them.
- The wood ruin has a second road, out of the foot of its own stair and south-east under its south wall through 4787, -3082 and 4808, -3096 to that same junction at 4818, -3118, which is the ruin's way back to the main road. Its westward way out to 4689, -3051 stays as it was, so the two leave the stair foot on either side and neither borrows the other's line.
- The switchback up the massif's face is walked as a switchback: the pins on the two lines are the turns of its legs, alternating from 4885, -2684 at 155 down to 4901, -2690 at 125, and the far line is moved six blocks in to -2690 so the legs stand six apart - five wide each, a block of the hill left between them - rather than seven blocks of slope apart. The line at -2684 stays where it is, because that is the one the country road above and the shoulder below both join.
- The ford road's descent off the massif is re-walked through the pins the scout re-laid: from 4885, -2684 at 155 the road takes the shoulder north-east to the hairpin at 4937, -2627 where the switchback turns, comes back south-west, and falls to the ford's west bank at 5003, -3060 at 80 in sixty-three bends, three blocks a leg at most, instead of the eleven the straight shot allowed. The line of pins twelve blocks south at z -2696 is the cutting's other lip: it ends at 4901, 125 with no road beyond it, so the road is walked on the north line.
- The troll caves' cluster at 4528, -2475 is walked as three roads: the two cave roads that meet there, and the road to the first troll cave, which carries on west through the junction pinned at 4478, -2489 to the crag the ford road leaves by. Every section is five wide, of the country's own surface.
- The ford road's descent off the massif is cut into the face instead of stepped down it: a route may now name its own cutting, and the section from the crag's line at 4875, -2691 to the pass at 4898, -2697 is allowed sixteen blocks of shelf instead of the ordinary four, so the road descends the fall at an angle on ground that holds it.
- The wood ruin's road leaves from the foot of the ruin's own stair at 4787, -3064, south to the junction at 4784, -3069 and away west to 4689, -3051, and the ruin's footprint now covers the stair it leaves by: measured short of the stair, the road's band graded the ruin's own steps into road and climbed the tower.
- The valley's roads are laid from the pins the scout walked: every section five wide, of the country's own surface, with the doubled pins at 4638, -3355, 4804, -2896 and 4824, -3055 read as the junctions they mark.
- The crossing at the ford stands on the line the scout pinned at z -3060 between the banks at 5055 and 5003, so the deck is 80 over water at 78 instead of the scouted 76 on a span that no longer lies there.
- The four crag roads are no longer walked and are gone from the table until they are marked again.
- The Thorenhad road ends on the crossing's road end rather than the mark it was walked at, so the two sections of the road meet on the ground the massif now stands on instead of thirteen blocks apart.
- The crossing at the ford is laid two blocks over the highest water on its span, so the bridge stands above the Bruinen instead of one block under it.
- The branch falls one block in twenty-four instead of twelve, so the four blocks between the city's water and the ford are carried down the whole course rather than cascaded into the crossing.
- The massif's bench beside the river gets the gorge wall's ribs, so the ground that comes down to the water is broken into spurs and hollows instead of ringing it in plates.
- The dale follows the Bruinen on both halves of the river and reads the water's level from the run, so the drawn channel beside the vale no longer tilts across its own width or steps into the ford.
- **The dale's bank grades the massif's outward fall, not only the drawn country.** The Bruinen crosses
  that band on its way past the vale, and the bank skipped it: the valley answers before the bank does, so
  the rock kept the crown's own height over water a hundred and eighty blocks under it - a sheer face down
  the range's whole western side and its north face, with the drawn channel cut through it. Ground past the
  rim is graded to the water now; the floor, the shoulder and the crown are still the city's own.

- **The bank climbs two blocks for every block of run out from the river, not one.** At one the cut from the
  river up to the drawn country was as wide as it was deep - seventy-five blocks of tread either side - so
  the vale read as the floor of a bowl with a rim where the drawing started again. Two brings the drawn
  ground in to one flank's width, and the bank still reaches the massif's crown (a hundred and twenty-one
  blocks of run, inside the field's own hundred and twenty-eight).

- **The dale's run is a distance along the river again, so the Bruinen is no longer held flat at the vale's
  water.** The following was seeded from every drawn water cell at once, which gave every stretch of the
  river a run of nought: the water never climbed, and `daleWater` returned the vale's water for the whole
  downstream course - seventy-seven blocks under the drawing, which is a canyon with the drawn country for
  its walls on every stretch the bank could not reach, and no grading at all inside the valley. The ford's
  own water is now the only seed and the run is the water's own distance from the crossing, so the river
  climbs back to the drawing one block in twenty-four as it was always meant to. Only the half leaving the
  vale keeps that run; the half coming down into it keeps the drawing's own fall.

- **The bank is graded on both halves of the river, not only the water leaving the vale.** The run the bank
  is grown from was the same one the dale's water level is read from, which covers only the half downstream
  of the ford - so every upstream stretch of the drawn river kept the drawing's own edge, a channel at 101
  in country at 154, and stood as a fifty-block wall with the water at its foot. That is why the walls were
  always on the side coming down into the vale. The bank now has a run of its own, followed on both halves;
  the water level keeps the leaving half's.

- **Every river leaving the vale is graded, not only the one drawn as the Bruinen.** The dale's cells were
  seeded only from the named Bruinen, and the bank the ground is graded over is grown from those seeds, so
  every other stretch of drawn water leaving the vale kept the drawing's own edge: a channel at a hundred
  and one in country at a hundred and fifty-four, which is a fifty-block wall with the water at its foot.
  The seeds are now every drawn river cell on the half of the map the vale drains into, so the bank reaches
  those stretches too.

- **The massif's opening to the water is eased out where it hands the ground back.** The vale's fall ends at
  a fixed distance from the floor and the cut the water gets ran right up to it, so the ground inside the
  seam stood where the water's curve put it and the ground outside at the drawing's own height - a face
  standing along the vale's outline, straight where the seam is straight and diagonal at its corners. That
  is the stepped edge the save showed in the sand beside the river at 6049, -3034, and at every other point
  on the seam. The last ninety-six blocks of the fall now ease the cut back to the ground the massif hands
  back.

- **The bank beside the dale's river climbs a block per block again, over a reach that can carry it.**
  Three was seventy-two degrees, and the save showed the branch running in a gorge with a wall on either
  side. One is forty-five, which is the flank the drawn country beside the vale already has; the reach grows
  from ninety-six to two hundred and fifty-six so the bank can still climb to the massif's crown, which
  stands two hundred and forty-two over the water.

- **The dale's water falls more slowly, and hands itself back to the drawing at the end of the reach.**
  A block per twenty-four of run rather than sixteen, which spreads the branch's climb over three or four
  hundred blocks of range. The fall no longer stops at the reach's edge: over its last three hundred and
  twenty blocks the drawing takes the water back, so a branch climbing into the mountains ends at the
  drawing's own level instead of in a step of twenty blocks or more where the field ran out.

- **The massif opens onto the river over twice the ground.** The climb from water to crown now takes three
  hundred and twenty blocks rather than a hundred and sixty, so no part of it is steeper than a block and a
  fifth instead of the sixty-six degrees that read as a striped face closing the ford.

- **The city's branch meets the drawn Bruinen at the drawn river's own level.** The profile's last step was
  read a few blocks short of the join, where the drawing stands a block higher, so the branch arrived a
  block over the water it runs into. The join is now measured at the join.

- **The bank beside the dale's river climbs three blocks per block of run, not one.** One for one was
  too shallow to be a mountain: the massif's flank stands two hundred and forty blocks over the vale's water
  and the cut never reached it inside the bank's reach, so the last of its ground stood as a face while the
  shallower country beside it was cut all the way. Three climbs the crown within eighty blocks, inside the
  reach, so the bank can climb to any ground it cuts and no column of its field ends in a step.

- **The bank's own surface carries a few blocks of roll.** A cut to one curve is a cone: every contour on it
  is parallel to the river and it reads as a cut however shallowing it is graded. Four blocks of the map's
  own noise, read against how much the bank takes off the column, gives the slope spurs and gullies where it
  is deep and dies out where it is shallow, so the water's edge stays the water's edge.

- **The bank beside the dale's river rises a block per block of run, not per ring of the mask.** Its cells
  are eight blocks across and the growth added one block of height per ring, so the ground for ninety-six
  blocks out from the water was held at twelve blocks over it and the edge of that shelf stood thirty or
  forty blocks over the river as the sheer wall the save showed back from both banks. A ring is eight blocks
  of run, so a ring now carries eight blocks of rise: the shelf is the graded bank it was written to be, and
  the wall at its edge is a slope down to the water.

- **A branch falls into the Bruinen at one block per sixteen of run rather than eight.** At eight a crossing
  that entered the dale a hundred blocks over the river lost all of it within eight hundred blocks of the
  junction, so it came down the last stretch like a chute into water that stays at the vale's level for a
  thousand blocks beyond. Sixteen gives the same fall twice the branch to run down, which is the grade the
  ground beside it takes.

- **The crossings fall to the river along their own run instead of at one wall.** A branch that joins the
  Bruinen was held flat at the vale's 76 from wherever it entered the dale, so everything it had to lose it
  lost in one step at the dale's edge - the sheer face a crossing showed where it met the river. The dale
  holds a run now rather than a place: how far each column of drawn water is from the Bruinen's own course,
  measured along the water, and every branch stands one block of fall per eight of run above the vale's
  water, so it comes down to 76 at the junction and climbs away from it. The river itself is the run of
  nought and stays at 76 as before.

- **The canyon is graded from the water itself, not from a walk of eight rays.** The ground beside the
  dale's water was capped by walking eight rays out to the nearest river and taking that ray's own length,
  which answers the distance along the ray rather than the distance to the water: between two rays the
  answer came out long, so the cut left a wedge of ground standing between every pair and the canyon walls
  read as a fan of sheer faces. The cap is grown out from the drawn water one cell at a time now, and read
  between cells, so a column depends only on how far it really is from the river and the walls are one slope.

- **The Bruinen is held at the vale's water along its whole drawn course past the vale.** The river leaves
  the vale at 76 and the drawing climbs it back to 155 over the next few hundred blocks before bringing it
  down again over a thousand, so the vale sat at the top of a hump. The dale follows the course now: the
  names bake says which named river each drawn cell belongs to, and downstream of the ford that named water
  is held at the vale's level - with the ground beside it graded down over the same reach - until the drawing
  lays country at or under it. Only the half of the course past the ford is taken; the water coming down
  into the vale from the mountains keeps its own descent.

- **The Bruinen stands at the vale's own water and takes its descent at a grade.** The country the masks
  draw round the vale stands at 155 to 175 and the vale's floor at 74, and the hold blended that height back
  into the drawing over two hundred blocks - one block in three, which is a wall of water at the rim, and a
  river held at the city outlet's 82 over a floor at 74. The water is the vale's own now, two blocks over
  its floor and held across the vale, and past that the height comes back at one block of rise per eight of
  run, with the ground beside it graded down over the same run and the drawing taking the water back where
  the ground it lays is already lower.

- **The dale holds the river up as well as down, and the branch meets it at its own water.** The vale's
  apron pulls the drawn channel down to the vale's floor, so at the join the Bruinen's water stood eight
  blocks under the water the city holds - a dip in the middle of the dale - and the branch, read off the
  ground under the fork, fell the same eight blocks below the river it feeds: the wall at the mouth. The
  hold now works both ways within its reach, so a river by the city stands at the city's water wherever
  the drawing gives it above or below that, and the branch reads the river's water rather than the ground
  and comes to the join level.

- **The vale holds the Bruinen in a dale instead of dipping it in and out of the vale.** The vale's
  apron is a radius and the Bruinen is not, so the drawn river was pulled down to the vale's own water
  where the apron came nearest - the city's mouth - and released again as it left: the water went down
  into the vale and climbed back out of it either side, which no river does. The river is now held at the
  water the city itself stands at for 192 blocks of the join and given its drawing back over the next
  208, and the ground within forty blocks of it comes down as a bank to that water, so the descent into
  the vale and the climb out of it are one broad dale with the country following it.

- **A drawn river stands at the drawing's channel, and the rock beside it gives way to that.** The
  mountain mask lifts the ground toward the crown, which is rock, and that lift was being read into the
  Bruinen as its level: the stretch by Rivendell stood twenty blocks over its own drawing wherever the
  mask had any brightness and at the drawing's level where it had none, so the river dipped into the
  vale at the city's own cut and climbed back out of it either side. The water, the bank that holds it
  and the ground that gives way to it now all read the channel's own level from the drawing; the lift
  is what the rock beside the river keeps.


- **The drape's deck line is the build's own floor, not a planted height.** A column counted as a deck
  only where its top stood at or under 76, which is the floor tile's own course in a world whose floor
  is 74. A world made since Arda was raised to 512 lifts the valley floor to 266, so every column of the
  city topped out far over 76, nothing read as a deck, and the drape would cut the ground outside every
  open face down to the roofline - the trench the deck rule exists to keep shut. The line is now the
  corner the city was planted at plus that course, so a raised valley is dressed as a low one is.

- **A drawn river's water stands at the channel, not at the step above it.** A drawn river took each
  column's own ground as its water's surface, and that ground is the drawing with the range's lift and
  the crag folded into it - which step at the mask bake's own pixel edges. A steeply drawn channel came
  out as a pool per step, and vanilla water then spilt from the high pool into the low one: water top
  217 at one row and 246 eight blocks later where the drawing puts the river at 214. The water now
  stands at the channel's own low level within twelve blocks of the column, so a stretch drawn up a step
  is one fall instead of a row of pools, and a channel drawn as a slope descends with it.

- **The drape cuts the mountain standing over a spring at the wall.** A spring held up at the build's
  edge is a channel, and a channel column was never cut at all - only filled - so the mountain over the
  pool stayed exactly where it was: one chunk of crown left hanging eighty blocks over the spring at the
  back of Rivendell while every other face of the city was dressed. A spring's column is now carried down
  to the pool's own surface and no further, so the rock over the water comes off and the water stays; a
  deck is still left alone, since a deck is not a roofline.

- **The vale begins at the massif's foot, not past its fall.** The apron that brings the country to the
  city's floor started where the massif's fall ended, so the fall had nothing but the floor to land on:
  beyond the crest was a flat ring where the drawing puts the range, a hundred and sixty blocks of
  mountain at seventy-four. The apron now starts at the foot, so the ridge comes down onto drawn country
  and the range keeps its height outside the crest.

- **The valley's ground holds the water it stands beside.** The valley answers before the bank does, so
  the floor beside a drawn river stayed at the floor while the river kept its own surface: on the vale's
  outer edge the water stood twenty-three blocks over the ground with a face at its edge, which is the
  wall of water. The bank now reaches the valley's ground the way it reaches the country's.

- **The massif gives way to the branch only along the branch.** The ground round the valley is brought
  down to the river it opens to, and a column beyond either end of the course was measured across the
  course's extension: at the back of the city, level with the branch and six hundred blocks from any
  water, the rock came down from the crown to the river level - a vale gouged into the massif behind the
  walls. Past the mouth and past the join the branch no longer answers; the drawn water at the join is
  still found by the walk.

- **Only the Bruinen's channel is river, and its cut stops at the end of its climb.** A column six
  hundred blocks off the course still projected onto one of its corners, and the vale floor there stands
  under the branch's water: every such column read as river, so the mountain wore a river's bed of sand
  across the vale. The cut now ends where its wall reaches the crown, and the river flag follows the
  channel rather than the water level.

- **The vale stops flattening the Misty Mountains.** The country beside Imladris settled to the city's
  own floor over nine hundred blocks, so the range south of the valley stood at seventy-four where the
  drawing puts a hundred and sixty and the Bruinen ran in a slot under a plain labelled river. It now
  settles over a hundred and twenty-eight blocks, and the range has the height the drawing gives it.
- **Glóin stands on the dwarf model too, on his own face.** His camp page had him a scaled man like
  Gimli was; he is now the pack's dwarf wearing `middle-earth/gloin.png`, which the same named-skin
  lookup picks up from the name he wears, so the two dwarves of the chapter stand alike and a world
  already holding the scaled man retires him for a dwarf.
- **Gimli is drawn as a dwarf, on his own face.** He was stood as a scaled man because the pack had no
  face for him; his own skin under `middle-earth/gimli.png` and his own model file are now what he is
  drawn with, looked for by the name he wears, so a dwarf with art of his own wears it and every village
  dwarf keeps the clothes of his folk unchanged.

- **The seam scene is parked, and the Bruinen by the valley is shown to be drawn.** The chapter-one to
  chapter-two cutscene now has its stop recorded in `docs/HANDOFF.md` - Elrond's own departure dialogue
  (`108`-`111`), the pins the survey still owes and the pass road that does not exist yet - so the next
  sitting can build it without re-deriving the script. A check of `Serious Test`'s player column against
  the river masks found the drawn Bruinen present in both masks and its chunks watered at y 62, with the
  low ground beside it the gorge's own give-way rather than a branch the bake dropped.

- **The Gladden grows its reeds and irises, and three lands get the trees canon gives them.** A reed block is
  sugar cane's plant under the mod's own name and the owner's own model, standing three high along the Gladden's
  water; a Gladden iris is the great yellow flower the marsh is named for, laid in beds of its own. Eryn Vorn and
  Mirkwood's fir south take a dark conifer, Eregion takes holly - the country is Hollin and the pack grew none - and
  Ithilien takes the tall southern evergreen. Each new leaf has the hardened twin the older woods keep.

- **Seven countries are put right on canon.** **Lorinand** is gold rather than Mirkwood's near-black - foliage
  `#9DA24A` over grass `#7E8B3E`, in a pale golden mist - and **the Gladden** is a fen: marsh greens, the fen fog,
  no human village, and out of `#arda_woodland` so the Vales' broadleaf stops growing over the reeds. **The Mark**
  takes no culumalda, being open grass. **Emyn Muil** loses the elves and the farm animals, **Cardolan** and
  **Minhiriath** lose the hobbits - all three now carry one empty-lands list of a wolf, crebain and birds - and
  **Khand** loses Harad's muster, no mumakil and no Haradrim, for riders and their horses. The five biomes that
  shared the near-pure `#22F927` green each have their own now (the Shire a rich `#77B255`, Firien, the Iron Hills,
  the Misty Mountains and the White Mountains greyer and paler), the Blue Mountains lose their teal grass, and the
  Misty Mountains are snow over bare rock instead of a glacier, ice left only in the highest hollows.

- **The biomes are audited against canon, and none of it is applied.** Every country's biome was read against what the
  books say about its ground, trees and people, and set beside the `arda_*` tags, the tree modifiers and the surface
  floors. Most stand: Enedwaith's fens, the Brown Lands' barren floor, Mordor's ash, Forochel's ice, Nurn's
  field-shores. Twelve do not, and `docs/arda-biome-canon.md` writes each one up with the passage it misses and what
  the fix costs - the loudest being the Barrow-downs growing dead trees where Tolkien says no tree grows at all,
  Lorinand wearing Mirkwood's near-black palette instead of gold, and the Gladden being a tan plain with a wood on it
  rather than the reed-beds and yellow irises canon gives it. The verdict came in the same sitting: the applied ones are above, the Downs keep their dead trees by choice, and the new trees wait on their textures.
- **The wood ruin's door is where its road arrives.** Its seat named the northwest corner of the floor plate as the
  doorway, so the ground it was measured against - and the travel stone stood beside it - was five blocks west of the
  way in the road actually meets: the ruin's own `.end` pin is corner + 5 east on the same north face. The plate is
  uniform at layer 0 - every one of its 182 columns has a block there, and the layers above are open across the whole
  footprint - so any column on that face is a valid sill, and the arrival column is the right one. The sill layer and
  the facing were already right and are unchanged.
- **A crag is seated by its own entrance.** The crag seat was fifteen to sixteen blocks down, measured against
  nothing: the shipped cuts are 56 by 56 with ten layers of solid rock, chambers at y 6 to 19, the way in cut through
  the east flank at y 6 (Matuna's lair at y 15) and the grass crown at y 27. Fifteen blocks buried the tunnel under the
  hill's own flank while leaving eleven blocks of turf and rock standing proud, which is what Bandrith's and Hoklond's
  came out as. Each crag now carries the mouth its own template has - the column and the layer its floor sits on, read
  off the cut - and is seated so that floor meets the ground outside it, with the crown as the cap on how far it may
  ever be set in. A crag whose file will not read keeps its ground rather than being sunk on a guess.
- **A ruin takes the ground out of itself.** The sweep that carries the ground around a build to its roofline only
  ever cut *above* that roofline, so a ruin seated on the highest column of a bank kept the hill standing inside its
  own walls - and where the template has a keep, the keep's height was the cap, so the ground filled the ruin to the top
  of it and it read as a mound of dirt and grass. Inside the footprint the cut now comes down to the build's own floor
  in each column, reads the template's own blocks out of its file and never takes them, and leaves water alone. The
  ring outside the walls is unchanged.
- **A place's arrow points at its stone.** The needle for a page that names a place pointed at the place's middle, which
  for Rivendell is the low corner the city was cut from, a mile from anybody a page names. A place that carries a
  milestone of the campaign's now points at that stone's own coordinates - the block it stands on, or the ground it will
  stand on if it has not been set yet - falling back to the place's middle for a place with no stone.
- **No build can be laid over its own blocks.** Every hand that lays a template for the chapter now asks
  `Sites.standsAt` first: the cut's own blocks are read out of its file and a majority of them standing at the spot
  refuses the lay, with a few blocks of band so a seat measured by two different hands still counts as the same
  building. The record a site keeps is read through the same guard, so a camp a couple of blocks off its written corner
  is still the camp. This is the rule no caller's arithmetic can talk its way past: a building seated on the ground
  under it is ground of its own once it stands.
- **Elladan's camp stopped stacking itself to the sky.** The campaign's fallback that lays a camp straight from its
  template re-read the ground under the footprint on every look, and a camp already standing is ground of its own: the
  heightmap answered with its roof, so each look put the next copy eight blocks higher until the world's ceiling. It is
  now seated on the ground the place was named on, and written down as standing the moment it is down, so the next look
  finds it. A building the world's own pass raises is recorded before its keeper, animal and stone are dressed, so a
  failure at the door can no longer leave walls standing that the next tick builds again.
- **The highland rock and woodland leaf mould are measured, not guessed.** The game's own `minecraft:surface`
  noise, sampled over Dunland's and Mirkwood's drawn cells, is near-normal about zero with a standard deviation of
  0.31, so the first bands were off in both directions: stone on 8% of the highlands and, because a band 0.35 wide
  holds 39% of this noise, leaf mould on 39% of the woods. Both floors now hit measured targets: highlands rock
  0.6-1.0 (3.0% of columns) over scuffed coarse dirt 0.4-0.6 (7.9%), grass the other 89%; woods coarse dirt 0.6-1.0
  (2.6%) and podzol -0.1-0.1 (24.9%), grass the other 72%. Steep ground is still rock, which the shares do not count.
- **Every country now stands on ground its canon asks for.** The countries that still wore the shared grass floor are
  given their own: sand and gravel beds under all ten rivers, lakes and inland seas; snow, packed ice and stone over
  Forochel, its slopes and Ered Mithrin; mountain stone through Moria; rock showing through grass on the high broken
  countries (Cardolan, Rhudaur, Dunland, Ettenmoors, the Barrow Downs, Emyn Muil, the Trollshaws, Eregion); mud through
  the grass of Enedwaith and the Gladden; and leaf mould and bare earth under the woods (Mirkwood, Mirkwood Spooky,
  Eryn Vorn, the Old Forest, Fangorn, Lorinand, the Wild Wood, Firien). The Dagorlad joins the Brown Lands' dry floor
  so the two sides of one plain match. Eleven grass countries - the Shire, Eriador, Lindon, Minhiriath, Rohan,
  Belfalas, Gondor, Ithilien, Dale, Rhun and Dorwinion - keep the shared grass over dirt, which is already right for
  them.
- **The older countries are checked against their own tags.** Ered Mithrin was tagged hostile but carried the Iron
  Hills' peaceful list - dwarves, goats, crebain, no monster - so it takes the Misty Mountains': trolls, goblins,
  duergar. Eregion was tagged peaceful but shared Firien's list, Hurons and all, and has its own now, Firien's without
  them. Brown Lands and Emyn Muil were tagged sparse yet sat in `#tolkienmobs:arda_broadleaf`, so broadleaf trees grew
  over the barren land and the crags; both are out of that tag. Khand is tagged desert and draws the desert's
  generation, but had a grass floor - it lays the Haradwaith's sand and sandstone now. The Brown Lands and the Eastern
  Waste are dry wastelands growing green grass everywhere, so their floor is coarse dirt with gravel outcrops and
  grass only within reach of water. The southron trader and the orc trader were the only land spawns in the pack with
  no spawn charge: they now cost what every other spawn costs.
- **Umbar has its own coast.** The corsairs' country wore the fallback floor every biome gets - grass over dirt - in a
  land that takes no rain, beside the Haradwaith's sand. It lays sand from the shoreline up to `UMBAR_SHORE_HEIGHT`
  (68) now, keeps the olive grass its own tint makes of the higher cells, and puts sandstone under the sand. The
  ground cover is short grass, which sand refuses, so the low coast stays bare; chunks already generated keep theirs.
- **The new countries now carry their own people and their own ground.** Umbar was registered hostile but spawned Gondor's list - a friendly people, farm animals, not one monster - so it spawns the Haradwaith's now: Haradrim after dark, mumakil, a rare southron trader and crebain. Minhiriath is tagged sparse and described as the grey, empty coast, yet it sat in `#tolkienmobs:arda_broadleaf`, so culumalda and lebethron grew over it; it is out of that tag and keeps only the grass cover the other sparse countries keep. Its leaf and grass tints were a light blue (`#6c85a2`, `#8abce1`) and are now the grey-greens of its own description (`#6e8060`, `#82996a`). Cardolan, Enedwaith and Dorwinion were checked and left as they were: Eriador's and Dunland's ground suit the first two, and Dorwinion is peaceful and carries the Gladden's people, which is what its tags say.
- **The countries the poster names now have ground.** Five registered countries had a biome, a height, a name and
  not one cell of the world: Cardolan, Minhiriath, Enedwaith, Dorwinion and Umbar. Cardolan takes the hills between
  the Barrow Downs and the Greyflood (the cells the hole sweep had given to Eriador), Minhiriath the coast below the
  Shire, Enedwaith the ground between it and the White Mountains' western end, Dorwinion the north-western shore of
  the Sea of Rhun, and Umbar the coast below Belfalas. `IMLADRIS` still has no cell on purpose - the valley is cut
  out of the Trollshaws' cells and blended back in - and the three seas are the grid's own default.
- **No more inland water where the poster has land.** The country grid begins as open sea, so every cell a fill
  never reaches stays a bay of it. Forty-four were closed in on at least five of their eight sides by land - the coast
  below Lindon, the Misty Mountains' northern flank, the ground west of Gladden, the Wild Wood's north edge, the Gap
  of Rohan, the cells between Eriador and Dunland, the White Mountains' east end, and the Harad/Nurn/Khand border -
  and each takes the country around it. Filling one uncovers the next, so the list in `draw()` is what the rule
  settles on over four sweeps, not what one pass found; a bay the poster draws stays, because it reaches open water.
- **The beach between the Midgewater and the valley was a hole in the drawing.** The Firien fill had been narrowed
  to two cells to keep off the Trollshaws' rows, and the cell it gave up stayed the map's default sea - two cells of
  water inside Eriador's eastern edge, on the walk from the fen to the valley. The shore was also being read off the
  *blended* cell height rather than the ground: a cell drawn at 70 beside a sea drawn at 38 crosses the waterline
  over an eighty-block band, so the land beside the hole came out as a beach. The hills are filled three cells wide
  again, with their southern foot, and `Sample.shore` now tests the ground that is actually there.
- **The fens have their plants as well as their trees.** The marsh's grass, swamp flowers, dead bush, mushrooms,
  pumpkin and lily were vanilla's placed features, which every Arda biome strips back out - so the fen was trees
  and reeds over bare ground. Each now has a feature the mod names (`marsh_grass`, `marsh_flowers`, `dead_bush`,
  `marsh_brown_mushrooms`, `marsh_red_mushrooms`, `pumpkins`, and the lily's existing `waterlily`), with vanilla's
  modifier lists copied in order, the pumpkin's grass-block test included. The Dead Marshes take the dead bush and
  the mushrooms and nothing green.
- **The fens have their trees.** The marsh asked for vanilla's `trees_swamp`, which every Arda biome strips out
  because Arda keeps only the features it names itself - so both fens were generated treeless. A mod's own
  `swamp_trees` now carries vanilla's placement unchanged: a weighted two or three a chunk, in square, on the
  ocean floor through no more than two blocks of water, and only where an oak sapling itself would stand, so a
  tree keeps out of the pools, off the reeds and off another tree's trunk. The Dead Marshes are left as they
  were.
- **Shore cane can no longer stand in the water.** Two things grew sugar cane: the mod's `shore_reeds`, which refused
  a water column but never asked whether the cane could stand where it was being put, and the marsh, which named
  vanilla's `patch_sugar_cane_swamp` with no filter on it at all. The shore feature now also carries the cane's own
  survival test - soil under the position and water beside it - so a column read above a channel, or a position at a
  bed with water round it, cannot be reeded; a marsh grows the same filtered feature rather than vanilla's. The client
  held the patched jar, so `runData` could not run: the two JSONs are written by hand in its shape, and the next
  datagen run should report nothing written.
- **Rivendell's raise works on ground the world has, nearest first, at the world's own pace.** `lay` made every chunk
  under a piece before standing it - nine chunk columns of generation, inside a tick, for a piece that may be four
  hundred blocks from the player. The raise now lays only into chunks that are already made, so the city fills in as
  the player walks it and nothing is generated to build it; `/tmcity band` still makes its ground, because an
  operator asked for the whole band and is waiting on the answer. A band's pieces are ordered nearest the player
  rather than by the cut's table - the widest band is a hundred and thirty-two pieces, laid in a sweep that could
  start four hundred blocks away - so the work arrives where it is being watched. And the cadence walks between one
  piece every four ticks and one a second on `MinecraftServer.getCurrentSmoothedTickTime()`: past 45 ms a tick the
  raise gives the next tick to the world, so a server over budget gets a slower city rather than a freeze, and gets
  its two minutes back when the world recovers. A piece left for its ground comes down on a later pass; a city nobody
  walks is a city that is not finished, so the road off it waits for the walk.
- **A road is only ever laid on the ground.** `RoadCommands.surfaceAt` walks a column down from the no-leaves heightmap
  to the soil the tree is growing on, and it was skipping air and logs only: a column whose topmost log had leaves
  beneath it stopped at the leaves and was read as that height, so the road went on the crown. Measured in Roads 7:
  road blocks with `oak_leaves` directly beneath them above Barachen's camp, and seventy-eight columns of the camp's
  own way in left hanging in the air once the canopy clearing took those leaves away - a road that stands on nothing
  is what a road on a treetop looks like. The walk now skips `RoadPaint.cover`, the same list the corridor clearing
  uses - leaves, trunks, vines, snow and the plants between the pavers - so the reading and the clearing cannot
  disagree about what is ground. Roads already laid keep the columns they have; this is what the next ones stand on.
- **The default trees grow instead of being laid down.** The three trees built by hand in `structure/natural` were
  planted as structures, so every wood repeated the same three trees block for block and a stand of them read as
  copies. Their shape is measured into code now. The templates stand thirteen, twenty-four and forty-six blocks tall
  on boles of ten, thirteen and forty; their feet reach nought, two and three blocks out; five to six, six to eight
  and ten to thirteen limbs leave the bole's upper half in a spiral; and their crowns reach five, eleven and nineteen
  blocks from the trunk. `OpenGrownTrunkPlacer` grows that shape - a foot of roots that closes to a single block
  within six, a bare bole, then limbs leaving one by one round the trunk and breaking upward at the end - with its
  limb settings in a `LimbsConfig`, the way the branching placers keep theirs in a `BranchesConfig`. The existing
  `SpheroidFoliagePlacer` crowns each limb end at the measured radius, so the crown stays the leaf placer's business.
  `NaturalTreeFeature` and its three feature registrations are gone; the woodland modifier still plants the same three
  placed features at the same weights, so the woods change in shape rather than in number. The templates stay in
  `structure/natural` as the reference the shape was read from. Arda reads its worldgen from the pack's generated
  JSON rather than from the code registration, so the three configured features were written out again with
  `runData`: until that ran, the pack still named the retired `tolkienmobs:natural_tree_*` feature types and the
  world refused to load at all, reported as unbound `tolkienmobs:tree/natural_tree_1..3` in the registry log.
  Loaded, it showed the other half of the old feature's work: `NaturalTreeFeature` had judged the ground itself,
  and a plain tree placement does not. Its heightmap ignores leaves but counts logs, so every tree found the top
  of the tree below it and grew from there - in a 61 by 61 box at the ruin, 3,091 wood columns topped out above
  y 150 and the crowns reached y 319. The three placed trees now carry vanilla's oak-sapling ground filter, which
  asks for soil below and room above and nothing about species, since these are oaks; a trunk is not soil, so the
  climbing stops. Chunks already grown keep their stacked trees.
- **The wood ruin's road ends on Barachen's own road.** Its line ran from the ruin at 4787, -3058 down through the
  bend at 4786, -3082 to the shared crag pin at 4741, -3092, which left it ending in open country once the crags'
  roads took that pin between them. Two marks walked in Roads 7 settle it: 4787, 78, -3082 below the ruin, and
  4892, 74, -3044, which stands on Barachen's camp road's own line. The route now leaves the ruin's south edge, runs
  south through the first mark and east to the second, so the ruin joins the track the camp's traffic already uses.
  The ruin was measured at 4783..4795 by -3058..-3047 of stone brick, spruce fence and trapdoor, and its footprint is
  that box less the row the road leaves on.
- **A wild track steps up stepped country instead of cutting a notch through it.** A route that names no kind was
  held to a named road's four blocks of cut and fill, and measured from the relief - the middle of the ground eight
  stations either side - which on terraced ground sits below the terrace the track is crossing, so the grade cut a
  slot through it: measured in Roads 6 at the third cave's crag, five blocks into the hillside at the median, with
  bare dirt walls either side and the terraces stepping over its head. Such a route now takes the ground of its own
  column and may stand one block off it, and the step sweeps turn a terrace into a step two stations long, which is
  what a path over a terrace is. A named road keeps its four, being a road built for wheels and a ditch.
- **A road is planned over ground that has been made.** `surfaceAt` read a heightmap off chunks nobody had
  generated, and `fillVoid` then carried the nearest readable station's ground across them, so a route's middle was
  a flat line at the height of the stations its own ends stood on. Measured in Roads 6: Barachen's camp road level at
  74 for all 241 stations while the country it crossed rose to 90, and `troll_cave`, `_2` and `_3` level at 75 up
  Eregion with the climb appearing only at the crag end, which the player had already stood at. The same fault chose
  their direction: the search preferred the corridor it could read, so cave 2 and cave 3 shared 909 stations within
  seven blocks of each other and both set off south before turning for their caves. `surfaceAt` now makes the chunk
  before it asks for its height, so what a road is graded against is the country that exists.
- **A main road is the ground it crosses, not a shelf four blocks over it.** The grade began at `relief`, the middle
  of the ground eight stations either side, and a route naming its kind answered to its own ground only as a band -
  so a profile that started as an average stayed an average, and the band could say how far it might drift without
  ever pulling it down into the hollows it was passing over. Measured along the Thorenhad road in Roads 5: level at
  80 for three hundred stations over ground between 74 and 79, with four to five blocks of stone under it the whole
  way, which is the fill that read as the road taking off and staying up. Such a route now starts its profile on its
  own column's ground, so the smoothing has a dip to walk into rather than an average to flatten; water keeps the
  relief, since a station over the river has no ground to answer to.
- **The chapter's roads are recorded end to end.** The wood ruin gets its way down to 4741, 81, -3092 - the pin two
  of the crags' roads also arrive on - through its marked bend at 4786, 78, -3082, and each of the four crags gets a
  road from the ground a road arriving at a crag meets, its `.end` pin twenty-seven or twenty-eight blocks out, to
  one of the three pins: Matuna's lair to 4741, 81, -3082, Hoklond's cave to 4741, 81, -3086, and Bandrith's and
  Sholdroth's together to 4741, 81, -3092, which is the order the walker's distances give. Barachen's track is bent
  through the two marks at 4819, 76, -2903 and 4896, 74, -2980 rather than laid across the shoulder in one leg, and
  still ends where it did. All five are wild builds' tracks, so all five are laid three wide of the country's own
  gravel and dirt; none has been seen in a world yet.
- **A river is not a marsh: the reeds keep to its banks.** `shore_reeds` placed sugar cane on
  `WORLD_SURFACE_WG`, and Arda's drawn rivers hand that heightmap a bed rather than a surface, so the cane
  started on the riverbed, found sand under it and water beside it, and grew up through the channel: a river
  came out standing full of cane, all the way across its width. The placed feature now refuses a column that
  is water before the cane is asked for it, so what the biome grows is the cane on the bank and the channel
  keeps its grass and kelp.
- **The Thorenhad road follows the ground again instead of riding a fill above it.** The grade answered to `relief`,
  the middle of the ground eight stations either side, and `relief` is a height a hillside does not have: the road ran
  flat at 81 for two hundred and seventy stations over ground between 75 and 82, and stood as much as eight blocks of
  stone above the low part of it. A route that names its kind now answers to its own column's ground, within the same
  four blocks of cut and fill, so the road walks down into a hollow and up out of it; water keeps the relief, since the
  river is the bridge's and a station over it has no ground to answer to. Settlement approaches are unchanged and may
  still be carried - a build may stand on what it is built on.
- **The Thorenhad road is a main road now: seven wide, of the country's own surface.** Laid by the bands it was seven
  blocks of street at the camp, five as the paving started, and a three-wide track for the rest of its length - a main
  road that narrowed to a path between places. It names its own kind instead, a new `MAIN`: seven wide from the
  junction to the ford, and gravel, coarse dirt, grass and path the whole way rather than the stone a town paves with.
  The bands are left to Rivendell's own road, which is a settlement's street and does widen as it comes in to the gate.
- **Arda's rivers, lakes and seas are furnished again.** The ten water biomes were left with nothing in them: their
  only furnish was vanilla's lily pads and grass, and the rule that Arda grows nothing but the mod's own places took
  those away. They now have ten features of their own - clay, gravel and sand in the bed, stones among them, seagrass
  and tall seagrass, kelp, sea pickles, lily pads and reeds at the edge - all of them placed on the water's own floor
  and refused where the water is too shallow, so one list gives a stream its grass, a sea its kelp forest and a lake
  both, without a depth being written down anywhere.
- **Other worlds' lakes, monster rooms and ores stay out of Arda.** The rule that Arda grows nothing but the mod's
  own places was only asked of features handed to a biome as holders, and vanilla's own default lists name theirs
  instead: the builder looks the key up itself and adds it without passing the rule. So the filter held, regeneration
  after regeneration, and then filled the data back in with lava lakes, monster rooms and every ore vein the first
  time the biome data was generated again. A named feature now answers to the same rule as a held one, so regenerating
  Arda's biome data leaves it exactly as it was. Those files also settle back to the shape the codec writes - key
  order, and no trailing empty generation steps - which an earlier hand edit had reshaped; nothing in them changes,
  and running the data generator again now rewrites nothing at all.
- **The woods plant the chapter's own trees.** A wood with no tree of its own - Eryn Vorn, Gladden and the
  Trollshaws, the three woodland biomes - was planted with the Mirkwood tree, which is a wood's answer only where
  Mirkwood was asked for. The three trees built by hand in `structure/natural` are now the default: each is a feature
  of its own (`natural_tree_1..3`), laid down as a structure rather than grown, and the woodland modifier plants all
  three at different weights - sixty attempts a chunk for the first, eight for the second, two for the third - so a
  wood is mostly the first tree with the other two rare in it. The feature judges the ground itself, soil below and
  six blocks of air above, rather than testing against a sapling: these are not one species' trees.
- **The wood beside the road is trimmed, not sheared.** Canopy clearing ran down the road's own columns only, to
  their full height right at the edge, so the leaves still standing beyond it made a sheer face along the lane: a
  corridor with walls. The columns beside the road are now trimmed too, a step shallower for each one out - three
  columns of shoulder, at three quarters, a half and a quarter of the reach - and only their leaves go, never their
  trunks, so a tree beside the road keeps its crown instead of being left as a crown in the air. Past the shoulder the
  wood is untouched, and a column with no crown over it costs two heightmap reads and nothing else, so open country
  pays nothing for it.
- **The road clears the whole crown of a Mirkwood tree.** The corridor's columns were cleared of leaves and trunks
  sixteen blocks above the surface, which is enough for an oak and not for the chapter's own woods: sampled along the
  Thorenhad road's line, twenty-two of a hundred and eighteen columns carry a crown taller than that, the tallest
  twenty blocks over the grade at 4939, -3088, and the camp's new junction at 4633, -3331 has one seventeen over it -
  so the branch would have come out under the top of the tree at the very join it was bent to make. The reach is now
  twenty-four, and a road cut through Mirkwood runs under sky.
- **Elladan's camp branch is bent to meet the main road.** The branch ran straight out of the door to a mark at
  4648, 89, -3325, sixteen blocks short of the main road's own start and past the line that road runs on, so the two
  sections never met: the camp's path simply carried on into the trees. It now follows the camp's paving to a bend
  recorded at 4648, 89, -3321 - where the stone path ends - and comes down onto the main road's start at 4633, 91,
  -3331 along the line the main road leaves on. About sixty degrees of turn at the paving's end, and a junction
  instead of a track running on past it.
- **A road is met along its bends, not through the hill.** Which end a pass set off from was read as the straight-line
  distance to the route's two ends, so a player standing on a bend could be nearer the far end through the rock than
  along the road and have the road laid from the other end. The meet point is now measured on the road itself - the
  door, the recorded bends in the order the road walks them, the far end - so the end a pass lays from is the end the
  player is nearest to on the ground they are standing on. A route with no bends is the one straight leg it always
  was, and picks its end as before.
- **A road is laid from the end it is met at.** The pass walked its stations from the route's own start, so a player
  who came on the main road at the ford watched it fill in from the camp three hundred blocks away - and only while
  that far end's ground was loaded. A pass now starts from the end nearer the ground that asked for it: met at the far
  end, the alignment is walked the other way, its stations turned round and the two ends' ground swapped, so the road
  lays outward from where the player is standing. A crossing keeps its own order, since its span and abutments are its
  own geometry, and a pass already in hand keeps the direction it began with.
- **A road begins wherever its ground loads, not where its build was found.** A road was planned only when a
  build's site was dressed and stepped only while a player was within ninety-six blocks of its head, so the crossing
  out of Rivendell led away across the ford and stopped: the road existed up to the first build that had been found
  and no further, and the rest began only when somebody walked to the other end. The passes are the world's now,
  held by road rather than by player - one pass per road, asked for by any caller - and a chunk that a road passes
  through begins it, from whichever end the player meets it. The tick carries the pass on while the ground under its
  head is loaded and waits when it is not, which is what resumes a road across a logout or a walk away and back. A
  crossing is not begun this way: a bridge needs its city's span and abutments, so it stays the dressing's.
- **The main road follows the bands, and Elladan's camp leaves by a wild branch of its own.** The road was to be
  laid seven wide end to end, which turns a road that runs from a camp to a crossing into a street the whole way. It
  keeps its own name and record and is banded instead - seven where it leaves the camp's ground, five as the paving
  starts, three for the country between - so the width still says where the road is rather than what it is for.
  `Roads.BANDED` names the roads the bands decide (Rivendell's crossing and this one) and every other route stays a
  wild build's track. The road now ends at the mark at 4633, 91, -3331 with the ford as its other end, and Elladan's
  camp walks out of its door at 4648, 90, -3314 to the mark at 4648, 89, -3325 on a branch of its own, three wide and
  wild, recorded under `ELLADAN_BRANCH` so it cannot inherit the note the main road left under the camp's name.
- **The Thorenhad road is a main road, seven blocks wide its whole length.** It was recorded as Elladan's camp's
  own road, which made it the camp's way out: a wild build's track, three wide, whatever it carried. It is a road in
  its own right now - `Roads.THORENHAD_ROAD`, so it has a record of its own in the save - and `Roads.KINDS` lays it
  `URBAN`: seven blocks of the settlement's own mix from the camp down to the ford's road end. The camp's own branch
  off it is to come, as a wild track three wide pinned where the branch leaves the main road. The new name means the
  old note (`tolkienmobs:key_structures/elledancamp`) is not this road's any more, so the next pass plans it fresh
  instead of believing an alignment walked for a camp.
- **A build is a city or a wild place, and its road is laid to match.** The three kinds were read off the ground
  the road crossed, so a camp's own track left the camp as a paved street five blocks wide, and the camps - nearly
  every build the chapter finalises - were given a gate they do not have. `Roads.CITIES` now names the cities
  (Rivendell, so far); every other build is a wild place, and its road is a wild track from its own door out: three
  blocks wide, of the country's own gravel and dirt, with no paving anywhere on it. A city's road keeps the three
  bands, its street inside the wall's band and the paving where the approach starts. A route that names a kind in
  `Roads.KINDS` still overrides both, which is what a main road between two wild places will be.
- **A road's kind is the road's, not its address.** Which of the three kinds a road was came from how far its
  columns stood from the walls, so a main road through open country could only be a track: the width followed from
  where the road ran rather than from what the road was. A route may now be given its kind in `Roads.KINDS` - `URBAN`
  for a main road, seven blocks across a moor as much as through a gate; `WILD` for a track that stays three wide at a
  camp's own door - and is laid at it from end to end. A route that names none is laid by the bands exactly as before,
  and what a column is made of is always the bands': a main road through the wilds is seven wide of the country's
  gravel until it reaches a town. Barachen's fire's track now names `WILD` instead of carrying a half-width number,
  and the Thorenhad road names nothing yet, so it still widens at the camp and narrows in the country.
- **A road is one of three kinds, and the kind is its width.** The bands decided what a column is made of but not
  how wide the road is, so every road outside the transition band was laid a wild seven and no wild stretch was ever
  three blocks wide. Three kinds now, named for the band that belongs to them: a wild track three blocks wide, the
  transition's five, a settlement's own seven - so a road widens as it comes in from the country to a gate instead of
  being one width from end to end. A hairpin's apron is one kind wider than the run it turns, rather than two
  half-widths, and the wild edge is worn by one column in three rather than left with one in three. A route that is
  one kind the whole way says so in `Roads.HALVES` and is laid at that width exactly, apron and ragged edge both.
- **Barachen's fire has a track of its own: a wild path, three wide the whole way.** It leaves the camp's south door
  at 4820, 76, -2886 and ends at the mark at 4892, 74, -3097 where the Thorenhad road runs past, so the two roads meet
  at the mark rather than each grading the country the other has already laid. The camp is read as sixteen by sixteen
  from its corner pin at 4812, 77, -2885, and the route is recorded as wild so the track stays three wide even at the
  door, where the bands would have widened it to the transition's five and then the settlement's seven. What a column
  is made of is still the bands' own: the camp's stone at the door, the country's gravel and dirt past it.
- **The Thorenhad road is bent through three scouted marks.** The walk between Elladan's camp and the ford's
  road end is now forced through 4875, 74, -3179, then 4885, 74, -3133, then 4903, 74, -3102 - the marks
  the scout walked in Roads 3 - so the line keeps off the hill above the crossing instead of taking the
  long way round the ford. They are recorded in the build, not pinned in a world, so every world walks
  the same line.
- **A road can be bent where the country needs it.** The walk between a build and its far end was one line chosen by
  cost, so where the country offered only one way round a hill it took that way even when it meant crossing at the
  ford and coming back to its own end - a road 1016 stations long for a 423-block run, turning back on itself
  forty-eight times. A route may now be given the blocks it must pass through, recorded in `Roads.BENDS` from the
  ammolite markers walked on the ground, and the walk is split into a leg per point: door to first bend, bend to
  bend, last bend to the far end. Nothing is pinned in a world - the positions live in the build the way a route's
  ends do - and a route with no entry there is still walked as one leg, so a road that needs no bend behaves exactly
  as it did before.


  A road already half laid carries the alignment it found, so `/tmroad forget` at the build throws that note
  away and the next pass walks the road again through the bends now recorded for it.
- **A camp's keeper is the man the camp is named after.** The world raises Thorenhad and Barachen's fire from their
  pins, and it stood a stranger named out of the giver pool at each of them while the campaign stood Elladan and
  Barachen separately once their pages came up - so a player who walked to Thorenhad before taking one found a
  nameless ranger at the fire and no Elladan, and a player who came later found two men. The world now stands the man
  the camp is named after, wearing the campaign's giver tags and written into the keeper record under the tag the
  line knows him by, so the line finds that body rather than standing a second one beside it; a nameless keeper left
  at a named camp is taken away instead of kept. The campaign also lays each camp's own cut rather than Elladan's
  camp at both, and never lays a camp the world has already raised at its pin.

- **A walk is given the whole window, and says when it gives up.** The search stopped after settling half the
  candidates it could see, so a fresh world's road came back as a two-station stub with nothing in the log to explain
  it. It may settle its whole window now - still a few seconds of ticks - and writes down the candidates it settled,
  the attempts it made and how far short of its far end it stopped, so a road laid straight through the country is a
  thing the log admits rather than one the player has to discover. The price of standing off the grade is also a fifth
  of what it was, since at a fifth the distance between the ends still counts for something when the country is flat.
- **A road with no bends is still a road.** A walk that found no way fell back to the straight line between the ends,
  and the simplifier then dropped every station of it - a straight line has no bends to keep - leaving two stations four
  hundred blocks apart and a grade that built a stub at each end and called the road laid. Stations are kept no more
  than four blocks apart now, wherever the line came from, and a walk that finds no way says so in the log instead of
  laying a line quietly.
- **How long a giver waits is a setting.** A traveller holding an offer nobody has taken already moved on half an
  hour after arriving; that half hour is `giverLifetimeMinutes` in the quest section now, so a pack can lengthen or
  shorten it, and nought keeps them standing for ever. A giver whose work has been taken is still left alone, since
  they are the body the work is carried back to - it is only the offers nobody reads that go stale.
- **A road leaves its build square, and never through it.** The walk could take its first step diagonally, and a
  band seven wide swinging off a door cuts the corner of a sixteen-block camp - which is a road that took a piece of
  the building as it turned. Every road now runs sixteen stations straight out of the door, square to the wall it
  leaves by, before the country may turn it; and no column inside a build's own footprint is cut, filled, cleared or
  paved, so the road meets the wall and the wall stays the build's.
- **A finished road keeps the alignment it was laid along.** The note a pass leaves when it finishes carried where the
  road met the build and the block under it, and nothing of the way it took - so every road became unreadable the
  moment it was done, and the first worlds whose passes completed had five-word records and no way to say how many
  stations the road had run, where it doubled back, or how high it had climbed. The note carries the alignment and the
  grade now, which is the only account a laid road leaves. The ground the note probes is read after its chunk is
  made, too: a note written off a column nobody had loaded said the road met its build at y -65, the floor of the world.
- **A road keeps to the grade between its two ends.** The walk priced what a step did to the height and nothing else,
  so where a valley ran straight to the ford and a hill stood beside it, it climbed the hill - a gentle climb is cheap,
  a steep descent is not - and then had to come down in switchbacks at the last, doubling back over ground it had
  already crossed. Every candidate now pays a little for standing off the straight grade between the ends, up to a
  limit, so the road takes the valley that leads where it is going and saves the switchbacks for country that offers
  nothing better.
- **A road no longer wanders to keep off a hill.** The way is chosen by cost, and cost alone will pay any length to
  keep off one steep step: the first walk in a fresh world came back with three thousand eight hundred stations for a
  four-hundred-block road, doubling back across the same meadow until it had paved a plaza. The walk now carries how
  far it has run and refuses a detour longer than two and a half times the distance between its ends, with one longer
  attempt when the country really needs the switchbacks; and its guess at the way ahead is measured in the eight
  directions it may actually walk instead of four, so it stops settling stations by roads it would not have chosen.
- **The four crags' road ends are recorded.** The redux walk left an ammolite marker on each crag's footprint edge -
  Matuna's lair among them - and the four are in the pack's pin table now, so a road to any of the stone's three or to
  the wood-troll's own hill has an end to grade to rather than the survey's guess. The ford's marker reads one block
  west of where it was written, and is recorded as it reads.
- **Matuna's lair is set into its hill like the stone's three crags.** The fourth crag was not in the list of crags,
  so it was seated on its ground the way a hall is: its rock skirt stood above the hill and its grass line six blocks
  higher than the ground it had been dropped on. It is a crag like the others now, buried to its grass line with only
  its mouth and its crown's own rock showing.
- **Matuna's lair has its ground in the pack.** The fourth crag had a template, a stone name and a checklist line
  but no pin, so a command asking for it was told nothing was called that, and any world without a hand-written pin
  fell back on the middle of the Trollshaws. The hill the scout marked in the scouting world, 4633, 94, -3081, is in
  the pack's pin table now: the lair is raised there like the stone's three, whether or not the page that names him has
  been taken.
- **A saved grade is believed only where it is a height.** The pass stopped in the middle of the Thorenhad road had
  already written a profile built on the floor of the world into the world's own note, and resuming it would have laid
  the rest of the road under the ground. A station whose saved height is not inside the world takes the ground under
  it, so a road caught by that bug carries on instead of resuming the dive.
- **A road grades from ground the world has actually made.** The Thorenhad road in the redux world ended in a trench
  because a height read off a chunk nobody had made yet came back as the floor of the world: the grade fell from 78 to
  -65 in one station, every column after it was left open, and the road stopped at the lip of its own cutting. A column
  of nothing is now read as nothing and given the ground of the nearest station that has some, and the grade is lifted,
  a step at a time, to ground it meets that stands more than a road's cut above the plan - so a bank is climbed rather
  than ending the road.
- **A crest or a hollow is no longer the road's shape.** The grade answered to each station's own column, so a knoll
  three blocks over the grass became a filled hump across a flat road, and a cave's mouth pulled the profile down into
  it. It answers to the middle of the ground eight stations either way now: too narrow to move a middle, and a hillside
  is still a hillside.
- **The cave's troll and the crown's ring are written down too.** The record the Elves keep now carries the crag's
  keeper, the Witch-king and the three keepers of his ring, each by tag, UUID and the ground it was left on. A page
  carried into a chamber the world has not read, or an arena walked back into after a reload, finds the body it stood
  instead of standing a second one; the ring is stood only for the slots its own count leaves empty, so a keeper that
  fell is still missed and a keeper merely out of sight is not replaced.
- **A keeper is remembered, not only looked for.** A tag only reaches as far as the chunks a world has in
  memory, so a keeper left in an unloaded chunk was invisible and the line stood another beside the one it could not
  see - the other half of the forty-five Glorfindels. Every body the line stands is now written down in the save
  (`tolkienmobs:keepers`) with its tag, its UUID, its world and the ground it was last on: the body is found by its
  identity after a reload, and the chunk it was left in is read once so it is back before the line decides it is short
  one. A record whose body has been killed is forgotten, so a keeper can still be stood again.
- **A keeper is found by his tag, not by his post.** The guard that decides whether a named Elf is already
  standing looked at a box around the valley, and a keeper whose step had no ground under it was stood beside the
  player instead: once the place's own middle moved, the box held none of them, so every visit stood another, and
  `Scouting World` reached forty-five Glorfindels and forty-three Elronds stacked on two blocks. The body is now asked
  of the world by its tag - one tag, one body - and a keeper left outside its own reach of the step is walked back to
  it instead of a second one being stood. The same guard is rewritten for Glóin, Gimli, Elladan and Barachen, whose
  camps can come back at a new pin the way the valley's middle did.
- **The valley keeps a crowd, not a flood.** The tick that keeps the city lived in stood its people again on every
  pass, and counted only the bodies it had just put down: once every mark was answered it stood the whole remainder of
  the crowd over again, every second, for as long as a player was within sight of the valley. `Scouting World` reached
  2,570 walkers and three and a half thousand Elves, and the server fell eight seconds behind a tick walking them. The
  crowd is now read before it is stood - a body counts while it wears the tag and no mark's own slot - so only the
  shortfall is put down, and a mark's body is found across the whole home it was given rather than the few paces round
  the mark, so one that has wandered is not stood a second time either.
## [Unreleased]
### Added
- **The city stops at the sky.** The cut's guards read the sky as the highest block a piece might stand on, when it
  is the first block above it, so a piece whose top reached the sky was laid - and a block written there is dropped by
  the world without a word while its block entity is still written down. A loading chunk then finds a dummy at a
  position no section holds, and the point-of-interest update for it throws `sectionPos out of bounds`. Pieces are laid
  only wholly under the sky now, and each is clipped to it as it goes in, so a template carrying block entities above
  this world's ceiling puts nothing up there at all - neither block nor block entity.
- **A road is down when the road is there.** A finished pass left a note saying the crossing was laid, and the
  note was believed for ever: a road laid before the valley was cut, or before the city it belongs to was raised, kept
  the dressing from ever planning a pass over it, so a fresh world reached the place where its road should start and
  found nothing. The note now carries the block under the point where the road met the build, and is believed only
  while that block is still there; one too old to carry it is not trusted at all, and the pass runs again.
- **A road already down says so instead of throwing.** A pass that found its own note answered with a summary whose
  far side held two numbers rather than three, and the crossing's report read the third: `/tmroad build` ended in
  `Index 2 out of bounds for length 2` with the road's own answer lost behind it.
- **A tree is not ground.** The drape read a column's surface from the motion-blocking top, which on a wooded column
  is its leaves, and laid that block back down as the cap - so dressing the ring round the valley floored it with the
  leaf blocks it had just cut through. The skin now comes from under the crown, and the tree above it is cleared with
  the column.
- **Mallorns grow in Lórien, not in Rivendell's valley.** The mallorn tag named Imladris, which is the valley the
  city stands in, so the city's own biome was carpeted in golden crowns from end to end - trees no cut can clear,
  because a cut lays its own pieces and leaves the country beside them alone. The tag names Lórien now, and the valley
  is left to the city.
- **What a world writes down lives in the world.** The city's markers (`_laid.txt`, `_dressed.txt`) and any
  re-cut of the cut were written to a folder beside the game directory, which is one folder for every world and belongs
  to none of them: two worlds shared one answer, a save copied to another machine lost the record of what was dressed,
  and a release left working files in the player's home. Both now live under the save, in `tolkienmobs/rivendell`, while
  the cut itself is read from the jar. A world whose marker was in the old folder dresses its ring once more, which is
  idempotent, and a road already down stays down.
- **The mod grows its own woods and ground cover.** With vanilla's features out of the biomes, the country had
  nothing left to grow. The mod's own tree features are handed to it by biome modifier: culumalda and lebethron over the
  broadleaf country, mirkwood over the wild woodland, mallorn over Imladris, and a new ground cover of grass scattered
  over every land biome through the `arda_cover` tag. Densities and sapling checks are the ones the mod already grew
  with, so nothing new is asked of the world - only that it grows what belongs to it.
- **A crag is buried six blocks deeper.** Seated by its grass line the crag stood with its grass exactly at grade,
  which left the whole rock skirt and the bare shell showing - stone and deepslate, a hill set down on a field rather than
  one the ground had taken back. The seat goes from nine blocks in to fifteen, so the grass line lies six under the
  ground and only the mouth and the crown's own rock show. Crags already standing in a world keep the seat they were
  placed with.
- **Arda grows nothing that was not drawn into it.** Every biome listed vanilla's trees, grass, flowers, ores, lava
  lakes and monster rooms as features and vanilla's cave, canyon and ravine as carvers, so a world the map had drawn and
  this mod places was furnished and undermined by another world's answers. The biome generator now keeps only the mod's
  own entries - rubble, stumps, the mallorn, the flowers - and drops every carver, the carvers having cut where the
  noise's terrain was rather than where the map's is: a mouth in the middle of a field, a shaft under a road. The crags
  are sunk templates and keep their own chambers. Generated chunks are untouched; the change is in chunks made after it.
- **A hole in the ground is spanned rather than followed.** A cave's mouth, a ravine's lip or a shaft read as ground
  is a grade the profile took, so the road dived into it, the band could not be filled deep enough, and the road ended
  at the lip with a gap beyond. Ground that stands `HOLE_DEPTH` above a column on both sides, with the two sides
  agreeing, is now read as a hole and carried over at the level of its sides; a fall line has one side high and one
  low and is graded exactly as before. A column below the grade is filled up to it however deep the hollow, while a
  cut stays held to `CUT_FILL`, so a hollow is bridged and a hill is still walked over rather than bored through.
- **Every build has the ground around it carried to its roofline.** The valley has been dressed column by column to
  the cut's own outline since it was first laid, and a building placed from a template had nothing of the kind: a camp
  seated in a hillside kept the hill leaning over its roof and a wood's crown through its hall. Each placement now
  sweeps its own footprint clear above its top and cuts the ring outside to the same height, easing back to the ground
  twelve blocks out, with water left in its channel and a crag untouched. A build already standing has the same pass
  run once a session when somebody walks up to it.
- **A footprint is measured past what grows on it.** The surface read took the top of a flower as the top of the
  ground, so a column with a tuft on it was read as a cliff and the footprint was refused; a crag is wide enough that
  some column of any meadow has one, which is what stopped two of the three crags being raised at their pins. Ground
  is now walked down through anything with no collision, and a pin still refused says which column refused it and what
  stands there.
- **A road that is down is recorded as down by the road.** A finished pass left nothing behind, so the world's
  dressing kept that answer in a marker beside the cut - and a pass that failed was written there as laid, which is
  why the crossing was never planned again and Rivendell's road never appeared. The pass writes its own note now, a
  failed pass is not recorded as laid and is retried a few times before being left for the next visit, and a route
  with no note is planned and laid.
- **Only the two scouted road sections are walked.** Rivendell across the ford is the crossing, and Thorenhad's camp
  to the ford's road end is the plain riverless one; they meet at the marker at 4972, -3075 instead of the camp road
  running on to the far bank and grading the same country from the other side. The ruin's road to Barachen's fire is
  not laid at all.
- **A road is laid as it is walked.** The pass was stepped only while its building was within reach, so a road stopped
  after the first few chunks the moment a player walked along it. It is stepped from the block the pass has reached
  now, so it keeps up with whoever is walking it and waits there for the next visit.
- **A road's ends are read from the ground rather than the treetops.** The ends came from a heightmap that counts
  leaves, so a road leaving a camp under a canopy began at the top of a tree and laid a staircase of stone into the
  air with the path on top of it. The ends are measured the way every other column of the road is.
- **A road follows the ground instead of boring through it.** The grade was free to stand twelve blocks off the
  ground and the alignment's own column was exempt from that limit, which cut shafts under ridges and stood stone
  towers over dips. The window is four blocks and no column is exempt: the ground has the last word on a station.
- **A pinned building stands at its pin, and nothing is searched for.** A pin names the corner the template is laid
  from, and a scout has already walked that ground with the building on it; the only thing left to measure is how
  deep the footprint's ground sits, which the seat reads. The ground search could walk a camp up to eight rings from
  the pin - while its road still left from the door's block, so the camp and its road ended in different places - and
  even with the pin tried first it still drifted to that search whenever the pin's ground was refused. A pin the
  world will not take is now reported and left unbuilt, to be walked again. The footprint is also checked for
  loaded chunks at its own far corner rather than in a box around the centre, so a build laid from its corner can no
  longer write the far half of its walls into a chunk nobody has loaded and lose them.
- **The valley's raise measures its reach from the city's box.** The pin is the city's own low corner, so a player at
  the far end of a five-hundred-block city, or on the road up to it, was never near enough to start it.
- **Rivendell raises itself from its pin.** The city only ever appeared if worldgen happened to start it or an
  operator ran the city command, so a world that generated the valley without the cut stayed empty. A player coming
  within a hundred and sixty blocks of the pin now plants the corner there and lays the cut a piece every four ticks,
  writing the same marker the hand writes, so the dressing pass and the road find it. A city already standing - made
  by the world or planted by hand - is left alone.
  ground it had already proved, and a site is standing from the moment it is raised, so no world's own camp was ever
  dressed or given a road: Elladan's camp stood empty and no road left its door. A standing build is now dressed and
  roaded on every approach, and a keeper is stood in the first place his column will actually hold him - the pin's
  own layer is where the scout stood, four blocks under the camp's floor.
  it finishes, and the pass reports the same of itself when the road is down, so the step budgets can be checked
  against real ticks instead of against a count of columns.
  in one step - ten templates, some five thousand blocks - and the lip pass walked every terrace of both roads in the
  tick after the last station, while a graded step laid eight bands of up to a hundred and twenty-one columns each.
  The bridge is now a piece a step, the lips a thousand columns a step with a cursor carried in the save, and the two
  graded roads share two hundred and fifty-six columns a step between them.
  column laid, so writing it once per step cost more than the road did; it is now written when a stage is finished, when
  the road is down, and every eight steps between. A world stopped between two writes re-lays the few stations those
  steps had reached, which are the same stations they were the first time.
  step - the stage, the alignment the search found, the grade the profile settled, the terraces it has drawn and how
  far each road is laid - into the world's own data, so a world stopped in the middle of one picks the road up on the
  next start instead of sweeping the bridge and re-cutting the shelf. The search itself is not carried: its state is a
  hundred thousand candidate blocks, so a world stopped inside that stage searches again.
  again, once for each build a player walked up to - a contour search and four hundred blocks of grading apiece. The
  answer now lives in the world's site record beside the building it belongs to, and a build with no route writes
  nothing down, so the day a route is walked the world can still lay it.
  that laid the valley's road was already written, but only the city's own dressing ever started it - so no camp's road
  was ever laid by anybody. The world now plans a build's pass the first time a player is near enough for the build's
  ground to be loaded and steps it on each approach after that, and Elladan's camp's route ends where the valley road
  ends, at 5006, -3075, rather than at the ruin's road end.
  walked height would be, and the road reads the drawn ground at each point along it and eight blocks either side,
  averaged, so it follows the contours of whichever world raises it instead of the contours of the world it was scouted
  in. A line is for a road with no build behind it at either end; a build's own road is a route and is laid by the pass.
- **A road's grade curves through its bends instead of kinking at each waypoint.** The height between two waypoints
  was the straight line between the heights walked at them, so a road that changed direction while it was falling had
  a corner in its profile, which a walker meets as a step. The grade is now a cubic through the waypoints, its slope at
  each averaged from the legs either side, held to a third of the shallower and levelled at a crest, so a bend eases
  through and the profile cannot dip below a height nobody walked.
- **A generated road can no longer be dug into a trench by two waypoints that disagree with the hill.** The corridor's
  shelf is held within four blocks of the ground it crosses, cutting and filling only that deep and keeping the ground
  it has beyond it, and the painted surface and the heights carvers and structures read are all that same bounded
  shelf. A road whose grade would stand in water is still left to the map.
- **Elladan's camp is recorded where the world raised it, dropped by its own door.** Scanning the scouted save put the
  camp at 4640, 92, -3313 - six east and one north of the pin that asked for it - with its floor four blocks above the
  ground outside its door, which is the seat's drop. The pin, the doorway's pair and the fire are now written four
  lower, so the keeper stands on the floor that will be under him, and the camp's route leaves from the block outside
  the door that is actually there.
- **A marker on the ground is an ammolite block, and that is written down now.** It has been the scouting habit
  for a long time - one `tolkienmobs:block_ammolite` where a doorway, a road end or a body belongs, corners for a
  cut - but it lived only in conversation, so every pass over the ground re-derived the spots from a template's
  shape. `AGENTS.md` and `docs/arda-pinning.md` now carry the convention, the block that counts (`block_ammolite`,
  not the pane or the ore), and the way it is read back: `tools/ammolite_markers.py`, which groups the markers in a
  save and prints their coordinates, with `--compare` to measure them against a known pin.
- **The valley's stone is a place, not a page.** `Sites` keeps a table of stones that stand with no build of ours
  under them - ground another hand raised - each pinned under its own key and stood when a player comes near;
  Rivendell's is waiting on the marker that will be its spot.
- **The ruin's steed lies on its side.** The model gains one root bone over the seven it had, and a `fallen`
  animation rolls the whole animal a quarter turn about its own body; the steed the world leaves at the ruin wears
  a synched flag that plays it, so it is found down rather than standing.
- **Every building carries its stone.** A milestone now stands at each build's door when the building is laid -
  two blocks out from the entrance and one block to the side, the same spot in every world - instead of being
  planted by each page that sent a player there, which is what left a second stone at a camp that two pages
  named.
- **A build's facing is read from its entrance.** The doorway table records which way each door opens, and the
  keeper, the stone and the seat are all measured from it, so a building's front is data rather than a guess.
- **Barachen's fire and the ruin's steed.** Barachen's camp is raised with the rest of chapter one rather than
  waiting for a page, and the ruin keeps the black steed its rider came off, left there by the world.
- **A camp's keeper stands at its door.** The body the world puts at a camp was placed beyond the far edge of the
  footprint, which is where a fair-weather tent's front was and is behind Elladan's camp; with the doorway known,
  he now stands two blocks outside it on the ground the road arrives on.
- **On-surface buildings are seated by their doorway.** The wood ruin, the tents, the castle and the arena - and
  Elladan's camp, which was the same case fixed for one building - now measure the ground at the column their own
  doorway opens onto and lay the sill level with it, so a player walks into the floor rather than up onto it. A
  build with no entry in `DOORWAYS`, and every crag, keeps the older seat.
- **One camp, at the pin.** The worldgen anchor that put Elladan's camp at the middle of Thorenhad's cell in every
  world is retired - its structure, set, pool and biome tag are gone from the generated data - so the camp a world
  holds is the one `Sites` raises at the pin the scout walked.
- **The world's own camps stand with somebody at the fire.** A camp the world raises now plants its keeper with
  it, the same named body a page-raised camp gets, so a player who walks to Thorenhad before any page finds
  someone there. The crags and the ruin are still raised empty: what is at those belongs to the page.
- **Elladan's camp sits where its door is, and level with it.** The camp's doorway was marked in the scouting
  world at 4642, 91, -3314 - the middle of the template's south face, eight blocks in from the corner it is cut
  from - so its pin moves to 4634, 92, -3314. The build's floor is also cut two blocks above its own base, which
  left the entrance two blocks up out of the ground it stands on; the camp is now set two blocks into its ground,
  so the floor meets the ground the road arrives on.
- **The camp's road to the wood ruin.** From the block beside Elladan's door (4642, 91, -3315) to the ruin's road
  end (4782, 78, -3057): the second road on settled ground with no water on it, and the first that starts at a
  camp rather than at a build's own wall.
- **Chapter one's buildings are raised by the world.** Elladan's camp, the wood ruin and the three crags are cut
  ground rather than generated ground, so they appeared only once a page named them and a player walked close. The
  world now plants each one at the pin the scout walked and stands it when a player comes within four chunks,
  whether or not anybody has been sent - so a regenerated world has the chapter's buildings in it, and a page that
  names one is answered with the ground already standing rather than a fresh survey and a second copy.
- **The wood ruin's road to Barachen's fire.** The first road on settled ground with no water on it: from the
  block beside the ruin's door (4782, 78, -3057) to the block beside the fire (4820, 76, -2886), a hundred and
  seventy-five blocks, searched and graded by the same pass the valley's approach uses and arriving on the ground
  at the fire rather than on a deck.
- **A marker scan for a scouting world.** `tools/ammolite_markers.py` reads the ammolite blocks a scout put
  down out of a save's region files, groups the ones standing next to each other, and measures every group
  against a pin named on the command line - so a location marked in the world can be read back without
  opening the game, and a pair of markers can be told apart by which one is nearer the build.
- **A dry road is a route of its own.** The pass was written around a crossing - a span to sweep, pieces to stand,
  pillars to draw - so a road between two builds on the same bank had to carry numbers for water it never meets.
  `Route` is two kinds now: a `Crossing` with banks and a deck, and a `Riverless` that is a build's entry point and
  the far end, graded by the same search and the same profile and arriving on the ground rather than on a deck. A
  new road is one line in `Roads`.
- **`/tmtp` takes a name as well as a region.** The jump read only countries, so the valley and every
  building had to be reached by typed coordinates even though the world knows their ground. A name is now
  looked up in the world's places and pins first - a build by the last word of its pin key, `troll_cave` -
  and a country is only what answers when nothing has taken the name, so a name moved with `/tmplace` is
  moved for the jump as well. What it landed on is said back.
- **`/tmtp trollshaws` lands at Thorenhad.** The region jump used to drop a caller in the middle of the
  Trollshaws' cells, which is the drawing's answer and not the ground: the camp is settled now, so the
  country is locked to the block the scout stood on and a jump puts a builder in the camp. The table takes
  one line per region, keyed through the country enum so a region cannot be spelled two ways.
- **Chapter one's ground is kept for every world.** The scouting world's pins are in `Pins.SOURCE` now -
  Thorenhad, Barachen's fire, the ford, Elladan's camp, the wood ruin and the three crags - so a world nobody
  has walked raises them where the scout stood instead of surveying the drawing for a hillside. The keys are
  the `Sites` constants, so a checklist line and a placement cannot drift apart.
- **The wild tent's chests draw from the tent's own table.** The canvas the world generator scatters carries two
  chests, and the template brought them across bare - no loot table, no contents - so `tolkienmobs:chests/wild_tent`
  was registered and rolled by nothing, and every tent met on the road opened two empty boxes. Both are pointed at
  the table now, the way the rider's ruin's chests are pointed at `desolate_ruins`, and the table fills them the
  first time each is opened.
- **The third crag answers to its name as well.** `/tmpin checklist` had the last of the stone's three caves down
  as "the third cave" while the page asks it as Sholdroth, so all three now read the names the notes give them -
  Bandrith, Hoklond and Sholdroth - in the order the page asks them.
- **The checklist calls the second cave by the name the page gives it.** The stone asks its three caves in the
  order the notes name them - Bandrith, Hoklond, Sholdroth - and `/tmpin checklist` had the middle one down as
  Sholdroth's, so a scout pinning ground off that list would have put the second crag where the third cave is
  asked. The label reads Hoklond's now, and the pinning guide's row names all three in the same order.
- **The ground still to scout is written down in one place.** `docs/quest-locations.md` lists the fourteen
  locations the pages walk to once Rivendell is set aside - the pin command for each, the cell the drawing
  would use, the template's own footprint and what the sight of the ground should be - so a scouting pass can
  be read in one window instead of put together out of two chapters of pages.
- **The rider's ruin has moved in with the campaign's own ground.** `ttmruin_large_2` still stood in the worldgen
  folder although nothing generated it - no structure, pool or set named it - so the one template the Missing Rider
  page raises sat among the ruins the world scatters. It is `key_structures/ttmruin_large_2` now, beside the crags
  and the camps, and `Sites.WOOD_RUIN` names the new path.
- **Barachen's camp is a build of its own, and the tent is the world generator's again.** The canvas the camp errand
  used to pitch is `key_structures/barachans_camp` now, named in the Trollshaws in the cell Barachen's fire is
  surveyed in; the traveller's tent keeps its own `wild_tent` template in the worldgen folder, and `Sites.WILD_TENT`
  names that one, so the side errand and the scenery still raise the tent while the campaign's camp stands on ground
  of its own.
- **Each build carries its own road.** The road's valley numbers - the path's end, the two banks, the span's line
  and width, the deck, the run-in and the exit, the two bridge pieces and the build's own footprint - are a `Route`
  in `Roads` now rather than constants the pass happens to know. The dressing looks a city's route up by its
  footprint and lays that road; a build nobody has walked a route for is draped and left alone rather than handed
  another city's road. `/tmroad build` lays the road of the build nearest the caller.
- **The world only dresses the valley while someone is in it.** The dressing pass now waits until a player is within
  320 blocks of the city's footprint before taking a step, and pauses again when they leave - so a generated world
  that nobody visits costs nothing, and the drape, the bridge and the road are all done on ground the game is
  already keeping loaded. The gate measures from the footprint rather than a corner, because the city is five
  hundred blocks across and a corner is the wrong end to measure from.
- **The road is laid in stages, like the rest of it.** The pass the hand runs in one word is a cursor for the world
  that runs it for itself: twelve columns of the old timbers swept, then six bridge templates in one step, twelve
  columns of pillars drawn down, four thousand settled candidates of the descent, and eight stations of each graded
  road a step. The search and the grade are the same arithmetic, only resumable, so a tick never carries the sweep,
  the bridge, the search and a hundred-odd stations at once. The dressing also stops looking for a city every tick:
  it looks once a second and works every tick, which is what makes two chunks of ring a tick mean half a minute
  rather than ten.
- **The ground and the road happen by themselves.** A finished city - generated, or laid to its last band - now has
  the ring of ground outside its walls draped to the build's outline, and the road and bridge to the Bruinen built,
  without a command. Neither runs whole: the drape reads the cut's outline four pieces a tick and dresses two chunks
  of ring a tick, and the road goes down in one pass once the ground is dressed. What was done is recorded in
  `_dressed.txt` by dimension and corner, so a restart does not repeat it, and `/tmcity drape` and `/tmroad build`
  remain the same passes for an operator working by hand.
- **The campaign finds a city the world generated.** `CityCommands.plantedCorner`, the tick-time answer to "where
  does the valley's city stand", read only the marker, so a world that generated Rivendell looked cityless and a
  body's visit found nothing to dress. It now falls back to the structure start in the chunk the pin falls in,
  reading only a chunk that is already made and asking again at most once a second while the answer is no, since
  the valley is empty until a player walks into it.
- **`/tmcity` finds the city the world generated.** A generated world writes no `_laid.txt`, so every hand used to
  answer "no corner planted yet" - and planting at the pin would have laid a second copy of the same 584 pieces on
  the first. The commands now read the structure start in the chunk the pin falls in, and treat it as a city that
  is planted and whole: `status` says it was generated, `goto`, `drape`, `apron` and `folk` work on it, `band`
  reports nothing left to lay, and `origin`/`summon` refuse to plant over it. `/tmcity relay` still takes such a
  city over, which is how a re-cut is laid on top of it deliberately.
- **The city generates from the jar, as a cut of 584 pieces.** A `tolkienmobs:piece_layout` structure carries a
  cut's corner and its piece table - a template and the step from the corner, for each of them - and places every
  piece there with no ground reading, no rotation and no terrain adaptation, so Rivendell stands from 5200, 75,
  -3370 as it was scouted rather than waiting for an operator. The table is not transcribed: the data generator
  reads the cut's own `.parts` file in the jar, so `worldgen/structure/rivendell.json` and `/tmcity band` put the
  same piece on the same block. The pieces cost nothing at world start - each places itself as the chunk it
  touches is made - and nothing is bearded under them, the floor being the map's own ground.
- **The city ships with the mod.** The cut - 584 pieces, a hundred and four megabytes of them, with the two
  tables that place them - is in the jar under `data/tolkienmobs/structure/rivendell`, so a world gets Rivendell
  with nothing downloaded beside the pack. `CityCommands` asks the jar first and falls back to a file the jar
  does not carry in `run/rivendell_art`, so a re-cut can still be dropped in and looked at without rebuilding,
  while a folder left over from an old cut can no longer quietly win over the shipped one. The marker
  `_laid.txt` stays in the game directory: it is where this world planted the city, not part of the cut.
- **A build can be pinned into the world's own generation, and Elladan's camp is the first.** Every structure the
  mod generates was scattered by vanilla's random spread, which cannot be asked for a particular block - its
  cells are a hash of the seed - so a place a page walks to could only be planted by hand. A `tolkienmobs:pinned`
  placement now answers for exactly one chunk, the one holding its anchor, and a `tolkienmobs:pinned` structure
  places a pool's pieces at that chunk's corner rounded out by the anchor's place inside it, seating them on the
  ground under them. `tolkienmobs:thorenhad_camp` is Elladan's camp, `key_structures/elledancamp`, pinned at
  Thorenhad's own cell middle, 4864, 0, -3328, in the Trollshaws; the anchor and the pieces are written from one
  `Pinned` line in `TolkienStructureProvider`, so they cannot drift apart. Generation costs nothing at world
  start, because a structure's pieces place themselves as the chunks they touch are made.
- **The road out of Rivendell is settled, and the notes say what it does.** Three records still described the
  provisional pass - the road "painted straight in and straight out along the span's own line", its shape
  "still being argued about" - which stopped being true when the approach became a search with a graded
  profile, eased corners and a half-step lip. They now record the road as built and as shipped: the pass
  lives in the jar beside the two bridge pieces, the alignment is the one thing a generation rule would have
  to bring with it, and rerunning the hand on the same ground reproduces the road.
- **The road no longer climbs what it cannot walk round, and no longer drops.** The bridge approach was
  forced straight for sixty-five blocks; a fresh generation stands a fifty-nine-block rock fin on the bank
  astride that line, so the graded road climbed the fin and plunged off its end sixteen blocks before the
  deck. The search now runs all the way to the launch onto the abutment and only the last sixteen blocks
  are pinned to the span's line, the profile is swept to at most one block of rise or fall a station so a
  fall reads as half-steps rather than a drop, and the column the alignment runs through is laid whatever
  the ground under it does, so the road narrows against a rock face instead of breaking over it.
- A road finishes on its step sweep rather than on the ground clamp, so no station keeps the two- or three-block
  riser the clamp was putting back after the sweeps had already pulled the profile a step at a time.
- The hairpin apron is laid only where the alignment doubles back, not at every bend of more than thirty degrees,
  so an ordinary corner no longer widens the band out over the bank it is turning beside.
- The road carries only its own three columns over water and leaves the rest of the band at the bank, instead of
  laying a stone-and-gravel shelf out onto the river at the wide stretches.
- **An ordinary corner is eased rather than hinged.** Every corner was cut back at most ten blocks, which
  is barely more than a hinge at walking speed. A bend now opens out as far as its legs allow, up to
  twenty-four blocks either side, so the road arcs round it and a walker carries speed through; a
  switchback stays tight at ten, since easing one wide would swing the road off the shelf it is on. The
  wider arc reaches ground the search never walked, so it is closed back until it stays off the river,
  out of the city's footprint, and on ground the corner's own cut and fill can answer for.
- **A step's slab is the lip of the terrace above it, not the foot of the one below.** The half-step slab went
  into the lower column, where a bottom slab fills the bottom of its cell: the walker dropped a block and a
  half onto it and still had to jump to climb back. The slab is now the higher column's lip, half a block under
  the terrace floor with the terrace below as the other half, so a fall walks as block, slab, block, slab in
  both directions. Only the two positive neighbours were compared as well, which left the slabs as a sliver off
  one corner of a diagonal step; the lip now follows every side of the terrace.

- **The switchbacks are hairpins, not hinges.** The searched walk turns within a single block, so its
  corners were right angles a walker could not take. The alignment is simplified to the vertices that
  carry its shape, and every corner left is now cut back ten blocks along both legs and joined by a curve;
  the surface is widened by two blocks either side where the road is turning, so the inside of a hairpin
  is an apron to walk round rather than a lip to fall off.

- **The road switchbacks where the hill is too steep to follow.** The descent paid linearly for height
  change, so on a uniform face it simply walked the fall line, and the fall line here is steeper than any
  road: the profile was left choosing between holes in the grade and embankments of stone. A step now costs
  its length, twelve times the square of any grade above a third of a block per block, and a little for
  every change of direction - so the walk runs along the contours, turns where the face is too steep to
  fall down, and comes back lower, and the road's profile is then the ground's own, smoothed.

- **The road hugs the hillside instead of spanning it.** Pinned between the path's 134 and the bridge's 76,
  the graded profile became a ramp: where the mountain fell faster than the ramp the road stood in the air
  on twelve-block stone columns, and every column whose cut exceeded the budget was left as a hole, so the
  stretches over the water's edge were laid while solid ground under the same road was missing. The profile
  now follows the ground - a moving average along the alignment takes the bumps out, and eight passes hold
  the result within twelve blocks of the ground - and the descent search pays linearly for height change,
  so a long gentle line is preferred to a short steep one.

- **The road's corridor is cleared of canopy.** Cutting a road through the trollshaws' woods took the
  trunks away with the ground but left their crowns hanging over the grade, so a cut stretch sailed under
  a floating roof. Every column the road lays now has leaves and logs cleared from one block over its
  surface to sixteen blocks above it, and the pass reports how many it took.

- **The graded road reads the ground, not the canopy, and its grade has the last word.** The first graded
  run reported a steepest step of eighteen blocks and stood stone columns up to the treetops:
  `MOTION_BLOCKING` answers with leaves, and the ground clamp ran after the grade clamp, undoing it. The
  road now reads past leaves and trunks to the soil, applies the cut-and-fill budget first and the grade
  last, and carries its last verge columns over the water on stone while its grade is above the water
  line rather than stopping short of the bank. The bridge also lost its last middle: the loop asked for
  eighteen columns of room when a middle plus the far end need seventeen, so the run closed one piece
  short and left open water in front of the far abutment.

- **The road is graded, and the bridge stands where the markers are.** The first road followed the ground,
  and the ground under the valley's own path is a wall face - 134 at the path's last block, 109 six blocks
  out, 74 at the river - so painted it read as a staircase down a cliff. It now grades: the pass searches
  its own way down the live heightmap with a weighted walk that prefers level ground and pays for every
  block of rise or fall, so it contours and switchbacks where the face is a wall, then gives that line a
  profile clamped to 0.45 blocks of rise per block of travel - cutting into the hillside above the line and
  standing it on stone where the hill falls away, to a budget of twelve blocks either way. Two anchoring
  errors in the bridge are fixed: a template turned through half a circle is anchored at the far corner of
  its own box, so the east end now lands on 5055 instead of eight blocks out in the water, and the middles
  are laid from the east cursor by their own width instead of from their west edge, which had left a gap of
  open water before the far abutment.


- **The road out of Rivendell, and the bridge over the Bruinen.** `/tmroad build` paints the approach
  from the path's last block at 5200, 134, -3034 down the wall and along z -3075, stands the bridge from
  the shipped `bridge_end` and `bridge_middle` pieces - two ends and four middles over the fifty columns
  between the bank markers at 5055 and 5006 - draws every pillar the pieces carry down through air and
  water to the bed under it, and carries the road straight out of the far abutment. Both ends are met on
  the span's own line, because a road arriving diagonally would need junction pieces the cut does not
  have. The surface is the one `docs/arda-roads.md` settled: three bands by distance from the walls, a
  per-block hash, and a bottom slab on the low side of every one-block rise.

- **The ground outside the walls is dressed to the build's own outline.** `/tmcity drape` - and the
  end of `/tmcity band` and `summon`, so it is part of laying the city rather than a second word -
  reads all 584 pieces' templates, takes the highest block of the build in each of the 554 by 512
  columns, and then cuts every column in a 32-block ring outside the footprint down to its nearest
  wall column and fills it up to it, easing back into the drawn country at the ring's edge.
  Water columns are left exactly as they are, which is what keeps the river's mouth and the
  spring's outlet open rather than filled to the walls. The outline comes from the cut, not from
  the world, so it measures the city and not the rock the city stands in.
- **The branch runs again, from the new mouth to the drawn Bruinen.** With the planted ground laid, the
  city's water still had no cut out of it: the massif stood at 318 across the hundred and forty-seven blocks
  from the west wall at 5200, -3067 to the drawn river at 5054, -3086. `BRANCH` is on and the course is those
  two columns - the city's water at 82 at the wall, the vale's 74 at the join - so the map cuts a twelve-wide
  channel whose walls climb from the water at 1.2 blocks per block out, and opens the massif along it at 0.6.
  The spring at the rear, 239 over a floor at 74, still has no cut of its own.
- **The city's own ground is drawn round the pin again, and the river is what is left.** The planted valley
  - its floor at 74, its massif at the crown 318 from the footprint's edge outward, and its empty north-east
  corner filled with rock - was switched off and still keyed to the old pin at 4352, -3833, which is why the
  ground round the planted city was plain vale and `/tmcity apron` had to invent a shelf 153 blocks up. The
  frame now follows the pinned corner 5200, -3370: its middle at 5476, -3114, the empty corner over
  x 5645..5753 by z -3370..-3311, and the mouth at 5200, -3067. The ground is laid again, and the branch
  from the mouth to the drawn Bruinen at 5054, -3086 is left uncut - the one piece of terrain still to draw.
- **The shoulder and the water line open the chunks they read.** A heightmap is only filled for a chunk
  the world has opened, and the shoulder reads rings up to a hundred and twenty-eight blocks past the
  walls while the water line reads a hundred and sixty: from the middle of a city this size that is far
  outside the chunks a player keeps loaded, so the ground out there read as nothing and the blend was
  written against a datum that was not there. `Sites.height` now opens the column's chunk before asking
  for its height, the way laying a piece already does.
- **The build's own water gets somewhere to land, which the shoulder cannot give it.** The apron reads dry
  columns only - a wet one is dropped from the rim datum and left exactly as it was - so where water leaves
  a wall the ground outside kept whatever height it had: the river leaves at 82 onto a vale already at 74,
  which it falls on its own, while the spring reaches the east wall at 239 over a shelf 124 blocks below it
  and simply hangs there. `/tmcity apron` now also runs a water line: every column of the footprint's edge
  that carries water is an outlet, and where that water stands more than twelve blocks over the first dry
  ground outside it, the ground is built up into a bank under it - a bed one block below the water, falling
  one block per block out until it meets the ground, or 160 blocks out. Nothing is cut and no wet column is
  touched. The report adds the outlets found and the columns built out.
- **The three new camps come out of their builder's footing.** `elledancamp`, `wild_tent` and
  `snow_camp` carried a ring of white, purple, black and magenta wool round their floor - the pinks are
  magenta in the template - with a target block at each of two opposite corners, one block inside a
  20-wide box. The ring is cut and each template cropped to what stands, so the footprints are 16 by 16
  rather than 20 by 20, and only the camps' own wool is left in them.
- **Rivendell's pin is the corner the raised city stands on.** `rivendell` moves from 5097, 75, -3010 to
  **5200, 75, -3370**, the low corner of the build put down in the scouted world, so the city covers
  x 5200..5753 by z -3370..-2859 with its crown at 318. Every column measured from the pin moved 103 east
  and 360 north with it: the water at 5200, -3067, the east wall's aqueduct at 5748, -2859, the spring over
  x 5708..5753 by z -3058..-3017, and the branch's legs at 5120, -2793 and 5800. The drawn river is the
  map's own and did not move.
- **The pin is the corner, not the middle.** `rivendell` at 5097, 75, -3010 is the block the cut's low
  corner goes on, so `/tmcity origin` and `summon` plant the corner there from whatever block the caller
  is on, and the message says so. The city covers x 5097..5650 by z -3010..-2499 with its crown at
  318; every column the valley prose names moved with the corner.
- **The unused valley floor says which planting it was cut for.** Its 544 by 502 profile, centred on
  4628, -3577, is the shape the valley had before the pin; the doc now says so rather than leaving it
  reading as ground the city stands on.
- **The city's own columns follow the pin.** Every world coordinate the valley's water, its spring and
  the walkthrough were described at was measured from the planting at 4352, -3833; with the city on the
  pin they move 468 east and 567 north, so the branch's mouth is 4820, -2963, the spring ends over
  x 5328..5373, and the mouth is 387 blocks from the drawn Bruinen rather than 681. The drawn water
  itself is the map's own and did not move.
- **`/tmcity` plants the city on the pin.** In Arda the corner comes off the `rivendell` pin rather than
  off the caller's feet, so `origin` and `summon` put the same 554 by 512 footprint in the same place
  from whatever block the caller is on, at the pin's own y, and the planting message says the pin
  decided it. That lands the cut's low corner at 4820, 75, -3266 and the crown at 318; nowhere but
  Arda changes, where there is no pin to obey.
- **The valley is pinned where it was scouted, not where it was worked out.** The scouted pin is
  **5097, 75, -3010**, read back out of the world's own `pins.dat` - `/tmpin` keeps a pin in the save and
  prints the source line, it does not write the source - and the planting corner is **4820, 75, -3266**,
  derived from the cut's own 584-piece table rather than from a remembered footprint. The first pass
  had the middle computed from the old planting, which was the right size in the wrong place.
- **The city marker is read once, not once a visit.** `CityCommands.plantedCorner` keeps the corner it
  read for the dimension and every write to the marker drops it, so dressing a planted city costs a field
  read on the tick rather than a file read every time a player walks near the valley.
- **Rivendell is pinned, and the valley stopped building its stand-in.** `Pins.SOURCE` holds
  `Map.entry("rivendell", new BlockPos(5097, 75, -3010))` - the middle of the cut's own 554 by 512
  footprint, planted from the corner 4820, 75, -3266 - and `Campaign` no longer lays the old square kit at
  all: `standValley`, its pieces, their seating and their stone-naming are gone. A visit to the valley now
  reads where `/tmcity` planted the city, stands the build's own marks and the crowd in it, and leaves the
  Elves on the pinned ground until a city is there. `/tmpin` prints its source line as a `Map.entry(...)`,
  which is the shape the table takes.
- **The apron blends from the rim, not from the build's corner.** `Sites.apron` reads the highest dry
  column on the ring just outside the footprint and ramps down from that, and it leaves wet columns alone so
  a river leaving a build keeps its channel. On even ground the two datum agree; on a hillside the shoulder
  now meets the highest ground at the wall instead of cutting a canyon - the trade is that a low side is
  filled up to it.
- **The Arda docs hold one copy of each fact again.** Rivendell's pages were listed differently in three
  places, and wrongly in all three: a page needs the city whenever its giver or turn-in is Elrond,
  Glorfindel or Legolas, which is 1, 2, 5, 6, 7, 8, 12, 13, 14, 15 and 2-1, 2-10, 2-11. The guide now
  carries the lists the code actually has, `arda-pins.md` keeps the table and the source-line format and
  leaves the commands and the scouting to it, and `commands.md` points at it rather than repeating what a
  pin changes. Gundabad is also noted as unpinned today, and Rivendell's ground as drawn rather than cut,
  since the city's own floor is behind `PLANTED = false`.
- **The pinning instructions are in one file.** `docs/arda-pinning.md` collects what was spread across
  several conversations: the `/tmpin` commands and what each does to placement, the two kinds of key and who
  reads them, the world-pin-over-source-pin order, the fifteen locations the two chapters ask for with what
  ground each needs, which five quarries need no pin at all, and the traps worth knowing. `docs/arda-pins.md`
  points at it rather than repeating the commands.
- **The cast's faces are written down.** `docs/arda-faces.md` records the order the renderer asks in -
  keeper, named file, alliance cloth, variant face - the slug rule (`Gloin` is `gloin.png`), the cast and
  the file each wears, and what the faction folders are for. It also records that `maelgrin.png`,
  `guthlac.png` and `zarabi.png` are byte-for-byte copies of the three `quest_giver_*` cloths: a herald can
  be redrawn on his own, but he will not follow a repainted cloth until the copy is taken again.
- **Gimli has a face, and four named files are repainted.** `middle-earth/gimli.png` closes the last named
  body that had no art - the dwarf beside Gundabad's well - and `elrond.png`, `gloin.png`, `legolas.png` and
  `ranger.png` are redrawn in place, so every named giver and the camp keeper's fallback now wear current
  art. All of them are 64 by 64 like the rest of the folder, which is what the human model's sheet is.
- **The three heralds have files of their own.** The faction folders' `quest_giver` skins are copied into
  the named-giver folder as `maelgrin.png`, `guthlac.png` and `zarabi.png`, so a herald resolves by name
  before his alliance's cloth. They are copies rather than moves on purpose: those three files are what
  every giver of the West, East and Harad wears, so moving them would leave every one of them without a
  skin and a warning on each load. The art is unchanged for now - the point is that each herald can be
  redrawn on its own without touching the cloth the rest of his front wears.
- **Barachen has a face of his own.** `middle-earth/barachen.png` is drawn and dropped into the named-giver
  folder, which is where the renderer already looks for a giver whose name it knows: a body wearing the
  West's work and the name "Barachen" is looked for as `barachen.png` before the alliance's cloth, so the
  art applies with no code of its own. Nothing else moves; a pack without the file still falls back to the
  West's giver skin in silence.
- **Elladan's camp is a cut of its own.** `key_structures/elledancamp.nbt` - 20 by 8 by 20, 940 blocks,
  no entities - is `Sites.ELLADAN_CAMP`, canon'd to the Trollshaws and pinned to Thorenhad's cell, and
  `Campaign.CAMP_TENT` lays it at the named camp in place of the Rangers' tent. The tent keeps both of its
  jobs: it is still what the camp errand pitches and still what the world generator stands in the wilds, so
  nothing else moves. `/tmpin build camp` and `elledancamp` name the new cut, and the scouting checklist
  counts it instead of the tent.
- **`/tmpin checklist` says how much of the map has been walked.** The command lists the fifteen locations
  the two chapters ask for - six names and nine buildings - each marked as pinned in this world, pinned for
  every world, or still left to the drawing, with a count at the top. It places nothing: it is the checklist
  of what scouting still owes, so progress is readable in game rather than by reading two chapters of pages
  and remembering which ones needed ground.
- **A location can be pinned where you stand, by `/tmpin`.** `Pins` is now saved with the world as well as
  written into the build: `/tmpin place <id>` pins a catalogue name at the caller's feet, `/tmpin build <name>`
  pins a template by a short name (`camp`, `ruin`, `cave2`, `gloincamp`, `castle`...) or by its own id, and
  both print the exact `Pins.SOURCE` line so a spot scouted once is kept for every world by copying. A world
  pin outranks a source pin, which outranks the drawing; `/tmpin list` and `/tmpin remove` manage them, and it
  answers to the same caller as the other arrangement commands, inside Arda only.
- **A location is pinned to the block it was scouted on.** `faction/Pins.java` is a table of hand-chosen
  points, read before the drawing by both of the things that place ground: `Places.ensure` puts a name
  exactly at its pinned block, skipping the cell survey and the spacing rule, and `Sites.survey` writes a
  building's spot down as given, leaving the laying to seat the footprint on the ground the world actually
  made. An empty table is the working default and behaves exactly as before. The pin is also the way a name
  stands where the drawing paints nothing - the valley, which no column samples as `IMLADRIS`, can be pinned
  by hand and stop holding the pages that need Elrond, Glorfindel or Legolas. `docs/arda-pins.md` records the
  two key forms and the chapter-one ground still to scout.
- **Ground is scouted before a location is settled.** `tools/arda_scout.py` renders a window of the drawn
  world at two to four blocks to the pixel, hillshaded from the sixteen-bit topography and with the river and
  lake masks painted over it - the painted terrain draws water as sand, so a river reads as a beach without
  the masks - under a 128-block grid labelled in block coordinates, so a spot is read off the ground instead
  of guessed from a cell. Each `--at X,Z,name` is marked on the render and reported as the ground a building
  would have to stand on: the height at its middle, the lowest and highest ground within the footprint, the
  drop across it, the share of it under water, and the distance to the nearest river. The first survey with it
  found the camp and the ruin flat and a hundred-odd blocks off the Bruinen, a dry 174-block bench for the
  valley, and the ford's pinned cell off the water by 1,155 blocks and off the drawn road besides.
- **`/tmtp` jumps to any of the drawing's 49 countries.** A quick map for walking the regions while no
  build is pinned: `/tmtp <region>` reads the country's middle from `ArdaMap.anchor`, puts the caller eight
  blocks over its ground - over the water, for the three seas - and moves them into Arda from wherever they
  were. Tab completion offers every name and `/tmtp list` prints them. The points are computed rather than
  typed, so they follow the drawing when a fill moves; a country whose build is settled gets one line in
  `TeleportCommands.LOCKED` and lands on the build instead. `docs/commands.md` records it, and
  `docs/arda-teleports.md` points at it.
- **The road's surface is specified: three widths, three bands, and a mix for each.** `docs/arda-roads.md`
  now records what a road will be laid in. Width is set per stretch as the builds are placed - wild at 7
  blocks, urban at 5, and rural at 3 for a spur - and the surface is chosen by distance from a pinned cell:
  gravel, coarse dirt, dirt path and grass beyond 64 blocks; cobblestone, dirt path, coarse dirt, mossy
  cobblestone and a little grass from 64 down to 16; and inside 16 that same mix with dirt path, coarse dirt
  and grass swapped for stone path, stone bricks and mossy stone bricks, which is the surface the mod's own village streets
  already use. The mix is picked per block from a hash of the column so it is seamless across a chunk border,
  and a one-block rise becomes a cobblestone bottom slab. Nothing is generated yet.
- **The roads, surveyed before any ground is moved.** `docs/arda-roads.md` records where the roads in Arda
  can come from - the canon map's line symbology, the places the mod already pins to cells, and the drawing's
  own grid - why they cannot be traced in pixels (the two references are in different projections, and road
  ink is the same ink as labels), and seven candidate routes with their cell waypoints. `tools/arda_roads.py`
  renders them over the reference terrain so the routes can be argued about first. Nothing is generated yet.
- **More of the drowned dead, and pools that hold a few of them.** A placement now searches nine blocks
  around its column instead of five and lays up to two bodies on separate beds, and four placements are
  rolled per chunk rather than two, so a deep pool is not left with a single corpse in it.
- **The drowned dead are laid in the pools again.** The bodies feature had been reading the block the
  placement handed it as the bed, but that column is the block above the ground - so it was never solid,
  and no body was ever placed. It now finds the water itself, within two blocks of the placement and ten
  blocks down, and asks a bed only to have two blocks of water over it.
- **Marsh pools lie in the fen instead of on it.** A pool's water line is now the middle ground of its own
  disc rather than the height of the one column the feature happened to start on, and the block or two of
  ground standing over that line is cut instead of left as an island: no more sheets standing above their
  shore, and no more dirt rings inside the water.
- **The Sea of Rhun ends in Rhun, not in the eastern waste.** The waste's fill runs the whole eastern edge
  of the map and was painted after Rhun, so the three columns east of Rhun's own cells came out as waste and
  the sea ended against it on that side. Columns 44-46, rows 16-22 - the shore east and north of the water -
  are painted Rhun after everything else, so the sea is ringed by its own country.
- **The drawing is measured against the reference the masks came from, and the offsets do not hold.** The
  table in `docs/arda-map-gaps.md` was measured from a map that is not in the repository; rendered over
  `Reference/ME_Terrain_albedo.png` - the painted terrain every mask is cut from - the eastern fills are
  already on the ground they name, and the Sea of Rhun falls at columns 41-46 rows 17-22, inside Rhun's own
  cells, not at the table's 40-43 rows 12-15. The fills are unchanged; `tools/arda_grid_overlay.py` renders
  the comparison so it can be repeated.
- **Mountains are ridges and gullies, not smooth hills.** The drawn ground was raised toward the crown by
  the mountain mask's brightness, and that brightness is blurred, so every range came out as a cone with
  nothing on it but its own gradient. Ridged noise is now folded into the climb over each range's flank -
  crests, spurs and the gullies between them, at a hundred blocks and at thirty - scaled by the square of
  the mask's brightness, so a plain beside a range is left exactly as drawn. The mask's whitest columns are
  still pinned at the crown, and a crag may stand up to about twenty blocks over it, within the ceiling.
- **The drawn ground runs in curves, not a grid of squares.** The heightmap was interpolated between two
  pixels, which turns every eight-block pixel into a flat facet whose contours run straight through it and
  turn at its edge - the squared-off terraces any drawn hill showed, whatever shape the drawing had. The
  ground is now a Catmull-Rom spline across four pixels, so each takes its slope from its neighbours and the
  facets join without a crease; the answer is held between the four pixels a column stands between, so the
  spline cannot overshoot a ridge or dig under a coast.
- **A lake lies at its lowest ground, and the rest of the sheet keeps its cliffs.** The level was the mean
  of the ground the sheet is drawn over, which sits partway up the sheet's own slope: every column under it
  was cut away, so the sheet ended in a dam of its own making several blocks high all the way round,
  Nurnen's the worst of them. The level is now the lowest ground of the sheet, so the water fills the basin
  the drawing gives it and the higher ground inside the mask is left as the land and the cliff it is.
- **A lake shore is measured in sixteen directions, and only low ground is cut.** Eight rays counted a
  diagonal step as one instead of one and a half, so the ramp's contours were square rings round the water
  whatever shape the drawing had, and ground standing well over the sheet was pulled down to it, which put
  a cone round every lake. The ramp is sixteen rays a block at a time, and a shore is only cut when it
  stands within six blocks of the water it faces.
- **A lake shore is a beach, not a wall.** A drawn headland, and the land just outside the sheet, now
  stand one block over the water where they meet it and climb a block for every block further out, so the
  rise in the middle of a lake and the ground along its edge walk down into the sheet instead of ending in
  a cliff. The grade never undercuts the bank that holds the water in.
- **Lake water shallows into its edge.** The bed is graded by how much of a column the drawn sheet covers,
  one block deep at the mask's edge and full depth inside it, instead of a flat shelf and a five-block wall
  that showed through the water as terraces; the lake id is also searched two cells out, so a rim column
  keeps its sheet's level instead of falling back to the ground it stands on.
- **A lake ends in a shore, not a cliff.** Ground more than four blocks over a sheet's level is left as
  land instead of being cut down to the bed, and water within that band shallows to one block at the edge,
  so Nurnen and Rhun meet their shores in water you can wade instead of a wall of stone. The load log now
  reports, per sheet, its level, how far the drawn ground rises over it, and how much of it falls inside
  that four-block band.
- **Each drawn lake is one level.** The lakes are measured once per drawn sheet and laid at that sheet's
  own level, with the bed cut to it, so the Sea of Nurnen and the Sea of Rhun are sheets of water rather
  than waterfalls down the terrain under them. Baked by `tools/arda_lake_ids.py`.
- **The Dead Marshes are a marsh again.** Emyn Muil's fill covered the Dead Marshes' whole rectangle - the
  two fills were the same cells, and Emyn Muil was painted last - so the marsh generated as Emyn Muil from
  edge to edge. Emyn Muil is now the row north of the marsh.
- **The marsh warns you when it deepens.** Each step of the dread puts a line on the action bar and a low
  sound with it, so a player being worn down can tell before the slowness has them, rather than discovering
  it at the bottom of the marsh with no idea how far in they are.
- **The Dead Marshes' dread deepens with the stay.** The dread now climbs a step at half a minute and
  another at a minute and a half, to a ceiling of two, using the same timer that brings the figures; it
  falls back to nothing when the player leaves and starts again from the bottom on returning.
- **Marsh figures end for everyone.** A figure now comes apart for any player who gets within six blocks of
  it, and carries a saved mark so a figure read back from disk - after a restart or a chunk reload - is
  discarded on its first tick instead of being left standing in the marsh as a real spirit.
- **The Dead Marshes keep their dead for those who linger.** After a minute in the marsh, figures of the
  drowned begin to stand out in the fog eighteen to twenty-six blocks away, facing the player, and come apart
  into soul-light when approached; they come more often the longer the stay lasts, and leaving resets it.
- **The Dead Marshes are haunted.** Corpse-lights stand over the pools around a player, a cry comes out of
  the water now and then, and the drowned dead stir where they lie - bubbles, a low sound through the water
  and a slow roll of the body - without any of them ever leaving the bed.
- **The Dead Marshes weigh on you.** A dread of the marsh itself takes the speed from your legs and the
  strength from your arm while you stand in it, with no wither and no blindness: nothing here is looking
  at you, the dead are only lying under the water. It fades a moment after you step out.
- **The great rivers are named.** `tools/arda_river_names.py` bakes the river mask's components into a
  names raster, so the Anduin, the Celduin, the Gwathlo and the Lune are their own biomes instead of all
  reading as the generic river; the rest of the drawing's water stays generic, because it fuses
  tributaries into one component and one name cannot stand for three rivers.
- **Rivers and lakes have biomes of their own.** Water used to read as the vanilla river wherever it
  lay; it now has a generic `river` and `lake` for the sheets the drawing does not name, and its own biome
  for each lake it does - Evendim, the Sea of Rhun, the Sea of Nurnen and the Long Lake - so a player can
  tell where they are standing from the biome name alone.
- **The Dead Marshes are their own biome.** The drowned battlefield between the Emyn Muil and the
  Dagorlad is no longer more of the Wetwang: `dead_marshes` closes the fog at forty blocks, darkens the
  sky and the water to the colours of a fen under cloud, and keeps the fell spirits out of its spawn list.
- **The dead lie in the water.** A `dead_bodies` feature lays fell spirits on the bed of standing water,
  two tries a chunk, and refuses any column that is dry or too shallow: they are laid flat with their AI
  off and no gravity, so they hold the bottom of the pool and never walk the bank.
- **The marshes hold pools of their own.** A `marsh_pool` feature digs shallow, level pools into the fen,
  two tries a chunk, kept inside its own chunk so the bodies laid after it find them: neither the drawing
  nor the mask has water in the Dead Marshes or the Midgewater, only in the Wetwang's channels.
- **The Midgewater and the Wetwang are marshes.** The fen east of Bree and the marsh at the Entwash's
  mouth are painted as marsh cells of their own, so the marsh biome reaches them rather than Eriador.
- **Lake Evendim is the mask's own.** Nenuial was thought to be the one lake of the west the mask draws
  nothing for, and was drawn by hand at Annuminas; the mask does draw it, so the hand-drawn ellipse is
  gone and the lake is the drawn one.
- **The halls are Moria whatever the surface is called.** The rectangle the biome answers in lies under
  a cell the drawing gives to Lorinand, so the country check refused it and the chamber read as Lorinand;
  the rock below the roof is the halls' own now, and the surface keeps its biome.
- **Rivers have sloped banks.** The water stands one block deep at the narrowed bank and deepens a
  block per block inward, reaching the eleven-block cap about twelve blocks out, instead of holding
  the full depth right up to the land - which is what made a river a square hole in the ground. The
  slope is measured from the cut bank, not the drawing's, or it would begin on dry land.
- **A river is at most eleven blocks deep.** With the channels a third narrower the old sixteen-block
  cap was a canyon under a brook; the bed still follows the channel's width, up to eleven.
- **The rivers are a third narrower.** The bake marks channels forty blocks across and more, which
  is a river with no banks worth the name; a sixth of the width is taken off each side, measured across
  the channel, down to a floor of three blocks.
- **The Barrow Downs are on the map again.** Dunland was painted after them and reached back over
  their two cells, so the country had a biome and nothing to stand in; the Downs are painted last.
- **Rivers and lakes keep their water.** Ground within twelve blocks of drawn water is raised to the
  water's own level where it stood lower, so a channel cut into a slope no longer floods the country
  on its low side.
- **The rock under the Misty crest has a room in it.** A chamber is cut beneath Moria's roof, so the
  halls' biome can be stood in instead of only read on the debug screen.
- **There is a teleport sheet for the map.** `docs/arda-teleports.md` lists one point per country,
  at the middle of the largest block of cells it is drawn in, with the height the masks give that column
  plus eight - so a region can be looked at from a command instead of by walking to it.
- **Moria is ground of its own, under the mountain.** The poster puts the halls below the Misty
  Mountains, so the surface over the Dimrill Dale keeps the range's biome and its drawn ground and the
  rock beneath that crest, below its roof, reads as Moria: dark, dry, close-fogged, and reached by
  digging rather than by walking. The biome source answers it by height, which is the one place the map
  has of putting a country at a depth instead of a place.
- **Cardolan, Minhiriath and Enedwaith are their own countries, and Eriador keeps Bree-land.** Eriador is
  the whole land west of the Misty Mountains, so one country carrying its name labelled the Shire, Arnor's
  old realm and the empty coast at once, and the poster's three southern countries had nowhere to be asked
  for. The masks keep ground only on rows 22 and 23 south of the Shire - the row below is sea - so the
  three are painted on the quarter-cell grid there: Minhiriath's grey empty coast west, Cardolan's downs
  between the Baranduin and the Greyflood, and Enedwaith's wet interior against Dunland. Eregion stays
  Hollin's name, under the mountains east of them.
- **The drawing has quarter-cell patches, and Umbar and Dorwinion are on it.** A cell is 512 blocks, so
  a haven or a shore of vineyards could only ever be a whole county: Dorwinion was Rhun's steppe and
  Umbar the Haradwaith's dune. The same drawing is now also painted at 128 blocks, laid over the cells
  for countries smaller than one, while the 48 by 36 grid keeps every calibrated coordinate - the valley,
  the place spacing and the canon cells are untouched. Umbar is a dry warm coast with brown hills behind
  it and Dorwinion a green warm shore on the Sea of Rhun's north-west corner.
- **The map's weather follows the climate reference, and every biome now has its data.** Temperature and
  rainfall are taken from the Koppen zones the climate map draws instead of being guessed per country: Cfb
  oceanic down the western seaboard, Dfb continental across Mirkwood, Dale, Rhudaur and the Trollshaws, ET
  tundra on the Blue Mountains, Angmar, the Ettenmoors, Ered Mithrin, the Iron Hills and the Misty crest,
  Csa round Gondor, Ithilien and Belfalas, Cfa in Rohan, BSk in Rhun, the Brown Lands and the Dagorlad, and
  BWh from Mordor through Khand to Harad. The generated biome data had gone stale with twelve biomes missing
  from it entirely - a world using any of them would have failed to find the biome - and `runData` writes all
  of them now.
- **Ithilien, the Grey Mountains and Rhudaur stop borrowing a neighbour's ground.** Ithilien read as
  Firien's hills, Ered Mithrin as the Iron Hills and Rhudaur as the Barrow-downs, so three countries on the
  poster had no weather, colour or life of their own: a warm southern land between the Anduin and Mordor's
  wall, a grey cold range north of Mirkwood, and cold troll-fells under the Ettenmoors.
- **Nine countries and their biomes are on the map, and Rhun and the empty east get their own.** Dale below
  Erebor, Eregion west of the mountains, the Ettenmoors, Eryn Vorn, the Brown Lands, the Emyn Muil, Belfalas,
  Khand and the eastern waste were all on the poster and not in the dimension: they read as Eriador, Firien,
  Dunland, Gladden or the Haradwaith's dunes. Rhun is dry steppe now rather than Gladden's wet grass, and the
  eastern edge is a wasteland of its own instead of Harad's desert, which had laid dunes along the northern
  cold-lands.
- **The masks alone make the ground now.** The city's valley floor, its 318 rim, the shoulder and the
  branch cut out of them were laid over the drawing at every column within reach of one footprint, which
  put a 244-block pit into ground the masks read at 154 to 173 and then argued about the walls of it. They
  sit behind one switch, off: the world is the drawn height, the drawn rivers and lakes and the drawn
  mountains, and a build is to be sited against that ground rather than the ground bent round a build.
- **The massif gives way to the river's own ends, not only its length.** The give-way read a column
  beyond the course's start as having no water to fall away from, so the mouth - water at 82 three blocks
  from the rock - was the one place the wall was left standing, and the join the same. A course's ends are
  water too, the city's at the mouth and the Bruinen's at the join, and the ground climbs away from them at
  the same grade as from the length.
- **The crown's summit rolls a few blocks below its own height.** A top standing at exactly 318 is a table:
  every contour on it is the crown's own outline, so the mountain's edge reads as a cut however the flank is
  graded. Three blocks of roll over the ridge's own scale make it a surface without raising the ceiling.
- **The massif gives way to the branch at a mountainside's grade, so the river no longer leaves the city
  between two walls.** The rock stood at the crown wherever the city's floor rectangle ended - 318, against
  the branch's water at 82 - and the water was cut through it as a slot whose walls went up at 1.2 blocks
  for each block out until they met the plateau. The ground now climbs away from the branch's own course at
  0.6 blocks of height per block, about thirty degrees, over the whole bank: the river leaves through a
  valley mouth, and the rock either side of the join rises as a slope. The plateau is only lowered where the
  slope would otherwise cut a step into it, which is what the removed corridor scaling failed to do.

- **The river now leaves Rivendell where the build's water leaves it, and reaches the Bruinen.** The
  branch's course was a straight line from 4900, -3322 - a point with no build water within 48 blocks - to
  the ford. The city's water stands against its west wall at 82, so the course is four legs: out west of
  the wall, south round the valley's own side, east below the city, and in to the drawn Bruinen at 4995,
  -3311. 1,104 blocks, continuous and twelve wide, falling the eight blocks to the vale's 74 at the wall
  and meeting the drawn river at its own level. The wander now falls to nothing at each corner, so the
  channel is one river rather than a river with a step at every turn.

- **The branch's water starts at the build's own level, 82, not two blocks under the floor.** The city carries
  its water out at the floor plus eight - mapped from its 584 pieces, 302,049 water blocks whose surface at
  the mouth is 82 - but the branch was initialised at 72, ten blocks lower, so the water at the mouth stood
  over a channel it could not run into. The branch also starts at 4900, -3322, which carries no build water:
  the city's low mouth is the west edge at 4352, -3530, and its east edge carries only a high aqueduct.

- **The river's corridor is open again at Rivendell's exit.** Within 24 blocks of the branch's bent course the
  valley floor runs out and the climb fades back in, so the water leaves the city between two slopes rather
  than through a notch cut in a wall. The corridor was built, softened and then dropped with the old valley
  profile; it is back beside the wall profile, and measured to the nearest point of the course so the bend
  is not walled either.
- **The build carries water after all.** 763 `minecraft:water` blocks across 306 of the city's 584 pieces, not
  the none an earlier note claimed: that is the pool and channel inside the city, and the ground has to meet
  it at the build's own mouth rather than the other way round.

- **Vanilla's water lakes and water springs are gone from Arda.** They are placed by the biome rather than the
  map, and a placed lake fills to a level of its own - which is the sheet of water standing three or four
  blocks over Arda's ground, and the pond sitting inside the valley. Both are filtered out of the biome
  builder and removed from the sixteen generated biome files; lava lakes and lava springs are untouched.

- **Water stands at the ground the drawing gives it, not at a graded average.** The surface was the mean of
  the drawn ground over ±64 blocks for a river and ±192 for a lake, which lifted water over the land wherever
  the drawing dips - a lake sheet three or four blocks above its own shore. The drawing's water is already
  drawn at its own level, so the top of the water is now the column's own ground with the bed cut under it,
  and a river follows the land down instead of being averaged across the country round it.

- **The reference set is sorted out, and so is the world a test needs.** The new canon map is a different
  projection from the masks - the coordinates for Rivendell land on Swanfleet on it - so it is a list of
  places rather than a layer; the terrain albedo is the painted source the masks were cut from, and the rom
  curves are finer computed relief. `docs/arda-masks.md` now says what each is good for, and
  `docs/rivendell-state.md` warns that a save older than the bake shows the old ground. `tools/arda_view.py` renders what the water arithmetic answers for a patch of
  the map without loading a world.

- **The river depth cap is settled at sixteen, and it is not provisional.** The mask's rivers really do run
  that wide in canon, so the wide bands are the drawing being right rather than something to narrow down to
  a channel; the note now says so, instead of leaving the cap looking like an open question.

- **The mask rules are written down.** `docs/arda-masks.md` now carries the decisions behind the terrain -
  the mountain mask's white pinned at 318, the river and lake masks as solid placement, the height map as
  the locator, river depth following width up to sixteen, and eight blocks to the pixel - with the open
  questions beside them, so the reasoning is not left only in the conversation that produced it.

- **A drawn river's bed is as deep as the channel is wide.** A three-block channel is three deep in the
  middle and a five-block one five, rising a block per block from each bank, in place of the flat four
  blocks every river used to get. The bed is capped at **16**: the reference paints its rivers as bands
  thirty blocks and more across, and a bed as deep as a band is wide is a canyon, not a river.

- **A mountain's whiteness is its height again, pinned at 318.** The range mask is read over the drawn
  ground at eight blocks to the pixel, interpolated, and scaled by the mask's own brightest pixel, which
  is the pin: a drawn summit and the city's crown stand at the same sky, black leaves the ground as the
  heightmap drew it, and every column between takes its share of the climb, which is what turns a bright
  patch of the mask into a range with flanks rather than a plateau.

- **River and lake banks fall where the drawing put them.** The two masks are baked as the share of each
  eight-block cell the drawing marks instead of one sample taken nearest, and the game reads that share
  interpolated between cells and calls a column water at a third of one: an oblique river used to be a
  staircase of eight-block steps, and its bank now follows the drawn line. No cell the drawing touches is
  left stranded without water beside it, so a thin tributary is still a channel.

- **The ground is the heightmap's own shape, read at eight blocks to the pixel.** The bake went from
  sixteen blocks to the pixel to eight, so ridges and river bends are twice as sharp, and the mountain
  mask no longer lifts a bright column toward the crown: that lift flattened every ridge it touched to the
  ceiling, and the height rasters already carry the ranges, so the mask is reference and probe only now.

- **The reference masks are pinned with a probe.** `tools/mask_probe.py` answers height, river, lake and
  mountain brightness at any block, and the nearest river to one, straight from `Reference/` at the
  rasters' own resolution - so a build or a river can be placed against the map rather than against
  anyone's recollection of it. `tools/arda_masks.py` now names the folder `Reference`, as it is on disk.


- **The Bruinen joins at 4995, -3311, and the mask is read finely enough to say so.** The river layer is
  baked again at three blocks to the pixel over the corner the city stands in (`bruinen.png`), and
  `ArdaMasks.river` reads that bake before the sixteen-block one inside its window: the nearest water to
  the outlet is 95 blocks east at 4995, -3311, not the 5008, -3328 the coarse pixel suggested, and the
  branch's join is moved onto it.
- **The branch winds from the city to the Bruinen instead of running straight.** Its ends are fixed - the
  build's outlet at 4900, -3322 and the joined drawn river at 5008, -3328 - and the course between them
  wanders up to 18 blocks on one and a half turns, with the channel, the bed and the gorge cut following
  it. A straight cut from one water to another is a canal, not a river.
- **The mountain stands at the crown at the city's walls again.** The floor is 544 by 502 inside the
  554 by 512 footprint and the rock is at 318 from its edge outward, which is the shape the valley had
  before `fef98351` turned the climb into a bank a hundred blocks out; `VALLEY_STEEP`, the shelf and the
  river corridor are gone with it. `/tmcity origin` still caps its base at the valley floor, because the
  corner is now under the crown.
- **The valley's floor runs out along the river, so the branch leaves the city on flat ground.** With the
  floor ending inside the south wall, the climb stood a bank across the branch's own course; the floor
  now follows the course for 18 blocks either side of it.
- **The floor runs three blocks under the build, and out past the drawn Bruinen.** A shelf of any
  width leaves a seam of flat ground the city does not stand on, so the valley's floor now ends three
  blocks inside the walls on the west, north and south and the climb starts under the build's own outer
  band. East it runs to 5040, past the Bruinen the map draws at 5008, and a drawn river now answers
  before the floor as well as before the massif - otherwise the floor would bury the river the branch
  runs out to meet.
- **`/tmcity origin` plants on the valley floor, not on the climb under the corner.** With the terrain
  starting inside the walls the ground at the corner is a dozen blocks up the hillside, which would have
  stood the whole build in the air.

- **The mountain's foot stands at the wall, ten blocks over the build's base.** The shelf is gone: the  floor is cut to the footprint and the climb begins at the first block outside it from 84, so the  city's own lowest band is under the hillside instead of standing in a strip of lawn. The east keeps  its hundred blocks, which is where the branch runs out to the drawn Bruinen.- **Four blocks of shelf, so the city's lowest band is inside the hillside.** Twelve still left a step  of floor between the walls and the climb, and the build's ragged edge standing in it. At four the  ground is already at 84-90 when it reaches the lowest band, so the city is laid into the hill rather  than set down beside it. The east keeps its hundred blocks for the river's mouth.- **The hillside comes back to the walls on three sides, and the east keeps its shelf.** A hundred  blocks of lawn all round left the mountain a field's width from the city. The shelf is twelve blocks  on the west, north and south - enough to keep the build's ragged edge out of the rock - and a hundred  on the east only, because the drawn Bruinen meets the branch there at the floor's own level and a  climb in between would put the river two hundred blocks above the city.- **The city stands on a lawn, not at the bottom of a pit.** The valley's floor was cut to the build's  own footprint, so the mountain's foot began in the same block as its walls: the ground climbed  immediately outside the build all the way round, and the city sat in a trench that hugged it. The  floor is the footprint plus a hundred blocks of level country on every side (`VALLEY_LAWN`), so the  climb starts a hundred blocks out. The `shoulder()` shelf goes with it - it stood rock at the crown  inside the build's own empty corner, which the lawn makes unnecessary.- **The city is planted on the ground under its corner, and the valley's water runs below its floor.**  `/tmcity origin` used the caller's own altitude, so a corner planted from the air left the whole build  hanging over the valley with the ground falling away from it; it now takes the ground height at the  corner's column. The vale's floor was eight blocks *above* the city floor, which put the country's  lowest ground higher than the valley's water and left a river unable to leave: the vale's foot is the  floor at 74 and the branch runs at 72, out of the build's own south edge at 4900, -3322.- **The branch from Imladris meets the Bruinen instead of ending at a ford the map does not have.** The  Bruinen the map draws runs into the massif the valley was planted over, so the rock buried it and the  branch had nowhere to go: a drawn river now answers before the mountain does, off the valley's floor.  The branch is the fork's lower arm - out of the valley at 4900, -3312 and east to the drawn Bruinen at  5008, -3328 - and the drawn river cuts its own gorge through the rock from there.- **The branch from Imladris keeps water under it all the way to the ford.** Its last stretch shallowed  to nothing, so the river stopped a little short of the crossing it is meant to feed: it now keeps two  blocks under it however shallow it comes, and narrows to seven tenths of its width instead of half.- **River beds slope from the middle out instead of lying flat with a cliff at the bank.** Every block of  a river used to sit the same seven under its surface, so the water met the land in a wall and the river  read as a square of water cut into the ground. The bed now rises from the deepest point to the water's  edge, and a drawn river's depth is measured off the drawing itself - eight probes out from the column,  full depth once three quarters of them are water. The Bruinen also narrows to about half its width as  it comes to the ford and shallows to nothing there, so it ends in a crossing rather than an edge.- **The mountain rises out of the ground at Rivendell's feet instead of standing beside it.** The rim  began at the crown, so the first block outside the city's floor was the top of a two-hundred-and-  forty-four-block face: whatever the range did further out, what met the build was a wall. The rock  now climbs from the floor to the crown over ninety blocks, so the city sits in a mountainside.- **The drawn range no longer stands as a blade in the vale, and the floor is the build's own footprint.**  The mountain mask is a ridge sixteen blocks wide, and raising it where the vale had already brought  the country down left a thin wall of rock alongside the city; the vale's blend now runs after the  mask, so the ridge stands only where the country does. The floor rectangle is 277 x 256 from the  valley's centre - exactly the build - so the rock meets the walls instead of leaving a ledge round  them.- **The mountain's cliff stands outside Rivendell's walls instead of through them.** The valley's floor  was 272 x 251 blocks from its centre, but the build is 277 x 256: the massif's inner face, which starts  at the floor's edge at the full crown, was standing five blocks inside the city's own outer wall, so a  wall of raw rock rose out of the build where the ground had been cut for it. The floor now covers the  build with four blocks of ledge to spare, and the rock begins where the walls end.- **Arda is a vanilla-height world again.** The dimension was 512 blocks tall, putting its ceiling at 447  - a hundred and twenty-eight above anything the overworld can reach - to leave the massif room over  Imladris. The ground round the city moves instead now, so the dimension is 384 again: the highest  block is 319, the city's crown at 318 stands one under it, and the world is one a player can walk the  height of.- **The country round Rivendell settles to the vale instead of standing on the city's rim.** Past the  massif's foot the heightmap is blended toward the height the city's water sits at, over nine hundred  blocks, and the massif's fall is shortened from four hundred to three hundred and forty so its foot  lands on the ford. Where the map is above the city the ground comes down to it, and where the city has  been raised the ground comes up; either way the valley belongs to the country round it and the river  leaves it at grade rather than in a cut eighty-four blocks deep.- **Rivendell stands as high as the world it is made in allows.** The city and its rock were planted at  74 and 318 in a world whose sky was 319. A world made since Arda was raised to 512 has a hundred and  twenty-eight blocks of room above that, so the valley floor, the crown, the shoulder and the outlet's  water all rise by whatever the ceiling gives - and by nothing at all in a save that has no room,  which keeps the ground it was made with. The generator reads its own ceiling and does this once,  before the first column; a 512 world gets floor 202 and crown 446, a 384 world gets 74 and 318.- **The Bruinen descends the course the way the country does.** Its profile is read forward from the  outlet now - the surface takes the ground wherever the ground has fallen under it and never rises  again - rather than being the lowest ground downstream, which held the whole course at the depth of  the ford. In the raised world that turns the ravine into a channel that is deep at the wall and at  grade by the ford.- **The Bruinen's water is read off the ground, not set at the ford.** The channel's height at any  point on the course is the lowest ground between it and the ford, capped by the build's own outlet  at 82, and no constant is left at the far end. In the valley as it stands - floor 74, country 156 -  there is no ground under those 82 blocks for the river to slope to, so it runs level at 82 and the  cut does the descending; once the valley is re-seated the same rule puts the water on the ground and  the river slopes to the ford on its own. The profile is read once and kept, under a lock, because  chunks are generated in parallel.- **Arda's ground is the reference heightmap now, not the drawing's flat countries.** `height.png` is  baked at sixteen blocks to the pixel from `Final Height.png` and read interpolated, so the country  rolls and climbs where the map does instead of standing at one height per cell: the Shire at 116 where  the drawing had 68, Rohan at 170, Mordor's plateau at 224, the Misty Mountains at 178, the ground at  Imladris 160 - from the sea's own 63 up to the crown at 318, with the mountain mask's white still  sharpening the crests. A column the map calls sea keeps the drawing's bed, because the map's zero is  the sea's height and a bed there would be a dry ocean.- **The massif's ridge comes down to the city's crown.** 318 is one block below the ceiling of a save  made before Arda was raised to 512, so the old ridge of 380 plus or minus 45 was being cut off flat in  any world that was not made since. The ridge still wanders in and out, but its top is the crown itself  now - level with the roofs it shuts in, and with the mask's white.- **No river runs below the sea.** The mask's rivers are laid at the grade of their own ground, and near a  coast that grade falls under sea level; the surface is held at 63 there, so the last stretch of a river  meets the sea rather than digging a trench beside it.- **Mountain ranges take their height from the reference mask instead of from the drawn bands.**
  `ArdaMasks` reads the mountain mask baked to sixteen blocks to the pixel over the world's own frame,
  and a column's brightness - white at the crest, black on the plain - lifts the ground toward the
  crown: a full-brightness column stands at 318, the height the city's own massif tops out at, so the
  mask's ranges and the build reach the same sky, and a black column stays exactly where the drawing
  put it. Ranges now follow their real courses: the Misty Mountains are a
  wandering wall rather than a five-point line with a half-width of two cells, and the Grey Mountains,
  Mordor's ring and the White Mountains stand where the map puts them. The drawing still says what
  the country is and what its ground sits at; the mask says how high it stands.

- **Rivers and lakes are the mask's now, and their water stands at the grade of the ground they cut
  into.** The seven rivers the drawing named as polylines are gone, and so are the two inland seas it
  laid down as rectangles: the river and lake masks answer instead. Each column of water sits at the
  average height of the ground about it - within five blocks of its own ground on 87% of the channel
  measured - so a river falls from the mountains to the sea the way the country under it falls,
  instead of standing at one level from end to end with a cliff at each end. A river's bed lies four
  blocks under the water and a lake's six, and a lake never drains below the sea's own height.

- **`tools/arda_masks.py` bakes the masks into the mod's assets.** It crops each 8192 mask to the
  world's frame at sixteen blocks to the pixel - 1536 by 1152 - using the calibration in
  `reference/work/calibration.json`, and writes `assets/tolkienmobs/arda/{rivers,lakes,mountains}.png`,
  a hundred and sixty kilobytes together. The game reads whatever is in the assets, so the masks can be
  re-baked without touching the code, and a build without them still makes a world.

- **The gorge is the width the build gives its river: twelve blocks, five to the left of the course and
  seven to the right.** The channel the city carries out of the west wall is twelve across with its middle
  block on the line the outlet and the ford draw, and not symmetrical about that block. The cut is measured
  across the course now - five blocks one way, seven the other - rather than as a radius of a fixed
  half-width, so the river leaves the build at its own width instead of widening or pinching a block at the
  mouth.

- **The Bruinen leaves the city in a gorge and runs to the ford, instead of a hillside sliced by a
  trench.** The build carries its water out of the west wall at 82 with its bed seven blocks under it, a
  little above the valley floor, and outside the walls the massif stands at the city's crown - so a channel
  cut the way the drawing's rivers cut one (every column flattened to 63) met two hundred and thirty blocks
  of rock and came out as the terraced cliff at the front of the valley. The river is cut as its own gorge
  now: from the outlet at 4356, -3528 to the ford the drawing pins in the Trollshaws at 3840, -3328, with
  the water falling from the build's 82 to the level the map's rivers stand at, a bed seven blocks under it,
  walls that rise a little over one block for each block out instead of standing sheer, ribs in the walls so
  they are not contours, and a channel that shallows into the crossing over the last fifth of the run. The
  cut takes the massif's flank and the country beyond the ridge alike, so the ground outside the west wall
  slopes down to the water rather than crowning over it. Columns now carry the height their water stands at
  rather than assuming the world's own, which is what lets the river leave the city at 82.

- **The mountain meets the city at the city's own roofline, all the way round.** The last change put the
  foot inside the build, but the climb still started at the valley floor, so the ground the build met was
  240 blocks under its own crown and did not climb back to 318 until 140 blocks out from the walls - the
  city stood in a pan with a cliff of its own making at the rim, and the transition the build was meant
  to embed into was a hundred and forty blocks away. The rock now stands at the crown of the build
  (318) where it meets it, on every side, and climbs to the ridge from there: 4 to 5 blocks inside the
  footprint, which is where the floor's rectangle ends, the ground outside the walls is level with the
  ground inside them, and the city replaces that rock as it is planted, so what is left is the walk from
  the build onto the mountainside rather than a step down onto a lawn. The corner the mountain stood in
  still reads as it did, because it was already standing at the crown.

- **The mountain's foot stands inside the city, and the apron that dressed the join is gone.** The valley
  floor was 586 by 544 blocks for a 554 by 512 city, which left sixteen blocks of flat ground outside
  every wall for a ramp to be walked across - the wide, stepped apron the last rounds kept producing,
  whatever the flank above it was doing. The floor is 544 by 502 now, a few blocks smaller than the
  footprint on every side, so the rising ground starts inside the build: the lowest band is laid into the
  rock, and the rock shows wherever the build has nothing of its own. Planting no longer runs the skirt
  round the walls either, since a ramp walking the ground down to the city's base would cut away the rock
  the build is meant to sit in; `/tmcity apron` is still there for a city on open ground. There is no
  join left to dress, so nothing has to be tuned to make one look natural.

- **A world keeps the height it was made with, so an old save cuts Arda's mountains off flat.** Arda was
  raised to 512 blocks so the massif round Imladris could stand over the city's crown, but a dimension's
  height is fixed the moment its world is made, and a save copied from an older one carries the old height
  with it. The world this round was measured in ends at y 335 (25 sections, -64 to 335), so every summit
  the map draws above that was cut off at the ceiling: a range that stops in the sky, which is exactly
  what a flat-topped mountain looks like and very hard to tell from a range drawn badly. Nothing in the
  terrain could be tuned out of it. The generator now says so once, naming the ceiling it found and the
  height the massif wants, rather than leaving it to be guessed at.

- **The massif stands closer to the city.** 220 blocks of climb put the summits 190 to 270 blocks out from
  the walls, which is further than the valley is wide; they are 150 to 210 out now, at about 1.8 blocks of
  rise for every block walked.

- **The mountain no longer ends in a cliff of its own making, and its flank is a mountainside rather than
  a wall.** Two things cut the massif off. The rib noise on the flank was still 12 blocks high where the
  climb met the ridge and dropped to nothing beyond it, so there was a sheer face running the whole length
  of the ridge that nothing had asked for; the ribs now fade out before the ridge as well as in from the
  floor. And the climb of the last change put the same 306 blocks of rise into 130 blocks - three up for
  every one out - which is a staircase wall up close; it is 220 blocks now, so the ground climbs about
  1.4 blocks for every block out, and the far side runs down over 400 blocks instead of 260, so the range
  carries on into the country instead of stopping at a rim a couple of hundred blocks away.

- **The massif round Imladris stands at the city's walls instead of across a lawn from it.** The same
  306 blocks of rise were spread over 240 blocks of climb, which put the ridge two hundred blocks out and
  left the ground between the city and the rock as a wide, evenly stepped apron - the mountain read as cut
  away from the build. The climb is 130 blocks now and steepest at its foot, so the rock begins a few
  paces past the walls, and the flank carries 12 blocks of rib noise so the even contours that stacked as
  terraces break up into spurs and gullies.

- **The mountains round Imladris are drawn as a range instead of a ring.** One height at one distance,
  rising on four straight sides to one summit, is a wall with a pyramid in it - which is why the corner
  came out as a spike and nothing read as mountains wrapping round the city. The ridge now wanders in and
  out by a third of its distance and its summits rise and fall 45 blocks either side of 380, its foot is
  measured as a distance from the floor's rectangle so its corners are rounded, and the climb is a curve
  with some of the straight line left in it at the bottom.

- **The city's missing corner is a shelf of rock at roof height, not a point.** The build leaves x
  4797..4905 by z -3833..-3774 empty above its lowest band - the mountain was there - and the rock now
  fills that whole rectangle level with the roofs it meets, falling away outside the walls over 110 blocks
  into the massif's foot. It stops at the gap's own edges inside the footprint, so it buries nothing.

- **The mountain the city's north-east corner was cut into is put back.** The build carries no blocks
  above its lowest band in that corner - 73 by 47 of it, against the north and east walls - because the
  rock stood there rather than the city. The valley raises that rock again: from the floor at the notch's
  inner edges to the city's own crown at 318 in the corner, and back down outside the walls over 60 blocks
  into the massif's climb, so the corner reads as a spur of the mountain with the city cut round it.

- **Arda is tall enough to build over Imladris in.** The world was 384 blocks tall, so its ceiling was 319
  and the city's crown 318: nothing in Arda could stand above the city's roof, and the mountains meant to
  close over the valley had to stop a block short of it. The world is 512 blocks now, which leaves 128
  above that crown for ground to stand in.

- **The mountains round Imladris stand over the city instead of ringing it below the roof.** The crest
  was 300 - eighteen blocks under the city's 318 - and a quarter of a kilometre out, so from the city's
  top the ground read as a distant green edge and the corners looked bare. The crest is 380 now, 62 above
  the crown, reached over the same 260 blocks of climb, so the ridge crosses the city's own height 224
  blocks from its walls and tops out above its skyline; the fall beyond it is 260 blocks, so the
  mountainside comes back down into the Trollshaws at a gentler angle than it went up.

- **Chapter two's state is written down.** With the valley done and other work next, the quest line's
  place needed to survive the gap: `docs/chapter-two-state.md` records the eleven pages as built - givers,
  quarries, ground and rewards - where each piece of ground is pinned, the two decisions left open (gating
  the inner keep's stair, naming the Pinnath Fenui), what is not built after `2-11`, and the loose end in
  the camp's place, canon'd to the mountains while its site sits in Gladden.

- **A page that names a dwarf at his fire points at the fire.** The needle read a page naming a body as a
  place the map already has, and Glóin is not one of those: his camp is ground the campaign pitches when
  the page is handed over, so `groundOf` had no answer for him and the road showed no direction at all.
  The ground a page's body is standing on is now asked for before the place the map would name, which
  also puts Gimli's page on the hold rather than on the country the hold stands in.

- **`/tmcity` names the city's own ground, and can put you on it.** The corner it printed was a
  bookkeeping corner of the cut's frame, more than five hundred blocks off the build, so a city that had
  been laid in full read as an empty hillside. Every message now speaks in the box the pieces fill,
  `goto` stands the caller on the middle of it, and the corner remembers which dimension it was planted
  in, so bands cannot be laid in a country nobody is looking at.
- **The planted corner tells the valley where its people go.** The marked spots are written in the cut's
  frame, so `/tmcity origin` now hands that frame to the campaign as well: the city stands up with its
  crowd already knowing where they live, instead of needing the origins wired in a second place.
- **The city can be stood up in a world before it is packed.** The cut lives in `run/rivendell_art` with a
  table of where each piece goes, so `/tmcity origin` plants its corner where the caller stands,
  `/tmcity band` lays the next band - a whole city is two thousand chunks, and those are generated chunk
  by chunk - and the last band blends the shoulder with `Sites.apron`. The pieces are read from the folder
  rather than the jar, so a re-cut is picked up by walking back to it, and the lowest band is underpinned
  as it is laid.
- **The ground meets a build instead of stopping at its wall.** A template has no terrain adaptation of
  its own - `beard_thin` and `beard_box` are jigsaw settings read while chunks generate, so a structure
  laid at runtime met the hill in a cliff. `Sites.apron` cuts or fills the rings just outside a footprint
  toward the level the build stands at, tapering the difference so no column moves more than six blocks,
  and `underpin` is public now so the same pass can put ground under a piece that was sunk into a slope
  rather than set on one.
- **The city keeps a crowd, and none of it is anybody.** Rivendell's six walkers are enough for a square
  and not for a city seven hundred blocks across, which reads as a place abandoned rather than a quiet
  one. The valley now keeps twenty-four people at once: the fifteen spots marked in the build are
  answered first, one body to a spot and no spot twice, and the rest are put down wherever the city has
  ground under it. None is a giver, none carries a name, and each is homed where it stands so the crowd
  keeps its own quarter and gathers at the bell the city already carries. The count includes the marked
  spots, so fifteen marks leave nine to fill; the six named walkers and the three Elves of the line are
  still the only ones who answer to anything.
- **Chapter two walks on to the hold.** Past Glóin's fire the line runs `2-2`, the goblin fires of the
  Pinnath Fenui; `2-3`, the hold itself; and `2-4`, the plans read back at the camp table. Gundabad is
  ground the campaign pitches now - `key_structures/gundabad` stands in the Hithaeglir as a site the
  way the camp and the castle do - so "go to Gundabad" is a keep that is already standing when the
  player arrives, not ground they have to be told about.
- **A structure file cannot leave a cell alone, so the keep writes stone instead.** Written with air
  dropped, the keep went into the file with no rooms in it at all - 151,055 cells against a box of
  580,476 - because a cell the writer skips is a cell the game reads as air, which hollows the rooms
  and takes the hillside under them away at the same time. Both directions are written now: deepslate
  through the course under the five lifted rooms, and each tile's own air over it - the schematics'
  rooms were being poured full of that stone until their boxes were written as air first.
- **The flooded storey is flooded everywhere.** The water the pieces carried only stood where it
  happened to settle, and the stair's well is cut after they are laid, so its foot was dry. The builder
  lays the course itself now, after the lanterns and chains are out of the floor: one block of water over
  every walkable cell of the lower floor, the base of the spiral's stair included, stopping one cell
  short of the outer wall so the pool cannot run out where a piece opens on its edge.
- **The hold keeps what was set into it by hand.** The scan cut the inner keep's six pieces back out
  of the test world but not the six tiles the hold above is drawn from, so a throne put into the hall
  stayed in the world and out of the build. Both are cut now: a lifted tile brings back the thirteen
  courses it carries and the spiral the whole twenty-six, written at the height it was cut at so no
  padding of air stands over the roof, and holding back only the camo glowstone the builder sets into
  the floors. The throne of dark stone and its two chiseled Angainor guard posts are part of Gundabad.
- **A rescan no longer lights a room twice.** Pieces cut from the world carry the last build's camo
  glowstone back with them, and the grid went on setting a second one beside every group of them - 63
  extra in the storey. A stretch of floor that already holds a light is left alone now.
- **The joiner reads the flooded floor the way the walk does.** Driving a passage between the chambers
  counted only air as open, so a chamber reached across the water looked shut and went unjoined: the
  well, behind the stair raised in it, was left as a second pocket. Water is open in both now.
- **Chapter two runs to the end of its line.** The notes wrote eleven pages and four were built, so
  the West stopped at the plans. The other seven are in: the key-stone's vault at the source of the
  Bruinen, Gimli at the stair into the inner keep and Dugorim below it, the camps on the borders of
  Carn Dum, the Dark Sigil carried out of them, Elrond reading it in his library, and Drugoth's dragon
  wight fought in Carn Dum. With the eleventh page the front is settled, and the finale stays shut
  behind its own flag instead of opening the moment the chapter ends.
- **Gimli keeps the stair at Gundabad.** A named keeper like the Elves: his father's height on a
  man's model, stood from the hold's own site beside the spiral's well while the inner keep is the
  step in hand, and found by his tag afterwards so a reloaded world keeps one dwarf at the stair.
- **The hold is lit from its own floors.** A camouflage glowstone every fifteen blocks, set into the
  floor course at both walk levels, carries the rooms: light fifteen, wearing the look of the stone it
  stands among, so there is no fixture to see and none for a player to knock out in the dark. The
  lanterns and chains the geomorph sheets hang come out, and water - the flooded storey is lit through
  it and swims the same as air walks.
- **The bottom storey comes back flooded.** The six inner-keep pieces were re-cut from the placed build
  in the structure test world, so the water standing in the lower rooms is part of the hold rather than
  something one save did to it.
- **A placed build can be read back out of a saved world.** `tools/world_scan.py` finds the corner a
  template went down at, compares the world with the template block by block, and cuts the inner keep
  pieces out again, so a build decorated in the test world can be brought back into the pack instead of
  being rebuilt by hand.
- **The inner keep is all six of the example structures.** `2tl` and `2tr`, `2ml` and `2mr`, `2bl` and
  `2br` go down in the drawing's own two-across, three-down grid, laid whole from the stair's floor up
  with their stone re-materialled and their soil read as the hold's rock. Each carries three floors and
  its own stairs and ladder, so the storey under the hold is a small fortress of its own rather than two
  rooms beside the stair; the stair's well is carried empty through all six and the course under the
  hold's floor is solid over the lot. 3,621 cells on the inner route at the stair's level, every piece on
  it.
- **The inner keep is a level under the hold, reached by the spiral.** The chapter's notes ask for a
  fortress inside the fortress and no drawing gives ground for one, so the ground is two of the example
  schematics that came with the drawings - the open hall and the barred undercroft - laid twelve blocks
  under the south row, on the level the stair's landing already stands at, and re-cut in the hold's own
  stone: their stone brick and cobble would read as somebody else's ruin inside a dwarven fortress, so
  what is taken from them is the shape. Each tile is lifted by its own lowest floored course, the stair's
  well is carried empty through them and a door cut in its wall and a way cut west from it until it
  stands in a room, and the keep is one template still, 122 by 183 by 26.
- **The inner keep is the example structures laid whole, three floors of them.** `2ml` and `2mr` came
  as 61 by 12 by 61 cuts of the valley the rooms were built in - a gallery at the base, two floors over
  it, stairs and a ladder between - and the hold's inner storey is exactly twelve courses, so they go
  down as they stand, their stone re-materialled into the hold's and their soil read as the hold's
  rock. The spiral tile, which stands on the hold's own floor and so reached all the way down, was
  burying the rooms it stands on: below that floor it now writes only its own well. 1,695 cells on the
  inner route at the stair's level, both files on it, the landing inside 2mr's hall.
- **Set 4 is cut as a cave dungeon of its own, and nothing places it yet.** `tools/cave_tile.py` takes a
  panel of the cave sheet at one block per cell - ink rock, open cells room, twelve blocks of tile with
  the cave seven high - digs the quarter-point doorways in from each face, smooths the hatching drawn
  inside the rooms out of the mask so it does not stand up as pillars, and drives a passage to any
  chamber the drawing walls off. `ch2_cave_a` and `ch2_cave_b` are `r2c1` and `r2c2`, 61 square, 40 of
  40 door cells reached on both.
- **The dwarven tiles are Gundabad.** The six geomorph tiles are assembled by `tools/gundabad_keep.py`
  into one keep, `key_structures/gundabad.nbt` - the sheet's own arrangement, two panels across and three
  down, 122 by 183 by 26 - with the five surface rooms lifted thirteen blocks so every room and the stair
  down are walked on one floor. The file writes its whole box, stone under the lifted rooms and the
  rooms' own air above it, so the keep cuts its rooms out of the hillside without taking the ground
  under them with it, and the chapter's open question about which build stands for the fortress is
  answered with ground the mapmaker drew.
- **The way down is a helical stair in the middle of the room.** The lower-right tile now cuts thirteen
  blocks below its own floor and winds one tread per block about a newel, so the stair is the room's
  centrepiece and no tile gives up its floor plan for a stairwell. The old gallery flight is gone, and the
  reading of the drawing's symbols is corrected with it - the concentric arcs are a well or pool, and the
  ruled marks with arrowheads are stairs, pointing the way they run.
- **The dwarven hold is built from the drawings, not from the example tiles.** The geomorph
  sheets are split and read into a 61 by 61 plan by `tools/geom_extract.py`, and
  `tools/fortress_build.py` raises the first tile from that plan in Angainor masonry: a hall
  behind a five-wide gallery ring, so the eight quarter-point doorways meet any neighbouring
  tile without being matched.
- **The other five tiles of the hold are raised, one of them two storeys.** The winding approach,
  the crenellated vault, the four-pier colonnade and the water hall join the great hall, and
  `ascent` carries a seven-wide flight up to a parapetted gallery over a double-height hall - the
  first tile with a level change, and the pattern the rest of the vertical work can follow.
- **A finished page offers the way back.** A story page that is done and handed over more than
  forty-eight blocks behind the player shows `Return` in the book, which asks the server to walk
  them back to the giver. The server checks the page again, refuses to move somebody who is
  already there or ground that has been built over, and answers at most once every twenty
  seconds - so the chapter does not need a stone outside every hall.
- **Stones travel with the buildings that carry them.** A milestone block written into a template
  is named by the visit that lays it, instead of being planted at a coordinate written in code.
- **The plaza keeps its air.** The valley's square clears sixteen blocks of air over its own
  footprint when it is laid, so a hillside or a stand of trees no longer grows through the paving.
- **Travellers with untaken work move on.** A naturally arrived giver whose offer nobody took
  after half an hour is discarded along with the offer; one whose work was accepted stays, since
  the page is carried back to them.

- **A line that runs out says what is left of the war.** A settled page already named the body who
  holds the next one, but a front's last page named nobody: the book emptied and the chapter said
  nothing. The last page of a line now names every front that still has pages, and once none has,
  the Rider - so the campaign is never handed back to the player without a direction.
- **A player who joins carrying nothing is told again.** The pointer is repeated on login for a
  player whose book is empty, because the gap between two pages outlives a logout.
- **The rule is written down.** `docs/war-of-the-three-fronts.md` carries the no-empty-page rule
  and the three questions a new page has to answer; `AGENTS.md` carries the short form.

- **The beryl says how it is used.** The stone's tooltip now names the one thing it does: right-click to raise it aloft, because an item whose whole use is a right click over open air is an item that reads as decoration.

- **The last step of the stone's page says what it wants.** Standing on the ground a page of the beryl names now says so once, with the one thing the page left to be guessed: hold the stone aloft there, in the air rather than on the milestone. Raising it out of reach now names the place and how far off it still is, and a place is read as a circle of ground rather than a sphere in the air - a ford is walked on its bank, not on the water its middle was surveyed on.

- **The stone's page now carries a direction.** A page of the beryl that names ground - "hold the stone aloft in the ford" - is settled by holding rather than by arriving, so it has no spot, and the needle read it as a page that names nowhere. It now points at the ground such a page names, with its distance, and the ground is written into the world when it never was.

- **The page that names the ford now has a ford.** A story page handed over by a speaker, rather than offered by a herald, no longer goes unchecked: the ground it names is written into the world when it has never been, so the book's place has a direction and the needle appears. The ford itself is placed below the valley, down the road, instead of at the player's feet.

- **A speaker the book names is always somewhere reachable.** If the valley's square has no room for an Elf at the step they keep, they are stood on open ground in front of the player rather than not stood at all, and the log records it.

- **A speaker is stood beside their step rather than lost to it.** Glorfindel stands eight blocks south of the valley's middle, and that step is only walkable ground if the square was laid exactly there; in a wood it was not, the ground check answered nothing, and he was never stood at all. Keepers now look outward from their step for ground that will take them.

- **The beryl is put in the hand the moment Glorfindel says "take this".** The stone used to be handed over when the survey it is lent for was taken from him, which is a visit later, so the line that lends it could be heard with nothing in the pack.

- **The missing rider's report is carried back, not asked for again.** Finding the black horse now puts the report to Elladan in the book itself, because the bridle is the subject of that beat and not a page he hands over. Walking back with the saddle in the pack and speaking to him finishes it in one visit, instead of a player being sent to the camp twice for one errand.

- **A finished page says whose hand the next one is in.** Between steps the book is empty and the needle went blank, which left a player who had just settled a page with no idea whether the fault was theirs. Settling a page now names the body holding the next one and the place they are kept in, and the needle keeps pointing at that town until the page is taken.

- **A page to a built place is finished by standing in it, not by finding its exact middle.** The reach that counts as arriving was a flat twelve blocks, measured against the centre of the footprint - so a player standing in the ruin they had walked to, under its own milestone, was still a dozen blocks short and the page never settled. The reach now follows the building's size.

- **The road's needle is drawn above the hotbar rather than at the top of the screen.** Vanilla draws a boss bar across the top centre, and any boss in the neighbourhood covered the needle completely; the plate now sits where nothing in the game or in the pack reaches it.

- **A hall's milestone stands in the middle of the hall.** The stone a taken page plants at the hall was put at the herald's own feet, which is wherever he happened to be standing and could be the inside of the man himself; the room he stands in is read instead - the floor he can walk across without leaving it - and the stone goes at its middle.

- **A page now points the way to the ground it names.** An arrow at the top of the screen gives the bearing and a rounded distance to the place the page in hand sends the player, and points at the town a named body is kept in when the page asks for a person rather than a place; it is a hint and not an answer, so the country still has to be crossed, and it clears itself when the page is paid.

- **The chapter plants its own milestones.** A stone is planted on the ground a page names and beside the body that handed it over, the first time the page is taken, so the halls and Rivendell have a way back that is not the road again. A stone still only lists itself to a player who has stood at it, so travel is unlocked by arriving rather than by being sent.

- **The two pages that promise a conversation are written down as a fight on purpose.** Barachen says to speak with the Giant and Legolas offers an escort to the Wood-trolls, and both end in a body on the ground: it is the reference misleading the player, not the line softening a parley into a hunt. The pages carry that note now, so the next pass over them does not read the invitation as a stage direction.

- **The beryl's page no longer names a closing line it does not have.** A survey is settled where it is asked and is never read back, so its closing key was a page in the leaf that would have printed as its own name; the page carries no closing, as the cave search's does not.

- **The pages hand over what the reference gives them, and the choices are written down.** Most of the chapter's non-coin rewards were unpicked: the reference gives the first two pages bread, the report at the camp a piece of equipment, the cave hand-in a random reward and the last page an earring. They are Cram, Lembas, an ammolite sword, a potion of healing at the camp and the West's own charm now - drawn from the pack's food, drinks, gear and trinkets - real gear, not the decorative swords and picks that do nothing - with ores, the herb and a second trinket deliberately left out. `docs/chapter-one-rewards.md` holds the page-by-page table, the pool the choices come from and the reason for each, so the next chapter starts from a decision rather than from the pool again.

- **The herald asks a rank before he hands the chapter over.** A hall opens on a rank - nothing appears over the herald's head until the player has made a name for itself - and the chapter now begins at his bench, so the opening page is behind that rank like every other page he holds. The speakers along the line are not gated: Elrond and Elladan hand their own pages to whoever has come that far, and the only body asking for a name is the one standing in a hall.

- **The chapter starts at the West's herald, and the road takes it from there.** A hall is where a player is given work, and a chapter that opened in a valley gave the herald nothing to do but hand over the front's own three pages. He hands the opening page over at his bench - Elrond is still the one who hears it, at the end of the walk - and every speaker after that is the one the reference names: Elrond, Elladan, Glorfindel, Barachen, Legolas and back to Elrond. So the gate of the valley stops being a giver's post and the stand that put Aragorn on it is gone with the page that needed him.

- **Chapter one's pages are checked against the reference's own quest list.** The document carries the dialogue tree and, separately, a spec for each quest - its type, what completes it, what it pays - and the two do not read the same way. The spec makes the ford its own quest, completed where the beryl is raised, and the return to Glorfindel the quest after it, so what had just been folded into one page is two again. It also makes the three caves three area kills in the deepest chamber of each rather than three troll hunts: the troll that keeps a chamber is what clears it, and the token it drops is what the camp asks for, so the three hunt pages the old campaign wrote between the search and the camp are gone. The line is fifteen pages for eighteen quests, and each one now stands where the reference puts it.

- **Chapter one is walked as the reference walks it: a relay of speakers rather than a bench.** The campaign's pages were all handed over at a hall's herald, so the old campaign's eighteen quests lost the thing that makes them a story - each one handed on by the mouth that finished the last. Every page names the body who holds it and the body who takes it back now, and the line follows the reference's own order rather than its numbering: the trail is regained before the caves are searched. So Aragorn meets the player at the gate of the valley and Elrond takes the news, Elrond sends them to Thorenhad and Elladan takes it, Elladan hands the wisdom, the ford and the caves, Glorfindel hands the beryl and hears the ford, Barachen hands the Giant and Elladan hears it, and Legolas hands the Wood-troll and sends the tidings back to the valley. A giver who is not the one holding the page points the player at the one who is, and the rank a hall asks of a stranger no longer stands between a player and the Elf who opens the chapter. The Elves are stood by reading the line - the page ahead is theirs to hand over, the one in the book theirs to take back - rather than by the stage windows they used to be written into.

- **The cells Arda's drawing gets wrong are written down, with the chapter that will walk into each.** `docs/arda-map-gaps.md` holds the nine countries measured cell by cell against `middleearth.jpeg` - Mirkwood two cells east of the map, the Sea of Rhun seven, the Iron Hills and Mordor five, Rohan three, Nurn six and seven wide where the map draws four, the White Mountains twenty-four cells across where the map draws ten, Ered Luin running twelve rows too far south, and four columns of invented Harad down the eastern edge - so that the first chapter to survey ground in one of them opens with a decision about it. Nothing is pinned in any of those countries yet, so they can still be moved without moving a build; the file names what a move has to carry when one is made.

- **The campaign happens in Arda and nowhere else.** Pages, places and pitched ground were served to any level a player stood in, and a level that is not drawn by `ArdaMap` has no country to survey - so a walk page taken in the overworld could not be finished, and a site there fell back to the older pick of "a spot out in front of the player". The commissions now refuse the overworld outright (`Places.ensure`, `Places.tick` and `Sites.plan`), so the vanilla world keeps its own generation and its own business, and everything the campaign does happens on the ground it draws. Work is taken and counted there too: a page cannot be accepted outside Arda, and a creature killed outside it advances nothing.

- **The camp and the three crags stand in one place each.** Those four were the last builds with no country written down: they were pitched forty-eight to ninety-six blocks in front of whoever took the page, so two players on one server were handed two camps and two of every crag, and neither could find the one the other meant. All four are canon'd to the Trollshaws and pinned to a cell - the camp at (17, 13), the first crag at (17, 15), the second at (18, 16) and the third at (19, 16) - so every player walks to the same door. A camp stands one keeper however many players raise it, since the ground is shared and would otherwise have put two men at one fire. The hills themselves were widened to three cells by five to hold them, which is room the map has and the old two-by-three did not.

- **The names chapter one walks to have ground of their own.** A name was surveyed from the middle of its country, and a country's middle is one cell, so every name in a country came back in that one cell: measured on the drawing, Rivendell, Elladan's camp and Barachen's fire all fell inside a single Trollshaw cell fifty to a hundred and twenty blocks apart, three places a player crosses in a minute and each announcing itself over the last, and the rider's ruin came back beside the camp that sends the player to it. A name now keeps a cell of clear ground from every name already written, and the four the maps pinpoint are asked for their own cells instead of their country's middle - the valley at the north-western corner of the Trollshaws, Elladan's camp a walk east of it, Barachen's fire south of the road, and the ruin in the channels south of it. Measured after the change, Thorenhad stands seven hundred and ninety-two blocks south-east of Rivendell and Barachen's fire nine hundred and thirty-five south of it, with the ruin a cell of its own between the road and the camp.

- **The Trollshaws are drawn again, and Rivendell stands at the mountains' feet.** The drawing painted the Trollshaws into rows 12-14 of the map and then filled Firien over the same rows, so the country ended up with no cells at all: `anchor` answered the world origin for it, and every name the books put there had nowhere to be surveyed. In Arda that made Elladan's camp and Barachen's fire impossible to place - a walk page to either could never be finished - and dropped the rider's ruin into whatever land lay within a kilometre of the origin, which is the Shire. Firien now starts below them, so the Trollshaws are six cells on the western slopes of the Misty Mountains, and Rivendell is asked for in that country rather than in Eriador, whose middle is Bree and the Weather Hills and which therefore stood the valley four thousand blocks west of the mountains the maps set it under.

- **Elladan's skin is the size his model reads.** The art drawn for him was a 64x32 skin, the layout the old pack's art was drawn in, while `human.geo.json` reads a 64x64 sheet - so his sheet was sampled against the wrong scale and the left arm and left leg, which exist only in the lower half of a 64x64 skin, were drawn from empty pixels. The file is 64x64 now: the top half is untouched and the left limbs are the right ones mirrored face by face, which is how the game itself converts a legacy skin. It was the last of the pack's named skins still in the old layout - Elrond's, Legolas', Glorfindel's and the Ranger's were already converted.

- **Three crags, one for each cave the notes name.** The stone's cave page asked in the same hill three times: the campaign cut one crag and raised it again for Bandrith, Hoklond and Sholdroth, so three errands walked to three copies of one cave. `tools/make_troll_cave.py` now takes `--seed`, `--out` and `--vary`, and the pack ships `troll_cave_2` and `troll_cave_3` beside the first - the same generator asked twice more, with the hill's own radius and the chambers nudged as well as the seed redrawn, so the second and third read as other hills rather than as the first with other moss. `Sites.CAVE_VARIANTS` holds them in the order the page asks, the campaign pitches the crag of the asking it has reached, and a crag already walked into is replaced by the next one rather than walked to twice. The shipped crag still comes back byte for byte from the generator's defaults.

- **Elladan wears his own face at Thorenhad.** He is a camp keeper, and a keeper was given one file for the job - the Ranger at the fire - before any named skin was asked for, so the art drawn for him could never be seen. A keeper now looks for the file of his own name first and falls back to the Ranger's face when nobody has drawn it, which is what every named giver already did.

- **The rider's bridle is the proof of the report.** The dark saddle was an item with nothing to do: the notes have the player take the horse's tack in the ruin and carry it back to Elladan, so the page that finds the rider now puts the saddle in the player's hand, and the report at the camp is not heard without it - Elladan says what is missing instead of paying for a tale that cannot be told, and takes the saddle when he hears it.

- **The campaign ruin's chests are stocked.** The ruin was cut and raised in a world, so its chests came across with no loot table and opened empty. They point at the pack's own `tolkienmobs:chests/desolate_ruins` now, applied by `tools/stock_ruin_chests.py`, which rewrites the template as a typed tree so nothing else in it is reordered or dropped.

- **What comes after chapter one is written down.** `docs/after-chapter-one.md` records what the campaign already has, and the gaps that will bite in order - Gundabad with no place or keep, Annuminas with no Arnor on the drawing and no city in the pack, Moria and Forochel with biomes and no campaign ground, the East and Harad lines at three pages against the West's seventeen - so the next chapter starts from a decision rather than from a survey.

- **The ruin the rider is found at is the campaign's own ruin, not the world's.** `ttmruin_large` is a structure the world generator places across the wild, so building it for the *Missing Rider* page raised a copy of ground the player could already find out there and left the ruin the notes send them to as one of many. The page now raises `ttmruin_large_2`, which nothing ever generates, so the wall the bridle is found at is a building of its own; the generated ruin keeps its worldgen definition untouched.

- **Chapter one is the notes' chapter one, and the old pages are gone.** The West's line was a set of pages this campaign invented between the notes' beats; it is the notes' eighteen quests now, *Troubles of Rivendell* to *Hasten Their Departure*, in the notes' own order, and the twelve pages that had no quest behind them - the first and last musters, the south road, the barrow hunt, the walk to the valley, the hilltop, the hunters, the raised ford, Bree, Tharbad and the Old Forest - are removed rather than left standing beside it, because a line answering two books at once cannot be checked against either. The notes' *Hiding in the Dark* is four pages (the stone asked at the bottom of three caves, then the troll of each, named as the notes name them), Barachen is spoken to rather than merely walked to, and the ford and the return to Glorfindel share one page because a stone's asking is settled in the hand that lent it - two pages of talk with the same Elf back to back would be one errand told twice. Standing stays at the flat tenth throughout, and the Giant now carries the notes' own purse (four silver, sixteen bronze, fifty experience) beside the Wood-troll's. The Chinese lines that changed are translated; the new ones are English until the rework has been played.

- **Chapter one is complete: the rider beats, the counsel, and the camp that hears them.** The ruin is the pack's own ruin template, stood in the Trollshaws by the map rather than declared as ground, so the finding has a wall to be found at. The notes' *Missing Rider* is two pages instead of one (the walk to the ruins in the narrow channels, where the closing line tells of the black horse found there, and the report Elladan hears at his camp), the notes' *Wisdom of Lord Glorfindel* and *Regaining the Trail* are pages of talk, and *Where is the Horse and the Rider?* now plays where the ids put it - the second page of the chapter, the walk to Thorenhad - rather than at the end. Elladan stands at his camp from that page to the Giant's, and Glorfindel now stands in the valley for any page of talk that names him, not only for the stone's work, which is the first thing his skin is worn for.
- **Glorfindel wears his own face.** The skin drawn for him was in `faction_west/`, which the renderer never reads for a named giver; it now sits at `middle-earth/glorfindel.png` with Elrond's and Legolas' art, where the hall's named-skin lookup finds it.
- **A page's purse is read on a hover, not a new layout.** The story leaf's reward row is unchanged; resting the pointer on it says what the page pays in coin and experience, and a page that pays none draws nothing at all.
- **The Wood-troll pays the notes' purse, and his tale is carried back to Elrond.** Pages can now carry coin and experience as well as standing and courage (`Page` has silver, bronze and experience, zero on every page but this one), and the kill pays the notes' eight silver and forty bronze and its fifty experience. The notes' *Hasten Their Departure* is a page of its own instead of being folded into the closing line: a page of talk owed to Elrond, handed by Legolas and settled by speaking to Elrond in the valley - who is now stood while a page names him, so the library is not empty when the player arrives. Taking the page also pitches the pack's own troll cave ahead, so Matuna is met at his lair rather than in an open field. A page of talk is never read back, so a page that settles itself now says its closing line when it settles rather than leaving the words unread in a book it has already left.
- **The valley is peopled by Elves, and its two named ones wear their own faces.** The six who walk the square were the human body the line's givers use; they are the pack's own Elves now (`entityttmelves`), and the home-keeping that stops a villager stroll walking out of a room moved down into `TolkienVillagerEntity`, so every one of the mod's folk placed with a home stays in it rather than only the human. Elrond and Legolas keep the human model their art is drawn for, and wear `middle-earth/elrond.png` and `middle-earth/legolas.png` - the named-skin path the hall heralds already use - converted from the 64x32 skins in `CustomSkins` to the model's 64x64.
- **The Wood-troll Matuna is the guest house's errand, and Elrond's to hear.** Legolas names the creature the Giant was the shape of and shows the player where to track one down: the notes' Aid of Mirkwood, priced at their twelve hundred taken at a tenth, carried back to Elrond with the line about the Nazgul's designs and the shadow on his heart. The pack has no wood-troll, so the campaign stands one cave-troll up ahead of the player, names him, tags him and counts that body alone - the page names a creature rather than a kind of mob, so no troll met in a cave can settle it.
- **Rivendell stands.** The valley is built out of seven elven pieces - a square at its middle, a library, two houses, a gate, a lamp at each corner of the square and a banner either side of the gate - laid at one floor so the buildings meet the paving, with dirt under each so nothing on a slope hangs. Six Elves walk the square, named, homed to it and never givers.
- **The line moves to the guest house to hear Legolas.** Elrond's audience hands on to the notes' guest room, walked to in the same valley at the notes' two hundred and fifty taken at a tenth, with Legolas stood in it to hear the tale and hand on the Wood-troll hunt.
- **The line comes back to Imladris to hear Elrond out.** After the Giant the notes send the player back to Elrond, who can name the creature and arrange the audience with the Mirkwood company: the walk to the valley is the page, priced at the notes' two hundred and fifty taken at a tenth, and Elrond now stands in the valley to hand on the guest room where Legolas is staying.
- **The scout's report pays off as a Giant in the valley.** The West's line gains the notes' hunt for the creature Barachen saw: a tree-ent, since the notes have it standing among the trees as tall as they are and then have Elrond say the player could not have faced a true Onod. Elladan hands the page at the camp and takes it back, so he now stands from the cave page until the Giant's has been taken off him rather than only while the stone is in somebody's hand.
- **The West's line walks on to Barachen's camp.** The cave search ends with the stone going back to Elladan and, in the notes, with a scout's report of a Giant in the Trollshaws: so the page after the caves is a walk west along the Great East Road to Barachen's fire, a named place canon to the Trollshaws rather than a hall the campaign raises. He stands there from the page that walks to him until the page he hands has been taken off him, and his slot is read off the line rather than written down a second time, so a page inserted above his cannot leave him standing at the wrong step. He wears the Elves' own giver tags, so the same right-click hands and takes his pages.
- **The Trollshaws are a country of their own.** The upland wood between Rhudaur and the Misty Mountains had no biome of its own: a player walking west out of Rivendell crossed Firien's hollies or Eriador's fields, and the hill-trolls the place is named for had nowhere to be. It is a temperate upland forest now - beech and oak below, pine on the higher slopes, ferns, fallen timber, boulders and stumps where the rock shows through - drawn higher and rougher than Eregion below it, and tagged hostile so its trolls, wargs and Rhudaur brigands keep it. Its climate is the old-growth birch slot, which nothing else in the region had taken, plus the forest slot's cool dry half; on the map it takes the north end of the old Firien cells, under Rhudaur and against the mountains. Elladan's camp at Thorenhad is canon to it now, so the camp and the caves about it stand in the country the books put them in.

- **Thorenhad is a tent with Elladan at it.** The camp was a name with a man standing in open grass, which is not a camp. It now lays the pack's own tent down on the ground the name is for, seated on the highest column under its footprint with the drop filled beneath it so it stands on a slope instead of hanging off one side of it, and Elladan is stood at the fire in the same look - there is never an Elf waiting in a field for a tent that never comes. It is laid straight from the template rather than through `Sites`, because a camp in the Trollshaws is one camp for the whole world and `Sites` keeps one site per player, which is already holding the cave this page was walked to.

- **Thorenhad stands in the Trollshaws.** Elladan's camp was a name the map was free to draw anywhere near whoever carried the cave page, and the Trollshaws are the western slopes of the Misty Mountains - the country this drawing calls Firien - so the camp is now pinned there and every world puts it on the same side of the mountains as the valley it is a day's walk from. Nothing is built at it; it is a camp, and it stays one.

- **The beryl changes hands at Elladan's camp, not in the valley.** The old campaign's notes end the cave search at Thorenhad rather than at Rivendell: Glorfindel leaves the stone in the player's care for the caves, and it is Elladan who takes it back to him and sends the player on to the next lead. The campaign now does the same. Thorenhad is a name of the place catalogue - a camp in the Trollshaws, drawn by the map rather than built - and Elladan stands at it while the cave page is the step in hand, wearing the same giver tags as the Elf in the valley, so the right-click that hands a page also takes the finished one and the stone with it. Glorfindel now waits only for the ford: once the stone's work is the caves, the hand at the end of the walk is Elladan's.

- **The three caves have their names back.** The old campaign's notes did not send a player to "any three caves": they named three troll-caves - Bandrith, Hoklond and Sholdroth - and gave each one its own quest, its own completion line and its own token, with the beryl held aloft at the bottom of each coming up dark because the Nazgûl was never there. The page now carries the names, and the stone says which cave it is being asked in as it stays dark, which is the answer the notes promised: the beryl says where he has not been as much as where he has. The three are asked in order, so the second asking is Hoklond's and the third Sholdroth's rather than three anonymous holes.

- **The cave the stone is asked in is cut and raised by the campaign.** The old campaign named three troll-caves on a hand-drawn map, and the mod's own caves are a Mirkwood jigsaw a country away from Eriador, so a player carrying the stone's second asking could search a whole country with no cave in it. The campaign cuts its own: `tools/make_troll_cave.py` builds a crag 56 by 36 by 56 with the inside taken out of it - four chambers under a lair with a hearth and bones in it, corridors between them, stalactites over them, moss, webs and dead brush on the floor, and a mouth cut open on the eastern flank - and writes it as a template under `key_structures`, so it is placed the way the camp and the hilltop are placed. While the cave page is the step in hand the campaign pitches one of these on the road ahead, once, and the three holds are asked in the rock the campaign itself put there. The template is a file rather than a build, so the tool that writes it is in the tree beside it.

- **The stone's first asking is the ford, as it was on the old map.** The stone was one page of caves, which skipped the asking the old campaign opened with: the beryl held aloft at the Ford of Bruinen, the last ground anyone knows the missing Rider touched, and carried straight back to Glorfindel. It is two pages now. The first is one landmark and one hold, finished by standing in the ford and raising it - and the ford is ground the place catalogue already names, so a world that has never been told where the ford is has it named from the map rather than the page waiting for an operator. The cave search that follows is the old campaign's three troll-caves without the hand-written coordinates: any three caves will do, asked at the bottom of each, which is sixteen blocks of rock over the player's head rather than the eight that only prove they are inside one. The lent stone is given back only after the last of its pages, so the ford cannot leave a player hunting caves with empty hands, and both pages are Glorfindel's to hand and to take.

- **The stone's work is Glorfindel's, and the stone goes back to him.** The beryl was lent by an Elf whom nothing in this campaign ever put a body on, so a Ranger handed out an Elf-lord's stone and the work paid itself in a field. The West's line gains the step before it: the herald says plainly that Glorfindel of Rivendell has asked for the player's presence, and that page is the walk to the valley - nothing in the pack has ever built Rivendell, so it is a name of the place catalogue the map puts in the west of Eriador rather than in whichever country the page happened to be taken in. Glorfindel stands in it while the stone's work is the step in hand, a campaign giver like a herald with a stone rather than a country's war to hand out, and the four caves complete the page rather than pay it: the last reading is the stone's piece, and then the player walks back to the valley and gives it to him, which is where it is paid and where the lent beryl goes back with the work. A herald asked for a page that belongs to somebody else now says whose it is instead of handing it over.

- **The Elf-lord's stone is held up, and it is the ground that answers.** The beryl was described, praised and never given: the page that pays for it was finished by being read, so the answers the stone has for five places in the world could not be heard, because nobody was ever handed one. The herald now puts it in the player's hand as he says hold out your hand, and the page is a survey of caves: the stone is held up - it is the one thing in a pack that is used rather than carried - and it wants rock over its head, so above ground it says plainly that it has nothing for open air. Underground it answers the cave it is held in, which is the question the old campaign asked it when the caves were hand-placed: five cave readings are written and the cave's own blocks pick one, so a cave keeps its answer and two caves do not say the same thing. Three caves complete that second page and each has to be a walk from the last, because two mouths of one system are still one cave, and asking twice in the same one is told so rather than counted. A stone put down is a loan spent rather than a stone lost, so it comes back to the hand it was lent to. The stone no longer answers by itself when a player walks into marked ground - that was the ground talking over the player's head, and it left the item with nothing to do.

- **A page no longer prints where it was taken as though it were where it is.** Every page in the book is drawn with "Taken at x, y, z" under the giver's name, which for an errand taken at a bench is a true and harmless thing. Ground the campaign builds is chosen where the player is standing when they take the page and does not exist yet, so that line printed their own feet, and a walk to Weathertop read as a walk to where they already were. A page naming built ground says nothing there now, and the leaf closes the gap rather than drawing an empty line.

- **The crown is met in his own house before he is fought in the ring.** The castle the war raises was a building the player walked into and left again, and the only Witch-king in the campaign was the one standing on the sand at the end - so the fortress was scenery and the meeting the last page promised had already happened somewhere else. He is now waiting inside it while the castle's page is carried: seated on a chair that draws nothing of itself - his own idle clip plays the saddle pose while he is a
passenger, which is how he sits a fell beast - without a mind of his own so he does not pace the halls, and as unkillable there as he is behind his ring, and it is him who says where the war is settled - come to the sand. The chair is looked for as air to stand in under a roof, nearest the middle, so the meeting happens in a hall and not on a courtyard's flagstones. The chair itself is now the block the castle build names
at 3850, 107, -7425, and the search is kept only for a build that names none. When the page moves on he is gone, and the seat is empty when it is looked into again.

- **The ring holds at noon.** The three Ring-wraiths that keep the crown, and the crown himself, stand where the campaign stood them in daylight as well as in dark: all of them are built on the same body as the mod's other servants of the dark, which walks out of the sun and into shade - the right habit on a road, and the wrong one on an arena floor with no shade in it to reach. The two sun goals are dropped from the bodies this fight is made of and from nothing else, so the Rider, and every Ring-wraith a player meets elsewhere, still keeps its habits.

- **The crown is fought for on the arena's floor, and never alone.** The last act was a Witch-king stood on the first patch of air over the middle of a castle and killed where he waited, which made the arena the campaign raises the one building the war never used. He now comes up in the middle of the arena's sand, and three of the Nine come up round him on the ring - two the player has met before and one they have not - and while one of those keepers still stands, nothing lands on him at all: the page asks for four bodies and not for one. The shield is an event rather than a rule inside the boss, so it belongs to the campaign's own Ring-wraith and to nothing else an operator or a pack might spawn. The arena therefore comes before the crown instead of after it, the crown's line and the arena's are both said over the sand once the fight is over, and the keepers are stood once with him rather than topped up, because a keeper who falls has been dealt with.

- **The campaign ends in the arena it raises, and the Witch-king waits in a room.** The fortress was the last thing the war built and the crown was the last thing it said, which left the arena - named by the ladder's own rung and by the words the campaign closes on - standing in the pack and never once raised, and left the Witch-king stood at the first two blocks of air over the middle of a courtyard. The campaign's own slot now walks four last acts instead of three: once he is felled, a page names the arena, the map gives it ground in Angmar the way it gave the fortress its own, and the war says its last over empty sand rather than over the body in the hall. He also has to be somewhere a roof covers him and a wall stands within reach, and that room is looked for across the building rather than straight up its middle, so a keep with a hall in it puts him in the hall instead of on the flagstones with the gate at his back. The book draws each of the four under its own name now, where it called all of them the Rider's page; the translations are in both languages, so the arena's title, its place and its closing line read in either.

- **Angmar generates in the overworld too, not only on the drawn map.** The new biome was wired to the map's country, and the map only reaches Arda: the region's climate had no cell for it, so a plain overworld never grew one while every other Tolkien biome did. It takes the frozen plain's dry third - ground cold enough to snow and too dry for the taiga that fills the rest of it - which is the one stretch of the far north the Misty Mountains' claims and Forochel's had left free, so no biome gives up ground for it.

- **A template says what it carries, and what it leaves alone.** Every build handed over now follows one rule: the air *outside* a building is left out of the file entirely, so the ground it generates into is not carved into a square, and the air *inside* it stays air, so terrain does not fill the rooms. `tools/WorldScan extract_enclosed` applies it mechanically - solid blocks kept, air that cannot reach the edge of the box kept, air that can reach it dropped, which is the difference between a room and the weather. Weathertop was re-cut under it, keeping 411 enclosed air cells and dropping 513,192 outside ones, and the faction hall brief now says to leave the outside empty instead of pouring structure void into it. The builds the campaign raises for its story - the camp, the hilltop, the fortress and the arena - moved into `data/tolkienmobs/structure/key_structures`, with their ids, their template pools and the structure provider all repointed, so the keys of the map sit in one drawer rather than among the villages.

- **Weathertop is raised, not merely named.** The West's walk to Weathertop is the campaign's first page to a landmark rather than a camp: a hilltop cut out of a finished build and carried as a template, 121 by 40 by 121, with its open sky recorded as structure void so the terrain it lands on shows through instead of being carved away. It comes out of `run/saves/weathertop` by way of `tools/WorldScan`, which grew a `layers` mode to find the floor by counting what each layer holds and an `extract_void` mode to write air as void. The building path grew with it: a site now waits for every chunk its footprint covers rather than only the one at its middle, clears a template that records its air and leaves the ground standing for one that records void, and searches a quarter of the building's width at a time out to the building's own size, so a hundred-block hilltop gets more than the single candidate the tent's step left it.

- **The body at a built camp is a ranger, not a passing villager.** A keeper was one of the mod's folk in whichever face its variant rolled, which is what a person in a village wears rather than what the man at a fire a page sent you to should wear. Keepers now ask for one file of their own, `textures/entity/middle-earth/ranger.png`, before the variant is looked at - and the file is optional in the same way a herald's own face is, so a pack without the art gets an ordinary human at the fire instead of a checkerboard. Whether a body is a keeper is a tag, and a tag is a server fact, so it is synched the way the givers' clothes are.

- **A page says which way the work lies, not only which country.** A country is a haystack: a site is one hashed point inside a five-hundred-block cell, and a page that named only the country sent the player out to search it. The country line now carries an eight-point bearing as well - "The work lies in the Shire, away to the north-east" - which costs nothing to say and turns a search back into a walk, while still leaving the ground unnamed.

- **`/tmquest page` says where it put the ground.** A page names a country and leaves the walking to the player, which leaves an operator testing a site with no way to know which way to walk: the command now prints the coordinates it chose, and only the command does.

- **Quarry a page owes is collected on the road, not at the bench.** A page used to put its mobs down the moment it was taken, a dozen or two blocks in front of the player, so every step of the campaign arrived within sight of the herald who described it. A hunt now holds its quarry back for a minute and a half after the page is taken, and only pays it out once the player is out under open sky and a hundred blocks from that bench - then stands it up forty to sixty blocks ahead instead of twenty. The Rider keeps his own arrival but waits on the same clock, and stands further off when he comes. One hand-over owes one meeting, so a quarry killed is a hunt that moves on rather than a road that restocks itself.

- **A quest site is chosen from the map, before any of its ground exists.** Arda's terrain is a drawing, not noise the world has to make: `ArdaMap` already answers, for any column, what country it is in, how high it stands and whether a river runs there. Ground for a structure is now chosen and judged from that drawing rather than from the player's facing, which means the spot is settled before a chunk of it is generated, that it is dry and level and in the right country by construction, and that it is the same ground for everybody - a page can name a country and be right. The country a structure belongs in is read from the structure's own biome list, so the tent's wilds and the campaign castle's Mordor needed nothing written down twice. A world the map does not draw keeps the older answer, and the build still judges the real ground before it lays anything down, since the drawing carries no warp and no trees.

- **The campaign has its ending back: a castle it raises itself, and the Witch-king waiting in it.** The castle and the arena have been in the pack for as long as the mod has, registered but never in a structure set, which means no world has ever generated one and nothing has ever pointed at them - the campaign ended on the Rider and simply stopped. The campaign's own slot now walks through three last acts instead of one: the Rider, then a castle whose ground is chosen where the player is standing when they take the page and raised when they come within sight of it, then what is waiting inside. The Witch-king is stood up in that castle - the first body the campaign has put in a building rather than on a road - at the first place in it where a body fits, tagged so a reloaded world finds the same one instead of a fresh one every look, and named off the registry so the page cannot ask for a boss the pack does not have. Settling him pays the rung the courage ladder has been holding back for the arena, and the campaign says its last words there. A herald whose own front is long finished now has something left to hand over, which is what the mark over his head meant all along.

- **Built ground gets its own groundwork.** A site was only placeable on ground that was already level, which a tent will find in most meadows and a castle will find almost nowhere, so the site now builds at the highest column under its own footprint rather than at the height where the player happened to be standing - a hillside through the middle of a building being rather harder to excuse than a gap under one wall - and clears the box the template is about to fill before it places it, so no treetop comes up through a tent and no corner is left standing in a courtyard. The box is the template's own and the plane is its own, and it is the only thing in the campaign that takes blocks out of a world rather than putting them in, which is why the ground is still only chosen in open country a long way from where the page was taken.

- **The campaign can build its own ground now, and the tent was only the first thing it built.** Every building the mod has was the world generator's or an operator's: nothing in it had ever put a template down itself, which is why the arena and the castle have sat unbuilt for want of a campaign to place them. That is now a thing it can do, and it does it for any template there is. A page that names built ground - `site:tolkienmobs:wild_tent` today, an arena or a castle later - has its spot chosen from where the player is standing when they take it, forty-eight to ninety-six blocks on and nudged off their line so it is not always dead ahead, and nothing is written to the world until they are within four chunks, because at that range the chunk is loaded anyway and no chunk is force-loaded to hold a building nobody may visit. The footprint judged before anything is placed is the template's own size, read from the file at build time: every sampled column within tolerance of the site's height, over solid ground and out of standing water, with the tolerance growing with the building - two blocks across a tent is a hillside through the middle of it, and two blocks across a castle courtyard is flat. A spot that will not take it is searched for in rings around the written one, and if none of them will, nothing is built and the next look tries again. A camp is the same machinery with a body at the fire, which is all that makes it a camp; taking a second page to ground the player has not reached yet keeps the site they already have rather than pitching a rival beside it, which is what a quest in pieces needs.

- **The West's line is twelve pages, and half of it is walked or heard rather than killed.** Three pages took the West from its own door to the barrows and then stopped, so the front read as three fights and a heap of dead. It runs to twelve now: after the barrows the herald puts Glorfindel's stone in the player's hand and tells them what it is for - the Elf-lord's beryl, carried south once before and never brought back - and from there the line is three walks and three conversations between the fighting. The walks go to Weathertop, the ford and drowned Tharbad, all of them ground the place catalogue already names, and the herald will not hand one out in a world that has not named that ground, because a page nobody can walk is a dead end in a book. The conversations are read rather than handed over: one at the herald's own bench, and two from whichever giver a player asks, so Bree's gossip and the Old Forest's indifference are things somebody in the world has to be asked for. The front's last words moved with the end of the line, so the page that finishes the West now carries the closing line rather than the third page that used to.

- **The errands are four shapes now, and only one of them is a country's.** Every offer outside a front was either goods wanted or beasts wanted, and the only other work in the mod paid standing, so a player who spent a month in one country spent it killing the same two armies and reading the same two sentences. Two more errands join them: ground to be walked - taken only from places this world has actually named, in the dimension the player stands in, so a walk can always be finished - and a body to be heard out, which is the one errand whose work is the telling. That one is a leaf of the office's own, so every giver in the world has the same tale to finish and none of it had to be written per giver: it is taken at the hand-over and told when the player comes back and asks for the rest, because hearing somebody out in the same breath as being handed the work is not an errand at all. The four errands are drawn evenly and the country's work keeps the chance the config gives it, so a run of errands is now a run of different things.

- **The beryl has something to say about marked ground.** Glorfindel's stone has been in the pack since long before any of this, carrying a memory from the old campaign - it was held up in caves to see whether the Enemy had been there - and nothing in this build ever asked it anything. The place banner now has that second half: come into named ground with the stone in your pack and, once the place has finished naming itself, the same banner comes back over with the place's name and the stone's answer under it. It is one `if` in the machinery that already runs, and the answer waits its seven seconds so the two banners are read as two things rather than as one flicker; it also refuses to arrive if the player has walked out of the place or put the stone away, because a line about the ground under their feet is worth nothing anywhere else. Which ground answers is a mark on the place's own entry in the catalogue, and what it says is `place.tolkienmobs.<id>.beryl` in each language - so a place's sheet is what makes the stone react there. The five the Enemy has passed are marked now - the Green Way, the barrow downs, Weathertop, the ford and Tharbad - each with the stone's answer of its own, while Bree, Hobbiton and the Old Forest stay ground it has no opinion about, which is itself worth knowing.

- **A refusal is said to your face, and a page re-read keeps its last line.** Being turned away by a herald was a chat line and a sound: the man himself said nothing, which is a strange thing for the one body in the campaign who is all voice. He now says it on the same leaf a page is handed over on, with his answer for a heading and one paragraph under it, and no replies, because there is nothing to answer. The leaf also remembers what it is: a page already in the book, opened again from the herald who gave it, starts at the last thing he said rather than at the question the player has already answered - it used to open at the opening, so reading a page twice meant answering it twice.

- **A herald can have a face of his own.** The three who hand the fronts out were one cloth each: Maelgrin, Guthlac and Zarabi all wore their alliance's single giver skin, so the campaign's three named people were three copies of the same soldier in three colours. A hall's herald now looks for art drawn for him first, at `textures/entity/middle-earth/<name>.png` - `maelgrin.png` for the West's, `guthlac.png` and `zarabi.png` for the others - and wears the alliance's cloth when that file is not there, so the portraits can be drawn one at a time and a pack that never draws them looks exactly as it did. The name asked for is the one the body is actually wearing, so renaming a herald in the world looks for the art of the name he now carries, and a named file that nobody has drawn is passed over without a word, since only the alliance's art is the mod's own promise.

- **A page can be walked to, or heard out.** Every page the book held counted kills or read the pack, so the campaign could not ask a player to go somewhere or to hear somebody out - the two things its own writing does most. Two shapes of work join the ones the givers hand out: a location page names a place from the catalogue's own list, or a point of its own with a reach round it, and is done by standing in it; a dialogue page names a page of talk to be read to the end, or a body to be spoken to, and is done by hearing the words out, with the leaf saying which page it was on the way out and only an answered leaf counting, so one shut unread finishes nothing. Both settle where they happen rather than being carried back to a hall, through the same hand-in as any other page, because reaching a place and hearing a conversation are each their own proof and there is nothing for a giver to check. `/tmquest <targets> page <kind> <target> [goal]` writes one of any shape into a book for trying them out, the target written the way a page writes it: `place:greenway`, `at:100,64,-20,16`, `leaf:campaign.tolkienmobs.page.muster` or `person:Maelgrin`.

- **A page can name the body it goes back to.** A finished page was paid by whatever giver a player reached first, which made every page local: there was no way to write work that sends them to another hall, and no way for a giver to say that a page in hand is somebody else's to take. A page may now name one of the mod's own - take this back to Guthlac - and only that body pays it: the giver who handed it over wears the work's ordinary mark, the body it is owed to wears the question mark instead, drawn only as far as a nameplate can be read, and a giver asked for a page that is not his says whose it is rather than going quiet. A page that names nobody is paid by whoever reaches first, so every page already in a world reads exactly as it did.

- **The page that ends a front has the last word of it.** A front's line simply stopped: the last page paid its standing and its courage and the front was over, with nothing said about what had been walked or what was left behind. Each front's last page now carries a closing line - what the quiet over the barrows costs, what the East has bought with a sealed scroll, what the sand will not say about the mûmak - spoken the moment that page is paid, and drawn on the page in the book but only once the work is done, because it is the one thing on the leaf a player cannot read too early.

- **The doorway guards stand on the step, not in the room.** They were baked into the hall's interior either side of each doorway, which put two soldiers in the path of anyone walking in and left the approach itself unwatched. All four of a hall's now stand outside, centred on the doorway's own columns one block out from the wall, on the ground the hall already carries so nothing has to be levelled under them afterwards, facing away from the building. Inside is unchanged: the warden, the envoy and the giver still hold the room, the near guard still says its line as a player comes up to the door, and the pose is the same frozen sway turned to a face.

- **A guard's shield shows its face.** The shield was hung a quarter turn out from the arm, so from the front a player saw the edge of a plate rather than the alliance's colours on it - the heraldry was right and unwatchable. It hangs square across the forearm now, low on the arm rather than at the shoulder and facing the way the guard faces, so the design looks back at whoever comes up the step.

- **Each hall hangs its own banner now.** The halls held their alliance's staff and its doorway guards, and nothing of the alliance itself on the walls, so the three buildings read as rooms that happened to belong to somebody. Each of the three is dressed with its own side's mark - the West's blue field under a white skull and a crowned flower, the East's black field under a white flower in a rhombus, Harad's grey field under a light rhombus and bar beneath a red-and-orange circle - hung on the outer face of the wall either side of both doorways, in the block beside each opening and above the heads of the guards posted below, so a hall says whose it is the moment a player steps through. The designs are the same list the guards' shields are cut from, so the banner over a doorway and the shield beneath it are one heraldry rather than two drawings of it, and each banner keeps its layers in the order they were cut. They hang as wall banners rather than standing ones, because a banner standing on the floor would stand in the very block a doorway guard already holds.

- **All three heraldries are in the mod now, not just in a world.** The West's blue field under a white skull and a crowned flower, the East's black field under a white flower in a rhombus, and Harad's grey field under a light rhombus and bar beneath a red-and-orange circle - transcribed layer for layer, in the order they were cut, from the designs themselves. A shield is a banner worn on an arm, so the guards at the hall doors now wear the shields that go with the halls they stand in, and the same list is what the halls' own banners will be hung from.

- **A hall keeps its door now.** Each of the three halls had a warden, an envoy and a giver inside and nothing at the door, so the building read as a room rather than as a place that belongs to somebody. Two soldiers of the hall's own alliance now stand either side of each of its two doorways - the blade in the right hand they were already armed with, and a shield on the left arm that nobody has to hand them - with no brain and no patrol, posed by the same idle the warden and the envoy stand in, so they sway at their post, turn to a player who comes in and settle back to it. The nearest one says its line as you step through, once a visit, and none of them lifts a hand while you are under the roof. They are baked into the three hall templates beside the staff, so a hall placed from a saved world keeps its door too, and a doorway pair is no longer counted as a patrol already sent.

- **A place says its name as you come into it.** The campaign's ground had names only inside the book: nothing in the world told a player they had come to the Green Way or crossed onto the barrow downs, so every road read as the same road. Standing where a place has been set now introduces it - the name over a four-word line under it, both bold and italic, the name in the ink of the country that holds the ground and ground held by nobody written plainly - arriving in a second, staying five and leaving in one, which is the timing the campaign's own notes had written out as command blocks. A player is told once ever, so the banner is an arrival rather than a notification, and the sightings are saved with the world. `/tmplace add <id> [radius]` sets a name at the caller's feet, `where` says what they are standing in, `list` reads the catalogue back with where each name has been put, and `forget` clears what a player has been shown so a builder can walk the same road twice. A place is a circle around a point rather than a border, because a world can say where its towns are but not what shape its country is, and where two overlap the smaller one is the one a player is told they have reached. The eight names are the West's, because it is the West whose line is written; the other two are a list here and a line in each language when their turns come.

- **Walking a front is what makes a player brave.** A campaign page paid one courage deed, the same as a villager's errand, so a player could carry three fronts to the end and still stand where the ladder starts: pinned by the first terror they met. A front's pages now pay courage beside their standing, rising along the line - two deeds for the first muster, four for the second, seven for the third - because the page that sends a player to their own country's barrows or sand is not a body count. Three fronts come to thirty-nine deeds, forty-eight with the nine the hand-ins already pay, which is stouthearted: the terror slows a player and no longer blinds them, and the fearless rung is still owed to the arena, which is where the story wants it. The Rider is left alone, because felling one of the Nine already pays five deeds and his page asks for him twice.

- **A page's quarry is on the ground when the page is taken.** A front's third page asks for wights or a mûmak, and those are a country rather than a view: a player who took the page in the West could spend an evening riding barrow downs that their world had not put within a day of them, and the errand the herald had just described read as a wild goose chase. Taking such a page now puts part of its quarry on the ground ahead, permanent and marked, with a line saying so - the same courtesy the campaign already does the Rider on the road, done for lesser quarry. What is put down is capped at three - which is the whole of the two wights and the one mûmak the pages ask for today, and only part of a longer hunt - and it is put down once, so a page taken twice does not double the mobs.

- **The Rider is not ended by dying.** His page asked for the Nazgûl once, so the one thing the whole campaign is built on - the kill that changes nothing - happened once, paid the page, and was never mentioned again; the second meeting, the one the act turns on, only ever existed in a sentence. The page asks for him twice now: fall him, and the campaign stands him up again on the road ahead once the player is away from where they met him, with a line saying he has lost nothing, and it is that second fall which settles the page. None of the body changes - the fight, the drop and the death message are the ordinary ones - because the lie the campaign tells is that all of it worked.

- **An operator can pay a book off without earning it.** `/tmquest <targets> complete [count]` settles what the targets are carrying exactly as a hand-in does: the standing, the reward, the courage deed, and a campaign page moving its front on. Three pages a front is a week of play, and a test does not need the week. It takes an optional count so a chain can be walked one page at a time, says plainly when there is nothing in hand to settle, and pays a fetch page without its goods being taken out of the pack, which is the one part of a hand-in a command cannot reproduce.

- **Every front ends on ground of its own.** A front was two soldier pages and then the Rider, so the three lines were the same two errands in three colours and the country a player had chosen never asked anything of them that the other two would not have. Each front carries a third page now: the West sends them to the barrow downs for the wights that have been opening the barrows from the inside, Harad to the deep sand for a mûmak out of its own host, and the East keeps the steward's scroll it already had. A page belongs to the campaign by what it asks for rather than by the kind of work it is, so a hunt of wights is not mistaken for the Rider's page - the campaign's own page is the one that names the Nazgûl - which would otherwise have ended the war on the first wight felled and left the three fronts standing unsettled behind it.

- **The book has a voice.** The ledger now sounds like the thing it is: a leaf turns under the story arrows and the two bookmarks, the covers open when the book is first shown and close when it is put away, a seal is pressed into wax when a giver's page is taken, and coins are counted out when a page is settled - where a villager's cheerful "yes" and the level-up chime used to stand in for both. The five effects are synthesised from noise and decaying oscillators rather than sampled, so they carry no licence, weigh five to nine kilobytes each, and are mono Ogg Vorbis at 44.1 kHz like the rest of the pack. A sound a screen plays has no place in the world, so the client looking at the page plays it, and each of the five carries a subtitle in English and Chinese.


- **The herald speaks.** His two moments have a voice now: the refusal that turns a stranger away, and the hand-over of a page. Both were read by a local voice model (Piper, `en_GB-alan-medium`) and are played at the herald's own position, so the words come from the body in front of the player; the chat line stays, because a player with the sound down, or reading in Chinese, still has to be told what happened. The lines are mono Ogg Vorbis at 44.1 kHz, and both carry a caption in English and Chinese.


- **A page is spoken, not only summarised.** The campaign script's best writing never reached the game: a page was a name, a place and a count, and a herald's whole part in it was five chat lines, so the conversation the West's line opens on - a Ranger's cold hall, a late scout, the horn - arrived as a notice. Taking a page from a herald now opens it as parchment and lets it speak: the speaker's name, the words, the two answers a player can give, his reply to whichever was given, and a row under it that puts the page away. The first page written is the West's first muster, and its words are the script's own. A page whose prose has not been written keeps the hand-over it had, because a screen with nothing on it is worse than a chat line: the guard is one set of page names, so giving a page a voice is naming it there and writing its keys. The words travel as keys rather than as text, so a page is read in the player's own language, and the name at the top is the name the body in front of the player is actually wearing, so the page cannot contradict its own nameplate.

- **`/tmcity relay` lays the city again where it already stands.** A corner is planted once and the bands
  walk up from it, so a piece re-cut after the city was laid is never laid at all - the counter is at the top
  and `band` has nothing left to give, which is what a re-lay after a cut change looked like. `relay` keeps
  the corner and the dimension and puts the counter back at the bottom, so one `/tmcity band 6` writes the
  amended pieces over the standing city.

- **`/tmcity summon` puts the whole city down in one word, in whatever world the caller stands in.** The
  city could only be raised by planting a corner and then walking the bands up, which is the pair for
  building it by hand but three commands and a remembered corner for anyone who just wants the build alone
  in an empty world. `summon` plants at the caller's feet and then lays every band and the shoulder, so a
  flat world can be turned into the build and nothing else.

- **`/tmcity folk` stands the city's people, and `summon` stands them as it lays the city.** The valley's
  people were only ever stood by the page that builds the valley as a place, so a city put down with
  `/tmcity` in a fresh country came out empty - no Elrond, no Legolas, no walkers. The three Elves of the
  line now stand at the marks the build carries, the fifteen unnamed walkers at theirs, and the rest of the
  crowd wherever the city has ground under it - all of it read out of the cut's own `rivendell.npcs` table
  rather than written down a second time in code, so a re-cut carries its people with it.

- **The shoulder of a city now says what it came to.** `/tmcity apron` answered only that it had run over
  four rings, which is the same answer whether it reshaped a hillside or moved nothing: it now counts the
  columns it cut down, the ones it built up, and the ones it left standing because they were more than six
  blocks off the city's base. Whether a city reads as a block cut out of the ground is now a number rather
  than something to squint at from across a valley.

- **Every command the pack registers is written down in one place.** `docs/commands.md` holds the five
  roots - `/tmcity`, `/tmquest`, `/tmplace`, `/tmfaction` with its two flat aliases, and `/tmcourage` - each
  hand's syntax and what it answers, who may run them, and, at the end, the walkthrough for standing
  Rivendell up in a world: the Arda cell and the corner that centres the footprint on it, the ceiling that
  decides how high the base may be, the band-by-band route, the shoulder and what its counts mean, and the
  re-cut loop.

- **Imladris is its own country: a hidden valley cut into the Trollshaws at the mountains' feet.** The city
  had nowhere of its own to stand - the ground under it was Trollshaws from edge to edge, drawn at 84 with
  sixteen blocks of relief, so a 554-block build dropped there either lost its crown to the ceiling or sat
  in a pit with a rock wall around it. Arda now lays an `imladris` floor at 74 with two blocks of relief
  across 640 by 580 blocks centred on the valley's own cell, blended back into the Trollshaws over a
  64-block rim, and the biome is what the books describe: oak and beech on the floor with pine holding the
  walls, elanor and fallen mallorn leaves, springs and ferns and loud water, elves rather than hill-trolls,
  and grass over mossy rock. The name `rivendell` is surveyed from the valley's middle now, so the place is
  written where the city goes rather than wherever the hills around it would take it - and a survey asks
  the ground what country it is in rather than asking the drawing, because a valley cut into the grid has
  no cell of its own to be found in. The command walkthrough plants from the valley floor now, rather
  than from the 72-block corner the Trollshaws' own ground needed.

### Fixed
- **The steed's fallen pose is read from the file the steed actually loads.** GeckoLib resolves the animal's
  animations from `special/horse`, so the `fallen` animation now lives there; a missing name left an empty
  timeline and crashed the client while it was drawing the body at the ruin.
- **A building the world raised before its keeper existed is dressed on the next visit.** A site is recorded as
  built once its blocks are down, and the laying is what placed the man at a camp, the animal at the ruin and the
  stone beside the door - so a world that had already raised Barachen's camp and the wood ruin kept the walls and
  nothing at them, and no later visit ran the code that dresses them. `Sites` now asks again on every approach; a
  keeper is still one a camp, a steed one a ruin, and a stone is never stood twice.
- **The keeper and the steed stand where the scout marked them.** Both were placed two blocks outside the
  building's door, which is the right answer for a camp nobody walked and the wrong one for a scout who has since
  put a block down: Barachen's place inside his own camp and the spot the black horse lies in are both marked, and
  read as pins (`<building>.keeper`, `<building>.body`) that the placement consumes.
- **A stone replaces a marker rather than being refused by it.** The milestone stood on open air over solid
  ground, so a scout's ammolite in the same cell read as a blocked spot; a marker is a placeholder, so the stone
  takes its block.
- **The valley's milestone is pinned.** It stands at `5499, 150, -3080`, marked inside the city once it was
  standing. The page-time hall stone is retired with it: nothing is planted when a page changes hands any more, so
  the stone at Rivendell is the city's own and cannot be laid twice.

### Removed
- **A road can be written down so every world generates it.** `RoadPaint` holds a line per road - block waypoints and
  a half-width - and `ArdaChunkGenerator` paints it over the drawn top as chunks are made, after the surface rules.
  It is a pure function of the column like the drawn rivers, so no chunk border splits it and no command has to be run
  in any world; water is skipped, since a crossing is a bridge's or a ford's business. The table starts empty: a
  section becomes a line once its markers are walked and its ground settled, and the two chapter roads already laid
  keep the pass until then, because a painted line cannot cut a shelf into a hillside.
- **The raised set follows the pins, so a scouted building no longer needs a page.** The auto-raised list was a
  hand-kept six, and a building outside it had no way into a world except a page that named it. `Sites` now raises
  every pin that names a template, from the pack's table and from the world's, and `SCENERY` keeps only what a pin
  cannot say - the man at a camp and the animal at the ruin. Chapter one is unchanged, all six of its builds being
  pinned and already on the list; the rule is what will carry chapter two's four and the last act's pair the day their
  pins are walked. Roles (`.keeper`, `.body`, `.end`, `.road`) and place names are skipped, and a page naming ground
  already standing is pointed at it rather than raising a copy.
- **A new pin moves a building that has not been raised yet.** A scenery site planted before its pin was walked kept the
  drawing's guess forever, so a scout who re-pinned Elladan's camp found nothing at the pin and a camp - or nothing -
  at the old spot. A pin now outranks a recorded spot: an unbuilt site whose centre is not the pin is re-seated from
  it. Ground already built is left where it stands, so a camp cannot be doubled.
- **A building the world says is standing is checked against the ground.** A site is written down once and trusted
  afterwards, which is right only while the world keeps its chunks: a save that keeps the site file and loses the
  region files loses the camp, and the world went on dressing a building that was not there instead of raising it.
  A scenery site is now proved against a sample of the blocks its own file holds, once a session, and one that is mostly
  gone is forgotten and built again at its pin.
- **A laid road can be read back out of a save as waypoints.** `tools/road_line.py` takes the pass's own surface names,
  groups the columns into the pieces a crossing splits them into, and prints a `RoadPaint.Line` every `--step` blocks
  with the height of the surface - the way a walked alignment is promoted to a generated one. Gravel and coarse dirt
  are shared with the terrain, so they are opt-in (`--wild`) rather than counted as road.
- **A generated road cuts its own shelf instead of only painting the ground.** Waypoints now carry the height walked at
  them, so a line has a grade of its own: the corridor is cut and filled to it - level across its width, a ramp along
  its length - and the road surface is laid at that height, with the carvers, the features and the structure sites all
  reading the same shelf. A road whose grade would stand at or under the waterline is left to the map, since crossing
  water is a ford's or a bridge's business.
- **A generated road's corridor is cleared with two blocks of verge either side.** A trunk one block off the pavement
  was keeping its trunk and losing only the crown that overhung the road, which left a bare pole at the verge; the
  cleared corridor is now the road's width plus two, and the verge is cleared without being paved.
- **A generated road keeps its corridor clear.** The road is painted before trees and grass are placed, so
  `RoadCorridor` clears it when a chunk holding road loads: leaves, logs, vines and replaceable plants above the road's
  own surface, with the road's block written back where a trunk took it. Chunk generation has no one place to answer
  for a neighbour's trees, and a finished chunk is the first moment the road and its cover both exist. Arda only, and
  safe to run again.
- **The door-and-road pair is written down as the marker convention.** Two ammolite blocks side by side are a
  doorway and the last block of the road that reaches it; a third and any after it are a body's feet. `AGENTS.md`
  and the pinning notes say so, with the keys a walked marker is pinned under (`<building>.end` included), and that
  a marker only exists in the world it was walked in - the coordinates are what travels.
- **Matuna keeps a lair of his own.** A fourth crag, `troll_cave_4`, cut by the same generator with its own seed,
  so the wood-troll's page no longer sends a player back into one of the three hills the stone's page has already
  emptied. It is raised by the page that names him, not by the world.
- **Barachen is at his fire.** The man minding Barachen's camp was one of the mod's folk wearing a drawn name; the
  camp named after a man now stands his body there, at the spot the scout marked.
- **The wood ruin's door faces north.** The scout's marker sits under the ruin's spruce door at 4787, 78, -3057
  with the road end one block outside it, so the gap opens north; the doorway table had taken the direction the
  road reached the wall by, which was west.

- **`Stones.nameLaid` is gone.** It named the stones a template had already laid, and nothing called it: no
  template the pack ships contains a milestone block, so there was never anything for it to name. A place's stone
  is the one `Sites` stands beside its door, or the valley's, which is pinned.

- Set the drape's ring back to thirty-two blocks: at sixty-four the pass reached past the branch the river
  leaves the city by and overwrote the water, and the river left the build. Thirty-two keeps the settled
  ring off the outflow.
- Spread the climb beside drawn water over a hundred and sixty blocks instead of ninety-six (`MOUTH_RUN`),
  with `WATER_REACH` out to a hundred and seventy-six so the reach still passes the climb: the massif's
  flank across the Bruinen stood at 319 within a hundred blocks of water at 74 and read as a wall, and
  now rises over nearly twice the ground.
- Hold the massif's line behind the city: `giveWay` opened the ground to any drawn river within a hundred
  blocks, including the Bruinen past the valley's east side, which brought the eastern flank down to 74
  and cut a valley the city has no mouth into. Only water on the branch's own side opens the massif now.
- Double the drape's reach from thirty-two blocks to sixty-four, so the ground is settled further out from
  the walls before it eases back to the drawn country and artifacts near the build fall inside the ring.
- Hold the spring in instead of letting it run out of the back of the build: water at the build's edge
  over `OUTLET_LIFT` above the floor is a spring, so the ground outside is banked to it and never cut,
  where it used to be left open and the water cut itself a false river down the flank. The city's
  outflow at the floor is still left open.
- Stop the drape leaving a pillar of ground wherever a pool reached a column's top: water out in the ring
  is a pool on the drawn ground and not the city's channel, so it is read down to the ground under it
  and cut with it instead of being skipped.
- Slope the river out of the city instead of dropping it at the wall: its surface fell the whole eight
  blocks to the vale at the first block of its course, which is a waterfall at the city's mouth. It now
  falls at most one block in twelve of run and meets the vale ninety-six blocks down the course.
- Stop the drape cutting the ground down to a deck: where the build's edge is floor tiles at 75 and the
  crown stands 243 above them, the outside ground was cut to 75 and the trench's face was the
  mountain's own shape. A boundary column topping out at or under 76 now takes the ground inside the
  box as its height and is filled, never cut - the wall the city is seen against is its roofline.
- Reach the valley's floor to the build's own outside edge: it stopped four blocks short, which left a
  strip of crown standing inside the footprint at 318 and read as a four-block wall at the river's
  mouth and again at the spring. The drape now takes that strip down on a world made before this,
  and counts a column as a watercourse only where water is its own top - water buried in the build,
  like the spring's aqueduct at 239, was leaving a hole in the draping in front of the wall.
- Open the ground round the Bruinen at one curve instead of two grades: the water's bank and the massif's
  cone met in a two-hundred-block seam just outside the west wall, because the branch opened the ground
  at 1.2 blocks per block and the cone cut it at 6, and the line where one gave way to the other was a
  face. `MOUTH_RUN = 96` climbs from the water to the crown in one smooth curve read from the middle of
  the channel, the drawn river's walk reaches 100 blocks and finds its edge a block at a time, and
  `MOUNTAIN_SLOPE` and `BRUINEN_BANK` are gone.
- **The massif stands at the roofline all round the city, and the range is a square.** What `giveWay`
  gives a river is a cone: rock no higher than the water's level plus `MOUNTAIN_SLOPE` blocks per block
  out. At six tenths that cone only reaches the crown (318 - 82) / 0.6 = 390 blocks from the water, so it
  lay over the massif's whole south side - read off the save, the south wall stands at 255 one block out
  and climbs back to 318 only a hundred and forty blocks from the build, and the north wall falls to 272
  and takes forty-five blocks to climb back. Six blocks per block out is a gorge wall's grade, near
  enough vertical: the rock returns to the crown within forty blocks of the river, so the channel keeps
  the slot it had and the mountains stand at 318 out to the walls. The shelf's own edge came in from 175
  to 96 blocks past the build, its fall from 340 to 200, and the ridge no longer swings in and out -
  `RIDGE_SWING` is read at nothing - so the range is a square rather than a ring.
- **A laying run that stops partway no longer has to be run twice.** The band mark was only written
  after the whole call, so a run that ended early left the mark at the band it started from: the world
  held bands the next call did not know about, and the pieces already down were laid again before it
  could get to the ones that were missing. The mark is now written as each band goes down, and each band
  logs `Rivendell: band N of M is down`, so a short run resumes at the band it reached and the log says
  where the last one stopped.

- **The map's ground is the ground, with no mountain left standing over it.** Arda cuts the noise's own
  terrain down to the height the drawing gives a column, but it did so by reading back the chunk's topmost
  solid block, and a heightmap that had not seen the noise yet answered with the valley floor: the column kept
  three hundred blocks of stone under a cap of grass at the ceiling, which is the flat-topped wall standing
  beside Rivendell. A column is now made the drawn one outright - air above its height, rock below - so no
  noise mountain can survive inside a valley.

- **The valley reaches the data as well as the code.** A country's biome and Arda's stem list are written by
  a data run, and neither had been written since the valley was added: the dimension still named thirty-five
  biomes and there was no `worldgen/biome/imladris.json`, so the registry had no such biome and the dimension
  failed to parse before a world could open. A data run writes both, the biome tags pick the valley up
  (forest, temperate, peaceful, and a new magical), and the source reads its biomes from the registry rather
  than from the stem's list - so a list that falls behind the map cannot close a world again.

- **The needle points at the body, not at the middle of the town.** A page that names somebody now
  points at the ground that body is standing on - read off him while he is out there, off the line's
  own stand points when he is not - instead of the middle of his place. Glorfindel's page led the
  needle into the Rivendell fountain, because the middle of the valley's square is a fountain.
- **"Speak with Glorfindel", not "speak with person:Glorfindel".** The prefix that says what a target
  is was being printed into the objective line.
- **Stones stay where buildings are.** A page of talk no longer plants a milestone where it sends the
  player, and a stone is only planted at the place a page was taken when that place is a room, so the
  line no longer scatters stones through camps and open ground.
- **The ford the beryl is carried to has its name back, and the page says how far off it is.** The
  crossing was catalogued as "The Ford", so a page that sent the player there read as an unnamed
  river crossing; it is the Ford of Bruinen now, the name Glorfindel's own words use. A page whose
  ground is far off also says the distance and the bearing once, beside the needle, since a line on a
  HUD gives a direction and no sense of how much road it is.
- **The cave search has a needle at last.** The crag the stone's cave page is asked in is pitched when
  the page is taken, and the campaign reads a pitched crag as a site - but the needle only looked at a
  site for the pages whose kind is ground, and a stone page asking for caves is neither ground nor a
  spot, so it resolved to nothing and the HUD went blank. It now points at the crag the page is being
  asked in, and says which way and how far, the same way it does for a named place.
- **The needle is looked at again instead of once.** It was worked out when a page was taken, handed in
  or read, and never again - but the ground a page names can appear after the page does: the cave page's
  crag is pitched once the page is in hand, and a place is named the first time somebody carries a page
  that names it. The clock now compares the mark with the one last sent and sends it when it differs, so
  a page cannot sit in the book with a direction that was never computed.
- **A finished page's leftover site no longer blocks the cave search.** A crag is the one thing the
  stone's cave page cannot be answered without, and the campaign refused to cut one while the player
  held a site of another kind - including ground belonging to a page already settled. A site nobody is
  walking to any more is ground the crag may have; one whose page is still carried is left alone.
- **The crag has its troll.** The stone's cave page is finished by killing the troll that keeps the
  chamber, and the crags the campaign cuts were empty - forty thousand blocks of rock apiece, no bodies
  and no spawner - so the search of the caves could not be paid at all. A keeper is stood at the bottom
  of the deepest chamber once the page is carried and the crag is built, named for the cave being asked,
  and never replaced: a kill at depth moves the page on to the next crag.
- **The needle follows the keeper into the chamber.** A cave page pointed at the middle of the crag, which
  is where the player walks in: once inside, the arrow was a bearing to the ground under their own feet.
  It points at the troll instead while he is standing, and says nothing at all inside a chamber that is
  empty, since the page asks for a kill at depth rather than a walk. The next asking brings it back.
- **The cave search pays mithril, not a keepsake.** Bilbo's Nugget is a cosmetic token carried by another
  page and worth nothing to a smith, so the end of the search now hands over four Mithril Nuggets instead.
- **The needle knows the Wood-troll's lair.** Matuna's page names a creature rather than a kind of mob, so
  it had no place, no spot and no person for the needle to point at and sent nothing at all - the one page
  whose whole errand is a walk into the wood. It now points at the lair the campaign pitched when the page
  was taken, and follows the creature himself once he is standing.
- **The valley keeps one of each Elf.** Rivendell's square was read by counting the bodies carrying the
  valley's tag, which kept a visit from standing a seventh and never dismissed a spare: a world visited
  many times carried close to a dozen of each name. Each of the six now has a slot on its tag, the first
  body wearing a slot stays, every other is sent on, and a missing one is stood again where it walks.
- **A page's question mark sits over the body it is owed to, and no other.** Elrond wore the West's giver
  clothes and a `campaign_giver` tag, so the nameplate read him as the front's herald and marked any West
  story page as his business - a gold `?` over Elrond for a Wood-troll kill that is Legolas' to take, on
  the one body that could only answer "that one belongs to Legolas". The book now marks a body by name
  for the page it is owed, and a speaker of the line is no longer offered work in a herald's name.
- **And the body holding the next page keeps its mark.** A speaker answers a herald's right-click and
  wears the herald's clothes, so the plate now asks the line who holds the page rather than asking the
  clothes: Legolas keeps his `!` while the departure page is his to hand, and no other Elf has one.
- **The East and the Harad leave the chain until their lines are written.** Their pages are still in the
  notes and their halls and heralds still stand, but neither front hands anything: `PAGE_LINES` holds the
  West's line alone, `FRONTS` is what the chain, the herald marks and the end-of-line sentence all read,
  and the next chapter's road - the Rider, the castle, the crown - is closed by name, since the old
  all-three-settled gate would otherwise have sprung the moment chapter one ended. A player who walks
  the West's last page is told the road has been written no further, instead of being sent to two fronts
  that cannot be walked.
- **The north's ground is named for chapter two.** *Last Refuge*'s places are in the catalogue: Glóin's camp
  at the east end of the pass, Gundabad and the vault on its eastern road (all Misty Mountain ground), the
  key-stone's vault at the source of the Bruinen in the Trollshaws, and Carn Dûm in Angmar - so a page of the
  next chapter can name its ground and have it placed where the books put it. `docs/chapter-two-last-refuge.md`
  maps the notes' eleven pages to their givers, quarries and ground, and names the two decisions the chapter
  still needs: which build stands for Gundabad, and what the notes' Dragon Wight is made of.
- **Chapter two opens at Glóin's camp.** The old world's dwarf camp is in the pack as a captured structure
  and is raised in the Misty Mountains when Elrond hands the new page over, so the road north ends at a fire
  that is already standing. Glóin is stood at its door - a human model at a dwarf's height, wearing
  `gloin.png` - and the page pays what the notes put on it: the dwarves' thanks, five silver and thirty-three
  bronze. Two fixes came with him: the two blocks the capture used to mark its door and front post are plain
  earth now, and a giver's name is finally flattened before it is looked for as a file, so `Glóin` asks for
  `gloin.png` instead of `glin.png` (the same was true of every accented name in the pack).
- **Glóin's camp stands at the east end of the High Pass.** First in the mountains and then below Caradhras,
  and neither was where the old world built it: the notes put the camp north of the valley, at the ground the
  High Pass drops into, which the drawing names Gladden. It is canon'd there and pinned to the pass's mouth,
  and the place line and the page's wording name the High Pass again.
- **A tool for taking a build out of a world.** `tools/grab_build.py` reads a saved world's region files, or
  an existing structure file, and cuts the build into 48-cube templates with a `.parts` table of the offsets
  they stand at - which is what a large build needs, since neither a structure block nor `Sites` will take
  one piece bigger than that. It reports the block palette before writing anything, so a name this pack no
  longer has shows up as what it will become: air. Cutting the camp and reading it back gives the same 4,928
  blocks; the world-reading half has no world in this checkout to be tested against yet.
- **The Trollshaws' data is generated data again.** The biome was added to the generator and its tag list, but the files in the tree were a hand-written copy of what that generator writes, so the country was in the code and in none of the data: an old tree feature the game had dropped, and no tags at all, because the tag files were last written before the country existed. A data run writes both properly - the wood is plains oak and beech with birch on the lower ground and the uplands' pines above them, ferns, lichen, gravel, clay and sand where the rock shows, boulders, stumps and fallen timber - and the country is picked up by `overworld`, `forest`, `temperate`, `hostile` and `windswept` from the tags on the biome itself rather than being named in any of them. Nothing is lost by it: the run rewrote the two language files and both came back with the same 2187 keys.
- **Arda's map and the dimension name the same biomes again.** The Trollshaws were added to the map's countries but not to the dimension's biome list, which is written beside it in the map's own order - seas, the north and the seaboard, the north-west, the Misty Mountains and the countries either side of them. The source compares the two counts as it is built and refuses a world where they differ, so the dimension failed to parse before a map could open. It is named between Firien and Lothlorien, where the map draws it.
- **The Trollshaws no longer stop a world loading.** The biome's wood was drawn with `minecraft:trees_birch_and_oak`, which 26.1 took out of the game; the biome file was a hand-copied list stood beside the code that generates it, and its copy had not been regenerated since. A feature name nothing defines leaves the placed-feature registry unbound, and the registry is loaded before any world is, so the whole pack failed at the load screen rather than in the wood. Every other feature the biomes name was checked against the game's own registry beside it; this was the only name in the pack that had gone.
- **A building the campaign raises sits in its ground rather than on a plinth of it.** A site was seated on the highest column under its own footprint and filled downward from there, so even ground chosen for being the flattest available still left the build standing as far above the world as the footprint's own fall-away, on a bare square wall of its own dirt with the sill a good way up in the air. A site is now set half that drop into the ground, four blocks at the most, so the ground comes back up around its foot and the slope shows through the bottom of the building instead of standing beside it. Flat ground is untouched by it: a site with nothing falling away beneath it has nothing to sink into, and keeps the seat it always had.

- **Angmar has a country of its own.** The northern barrow country was standing in for it, which made the Witch-king's realm the Barrow Downs with another name. It now has its own biome: a subarctic rocky upland, below freezing so its weather falls as snow, with thin cold soil over broken stone, grass only in the hollows, boulders and rubble, deadwood, stumps and brambles, wind-beaten spruces, a far grey fog, and a spawn list of orcs, uruks, wargs, wolves and crebain. The map's Angmar country, the fortress and the arena all name it, so the ground matches the country that was drawn.

- **The fortress and the arena say Angmar in the data too.** Both were seated in Angmar by the canon table, but their structure definitions still named Mordor, which is a claim nobody reads any more and everybody would have believed. Their biome list is now Angmar's, and the two generated `has_structure` tags were brought into line with it rather than left to a datagen run.

- **The last act happens in Angmar.** Weathertop was the first landmark to be seated by the map rather than by whoever took the page; the Witch-king's fortress and the arena beneath it are now seated the same way, in the country the books give them. Both were written into the canon table, which means they are surveyed from the middle of Angmar and may drift two map cells from it, so the campaign's finale stands in the same part of the world for every world and every hall - and, as before, a castle page already in hand is re-surveyed rather than kept. The arena also gained the id the table needs, so a location page can name it directly.

- **A landmark may drift, but only within its own country.** Asking the map for the middle of Eriador puts Weathertop in the right part of the world, but the middle of a country is not necessarily ground a hundred-and-twenty-block building will take, and a survey that refuses there falls back to pitching the site in front of the player again. A landmark's search is now held to its own country within two map cells of that middle - so it can take the cell next door when the exact one is a river or a slope, and cannot wander off into whatever else the map draws - and only widens past its country if nowhere within that reach will take it. Camps and the other non-canonical builds are unaffected.

- **A landmark stands where the map puts it, and a page is a walk.** Two faults put Weathertop beside the herald's own hall. The survey begins in the cell the player is standing in, so a structure whose biome list fits the country they are already in was seated at their feet - the site came out at the hall's own coordinates, and the log then refused to build it there. Sites now keep a floor distance of 192 blocks from the page-taker. Weathertop was the other half of the problem: it is Amon Sul, a hill in the heart of Eriador, and it should not depend at all on where the page was taken, so the landmarks the books place are surveyed from `ArdaMap.anchor`, the middle of the cells the drawing gives their country. A site of one is surveyed again even when a page to it is already waiting, which costs nothing - the answer is the same spot for every player - and corrects a site pitched before the map was asked where the landmark belonged. Camps and the fortress keep their biome lists and are still looked for around the player.

- **A hilltop in the trees can be seated.** Site ground was read off the motion-blocking heightmap, and the top of the world in a wood is a trunk: a 121-wide footprint under a Mirkwood canopy measured as a cliff from trunk to grass, so every candidate spot was refused and the page waited on a hilltop that was never going to stand (the log said only "No ground within 48 blocks"). The read now walks down past logs and leaves to the ground the column is actually made of. The flatness rule also no longer refuses outright: it still prefers ground that is level within tolerance and returns the first such spot it meets, but when there is none it keeps the gentlest ground it saw and builds there - standing on the highest column and letting the hill show through what the building left empty, which is the same bargain a build struck into terrain already makes. The fill under the floor now reaches as far down as the ground fell away, up to twenty-four layers, so a building on a bank gets a bank under it instead of six layers of dirt and an air gap.

- **Everything the mod puts down fills under itself, found or forced.** Two mechanisms, one rule. A structure the world generates is bearded: the game fills the ground beneath the piece up to the terrain around it, and every generated structure the mod ships already asks for `beard_thin` - the provider now says so for every surface structure rather than skipping the ones given a top offset or a companion, so a re-run of the data cannot drop the fill under the pieces that stand highest. Underground pieces keep `none`, since there is nothing beneath them to fill. Ground the campaign raises itself is not bearded by anyone - it is placed block by block after the world is generated - so it fills its own: every column of the footprint is put down to the first solid block below, six layers at most, which is what stopped the tent standing over the sea.

- **Structure void is not a hole: it is a block.** Every template the campaign has raised until now cleared its footprint first, and the hundred-block hilltop cut for Weathertop was going to carry structure void through its open sky so the ground it landed on would show. Neither was needed, and the void was worse than unneeded: this version's template placement does not skip it - nothing in `StructureTemplate` or `StructurePlaceSettings` mentions the block at all - and `StructureVoidBlock` is an invisible six-pixel cube, so a template full of it raises an invisible lattice wherever it is placed. That is what was showing up in the earlier converted builds. Empty cells in a template already mean "leave what is here alone" (the tent records air and so still clears its own box; the castle records nothing and never touched the ground), so the Weathertop template records no air at all, the box clearing is gone rather than special-cased, and `WorldScan` gained a `stripvoid` mode which has been run over the older converted templates to take the lattice back out of them.

- **A site a hundred blocks across can be raised without dropping half of it.** Three things assumed a tent. The build only asked whether the chunk at the site's middle was loaded, and a block written into an unloaded chunk is quietly discarded - so anything wider than a chunk would have been built in pieces, or in one piece and a blank. The footprint was cleared box-and-all before the template went down, which for a build carrying structure void across its open sky would have cut a crater the size of the building first; only the cells the template actually fills are cleared now, and its own air does the rest. And the search for ground stepped by half the building's width, which for a hilltop a hundred wide meant one candidate and no second chance, so the step is a quarter of it and the reach grows with the build.

- **A walk the world had not named is named by the map instead of refused.** The herald hands out walk pages only where a place has been placed in the world, and a placement has only ever been made by an operator standing at the spot - so on a world where nobody had done that, the West's line stopped dead at Weathertop and stayed there, with the book saying the ground had not been named yet and nothing anywhere to say how it ever would be. The map already draws every name in the catalogue, so a walk whose ground is unplaced now asks it where that place would stand, writes the answer down as a placement, and hands the page; the banner fires on arrival as it does at a hand-placed one. A place an operator has placed is never moved.

- **A site stands on ground rather than over a hole.** A building is set at the highest column under it, so on a bank or a shoreline the lower side of it was left floating with air or sea showing under a wall. Every column of the footprint is now filled back up from the building's own base to the first solid block, the way a village does it, for a few layers at most - so the tent on the beach in the test sits on earth, and nothing gets a plinth hung off a cliff.

- **A site the ground refuses is no longer silent.** Real ground was looked for within eight blocks of the spot the map picked, and if none of it would take the building nothing was built and nothing was said - so a player could stand on the printed point, watch nothing happen, and have no way to tell a hard spot from a broken page. The search now reaches forty-eight blocks, which is the order of the drift between the drawing and the world, and a site that still will not stand is written to the log once rather than swallowed.

- **Walking through a leaf pile no longer crashes the game.** The pile asked the client for a tinted falling leaf, handing the particle engine a colour to go with a particle type that was registered without one; the engine casts what it is given back to its own type, and that cast brought the game down the first time a foot went through a pile. The pile and the pack it sends for a client both ask for the plain maple leaf now, which is the same leaf the biome's own wind already drops.

- **A page in the book can be read to the end.** The leaves were narrow enough that an errand came out as "Reach somewhe..." and the line at the foot as "Hand a finished quest to...", which left the player holding work they could not name. The book is wider, the ask wraps onto a second line where the row has the height for one, the count has moved up beside the giver's name to free the foot of the row, and the foot hint wraps instead of being cut. Four errands still fit one leaf, because the room the hint needs is measured before the rows are sized rather than taken out of them.

- **`/tmquest page` can be run at all.** The branch was written to hang off the command root, but its brackets left it nested inside the target selector, so the literal was read as a player's name and the page was never built. It is a sibling of the selector now and writes the page into the book of whoever ran it.

- **Turning the story leaf crashed the game.** The line that says what a page pays handed the translation a string builder, which is not one of the things a translatable argument may be, and the game took the exception rather than the page: a player turning to the East's steward page - the one that wants a scroll carried and pays nothing in standing or goods - was looking at a crash before they could read it. It passes the words now, and a page that pays nothing in goods draws no reward line at all instead of announcing a reward of nothing.

- **The Rider is met on the road, not at the counter.** Taking the campaign's last page at a hall's bench stood the Nazgûl up ten to fifteen blocks from the player at the moment of the hand-over - on the hall's roof or in its yard, beside the body who had just written the page - which read as a conjuring trick rather than the meeting the page promises; and the body that was stood up was remembered only in the running session, so a reloaded world forgot him, and a player carrying the page in a country he had since left could be left with a hunt that had nothing left to hunt. He is owed the road now: the campaign remembers where the page was taken and stands him up once the player is out under open sky and a ride away from that door, a little ahead of them on the ground, with a line in dark purple saying that something is waiting; the same walk brings him back after a reload, because a Rider is found by the tag he wears before a second one is ever stood up. A player who has already felled him is not met by another, since the kill is counted while the page is still carried and a finished page is what tells the two apart.

- **A hall's giver says what he is, and what he has.** His nameplate carried his name and nothing else: no office, and no mark unless the player had already reached the rank of stranger with his country - while he hands his page over to anyone who asks, so a player standing in front of a body holding work was told by the plate that there was nothing there, and the page he was given a moment later proved it wrong. He now wears his office beside his name in the grey of a subtitle - Maelgrin, Herald of the West - and the mark and the page now answer to one rule instead of two: the gold **!** of work on offer appears only to a player the country will actually deal with, and the herald refuses the page below that same rank instead of handing it to someone his own plate had told there was nothing here. The grey **!** of work in hand and the **?** of work to hand in still appear to whoever carries his page, whatever their rank, and a hand-in is never refused. The mark still stops when his front has nothing left to give, and it is read from two places rather than one: the quest sync, and the book the player is holding, so a page taken before the sync carried a giver's id still marks the body that handed it over, and a page of a front marks that front's herald even when it remembers no id at all - the gold **!** of "work to take" standing over a body whose page the player is already carrying was the reading that could never be true. The office line no longer depends on the alliance the clothes are copied into either - a hall placed from a template can reach a client with that copy still empty, and a herald read as an ordinary man lost his office while keeping the gold name the server had given him - so a body named for a front is recognised by the name he wears as well.

- **The story leaf turns.** A player can carry a page from each of the three fronts at once, and the leaf drew whichever of them came first in the book and then stopped - the other two were in hand with no arrow, no count and nothing saying they existed, so a campaign in progress read as a single page. The heading now carries **<** 2 / 3 **>**: the arrows turn to the next front's page, the count says how many are open, and an end dims rather than vanishing so the count always reads true. The bounties leaf, which answered a player holding three of the campaign's own pages with "no bounty in hand - a giver out there has work", now says where those pages are.

- **Stranger was written in ink that could not be read.** The rank's colour was near-white (0xD8D8D8) in a book drawn in dark ink on parchment (0xFFE9D8B4), so the rung a player had just been told they had not reached was the one line of the ledger they could not read. It is slate now, and the chat colour is left alone - a name in chat is written on the dark, not on paper.

- **The story leaf reads its page whole.** The step's name and the place it happens were drawn as one line each, so a heading that fitted was followed by "Slay 12 soldiers of H..." and "the Green Way, where the..." - the two things that say what the page is and where it is, both cut off at the width of the leaf. The objective and the place now wrap onto a second line the way the prose under them already did, and the summary gives up one of its lines to make the room.

- **A page of the campaign says what it is.** Six of the seven soldier pages carried no name and no place, so the story leaf drew the objective alone and the gold heading a campaign step is supposed to have never appeared - the page read as one more errand with a name under it. Each of them is named now - the first muster, the long road south, the watch on the marches, the second levy, the sun on the sand, the gathering at the ford - with the ground it happens on written under it, translated into Chinese beside the English.

- **A hall's giver keeps the hall.** The herald stood at his bench with his brain on, so he pathed to the door, walked out of it and was next seen somewhere in the country; a player who came back to a hall they had already met found an empty room and concluded the hall had never been staffed. The home area the hall wrote into him was a list of ints where the game reads a block position back, so it never loaded and nothing was holding him; he is planted the way the warden and the envoy at their benches already are, in the template and on load alike, so he holds the spot the hall put him in - and he turns slowly to whoever comes through the door, a giver who cannot leave but is not a statue either.

- **A hall's giver is named in gold.** His name was written as the plain white text a template holds, so the one body in a hall carrying the campaign's pages read like any nameless traveller. He wears the front's gold now, in a hall just cut and in one already standing in a saved world, which is the same gold the mark over his head is drawn in.

- **The alliance guard's switch switches the guard off.** Its entity id was written with a leading space, so the switch never resolved to anything the game has - it logged that it named no such mob and left the guard spawning with the switch off. The id names only the mob now.

- **A front moves on when its page is paid.** The campaign noted the page it handed over at no progress, and then looked for that exact record when the page was settled - but a settled page has been advanced by every blow the player landed, so the records never matched and the herald handed the same page out again for as long as the player kept finishing it. Pages are now recognised by their terms - what they ask for, of whom, and how many - so the first page paid moves the front to the next one.

- **The campaign's own page is recognised while it is being handed over.** Whether a page was the Rider's was read from what the front had already noted as handed over, which is nothing at the moment he is handed over, so handing him out never spawned the Rider he asks for. It is read from what the page asks for instead, which is the Nazgûl, so the Rider is stood up when the page is taken and a second herald will not write a second copy of him.

- **One page per front, whoever hands it over.** A second herald of the same alliance - an old hall beside a new one, or a hall beside a giver stood up by the command - used to hand over a second copy of the step already in the book. Only the copy the front noted last could ever be paid, so the front could not move on at all. A herald whose front is already being carried now says so instead of writing it again.

- **A new world starts with no quests in it.** A world's campaign and ledger are held in maps that live as long as the game does, and only a world with a save file put them right: a fresh world opened in the same session was handed the last world's stages, its book and its giver clock, so a brand new player opened a book full of other worlds' work. A world with no file now starts those maps empty.

- **A campaign giver says when there is nowhere to stand.** The command had two answers, and the one for "a giver is already standing here" was also the one for "there is no ground within reach", so it claimed to have found the giver it had failed to place. There are three answers now, and the third names the ground as the problem.

- **Halls cut before the givers were named name their herald.** An old hall's "Herald of the West" is still the body that hands out that front's pages, so on load he is given the name the book writes down - Maelgrin, Guthlac or Zarabi - unless a pack has named him something of its own.

- **A campaign giver is stood on ground, not on the player.** `/tmquest <targets> campaign_giver <alliance>` dropped the NPC in the very block the operator stood in, so it rose inside them and could land in a wall, a river or the air; it now looks for solid dry ground a few blocks off, turns to face them, and reports how many actually stood up. The alliance it is handed is checked against the three real ids too - `Alliance.byId` answers the West for anything it does not recognise, so a typo quietly dressed Maelgrin and reported success - and the lookup for a giver already standing no longer walks every entity in the level. Both answers are translated now instead of being English literals written into the command.

- **The giver skins load.** A texture id on this version is the pack path itself, extension included - which is why the human variants are built as human1.png - so the three alliance skins asked the pack for a file with no extension at all, were reported missing and were drawn as the magenta checker. They name their .png now, and the renderer still falls back to the human's own clothes if a pack has no such art.

- **A giver without its art keeps its own clothes.** The renderer now asks the pack whether the alliance skin is really there, once per file per reload, before drawing it: a missing file leaves an ordinary human in the clothes of its own folk instead of the magenta missing-texture checker, and a single line in the log names the file that was looked for.

- **Two item models stop grumbling in the log.** The tobacco pipe and the fancy bottle were the only element-drawn models with no `particle` texture named, which the client warns about every time it loads models; both name one now, taken from the art they already draw with. Nothing about how they look changes.

- **The giver skins are in the same pixel format as the rest of the mod's art.** All three were saved as plain 8-bit RGBA, as every other skin in the mod is, instead of one paletted map and one image with no alpha channel - the client's own PNG reader, not Java's, is what has to take them, and this gives it the only form it has never had trouble with.

- **Quest givers can no longer be killed.** A giver is made invulnerable and persistent on its way into the world, so a mob that wanders into the scene cannot end a quest by killing the one thing that can hand it in - and givers already standing in a saved world are protected on their next load.

- **Sleepnesia keeps every sleeper's sleep to themselves.** An effect is one object shared by everything holding it, and the draught kept its slumber in a field of that object: the first sleeper in a world put everyone who came after them to bed for good, and nobody stood up again. Whether a sleeper is still under is now read from their own instance of the effect, they are stood up a few ticks before it lapses - once it has gone there is no tick left to do it - and only a player is laid down, so a boss handing the draught to something that is not a player no longer breaks on the cast. The movement modifier it added and never reliably took away is gone with it; the slowness and blindness it already applied are what keep a sleeper still.

- **The blessing of Eru cures what it is meant to cure.** It is an instant effect, and an instant effect is never ticked, so the cleansing written as a tick never ran at all - whatever the blessing was drunk or eaten from did nothing. The cure happens where the game applies an instant effect now, and it takes only harmful effects: the old test compared an effect instance against the category enum, which is never equal, so the draught was also stripping every buff its drinker carried, miruvor and elven vitality among them.

- **The book reads without its shadow.** Every line of the ledger is now drawn flat instead of with the drop shadow vanilla puts behind text. A shadow is drawn for the dark of the world behind a nameplate; on parchment it sat under dark ink on a light page and made names and numbers look smudged. The dim, gold and blue inks are a shade darker to make up the contrast the shadow was adding.

- **A new world no longer freezes on the way in.** Angmar's biome was registered and named by the tags, but its definition was never written out: every other biome has a file under `worldgen/biome/`, and the one key with nothing behind it made the biome registry refuse to freeze, which crashed the client while a world was loading. The data generator wrote `angmar.json`, and with it the biome list in `dimension/arda.json` that Arda's biome source counts itself against - the line whose absence would have failed the dimension the same way.

- **A building that is already standing is not raised a second time.** Taking the page again - a campaign
  walked through twice, or a second player sent to the same landmark - pitched the site afresh, and the
  survey put it back on ground its own first copy had already changed, so the crag went up beside the one
  already there. A built site now answers every page that names it, and a landmark stands once in a world
  rather than once for each player sent to it.

- **Gimli is stood at the hold he keeps.** The reach that decides when a keeper is put down was written for a
  camp, and it was measured from the middle of the building: Gundabad is a hundred and twenty by a hundred
  and eighty, so its own door is a hundred blocks from its middle and a player standing in the hall was
  never close enough for the dwarf to be stood. The reach now grows with the building, and the search for
  one already standing uses the same distance.

- **A city taller than the sky is refused out loud.** The world drops a block above its own ceiling without a
  word, so planting Rivendell on Arda's surface laid the top two of its six bands into nothing and left its
  towers cut off at the build limit, with the command reporting them as laid. `origin` now says how far over
  the sky the city would stand and the highest y the corner can be planted at, and `band` leaves those pieces
  out by name instead of counting them.

- **`/tmcity band` says which bands it laid, not how far up the city it has got.** Its count is how many bands
  to lay, and it was reported as "band 3 of 6" whatever number was passed: a caller reading the count as "which
  band" typed 1, 2, 3, laid six bands in three calls, and then found the last three had nothing to do because
  the city was already whole. The line now names the bands it laid ("bands 1-6 of 6 down"), counts what is
  still waiting ("3 bands to go"), and says plainly when there is nothing left.

- **A piece of the city keeps its own size, so a cut whose box is not a multiple of 48 lays whole.** Every
  piece was taken for a 48 cube, which is true of most of a cut and not of the slivers along its edges: the
  top band of the new Rivendell cut is four blocks tall, and a sky check that added 47 to its bottom refused
  the band as though it would reach past the ceiling.

- **A world made before the valley no longer refuses to open, and gains the biome.** Arda's biome list is
  stored with the world, and the valley added a thirty-sixth entry to a saved thirty-five, so the dimension
  aborted on load with "the two lists are written together and cannot be edited apart". The source no
  longer reads the world's copy at all: it names its biomes as ids and resolves every one of them from the
  registry, so a world made earlier opens with the real imladris biome rather than a stand-in, and its next
  save writes the full list.
### Added

- **The campaign has an ending: the Rider is everybody's page.** A front is two soldier pages now, and the Nazgûl is not the last of them - all three countries ask for him together once every line is settled, any herald hands him over, and all three pay the 200 standing between them, since he was feared by all and sent after by none of them alone. Settling him writes the war's end into the quest book's story leaf, and a herald with no page left says where the war stands instead of idling.

- **The herald speaks when handing over the Rider page.** When a herald gives the player the campaign's final page - the hunt for the Nazgûl - he says one line in gold before the book takes it: "All three fronts are settled. The Rider is yours." The same sentence is shared by all three heralds, and it is translated into Chinese as well.

- **The Rider is spawned by the campaign and outlives his corpse.** When a player accepts the Rider page from a herald, a campaign-spawned Nazgûl appears 10-15 blocks away, tagged as the campaign's Rider for that player. The Rider can be killed normally - he takes damage, drops loot, and shows a death message - but the campaign remembers that this was the campaign's Rider, not a random Nazgûl. When the Rider dies, he is removed from the tracking map, and can be respawned fresh for the next stage. This implements the campaign's narrative that the Rider cannot be truly killed: the player sees him die, but he returns unharmed on the next page.

- **Campaign pages can now differ per alliance.** The campaign's page line was a single list shared by all three fronts; it is now a map of alliance to page list, so each front can have its own sequence of pages. The East front now has a third page - a FETCH of the Witch-king's scroll, one of the mod's `item_scroll` - standing in for the campaign's page 9, "The Steward of the South". It is handed over and handed in like any other page and pays the act's turn; the arena the scroll names is neither placed nor revealed yet, and where the scroll is taken from is not yet pinned to a village. This infrastructure enables the rest of the campaign's staged pages to be added as fixed quests at known locations rather than random errands.

- **Named campaign givers can be spawned at faction halls.** A new system allows named NPCs like Maelgrin (West), Guthlac (East), and Zarabi (Harad) to be spawned at their respective faction halls. These givers carry the `tolkienmobs:campaign_giver` tag and are automatically named according to their alliance. The `ensureCampaignGiver` method checks if a campaign giver already exists nearby before spawning a new one, preventing duplicates. A new command `/tmquest <targets> campaign_giver <alliance>` allows operators to spawn these givers manually for testing or staging. This infrastructure enables the campaign's staged pages to be handed out by fixed NPCs at known locations rather than random travellers.

- **A third page on every front: the Rider.** A front no longer only counts soldiers. Page 0 asks for six of one rival's, page 1 for twelve of the other's, and page 2 asks for one Nazgûl for two hundred standing, so the line changes kind as it goes. Standing is now paid for any kind of page rather than faction work alone, which is what lets a story page be a hunt, and the Rider is named from the registry so a page can never ask for a mob this world does not have.

- **A second page on every front, and the campaign line is a table now.** Page 0 asks for six soldiers of one rival army for sixty standing; page 1 asks for twelve of the other rival's for a hundred and twenty. `Campaign.PAGE_GOALS` is the line, so a front has as many pages as that list is long and adding one is a single number. The ledger's courage row also sits with the countries now, one row-spacing under Harad, instead of pinned to the foot of the leaf.

- **A giver turns away a player who is already carrying all the work they can hold.** Right-clicking a giver with a full book of errands now answers "You look too busy to help right now." instead of silently refusing the new work. Handing in a finished quest and taking the herald campaign page still work at any fullness, since neither is an errand competing for a row.

- **The quest book has a bounties leaf and a story leaf.** The quest bookmark no longer splits the work down the middle of the spread and hides whatever did not fit: **Bounties** holds the errands, hard-limited to what one leaf was measured to show, and **Story** holds the campaign step alone, with the room the other entries took given over to its description. A campaign page is exempt from the bounty cap, so a book full of errands never blocks the mainline.

- **A campaign stage counter, and the herald has a page.** faction/Campaign keeps how far each player has got on each of the three fronts, saved with the world and synced to its owner. Page 0 of every front is written - six soldiers of a rival army for sixty standing - and the hall herald hands it over directly rather than leaving it as an offer to browse. The front only moves on when the ledger settles that exact page, so a page dropped or cleared from the book leaves the front where it was, and the mark over his head stops once a front has nothing left to give.

- **The herald's gate is cut.** His gold mark is drawn only for a player who is at least a **stranger** to his alliance - the first rank above outsider, 100 points of ordinary hall work - and a mark for anyone below is dropped, so the hall's herald keeps a bare name until the country knows you. The mark stands with no page behind it yet, which is the campaign saying it has somewhere to begin and not yet what; until a page is written, right-clicking him answers that there is no work.

- **The hall staff move, a little.** The warden and the envoy are frozen at their tables on purpose, so their life is drawn rather than walked: each turns slowly about the place it was set and lifts its head from the work now and then, and turns to look at a player who comes up to the counter before settling back to the work, in entity/util/TableIdle. It needs no new tag - the hall tags they already carry, and having no brain, are what make them staff - so a hall placed earlier comes alive without being cut again.

- **A hall has a herald.** The third body the campaign wanted stands in every alliance hall now, at the workbench the warden does not use, cut into the structure beside the warden and the envoy. He is a human in all three halls, because the skin he wears is the quest-giver art of his alliance, and he carries the giver, the alliance and the campaign tags, so his clothes and his gold mark are right from the first load. He walks the room rather than standing on it: the hall gives him a home area six blocks out from the middle, and a walk target that would leave it is pulled back inside, so a hall looks lived in without the herald wandering off. He has nothing to hand out yet - the pages and the rank that opens them are still owed - so he walks it silently, and turns to face a player who comes within five blocks of him. A herald already standing in a hall placed before this walks too: the join repair gives an alliance-tagged giver its brain back and a home area if it has none, so an older hall comes alive without being cut again.

- **The giver who stands in a hall is dressed for it.** The one body in each hall wears a skin of its own, one per alliance - `faction_west/quest_giver_west.png`, `faction_east/quest_giver_east.png` and `faction_harad/quest_giver_harad.png` - chosen by the scoreboard tag that names the alliance it speaks for. A giver met out in the world carries no such tag and keeps the clothes of its own folk, because the people who walk a country are ordinary people. All three are human-sized, so any race the mod ships can be pointed at a file.

- **A giver shows what it has for you, and which line it belongs to.** The mark over a quest giver is three states now instead of one: an exclamation mark when there is work here you have not taken, the same mark dimmed grey while you carry work from here and have not finished it, and a question mark when it is done and can be handed in. The ink is the line - blue for an ordinary errand, gold for the mainline - so the campaign's own business stands apart from a farmer's fence at a glance, and a giver is the mainline's by carrying the `tolkienmobs:campaign` scoreboard tag rather than by anything about the quest it holds. It is drawn onto the nameplate, so it costs no texture, no second render pass and no per-tick work, and it is read per player from the quest sync: the same giver can be a gold mark to one player and a bare name to the next. For the mark to follow the right body, a quest now remembers the id of the NPC that handed it out rather than only the name it was given; work already in a book carries no id and simply marks nobody.

- **The war's three acts are written down.** `docs/war-of-the-three-fronts.md` holds the West's questline page by page in the older quest-sheet style - who speaks, what they say, the ground the errand happens on, what it pays, and which of the mod's quest kinds can express it today. The Rider is killable and comes back, because a Nazgul that cannot be hurt reads as a bug while one that dies and returns tells the truth. A closing section lists what a line for the East or Harad would change, so the other two fronts are a rewrite rather than a redesign.

- **The fortress and the arena are structures of the mod now.** The Witch-king's fortress and the arena beneath it are registered builds, so either can be put down without carrying a template about: `/place structure tolkienmobs:boss_castle` and `/place structure tolkienmobs:boss_arena`, both cut from Mordor's own stone. Neither is drawn into a world by the generator - where they stand is the campaign's business - so Mordor is not littered with fortresses waiting to be found; giving one a structure set later is a one-line change.

- **Courage is earned, and the dark learns to let go of a player who has.** A player now holds a courage of their own, counted in deeds and read at the foot of the ledger's alliances leaf beneath the war horn that answers fear. Work handed in is a deed, standing in terror while it lands is a deed - once a minute at most, so the endurance is the deed and not the ticking - and felling one of the Nine or their master is worth several. The ladder is six rungs long and each rung takes exactly one thing from the terror: first the legs are given back at the price of a heavy, noticeable slowness, then the blindness, then the weakness, and from Fearless up the crippling terror fails to land at all; at the last rung even the dread aura of the Watcher and the Spider is refused, and only its wither still bites. When a scream fails to pin a player who has earned that much, one line appears under the item name and fades: *You do not flinch.* An operator can put a courage total where they like with `/tmcourage <targets> <deeds>`.

- **The war horn answers the Nazgul.** A horn of the campaign's own, blown on a note of its own: every player within twenty-four blocks of it is freed of the crippling terror and the dread aura, the one who sounded it included, and then it will not sound again for twenty seconds. It clears fear and nothing else - poison, corrosion and the rest are left alone - so a Nazgul met without it is still everything a Nazgul is.

- **The boss fortress and its arena are cut from Mordor's own stone.** Both builds stand in dark stone now - cobbled, brick, smooth and mossy - with Morgul iron where the iron bars were, and every plank, log, stair, slab, fence, gate, door, trapdoor, sign, ladder and torch that was oak, spruce, birch or jungle is mirkwood instead. Wool, glass, books and the growing things are left as the builder left them.- **The work can be arranged by hand.** `/tmquest <targets> give [count]` fills a book with work drawn the way a giver draws it, `quit` sets aside everything a player carries, and `spawn` stands a real giver with a real offer beside them at once - skipping the wait and the one-giver rule, so a scene can be staged or the system tried without a walk in the world. Like the faction commands it answers only to a gamemaster-level operator or a player in creative.

- **Work can be set aside.** A quest a player has changed their mind about - a hunt for mobs they cannot reach, an errand taken on too eagerly - can be abandoned from the book: a small **x** closes the giver's line on the quests page, and the first click only arms it, turning the button into a question, so a mis-click costs nothing. Setting work aside pays nothing and takes nothing out of the pack, and the place it held in the book is free again at once, so a player who puts one down can still take up another.

- **A pack can set the work, the numbers and the folk who hand it out.** Everything a giver draws on now lives in `[server.quests]` of the common config: the items a fetch quest may ask for, the mobs a hunt may ask for, the goods and the coin it pays, both bounds of each kind's goal, what a faction quest is worth in standing, and how often a giver in a country offers politics at all. The tables hold registry names, so a pack puts work to its own items and mobs without a line of code. It also names its own folk, because a giver is now one name and one title drawn separately from two tables and joined with a space - ten of each is a hundred travellers - while both tables left empty keep the mod's own eight, which stay translated. A quest keeps the terms it was taken on, so a change here reaches the next offer rather than the work already in someone's book.

- **Questing: a traveller with work turns up, and a book to keep it in.** Every so often a named traveller now arrives near a player - sixteen to twenty-eight blocks off, on dry solid ground, never inside a block - and stays put: he is persistent, so the work he is holding is still there when the player comes back for it, and a gold **?** sits on his nameplate until someone takes it. Right-clicking him hands over the work, and right-clicking any of them with a finished quest hands it in, so a giver who has wandered off is never a lost quest. Three shapes of work are drawn: **faction** work kills three to six soldiers of a rival alliance and pays standing with the country the giver stands in; **fetch** work asks for four to twelve of one item from a pool of nine, read straight off the pack; and **hunt** work kills three to seven of one mob. Goods and coin pay the last two, sized by how much was asked for, because a farmer has no standing to give away - so questing is worth doing whether or not a player cares who holds the West.

- **The quest book opens the ledger on a Quests tab.** A player is handed the book the first time they take work on, and only ever one: the quests belong to the player, so the paper is only how they are read. **J** opens the same screen on its Alliances page, and the two tabs sit under one title, because who a player stands with and what they are doing are two halves of one question. Each quest shows its picture, its objective, a progress bar and what it pays; a fetch quest fills its bar as the goods are gathered, a kill quest as the kills land, and a finished one says so until it is handed in. Up to four are carried at once, and a fifth is refused rather than silently dropped.

- **A workbench is furniture two blocks wide.** The factions work at a bench of their own now, drawn from a model that reaches a full block past the cell it stands in - tabletop across, a hutch along the back and a shelf over it - so its outline follows the drawing instead of blocking a plain cube, and the overhanging half is as solid as it looks. It turns to face whoever places it, drops itself when broken, and waits in the function tab; its item is drawn at half the size of a block item, so the bench fills the icon rather than swamping the hotbar and the hand.

- **Faction envoys sell for coin and token.** Every hall now keeps a merchant at its counter, and the shelves he opens are your rank with that side - Guest, Friend and Kinsman, one shelf each. Every offer costs faction coin and at least one faction token, and the top shelf carries a piece of the alliance's own: the Star of Earendil in the West, the Morgul Knot in the East and the Sun-brass Amulet in Harad.

- **A contract is titled with the work it asks for.** One taken from a board is named after the task - how many soldiers of which alliance - so a pack of them can be read without opening a tooltip. The tooltip is unchanged: employer, objective, progress and pay.

- **A contract board posts mercenary work, and a contract is the paper it is written on.** Standing with the three alliances could only be earned by killing and by handing tokens over, so there was no work to take. Right-clicking a contract board now posts a contract for the alliance the player stands best with, against one of its rivals: slay between four and eight of that side soldiers, drawn fresh once a day and different for each player reading the same board. The terms live on the contract item, so the paper and the work are one thing - a contract thrown away is work abandoned, and two contracts are two stacks. Every soldier of the target alliance a player kills moves every contract they carry against it, the count shows over the hotbar, and a finished contract is paid at a board for its reward in standing with the employer while both its rivals lose half of it, so serving one side is felt by the other two. The board is drawn with its own model now - a structure wider than the block it stands in, whose outline is carried as the block shape, so a click lands on the parts that hang over - though it still has no screen; the contract is a rolled scroll of its own now, drawn at 32 by 32 from the art painted for it rather than on vanilla paper.
- **An operator can set and read a player's standing by command.** Standing is earned in the world, which is no use when a world is being built, a scene staged or a rank tested: `/tmfaction set <players> <west|east|harad> <amount>` now puts a standing exactly where it is asked for, and `/tmfaction get <players> [west|east|harad]` reads one back, or all three on a single line in each alliance's own colour. Both also answer to the flat names `/tmsetfaction` and `/tmgetfaction`, so either order comes naturally, and everything the command writes goes through the same bounds as a kill or a hand-in - never below the floor a country turns hostile at, never above the ceiling - and is sent to the player it happened to, so their ledger redraws at once. Like any world-shaping command they are for a gamemaster-level operator or a player building in creative.
- **The Alliance Guard has a spawn egg, and a switch of its own.** The soldier the factions muster had no egg, so there was no way to meet one without first making a country hostile, and no way to test the liveries without `/summon`. It is in the spawn tab beside the other hostiles now, with `enableFactionGuard` under `[server.mobs]` so a pack can turn it off like any other mob. An egg names no side, so a guard that arrives without one takes a random alliance on its first tick and wears that side's skins - an explicit `Alliance` in a summon is kept. The three alliance tokens, the one thing else this work added, were already in the treasure tab beside the faction coin.
- **Standing with the three alliances, and a ledger to read it by.** A world now keeps a standing for every player with the West, the East and Haradwaith separately, because the three are rivals rather than a single score. Killing a soldier of one alliance raises the two that are not his own by five and costs his own the same, so a player who clears an orc camp is noticed by the West; handing the tokens of an alliance to its warden is worth ten points a token to that alliance and costs each rival half again as much, so swearing to one side is felt by the other two. A loss is felt in full, down to the floor of hostile: killing your way through a country's soldiers is one way to become unwelcome there and handing its rivals tokens is the other. There are six ranks from Outsider to Kinsman over a point total, and a total below zero is **Hostile**, which is where a country starts sending its guards. Every change is drawn on a screen of its own: **J** opens the Ledger of Alliances, which shows each alliance's rank, its bar, the next rank and the country it holds, all read off what the server last sent the player rather than off anything the client keeps. Killing, handing in and logging in are the three moments a standing moves, and each tells the player in chat or over the ledger.
- **Each alliance builds a hall in its own countries.** `tolkienmobs:faction_hall` is cut into three variants that share one plan and differ in the wood the hall is built in, so the West's hall stands in its countries, the East's in Mordor's, and Haradwaith's in the desert; each is a rare structure so a hall is found rather than stumbled over, and its shell is structure void so the country's ground is only replaced inside it. A warden waits at each hall with two scoreboard tags on him, one naming him a faction envoy and one naming his alliance; right-clicking him with that alliance's tokens hands over every one in the pack, and a hall also raises two of its own soldiers when a player it will not have comes within forty blocks of the warden.
- **Each hall stands its front's named giver.** A hall placed a body called "Herald of the West", "of the East" or "of Haradwaith" and nothing else: he handed out that front's campaign pages, but he carried no name of his own and nothing said the work he held was a front's pages rather than an errand. He is **Maelgrin** in the West's hall, **Guthlac** in the East's and **Zarabi** in Haradwaith's now - the names the pages are written for - and he carries the `tolkienmobs:campaign_giver` tag `ensureCampaignGiver` looks for, so a hall and `/tmquest <targets> campaign_giver` agree on which body is the front's own instead of the command standing a second one beside him. The book writes his name under the page he hands over, so a page taken at a hall says who gave it. A hall generated before this keeps the herald it already has; he is marked as the front's giver when he loads, so an old world does not gain a second one standing beside him. The three templates are cut again from `faction_hall.nbt` and every block of them is unchanged - only the body at the bench is.
- **A campaign page says what it is called and where it happens.** A page was a kind, a count and a price, so the story leaf at the back of the book could only ever say what to count: the West's muster, the East's scroll and the Rider all read as errands that happened nowhere in particular. A page now carries a name and a place, both translation keys, and the leaf draws the name over the objective and the place under the sentence of what the step is for - so the two steps the design names now read as **The Steward of the South**, met on the road through Nurn, and **The Rider's Word**, met wherever he chooses to be met. A page with no name of its own is left blank rather than given an empty heading, and the leaf falls back to the objective, so the muster pages read exactly as they did before.
- **A country that will not have you sends its soldiers.** An alliance a player is hostile with now patrols its own ground: within a hundred ticks it looks for a chance to place one of its soldiers twelve to twenty blocks off in open country, and two when the player is standing at a hall. The cap is counted on what is already standing nearby rather than on a timer, so a player who kills the patrol is met by another and a player who runs is simply left behind, up to three at a time. All three field a new **Alliance Guard**, one soldier drawing on each alliance's own painted set - eight liveries in `faction_west`, six in `faction_east` and six in `faction_harad`, and each alliance's variant rolls only inside its own set - and armed with ammolite in the West and morgul iron in the south and the east. A guard is a soldier rather than a night creature, so it stands in daylight, and it drops issued kit with the coin its alliance pays it in. It also takes its blade the first moment it stands, because 26.1's `Mob.finalizeSpawn` no longer hands a mob its equipment: a guard is placed by its faction rather than spawned naturally, so nothing else would ever have armed it.
- **The Shire grows pipe-weed, and a pipe blows shapes in the smoke.** Pipe-weed has been a crop with seeds and a block since before this and grew nowhere in the world, so the only way to farm it was to find the seeds in a chest. It now stands wild across the Shire as a meadow weed, taking hold on turf and soil as readily as on a tilled bed, in three strains: **Longbottom Leaf**, the leaf the mod already had under the plain name pipe-weed and the one in the most patches, beside **Old Toby** and **Southlinch** in fewer. Each strain is a crop and a seed of its own, so a garden tells you which leaf it will give, and a ripe plant gives its leaf and its seeds. A **tobacco pipe** - two planks and a stick - is drawn on until the bowl catches and spends one leaf of whichever strain the smoker is carrying, then blows that strain's shape into the air: a smoke ring for Longbottom, a little ship under sail for Old Toby, a long winged serpent for Southlinch. Each strain leaves a brief, small comfort as well - a little Absorption, Regeneration or Saturation - and a draw released before it catches spends nothing. A hobbit village grows its own leaf too: each of the three burrow plans now lays a nine-plant bed of tilled turf beside its door, mostly Longbottom with a plant of Old Toby and one of Southlinch, so a Shire town keeps a garden of it without a villager having to find the seeds. Its fields are leaf fields as well - the farm a Shire village draws is the vanilla plan with every row replanted in that same mix, so no wheat, carrots, potatoes or beetroots stand among the burrows, and the farm still joins a street from its own front edge. The plot is built like a hobbit's own, too: the one-block wall vanilla rings it with is lebethron, the wood the burrows are already built and lit in, rather than oak, and the composter in its corner is a lebethron barrel that opens toward the rows.
- **A draw that runs its full length smokes the bowl by itself.** Holding the pipe past the end of the draw used to achieve nothing at all: the draw completed, the bowl stayed packed and the use ended, so a smoker who held a moment too long had held the button for no reason. A completed draw now burns the bowl exactly as a released one does. That also makes a hold a loop: the draw ends with an empty bowl, so a smoker still holding the button packs the next leaf and draws again, one bowl at a time, and the pipe stops on its own when there is no leaf left to pack. Running out no longer fills the chat either - the "no pipe-weed" line is shown over the hotbar instead, where a held button refreshing it a few times a second cannot pile up lines in chat.
- **A draw on the pipe shows how far it has come.** Holding the pipe on a loaded bowl gave no sign of how long to hold: let go too soon and nothing was smoked at all, and nothing on the screen said where that line was. A short bar now appears over the hotbar while a draw is under way, filling in the bowl's own flower colour and carrying a notch at the shortest draw that counts - a release before the fill passes it only warms the leaf, and the fill takes the strain's pale smoke colour once it is past, which is the point where letting go will actually smoke. It sits between the item name line and the experience bar, and it is drawn only while a loaded pipe is in use, so an idle pipe shows nothing.
- **Each of the mod's villages stocks its houses from its own chest table.** Three of them built their houses with an empty chest, and two borrowed a table that belonged to a ruin. `tolkienmobs:chests/frozen_house` is now the Lossoth house's larder - cram, dried cod, leather, hide and the iron traded for at the coast; `tolkienmobs:chests/orc_house` an orc's den of grog, meat, bone, the coin it took off the last one through, gunpowder and a scavenged blade; `tolkienmobs:chests/desert_house` a Harad house of waybread, leather and what the caravan brought back; and `tolkienmobs:chests/nurn_house` a Nurn farmhouse of wheat, potato and beetroot, a hoe and very little else, because everything else is taken. The hobbit village's second burrow now uses the baker's pantry that had been written for it and wired to nothing, and the whole assignment lives in one place: a village's houses are prepared during data generation, so that is where each plan's chests are stamped with the village's table. Nurn builds the desert house and eats from a Nurn table, which is the point of doing it there rather than in the plans.
- **The overworld's own mobs drop the mod's coin in more of the places it belongs.** A bogged carries the bronze its drowned kin do, the pillagers and vindicators carry bronze and the evoker silver, since raiders are Men with a purse, and a witch - who has always gathered herbs - now and then carries the pungent herb. A shipwreck's strongbox carries silver beside the bronze the wreck's supply chest already offered.

### Changed

- **Imladris is a vale inside a massif, not a pan beside a hill.** The city is carved into the sides of a
  mountain whose crown it reaches, so the ground has to be there before it is planted: the valley floor is
  the city's own foot with a field's width around it, and the ground outside it climbs straight from the floor
  to y 300 - the map's own ceiling, just under the city's crown - before it is handed back to the Trollshaws
  over a second fall. The city's carved flanks run up into ground already as high, and the ridge is what a
  player sees from outside rather than a wall of the city's own stone standing on a plain.

- **The generator says which shape it is drawing, once, when it loads.** A client that is already running
  keeps the classes it started with, and a world made in it is generated by the old code however new the
  source is - which is how three test runs in a row tested terrain from before the fix. The chunk generator
  now logs one line at load naming the shape it draws, so the log answers "is this the new build" before a
  world is made rather than after.

- **The city's shoulder is a ramp instead of a step.** Four rings and a six-block cap could only meet
  ground already near the build's base, so the rim of a city on a hillside was left as the cut face it
  complains about: 5,518 of its columns were skipped for being too far off to meet in one step. The shoulder
  now walks out from the walls at about twenty-one degrees and stops where it has met the ground, cutting the
  hill down or building the hollow up a block at a time, keeping the grass on ground it moves and counting
  what it cut, what it filled and what already stood on the line.
- **The valley is centred on the city planted in it.** Its middle sat 276 east and 263 south of the city's
  own middle, so a city planted at 4352, -3833 stood with part of its footprint against the valley's wall.
  The middle is 4628, -3577 - the planted corner plus half of the 554 by 512 footprint - and the rim is 128
  blocks of climb rather than 64, so the ground outside the walls is a mountainside a player walks down rather
  than a cliff to look at.

- **The Wood-troll is Legolas' page at both ends.** A page owed to a body is handed and taken by that body alone, so naming Legolas as the turn-in is what keeps the hall's herald from giving out the hunt for a creature it has never seen - the same shape the Giant already had at Elladan's camp.
- **The Elves of the line stand at their doors.** Glorfindel is out on the square's southern road, Elrond at the library's door and Legolas at the guest house's, instead of all three on the middle of the place, where the buildings now are.
- **The duergar are the East's.** They are dwarves who took the Enemy's service, so the peoples table says so rather than reading as though every dwarf were Erebor's.
- **Every people in the notes is placed with one of the three sides.** The notes kept a faction per people, and this pack keeps three sides, so `Alliance` now records which is which: Elves of any kind, Dwarves, Hobbits and the Men of the West are the West; Mordor, Angmar, Isengard, Orcs, Goblins, Uruk-hai, Trolls and the Easterlings are the East; the Haradrim, the Corsairs of Umbar, Khand and the Variags are the Harad.
- **The notes' races and realms are read as the three fronts.** Where the document names the payer as the High Elves, the Dwarves or Gondor, the line takes it as the front the act belongs to and pays West, East or Harad standing, since a page pays one of the three countries and never a people.
- **The notes' standing is taken at a flat tenth.** Their ladder ran to five or ten thousand and this one tops out at fifteen hundred, so the rule is now one divisor rather than a figure per page: the cave hand-over's hundred pays ten and the Giant's nine hundred pays ninety. Pages the notes never priced keep the figures this line has always paid them.
- **The line's standing is the notes' figures scaled to this pack's ladder, and says so.** The notes were written against a ladder running to five or ten thousand; this one tops out far lower, so a page they price at a hundred pays twenty-five here and the Giant's nine hundred pays fifty. That rule is now written above the page list rather than rediscovered each time a page is added.
- **The Giant is worth fifty of the Elves' standing, not nine hundred.** The notes pay the kill nine hundred, which is most of a rank in this pack for a single fight; the page now pays fifty and keeps its courage, so the Giant is a step in the line rather than a shortcut up it.
- **The cave hand-over pays in goods as well as standing.** The notes end the search with a choice of equipment, an enchantment and ore; a page does not roll loot in this pack, so the camp now hands over a reforged blade and four mithril nuggets with the standing it already paid - twenty-five of the Elves', not the hundred the notes wrote, which is most of a rank here.
- **A cleared chamber pays its cave completion token, and the camp wants all three back.** The notes' cave quests each pay a token and the camp asks for three of them with the beryl, so the item the pack already shipped as proof of killing the troll is now actually handed out: one per cave troll felled at depth. Elladan refuses the stone while fewer than three are in hand and says how many are missing, and the hand-over spends the tokens and the beryl together.
- **A troll cave is cleared by its troll, not by standing in it.** The notes write the three caves as area kills of a cave troll rather than as ground to hold the beryl over, so the stone's caves page now moves on a cave troll killed under deep rock rather than on the raise: the raise is the reading it always was, and a chamber with its keeper still standing says so instead of paying the page. Depth is read off the body, so a troll dragged up to the mouth and killed there does not count.
- **The troll cave is set into its ground by its grass line.** Its template is cut in two halves - bare rock from its foot to y 8, grass from y 9 to its crown, with the mouth's sill at y 6 - so seated the way a house is seated, on the highest column under it, it stood on that rock skirt with the grass a good six blocks above the ground it had been dropped on, and the lower half read as stone that should have been buried. A building now says how deep it wants to be set instead of every piece being given one rule: a hall, tent, house or arena asks for no floor and keeps what it had - half the drop under its footprint, never more than four blocks - while the cave asks for nine, which puts its grass at the ground it is placed on, and never more than ten, so the worst a footprint this size can leave it is one block low.
- **The ledger is an open book, and both halves sit on one spread.** The ledger was one narrow panel with the alliances and the quests behind two tabs; it is a leather book now, with two parchment leaves, a shadow down the gutter and its two bookmarks stitched over the spine. The alliances bookmark keeps the standings on the left leaf and the work in hand on the right, so who a player stands with and what they are doing are read together; the quests bookmark spreads the carried work across both leaves, two entries to a page. Each quest also records who offered it and where, so the book prints the giver and the place the work was taken under its objective, with a sentence of what the work is for and its progress at the foot of the entry - and the giver is kept as the key it was translated from, so the book reads the name in whatever language its reader uses.

- **Every faction hall is built around workbenches, with its warden and envoy at one each.** The four tables a hall has are workbenches now, set against its side walls and turned to face the aisle so the two blocks a bench is drawn across stay clear of the way through, and the three alliance halls were cut again from the build as it now stands in the test world - its sea-lantern lighting and opened-up floor included. The warden works at the western bench, the nearer of its pair to the front door, and the envoy at the bench across the aisle, each standing on the side its own bench is worked from.

- **Every faction hall carries a notice board.** One stands outside each hall's front door, to the right of it as you walk up, so the hall is where mercenary work is taken on and where a finished contract is paid.

- **The notice board is drawn twice the size of the model it is cut from.** A model file cannot say this - an element may not reach past 32 pixels on any axis, and the board is already at that limit - so the drawing is scaled instead: the model is wrapped at bake time and its quads are moved as they are collected, which keeps the block in the chunk mesh with its occlusion and materials. The block outline grows with it, about the middle of its ground face, so a click still lands on the wood. Placed, it now stands 3.25 blocks wide and 2.5 tall.

- **The notice board turns to face whoever places it.** It had a single orientation, so a board set against a wall could show its back; it now takes a facing the way the well does, is placed with its front toward the player, and carries an outline per direction so the parts that hang over stay clickable whichever way it is turned.

- **The eggs are split by who each mob is, not only by its colour.** Eight of them carried the same face in eight skin tones - the plain Man, the hill-man, the brigand, the alliance soldier, the two Southron traders, the Haradrim and the Istari - so a chest of them could not be sorted without reading every tooltip. They differ now by what is on their head: a shaggy beard for the hill-man, a cowl for the brigand, a steel cap with a nasal down the face for the alliance soldier, a cloth wrap with a fold at each temple for the desert folk, a pointed hat over a long beard for the wizard, and the plain Man left bare-headed. The mouth on every face is a short line rather than a bar across it, because the lip shadow that followed it read as a small beard on all of them. The little things have faces of their own as well - two eyes too large for the face over a wide grin for Gollum, eyes on the crown of the head and a mouth ruled right across for the frog, eyes at the rim and a closed smile for the gecko, a pointed snout for the rat, two front teeth for the squirrel, and no face for the swarm at all, just specks - and the wraiths are told apart the same way: a helm with a dark slot for the Nazgûl, a crown of iron spikes for the Witch-king, the open hood for the spirits. The wolves gained pricked ears and a tapering muzzle and the horses a shaded one, so neither reads as a masked man any more.
- **Every spawn egg is repainted in the vanilla idiom.** Each egg had been its own mob's icon poured into an egg-shaped hole with the group's colour mixed through it, which muddied every face and, once the colour batching went, left several eggs almost blank - a black fell beast, steed or crebain showed nothing on a black shell. All fifty-six are drawn the way vanilla draws an egg now: a smooth shell in the mob's own colour, lit at the crown and shaded at the base behind a darker rim, with a face set into the middle of it - slitted orc eyes over tusks, four spider eyes above fangs, a mumakil's tusks either side of its trunk, a mimic's lid of teeth, a watcher's one great eye, a mumakil's and a minotaur's horns above a broad snout. Faces are built in code from a small alphabet of markings, eyes, teeth and lit glows, so a mob that is more than a man or a beast gets a face of its own rather than a recolour, and every face colour is held far enough from its shell to stay legible on a dark egg. The birds carry a bill rather than a smudge: crebain and the two eagles a bone hook tapering to a point under a heavy brow, with the corners of the mouth dark either side of it, and the thrush a small pointed bill between two wide-set eyes.
- **Loading a pipe is a gesture of its own.** The pipe loaded, drew and smoked on one held button, so there was no way to tell a loaded pipe from an empty one: a quick tap did nothing at all and looked exactly like a tap that had worked. Tapping a pipe with leaf in the pack now loads the bowl - one leaf leaves the pack and stays in the pipe, the bowl takes that strain's colour and the tooltip names it - and it stays loaded until it is smoked, so a pipe can be filled before a walk and lit on the way. Holding the button draws on a loaded bowl and smoking empties it again; sneaking and using tips the leaf back into the pack, so a pipe is never stuck holding a strain the smoker has changed their mind about. Smoking is also quicker and worth more: a draw is one second rather than two, the cooldown half a second, and the comfort lasts a minute (thirty seconds for Southlinch) rather than ten, so the leaf is no longer spent in less time than it took to notice.
- **A pipe blows its shapes in dots, not in clouds.** The ring, the ship and the dragon were all drawn from vanilla's campfire smoke, one puff of which is most of a block across: sixteen points a hand's width apart merged into a single cloud, so all three strains gave off what looked like the same puff with a silhouette in it. The figures are now twice the size and are drawn with a fifth-size smoke of their own, `tolkienmobs:pipe_smoke`, while the puff at the mouth stays a full plume.
- **Each leaf says what smoking it does.** The three strains are the same plant with three different comforts, and nothing in the pack said which was which - the bush told you a colour and the smoke told you a shape, but the reason to choose one over another was invisible. Every leaf now carries a line in the Shire's own words: hardiness for Longbottom Leaf, mending for Old Toby, a filled belly for Southlinch.
- **The tobacco pipe is a tool, not a plant.** It sat in the natural blocks tab among the pipe-weed it is packed with, where nothing you hold belongs; it now sits in the tools tab beside the coin pouch, the key ring and the hobbit ring.
- **A pipe's draw and its strains' comforts are settings.** The draw, the shortest draw that still smokes and the cooldown were three numbers in the pipe's own code, and the three strains' effects lasted as long as the enum said, so a pack that wanted a slower, longer smoke or a bowl worth a whole afternoon had no way to ask for one. They now live in a `[server.pipeweed]` section of the config: `drawTicks`, `minDrawTicks` and `cooldownTicks`, and a duration per strain - `longbottomDuration`, `oldTobyDuration`, `southlinchDuration`, all in ticks, twenty to the second. The shortest draw is held under the full draw whatever the two say, so a value set above it cannot leave no draw able to finish, and the numbers are read where they are used, so a change shows on the next pipe lit rather than the next world.
- **Two of the pipe's Chinese lines were saying the wrong thing.** Tipping a leaf back used 倒回 - "poured back", which is what one does with a pocket full of loose tobacco, not with the single leaf knocked out of a bowl - and the empty-bowl tooltip read 烟锅：空的 where a tooltip wants the terse 烟锅：空. They now read 你把长谷烟叶收回了背包 and 烟锅：空. The rest of the pipe's Chinese was already idiomatic and is unchanged, as are the three leaves' names and their lines.
- **A pipe's smoke is the colour of the leaf in it.** All three strains burned to the same grey plume, so the one thing a smoker actually watches was the same whether they packed Longbottom, Old Toby or Southlinch. Each strain now draws its plume in the colour its flowers are drawn in, lifted a third of the way toward the pale centre of the bloom so it reads as smoke rather than paint: pink for Longbottom, salmon for Old Toby and orchid for Southlinch, and the thin thread the pipe gives off while it is being drawn on takes the same colour, so it is clear what is about to be smoked. The two messages the pipe shows were sitting under the item's old name and came out as raw keys; they now sit under the pipe itself, so an empty pipe says you have no pipe-weed and a finished smoke names the leaf that went into it.
- **A pipe's bowl shows which leaf it is packed with.** The bowl was a solid lump of the pipe's own wood, so the one part of the pipe that holds the leaf was the one part that never showed it. Its opening now draws a packed-leaf texture of its own, heaped a little over the rim, and that leaf is coloured with the strain that is actually in the bowl: loading a pipe puts a leaf in it, and the bowl is drawn in that leaf's flower colour until it is smoked. The colour is the flower at three quarters strength rather than full: leaf down in a bowl sits out of the light, and full-strength Old Toby salmon was close enough to the pipe's honey-gold wood to read as bare wood. An empty bowl takes a dull ash brown, so a pipe lying on a shelf tells you whether it is loaded or burnt out. The pipe's tooltip says the same in words - **Bowl: Longbottom Leaf**, or **Bowl: empty** - and because the bowl is a state of the pipe rather than a reading of the pack, it says the right thing in a chest or an item frame and keeps saying it after the smoker has picked up a different strain.
- **The pipe-weed plant is redrawn.** Each strain's eight growth stages were a few stepped lines with three flat flower dots; they now carry a slender leaning stem, lance-shaped leaves set alternately up it with pale midribs and a shaded underside, and a head of blossoms that opens as the plant ripens - two buds, then four, then six, then a full head at the last stage - so a ripe plot reads as a mass of flower from a distance and you can see when to cut it. The three strains keep the old heights and the same eight stages exactly (7, 14, 25, 29, then full 32 pixels), and differ only in tone: sage leaf with dusty rose flowers, olive-gold with amber, grey-green with mauve.
- **Each strain of pipe-weed has its own leaf and seed in the inventory.** Longbottom, Old Toby and Southlinch differ as plants, as seeds in the ground and in the smoke they leave, but their two item icons were one picture copied three times, so a chest of one strain looked like a chest of another. Each has its own now: a sage leaf carrying one of the plant's pink blossoms for Longbottom, an olive-gold leaf with its salmon for Old Toby and a grey-green leaf with its violet for Southlinch - the blooms picked from the very colours the crops flower in at their last stage, so a harvested leaf is recognisably the plant it was cut from - with the seed piles shaded to follow: warm brown, golden, grey-violet. The three share one leaf and one shading, so what reads is three strains of the same plant rather than three plants.
- **Every village now farms in its own wood.** The Shire's field had been rebuilt as a hobbit's - lebethron wall, barrel in the corner - while the other villages still put vanilla's oak at the edge of their plots. The dwarves' fields are walled in dwarven maple, the men of the Vales' in culumalda, the elves' in mallorn, and the two desert villages' in the wood their own houses are cut from: mirkwood in Mordor and deadwood in Nurn. A desert plot's rim is smooth sandstone stairs rather than a fence, so it is rebuilt as the post it stands in for, and the jungle trapdoors over its channels become the country's own; the plains plots change only their wood. Every plot keeps the composter, which is where a farmer works - the Shire's barrel is the one exception - and every field still joins a street from its own front edge. Measured after `/place structure` on a flat world: the dwarven village drew five fields, each of dwarven maple and no oak anywhere in them, and the orcish village both of its desert fields in mirkwood.
- **A mob's drops now read as a purse rather than a shopping list.** Every table is built from the same three parts: what the creature physically is - hide, meat, feathers, a wraith's remains, a golem's stones - which is certain on a player kill; the purse of coin that matches its standing; and the one odd thing worth hunting it for. A regular mob's table now names at most six things, a boss is allowed more, and the pools that used to make the numbers unreadable are gone: the rock and elemental golems had eleven independent pools each, the swamp hag ten and the Watcher eleven, and both golems now give two rolls from a single weighted pool of their six stones.
- **Faction coin, faction tokens and dark sigils are carried only by the mobs they mean something to.** They used to drop from every hostile creature in the mod, which made a barrow-wight's sigil worth the same as a haradrim's and left the factions meaning nothing. The orc-kin, the trolls and the human raiders carry the faction coin, the orc-kin alone the token that raises standing, and the sigil only the dead and Mordor's servants. Everything else keeps the coin it had, at the tier its standing deserves: bronze for the rabble, silver for the undead and the elite, gold for a Nazgul, mithril for a boss.
- **The golems are no longer better to mine than an ore vein.** They dropped a mithril block and a morguliron block - nine ingots apiece, free - alongside all six of their stones. Their bodies now give the stones, and a cut of worked metal only sometimes: an ingot or two, not a block.
- **The beasts of the field have drops of their own.** The aurochs, the goats and Shadowfax all gave exactly the same three things, so a legendary horse of the Third Age was worth a side of beef. Goats give mutton, aurochs beef and their hides, the mumakil leather, meat and the bone of something that size, and Shadowfax gives the hide of a horse out of legend and nothing else. The Istari, a person rather than a monster, no longer leaves monster flesh behind.
- **Village chests hold a village's goods instead of a stronghold's.** A hobbit hole used to give diamond tools and an elven house golden ones, which was richer than any chest vanilla puts in a village. Each culture is now stocked in its own idiom and capped at iron: the hobbits with farm tools and a well-kept pantry, the dwarves with a pick, ore and coal, the elves with waybread, a draught and the arrows of the Galadhrim - the elven bow the one good thing in the room, at the end of the weights - and the houses of Men with grog, bread, the household's letters and an iron sword on the wall.
- **The metal ores climb a ladder again.** Mithril and morguliron each had their deepslate ore paying three to nine - more than the End and Nether ores above it - while the End rung paid two to five. Each now gains exactly one on the way up: stone one to three, deepslate two to four, End three to five, Nether four to seven. Deepslate still pays better than the stone it was, because it is the harder rock to break; it no longer out-earns the rungs above it. Ammolite's four had always run in order and are unchanged.
- **Chest loot runs the right way round.** The desolate ruins gave diamonds in threes to sevens at the highest weight in the table, so the best thing in a ruin was also the most common; the barrow chest's loudest entries were music discs and horse armour. The coin, the iron and the bone are now what a ruin is mostly made of, with the ammolite and the relic at the tail.
- **A boss always leaves its trinket.** The trinket is the signature of a fight - there is one per boss, already rolled for its effect - but it sat behind a one-in-four roll, so the reward for the fight was usually absent and the same boss could be killed for nothing at all. Every boss now leaves its trinket whenever a player makes the kill, and the roll decides which effect it carries rather than whether it appears. The Enraged Fell Spirit, the one creature in the boss tag that carried no trinket at all, now leaves the cloak - the shadow a wraith wears instead of a body. The Nazgul's cloak and Gollum's ring are certain too, since each is the reason to go looking for the creature that carries it. The mimic chest is the one trinket left to chance, where Looting still improves it.
- **The four lair tables that were copies of each other are four tables.** The swamp hag's hut and the spider tree gave identical loot, and the minotaur's hoard was the warg pit's with the coin changed. Each now holds what its occupant would have: the hag her herbs, her bottles and what she has been feeding; the spider its silk, its eyes and what it wrapped up; the pack its bones and offal; the minotaur what it has taken off everyone who came before.

### Fixed

- **The ledger keeps its text on the page.** Every line was drawn at whatever width it happened to be inside a 176-pixel panel, so a long objective or a rank name ran off the edge of the screen. Lines are measured before they are drawn now and either trimmed with an ellipsis or wrapped onto the leaf, and the book shrinks to fit a smaller window rather than hanging off it.

- **The notice board loads instead of showing a missing cube.** The upper rail was authored standing up and turned flat by a 90-degree element rotation, which put its bounds at y 38.5 - past the 32 the game allows, and it is checked before any rotation is applied - so the whole model was thrown away and the block fell back to an untextured cube. The rail is laid flat in the model at the same place, and the model carries display transforms now, so its icon and held form are scaled instead of drawn full size.

- **The world's milestone file was read lazily, so a change made early in a session could be lost.** A saved data is decoded on its first lookup rather than when the world loads, and decoding replaces the map, so a milestone placed, renamed or broken before that read would be dropped by it. The file is now read as the server finishes starting, before a player or a block can touch a milestone, so every change lands in the map the world holds and is saved.
- **A standing a command set never reached the ledger.** A world's faction file is not read when a player joins but lazily, on the first lookup of the saved data, and reading it replaces the standings held in memory. The setter wrote the new standing first and read the file afterwards, so that read threw the write away and the sync which followed told the player the numbers the file already had - the command answered correctly while the ledger, the ranks and the guards kept the old standing. The file is now read before the standings are read, written or sent, so a player who has just logged in is shown what the world holds rather than the empty row built for them, and a set sends the value it stored even when that is the value already there.
- **The ledger of alliances drew its rows empty.** Every alliance's colour is written as `0xRRGGBB`, and the screen handed those straight to the text and bar calls, which read a colour's top byte as alpha - so the three names, their ranks, the point totals and the bars themselves all came out fully transparent and a row showed nothing but its token and its background. They are drawn opaque now, and a bar fills in the rank's own colour.
- **A hall's warden introduced himself as a brace of JSON.** The hall's warden was placed with `{"text":"Warden of the West"}` written into its custom name, which 26.1 takes whole as literal text rather than parsing, so the name tag read the braces and the word `text`. The three halls now carry the plain name, and the tool that cuts them writes one.
- **Killing a soldier cost his alliance nothing.** A loss was held at the bottom of the rank the player already held, but a rank is read off the point total, so at a threshold the rule froze the number: six brigands killed moved the West by zero. Losses are now counted in full, which is what makes a country turn hostile from a player's own violence as well as from a rival's tokens.
- **Mithril ore was listed as an iron-tier block.** `tolkienmobs:ore_mithril` and `tolkienmobs:ore_nether_mithril` were named a second time in `#minecraft:needs_iron_tool`, a leftover from before all four variants of every ore went into `#minecraft:needs_diamond_tool` through the mithril, morguliron and ammolite ore tags. It changed nothing in play, because the diamond tag covers all twelve ore blocks, but it made mithril the one ore of the three whose declarations disagreed with its siblings. The iron tag now names no ore at all, so mithril, morguliron and ammolite ask for the same pickaxe everywhere.
- **A chest in Forochel, in an orc house and in the second hobbit burrow generated empty.** The three plans carry a chest but no loot table, because a village builds its houses from copies prepared during data generation and nothing ever put a table on them; the baker's pantry was written and generated but no template named it. Every village house now carries its own table, and the five prepared copies that changed were regenerated.
- **`shipwreck_supply` patched the village temple chest.** The global loot modifier named for a shipwreck's supply chest pointed at `minecraft:chests/village/village_temple` - the same table as the modifier above it - so a shipwreck kept an empty hold and a temple drew the coin twice. It names the shipwreck's own table now.
- **An acorn never fell from a mallorn.** Every tree the world places is built from the hardened leaves, the untinted face of the same leaves, and that table gave shears the plain leaves, a sapling and sticks - but not the acorn the plain leaves' own table carries. Since no tree is ever built from the plain leaves, the acorn never dropped where a mallorn actually grows and a squirrel was the only way to find one in Lorinand. The hardened table now carries the plain one's extra drop as well, so the two differ only in which leaves shears hand back.
- **The barrow grave and the desolate ruins multiplied their loot by accident.** A count function sat on the pool rather than on the items in it, so every item a chest produced had its count rolled twice on top of the pool's own three to five rolls. The counts belong to the items they describe now.

## [5.1.635] - 2026-10-28

### Added

- **Arda**, a second overworld of the mod's own, reached through the Arda portal. It stands at the overworld's height, under its sky and its clock, on the same coordinates, and no vanilla biome grows there: the country it holds is drawn rather than computed. A grid of 48 by 36 countries, the proportions of the maps themselves and so 24,576 by 18,432 blocks, is read through the world's own noise, which warps a border by up to 192 blocks so a drawn line comes out organic, and the height is blended between neighbouring countries with a smoothstep, which is what turns a drawn coast into a shore and a drawn wall into a slope. The Misty Mountains run from Mount Gundabad to the Gap of Rohan, which is what leaves Eregion and the Vales on their flanks; Ered Luin walls the western seaboard behind Lindon; Mirkwood lies behind the Anduin with the Wild Wood and the Brown Lands below it; and Mordor stands in its ring of ash - Ered Lithui across the north and Ephel Duth down the west - with Nurn and the Sea of Nurnen inside and below it. The map's own seas become ocean, banded by latitude so the water off Forodwaith is frozen and the water off Haradwaith warm; a land column within three blocks of the waterline reads as a beach; and the seven rivers the maps name - the Anduin, Brandywine, Greyflood, Isen, Celduin, Forest River and Entwash - are cut where they are drawn. The height a country is drawn at is the height it stands at, so every range rises where the map puts it and every field lies flat where the map has none; the world's own noise still supplies what it is good at, the aquifers, the veins, the deep stone and the caves, and water is laid to sea level wherever the map draws ground below it. A lit Arda portal carries you between the two worlds at the same coordinates, arriving at a doorway already standing within sight of the crossing or, when there is none, at an ammolite doorway the mod builds and lights where you stand, so a traveller is never left without a way home; the staff lights a portal on either side, and a place in Arda is the same place it is in the overworld, so a base built at the Shire's origin comes out in the Shire's fields.
- Seven countries of Arda had no biome of the mod's own to stand on, so each has one. **Wild Wood** covers the wood between the Anduin and the Brown Lands, which lets the Old Forest stay the Barrow-downs country its own lore asks for; **Rohan** is the Mark, the grass between the Misty Mountains and the white wall; the **White Mountains** are the wall itself, snow on the tops and stone under the feet; **Dunland** is the wild hill country north of the Isen; **Lindon** is the Grey Havens' coast behind Ered Luin; **Gondor** is the fields between the white wall and the sea; and **Eriador** is the empty middle of the north where Arnor's towns once stood. Each carries the plants, the fog and the mobs of the country it stands for: the Mark and Gondor are the free peoples' fields with their riders and their townsfolk, Eriador keeps company and hobbits, Lindon keeps elves, Wild Wood keeps goblins, spiders and brigands, and the White Mountains are troll country and wild as they should be.
- Arda's countries know which of them are safe to walk at night. A `tolkienmobs:peaceful` tag names the free peoples' heartlands - the Shire, Lindon, Eriador, Rohan, Gondor, Lórinand, the Gladden fields, Forochel, the Blue Mountains and the Iron Hills - and no biome in it spawns a hostile mob, so a traveller can cross the west of the map as safely as the Shire has always been; a `tolkienmobs:hostile` tag names the enemy's country and the wilds that answer to it, and those biomes keep their hostile spawns whatever folk live among them, so a road through Mordor or the spiders' wood stays dangerous. Two tags of the maps' own reading, `tolkienmobs:snowy` and `tolkienmobs:windswept`, are published beside them.
- **The southrons' and the orcs' traders are met in the countries that keep them.** Haradwaith's creature list carries a southron trader beside the mumakil - one entry of five, so a meeting every sixty-odd chunks of the country - while Mordor and the Ash Mountains, which share one list, carry an orc trader as the only creature they have, at a hundredth of their chunks; Nurn keeps the same trader among its farmers. A trader is placed only while a chunk is being made, never by the tick pass that keeps topping up the mobs around a player, so a country seeds its merchants as it is drawn instead of gathering them around whoever stands still in it, and each one arrives with vanilla's own visit timer. Measured in a fresh world: four southron traders over 256 chunks of Haradwaith and one orc trader over 256 of Mordor, both carrying `spawn_type: CHUNK_GENERATION`.
- **The seven countries drawn for Arda are met in the overworld too.** They were only in Arda's biome list, so a player exploring the overworld's Tolkien region could cross the whole of it without ever standing in Rohan or Gondor, however much of the map those countries cover. Five take the vanilla slot whose ground they share - the Mark the savanna's grass, Ered Nimrais the stone peaks, Dunland the last windswept hills, Lindon the cherry grove's green land, and Gondor the meadow rather than the Shire's plains - and two take cells the region's own slots had left over: the Wild Wood the warm rolling half of the forest, whose flat half is Lothlórien's, and Eriador the cool dry flats beside the Shire's fields. No older biome of the mod gives ground up for them, and nothing of vanilla's the region had not already renamed. Measured in a fresh overworld with no player online, all seven answer `/locate biome` within 2,500 blocks of the origin - the nearest, the Wild Wood, 45 blocks out - and each stands on the ground where it was found.

- **A traveller's tent is met in the wilds.** `wild_tent` is a structure of its own now, one piece dropped straight onto the ground it was found on, with a `#tolkienmobs:has_structure/wild_tent` tag of the twelve countries a road crosses rather than any that has towns of its own: the Wild Wood, the two Mirkwoods, Fangorn, the Old Forest, the Barrow Downs, Dagorlad, the Marshes, Firien, Hithaeglir, Dunland and Eriador. It is placed once every twenty-eight chunks, eight chunks of separation, so a tent is a find on the way somewhere rather than a feature of the country. Measured in a fresh world, `/locate structure` answered a tent 1,189 blocks from the Arda origin and it stood at -798,69,-877 in `tolkienmobs:eriador` with its campfire and its chest twelve blocks apart, exactly as the template lays them out.
- **A wilderness tent keeps a traveller's chest.** `tolkienmobs:chests/wild_tent` rolls two to four times, with one bonus roll, over six things a night out in the wild actually asks for: cram in ones to threes, two to four torches, four to eight arrows, a bottle of grog, flint and steel, and pipe-weed in ones or twos. The weights favour the food and the light, so a tent usually gives you something to eat and something to see by, and the pipe-weed only now and then. The `wild_tent` template's own chest names the table, so a tent that stands in a world fills as it is first opened.

### Changed

- **The hobbit village is built from three plans now, drawn equally often.** It used to put up the one hobbit hole over and over; it now builds that house beside two burrows cut into their own hills - a small one with a garden, a cellar, a smithy corner and a lodestone in its yard, and a large one terraced up a slope with a courtyard, a well and a quartz-walled room - so a village of the Shire is a mix of all three. Each plan was taken out of the build as it stands, its ground and its gardens part of the template, the empty corners of its rectangle written as structure void so they clear nothing they stand over, and its interiors left as air; every chest in a burrow is emptied and given `tolkienmobs:chests/hobbit_baker`, a baker's pantry of bread, cookies, wheat, cram, honey cake, pumpkin pie and honey. A plan's doorway socket is written on its own front edge, at the row its garden is walked on, and the village reaches no more than four rows below that row.
- **Five of the mod's villages light their streets with a lamppost of their own.** They used to stand vanilla's lamp - a fence post with four torches round its top on the plains, a sandstone post with one torch in the desert - because a street draws its light posts from the vanilla decor pool. The posts the mod has always carried are used now, the one that belongs in each country: the cobblestone and oak post of the human and dwarven villages, the black stone and soul fire post of Mordor, and the sandstone post with its sea lantern in Harad and in Nurn, where its stone is recoloured dark with the rest of the village. Each carries a decor socket at the foot of its post, and each village points its vanilla style's decor pool at a pool of its own which keeps vanilla's own draw - tree, flowers, hay, cactus, nothing - at vanilla's own weights, so only the light post changes; Nurn is the one that keeps no cacti, a cactus garden not being what a farmed lowland looks like. The elven, hobbit and frozen villages keep the vanilla posts they had. Measured after placing a village of each style in a flat world: 6 posts in a Harad village, 4 in a Mordor one, 2 in a Nurn one, and not one of vanilla's, every post with its foot on the ground and its head four rows above it.
- **The dwarven village is built from the five plans it was drawn out as.** It used to put up one house over and over; it now builds the two halls that fill it out, the windmill that stands behind them, and the smithy and the meadhall that front its square. The halls are what the house pool picks most - weight 6 each - and the windmill next at 3, while the smithy and the meadhall are not in the pool at all: they stand on the village's square, so each is built exactly once. Each plan was taken out of the build as it stands, so its plot's own ground is part of the template, the empty corners of its rectangle are written as structure void and so clear nothing they stand over, and the interiors stay air; every chest in a build was emptied and given `tolkienmobs:chests/dwarf_house`, and the smithy, which had none, was given one. Measured in two villages generated in a fresh world: both halls, the windmill, the smithy with its four forge blocks and the meadhall with its blue banners, one of each, with 1,200 and 1,369 blocks of the mod's path laid between them.
- **The human village is built from the five plans it was drawn out as.** It used to put up one house over and over; it now builds the triple house a street grows up around, the single house and the market that fill it out, and the inn and the church that stand about once in a village. The triple house is the plan the house pool picks most - weight 6 - the single house and the market next at 3 each, and the inn and the church once each at 1, so a village is mostly the triple house and its neighbours with an inn or a church only now and then. Each plan was taken out of the build as it stands, so its plot's own ground - the yards, the paths, the floor each house stands on - is part of the template, while the empty corners of its rectangle are written as structure void and so clear nothing they stand over; the interiors stay air, and every chest in a plan is emptied and given `tolkienmobs:chests/human_house`. Each plan carries its own doorway socket on its own south wall, at the column of the outermost door that faces the street - the triple house's south door one row in from its wall, not the doors of its two wings - and the market, which is an open square with no door at all, takes a point in the middle of its front. Measured in a village generated in a fresh world: three triple houses, two single houses and one market, with 3,102 blocks of the mod's path laid as the streets between them, where a house is only kept if its doorway reads on its own front edge.
- **The dwarven ranges are stocked with the mod's own goats.** The Iron Hills and the Blue Mountains carry the battle goats in place of vanilla's sheep and mountain goats - one in a hundred of them arrives with a dwarf already in the saddle - and vanilla goats now graze Forochel, its slopes and the Misty Mountains, which had nothing of their own below the snow line.
- **The three trades that name a building of their own work at the mod's own block.** The grocer worked at a bamboo block, the banker (the coin trader) at a gold block and the trinket smith at ancient debris - vanilla stand-ins, so the trades named for a fireplace, a piggy bank and a trinket table could only be taken at blocks that had nothing to do with them. A fireplace is the grocer's work block now, a piggy bank the banker's and a trinket table the trinket smith's, read from the blocks' own states, so a player can set up a shop for any of the three out of the mod's own furniture. The pet merchant, the junk trader and the trinket tailor keep the vanilla blocks they had. Measured in a dev world, a dwarf left beside each of the three takes the trade its block names - `tolkienmobs:grocery_store`, `tolkienmobs:coin_trader` and `tolkienmobs:trinket_smith` - remembering the job site on the block it stood beside, and the world's own poi data names a site at each of the three.

### Fixed

- **The small hobbit burrow could never stand in a village.** The village drew it, the game refused it, and the village simply showed no house where it should have stood - the burrow's front garden sits seven rows above the plan's lowest row, and a piece that dips more than four rows below its own doorway socket is dropped without a word. Measured in fresh worlds: five rows was refused on both burrows, four joined the street every time, and the small burrow appeared in no village at all. The plan is now cut down to its garden - the three rows taken off are solid hillside under it, which a village buries anyway - and its doorway and resident coordinates were written against the trimmed template; data generation now fails a plan whose doorway sits deeper than four rows above its lowest row, so another one cannot ship unnoticed.
- **A village's church never stood in it.** The church is thirty-one blocks across where every house is fifteen, so the game's own piece fitting, which draws a building onto the side of a street, refused it almost every time the house pool picked it: measured over village after village, not one had a church, and the dwarven meadhall and smithy - twenty blocks across - were dropped the same way. A village with plans like these now paves its meeting point out into a twenty-one block square, and those plans front the square instead of a street. The meeting point's own street ends are moved out to the square's edge, so the streets leave from there and nobody grows across the paving, and the buildings stand on doorway sockets the square writes for them, drawn ahead of any street by placement priority, so exactly one of each stands in every village. A village grows one step deeper to make up for the street ends a square full of buildings costs it. Measured in a fresh world: a human village holds one church (its two enchanting tables) and one inn, a dwarven one a meadhall and a smithy (four forge blocks), each village still laying 1,300 to 1,500 blocks of street.
- **Arda's ground was left as bare stone wherever its map disagreed with the overworld's noise.** Arda reuses `minecraft:overworld`'s noise settings, and the rule that dresses a biome is gated by `abovePreliminarySurface` - a guess at how high the ground stands, read from the noise itself. Arda keeps that noise but throws its height away in favour of the map's, so most drawn columns failed the guess by tens of blocks, the material rules were skipped with it, and the ground came out as the stone the generator had written, with grass only where the guess happened to land. Measured at the reported spot in a fresh world, 269 of 289 Shire columns had stone as their top block, with the flowers placed on it. Arda now lays its own surface from the same materials, unguarded, with vanilla's rule after them for the map's vanilla biomes and for the bedrock and deepslate: that same patch is all grass. The generator also rocks over the noise's own voids under the drawn top, since they were cut for heights the map does not draw - so the caves a player walks into are the carvers' own, cut from the drawn ground, and the brick under the map is solid.
- **A beach could open in the middle of a country.** The shore was read off a column's height alone, so any drawn ground standing within three blocks of sea level became `minecraft:beach`: the Shire is drawn at 68 with four blocks of relief, so its low fields turned to sand with no water in sight - measured at block 6,199 the whole blend is Shire and the top was sand at 65 for hundreds of blocks. A shore now needs a drawn sea among the countries the column blends, and is read off the blended ground rather than the relief laid on it; that spot is the Shire again, and its edges still read as beach where they meet the water.
- **Dry ground in a cell the map drew as sea read as open ocean.** A column's biome came from the cell it was drawn in, so ground that the blend had raised well above the waterline was still named for the sea beside it: at the Dunland-Rohan border, block 6600,1462, the ground stands at 71 under grass and read `minecraft:deep_ocean`. A sea cell whose ground stands dry now takes the biome of the heaviest land country beside it, so that border is Rohan and Dunland again.
- **The drawn water could be left hanging over the ground the carvers took.** The heightmaps were read from the noise's surface rather than the ground the reshape wrote, so the surface rules and the features placed from ground that was no longer there, and the carvers - which cut from the noise's terrain, not the map's - could open a cave through the bed of a drawn sea or river and leave its water standing over air. The heightmaps are re-read from the drawn ground now, and every flooded column is laid again after the carvers: stone where a cave took the bed, water over it to the waterline, and caves enclosed under the bed left alone. Measured in a fresh world at the reported warm-ocean patch, all 49 columns hold water from their bed to sea level with no air in it, and the coast that showed a floating river now runs water to the ground all the way along.
- **Every one of the mod's folk was named "Human" in the villager language table.** A race's no-trade name was written by hand as that one word, so the entry that tells a dwarf from a hobbit named all eight races a man; each race writes its own name now, and the entries read "Dwarf", "Elf", "Hobbit", "Human", "Southron" and "Orc" as they should. The same table had the pet merchant and the junk trader entered the wrong way round - the pet merchant named "Junk Trader" and the junk trader "Pet Merchant", in the per-race entries and in the profession names the game shows alike - and each now names its own trade.
- **The mod's folk were all introduced as "Villager".** 26.1 asks an entity for a type name, and a villager answers with its profession's name - so one that holds no trade, which is every dwarf, hobbit, elf, man, southron and orc of the mod until it finds a work block, called itself a villager in its name tag, in the summon message and in the death messages. A folk without a trade is named after its own kind again, while one with a trade still names itself after the trade, the way vanilla's own villagers do and the way the title of the screen it trades in does: summoned in a dev world, a dwarf comes out as `Dwarf`, a hobbit as `Hobbit`, an elf as `Elf`, a man as `Human`, a southron as `Southron` and an orc as `Orc`, and the same dwarf given `minecraft:armorer` comes out as `Armorer` and given the mod's `tolkienmobs:coin_trader` as `Coin Trader`. Vanilla's own villagers are untouched.
- **The Nazgûl steed's seat no longer points the same way whichever way the animal walks.** Its rider was placed by a fixed pair of world-axis offsets, so a Nazgûl who sat on the saddle facing north hung off the animal's side as soon as the steed faced east. The seat is now the steed's own passenger attachment point - a point in the animal's own frame, which vanilla turns by its yaw - so the rider keeps the saddle at every heading while the seat's height stays where it was. Measured in the dev world at four headings, the rider stands at 0.30 back-right of the middle at 0 degrees, 0.25 left and 0.30 forward at 90, and the same seat mirrored on through 180 and 270, always 1.375 blocks above the steed's feet.
- **A rider sat inside the animal rather than on its back.** All three of the mod's mounts placed a passenger 0.65 blocks above their feet, a figure that belongs under a much shorter animal: the saddle each model carries sits at 1.375 blocks for Shadowfax and the Nazgûl steed and 1.4 for the battle goat, whose body begins at 0.672 and whose back is drawn at 1.375. A dwarf jockey therefore came out buried in the goat's chest, and a Nazgûl in its steed. A rider now stands on the saddle - measured in the dev world, a jockey goat's dwarf at 141.4 over a goat standing at 140.0, and a Nazgûl at 141.375 over its steed.
- **None of the mod's mounts could wear a saddle in 26.1.** Vanilla names the mobs a saddle goes on in `#minecraft:can_equip_saddle`, which is the tag the saddle item's own equippable component reads, and the `Saddleable` interface the port removed is what used to allow the mod's goats and horses; the battle goat and the Nazgûl steed each kept a bare `this.isSaddled()` in the code their jockeys spawn from, which in 26.1 does nothing at all, because a saddle now lives in the entity's own saddle slot. The three mounts are in that tag, and a jockey equips the saddle its model draws, so a jockey goat or steed arrives carrying `minecraft:saddle` in its saddle slot and the saddle the model has always carried is finally drawn; a player can saddle and steer one again once it is tamed.
- **A country's own flocks could not be placed in it at all.** Vanilla's animal rule asks for `#minecraft:animals_spawnable_on` underfoot, which is grass and nothing else, so every sheep, cow, pig, chicken, horse, donkey, rabbit, wolf, goat and bear a country's list carries - and the mod's own aurochs and mumakil - was refused wherever the mod's map paints ground that is not grass: Haradwaith's sand, Nurn's dark stone, the Iron Hills' and the White Mountains' stone, gravel and snow, and the mod's own rock under Mordor and the Ash Mountains. Haradwaith could not place a single one of its mumakil and Nurn not one of its sheep or aurochs. Both passes ask the same rule, so the chunk generation pass that seeds a country as it is drawn refused them exactly as the tick pass did - the daylight the mod had blamed was never the trouble, and there was no daylight in it at all: a chunk being drawn reads as lit. A new `tolkienmobs:country_pasture` block tag names that ground, and the animals a country lists are given it in addition to vanilla's own rule, answered only inside the mod's biomes, so a vanilla desert is still no pasture for a sheep and nothing outside the countries changes. Measured in a fresh world with no player online, where the generation pass is the only thing that can place a mob: 7 mumakil over 64 chunks of Haradwaith, where the same patch of the same seed had none before the change; 3 sheep over 64 chunks of the Iron Hills; and 5 sheep and 2 aurochs over 256 chunks of Nurn, one auroch standing on `tolkienmobs:cobbled_dark_stone`. The Shire's own animals are unchanged, being on grass vanilla already counted.
- **A country's own people were written out as pigs, and met nowhere.** The mod's NPCs - hobbits, men, elves, dwarves and the traders - were registered the way vanilla registers a villager, as a `MISC` mob, and 26.1 will not spawn one of those from biome data: its spawner leaves the misc list out of the categories it rolls and refuses a `MISC` entry outright, and the spawn data quietly swaps such a mob for a pig as it is built. Every NPC entry in the generated biome data therefore read `minecraft:pig`, and no hobbit, elf, dwarf or man was ever met in the field. They are `CREATURE` mobs now - the only category 26.1 spawns out of a biome's list - and their entries sit in each country's own creature list with a spawn placement of their own, so the Shire's hobbits, Lindon's and Lórinand's elves, the Iron Hills' dwarves, and the folk of Rohan, Gondor, Eriador, the Gladden fields and Forochel live where the map puts them again. Data generation now refuses to write a country's list against a category that cannot spawn, so an NPC added later cannot quietly become a pig.
- **Forochel and its slopes were missing from Arda.** No country in the drawing named them, and Arda's biome list is written from that drawing, so neither biome could ever be placed - which also kept the frozen village, the only structure that stands in Forochel, out of the world altogether. The Ice Bay's snowfield now takes the north-west coast above Lindon, with the slopes climbing out of it toward the north end of Ered Luin. Measured in a fresh world, the snowfield stands at 72 and the slopes at 110, and the nearest frozen house is 97 blocks from the coast.
- Things placed against the drawn ground now read the map's water as well as its land. The generator answered every heightmap it was asked for with the drawn ground, even where the map lays water over it, and a village's site check begins at that height and walks down to the first block that is not empty - so a flooded column approved itself, because soil under a lake is dry soil, and a village could be seated on the lake bed. The heightmap type is honoured now: the world surface and the motion-blocking maps are read above the water, and only the ocean floor is the ground beneath it.
- **The cold countries could place none of their animals on snow.** Vanilla's floor test for a land spawn asks for a full top face, and a snow layer never has one, so a snowed column was refused before any spawn rule was reached - no rule of the mod's could put an animal back on a drift, whatever the pasture tag said, and nor could the mod's own floor. Forochel, Forochel Slopes and every range top above the snow line were shut to the polar bears, rabbits and goats their lists carry. The floor is replaced along with the rule now: a country's ground is vanilla's own, or the country pasture with a drift lying on it - the drift counts as the floor itself, since snow only ever settles on a full block - and only inside a country of the mod's, so a vanilla plain keeps vanilla's floor and its snow keeps its quiet. The vanilla animals a country lists each keep the rule vanilla gave them as well, rather than losing it to the pasture clause. Measured in a fresh world with no player online: 30 polar bears and 24 goats placed over 256 chunks of Forochel and its slopes, where the same ground placed none before.

## [5.0.607] - 2026-10-26

### Added

- The config file can switch the mod's content off, in bulk or one mob at a time. `[server.mobs].disableAllMobs` keeps every Tolkien mob out of the world at once, and directly beneath it each of the mod's 55 mobs has a switch of its own - `enableGoblin`, `enableMordorOrc`, `enableShelob` and the rest, each naming the entity it stands for and grouped as ambient, passive, NPC, hostile and boss mobs - so a single boss or animal can be turned off without touching the others. The master switch overrides them all, and a mob is left alone until it leaves a world it was already in. `[server.blocks].disableAllBlocks` and `[server.items].disableAllItems` take the mod's blocks and items out of the creative tabs and out of the recipes that make them, including another pack's recipe for one of them, and put them back when switched on again - a `/reload` is enough. Everything already placed, carried or stored is untouched. A switched-off mob is refused whichever way it was asked for, since every route into a world ends at the same point; note that vanilla's `/summon` still reports success when the entity is refused, because its own command does not look at whether the entity landed.
- A pack can tune the numbers behind the backpack and the camo spawner. `[server.backpack]` sets how far each magnet tier reaches - 16, 32 and 64 blocks by default - and the magnet items' lore states whatever is set, so a tooltip never names a reach the item does not have. `[server.spawner]` sets what a newly placed camo spawner starts with: its detect, spawn, count and cluster numbers and its two spawn delays, vanilla's by default, with the spawner's own screen still able to change any one of them. `[server.gameplay]` gains `trinketCraftTime` and `fireplaceCookTime`, the ticks either block spends on one piece of work, five seconds each by default.
- `[server.worldgen].tolkienRegionWeight` sets how much of a new world belongs to the mod's region: the mod's share is `weight / (weight + 50)`, so the default 7 is about an eighth of the overworld and 1 is about a fiftieth. The region is built into the biome source a world is created with, so a change shows in worlds generated afterwards.

### Changed

- The config file is grouped by who reads each setting. Everything the world decides is under `[server]`, in sections of its own for mobs, blocks, items, gameplay, milestones, the fell beast and world generation, and the two fell beast wind volumes - which each client hears at its own setting rather than the server's - are under `[client]`. `[server.milestone].item` is now `paymentItem`; every other value keeps its meaning and its range, and the options inside a section have lost the prefixes they no longer need. An existing `tolkienmobs-common.toml` is not carried over: the file is rewritten in the new layout on the next start, so anything changed from the defaults has to be set again once.
- The Tolkien biomes now cover about a tenth of a fresh overworld instead of about four per cent, and the twenty of them are close to evenly met. The region's weight beside vanilla went from 2 to 7, and the biomes that only inherited one of vanilla's small climate slots - Firien, the Marshes, Mordor, Ash Mountains, Hithaeglir, Fangorn, Mirkwood Spooky, the Gladden fields, Forochel Slopes, the Iron Hills, the Blue Mountains, the Barrow Downs, Lórinand and Dagorlad - were given parameter cells cut out of the three slots that hold most of the region: vanilla's plains, forest and desert. Each takings follows the biome's own climate and erosion, so a range takes the steep cells, a marsh the flat ones, and Dagorlad and Mordor the hot dry corners of the desert. Measured over 169 columns of a fresh world, every one of the twenty biomes is now within about 2,500 blocks of a random origin, where before fourteen of them sat between 4,700 and 6,100 blocks out.
- All twelve of the mod's flowers are now about as common wherever they grow: a patch of roughly a dozen blooms every other chunk, in the Shire and the Old Forest, the Barrow Downs, the Blue Mountains, Lórinand, Firien, both Mirkwoods, the Marshes and Mordor. They had all shared one profile - a patch every thirty-two chunks - so how often a player met one came down to how much of the ground accepted a flower, and the range ran from about four blooms per chunk on the Barrow Downs to one on a Blue Mountains slope. The patches stay patches: the mountains, the downs and the wastes ask for more attempts because their ground keeps few, the meadows fewer because theirs keeps nearly all, so a patch is about as full everywhere. Each count was measured in a world of that one biome rather than guessed.

### Fixed

- A camo spawner started life with a four-block detect range instead of vanilla's sixteen, so it only ran with a player almost on top of it. The numbers it was handed were in the wrong order; it now starts at vanilla's sixteen and follows `[server.spawner]`. A spawner already built keeps the numbers it saved, so only newly placed ones change.
- Mirkwood and Mirkwood Spooky close in at half the view again, the way both woods did before 5.0. Their fog was set by hand while the mod still drew its own overworld fog; 26.1 reads it from the biome, and the distance it reads is measured against a 1024-block default rather than the view, so a wood that had no distances of its own showed nothing until the far horizon. Both now end their terrain, sky and cloud fog at 96 blocks, in the grey-green their floor casts. The five woods and downs that reported the same in testing - Fangorn, the Old Forest, the Barrow Downs, Forochel and its slopes, and the Misty Mountains - keep the fog they were given then.
- The Blue Mountains' mountain stone counts as ground for the loose rock the way vanilla stone and Mordor's dark stone already did. It is what the dwarven range is built of below its grass and what its caves are cut into, so a pile or a lump of rubble can now sit on it instead of needing soil under every candidate column; the cobbled form counts too.
- Eight of the mod's flowers, the ammolite ore and the rat and gecko were attached only to `minecraft:plains`, `minecraft:dark_forest` and `minecraft:badlands`. The mod's region shares the overworld's climate space with those biomes rather than deleting them, so the patches of them that remain kept the flowers and the animals - but not one of the mod's own biomes grew any of the eight, and a player exploring them could walk the whole of Firien or Barrow Downs without meeting a single one of their flowers. Aeglos now also grows in the Blue Mountains, alfirin, athelas and mallos in Firien, lissuin and niphredil in Lórinand, simbelmyne over the Barrow Downs, and the lily of the valley in the Shire and the Old Forest; ammolite ore is in Mirkwood as well as its dark forest; rats live in the Shire, the Gladden fields, the Old Forest and the Barrow Downs, and geckos in Haradwaith, Nurn and Dagorlad. Every vanilla biome they already named keeps what it had. The elanor, mirkwood-flower, swamp-milkweed and brambles flowers and the squirrel and frog spawns are untouched: the mod's biomes already place them directly, and their modifier entries stay as they were for the vanilla patches.

## [5.0.606] - 2026-10-25

### Added

- The Misty Mountains rise into a range worth the name. Vanilla's height noise still shapes the world, so the summits are built on top of it: one tall stone cone every dozen chunks or so, a six- to ten-block foot climbing thirty to sixty blocks, seated into the slope it stands on and capped in snow over its upper half. They go in after the trees, and because the terrain underneath is untouched the valleys between the peaks keep their shape.
- A key held in either hand shows a camouflage block as itself when the key carries that block's code, so a keystone can be found among the stone it imitates; the dev tool does the same for every camo block while it is held. The sections holding a camo block are rebuilt when the hands change.
- A coin pouch with coins in it banks them when you right-click a piggy bank, and whatever the bank has no room for stays in the pouch. The bank's own menu still opens for an empty pouch or for anything else in hand.
- The camo keystone keeps a pulse length beside its delay, saved with the block, and its screen shows both numbers - the delay first, then the pulse - so each redstone mode has the duration it uses.
- The backpack can carry a magnet. Three tiers - iron, gold and mithril - pull loose items from 16, 32 and 64 blocks away, filling the backpack first and your inventory second, with a fourth toggle in the backpack's button row and `M` as a keybind that also moves that button while the screen is open. The pull works whether the bag is worn, held or standing on the ground, and its screen does not have to be open for it. The best installed tier sets the reach, walls and stone do not stop it, and a drop that is still counting down is left where it lies. Each tier is crafted from the one before it: a U of iron, gold or mithril around the upgrade base or the previous magnet, with ammolite between the prongs. The setting saves with the bag, and a bag saved before the magnet existed loads with the magnet switched on.
- A placed backpack is picked up by sneaking and using it, whatever is in hand. It folds back into its item with everything it held - storage, upgrades, bucket slots, tank, magnet tier, every toggle - and the sleeping bag or campfire it had put out is taken back in first. The item goes into your inventory when there is room and drops at your feet when there is not, and sneaking outranks filling the tank, so a full bucket cannot drink from a pack you meant to lift. Using it without sneaking still opens it.

### Changed

- The mod runs on Minecraft 26.1.2. Building now needs Java 25 and Gradle 9.2.1, against NeoForge 26.1.2.116, GeckoLib 5.5.2, TerraBlender 26.1.2-26.1.2.0.3, Curios 15.0.0 and JEI 29.47.0.111, and `./gradlew compileJava` and `./gradlew runData` both finish clean.
- The rendering library is GeckoLib 5. Its package is `com.geckolib`, and every model and animation now lives under `assets/tolkienmobs/geckolib`, which is where 5 loads them from. Every mob - the bosses, monsters, ambient and passive animals and the trading villagers - plus the three projectiles, the five block entities, the Arda staff and the three NPC profession layers are on 5's render-state API. Every held-item layer builds its props from the state, every controller is built the 5 way, and the dragon's `forceAnimationReset` calls are gone because 5 has no such call. The NPC layers are still attached to nothing, as in the source they came from, so nothing visible changes there.
- The rest of the tree is ported with it: the remaining entity families, the boats, items, blocks, block models and the whole menu, screen and button set, the particles and their descriptions, the entity and block-entity renderers, loot tables, loot modifiers and both recipe families, and the biome, carver, structure, tree and feature data. Saved data uses 26.1's value pipeline with the same keys, so a world loads as it did. Behaviour a player would notice is unchanged: a spider still snatches one item on the same cooldown, a warg still sometimes carries an orc, the walker's pumpkin still never drops, a chest boat still carries a chest, a broken barrel or chest still spills its contents, a boss still shows its bar the moment it is seen and the village professions make the same offers from the same trade sets, a held backpack still locks its slot, and a refused sleep still says why above the hotbar.
- A few things do behave differently, all deliberate. The overworld no longer raises its clouds to y=128, so clouds are vanilla's again. A player paralysed by fear has movement keys cleared rather than movement itself, because 26.1 keeps a player's movement private. An angry mob saved before this update returns calm, since anger is now stored in the shape 26.1 expects. The goat's head-up warning lasts its 40-tick scream rather than as long as its attack goal held the pose. A lockable chest saved by an earlier build starts at the default "Enter Code" and needs its code set once, because 26.1 cannot write a bare string as an entity attachment the way the old save did; after that it saves and reloads as before.
- A world's saved milestone list now lives in `data/tolkienmobs/milestone_data.dat`, which is where 26.1 names saved data. A world carried over from 4.4.83 therefore starts with an empty milestone list; the old file is left on disk, untouched.
- The mod's storage speaks 26.1's transfer API everywhere, so the compile's removal and deprecation notices are back on and only JEI's own recipe API still warns. Every menu slot, block entity, tank and item-held container was on the API 26.1 marked for removal; nothing a player sees changes - the backpack's storage, upgrade and bucket slots, the piggy bank, fireplace, trinket table, camouflage fluid block, coin pouch, key ring and key item screens open, quick-move and filter as they did.
- The generated data is regenerated for 26.1. Each biome's fog, sky, water fog, ambient sound, music and particles are environment attributes now, with its grass, foliage and water colours beside them; the forest flowers place the same number over the same spread; a recipe result no longer repeats a count of one; a trim material no longer names an ingredient; a cooking recipe states which recipe-book tab it belongs to; and the tag that lets the mod's jungle bushes take root is called `supports_vegetation`. Every value a player can see - fog colours, flower density, recipes, trims, village templates and the five loot modifiers - is unchanged.
- Mordor's stone spikes are twice as tall. The lumps stood seven to ten blocks high, which read as boulders beside the biome's rock piles; they run fourteen to twenty now, and the rare needle - one in sixty - forty to eighty where it used to be twenty to forty. A cone's base is built from a fraction of its height, so that fraction was halved and the spikes keep the slender profile they had.
- Data generation refuses to write a village house whose doorway marker is not on its own template's front edge. Such a marker can never join a house to a street - the game drops the whole house - which used to surface only as a town centre standing alone in a world. `./gradlew runData` now stops with the house's style, the marker's position, the template's size and the column it should have used.
- A village house pool no longer offers vanilla's second small plains farm, which 26.1 does not ship; the plains-style villages now offer the house and the farm that exists, and the desert styles around Nurn keep both of theirs. A village's site check reads the terrain column directly and asks its highest block whether it holds a fluid, instead of comparing two heightmaps - the same rule, stated plainly: a lake still refuses a site, open ground, ice and snow still allow one.
- Two things are worth a look in a world: the mod's decay blocks now join `supports_vegetation`, the tag a small mushroom reads, since 26.1 dropped the `mushroom_grow_block` tag they used to share, and the two molten metals no longer declare a translucent render layer of their own because 26.1 picks it from the fluid's textures.
- The mod's version restarts at 5.0. Its build number stands at 606, the number of commits behind this release, so the jar reports 5.0.606; from here the build-number task rolls it to 5.0.607, 5.0.608 and so on, and a 5.0 build can never be mistaken for a 4.3 release.

### Fixed

- A fireplace no longer closes the world. It took a piece of fuel by writing back one item fewer than it had read, and an empty slot reads as a count of zero, so the first tick of a fireplace with an empty fuel slot asked for minus one item and the game stopped with `Expected value to be non-negative: -1`. The slot is written only when it holds something that burns, and one piece of fuel empties it cleanly.
- The backpack opens without closing the world, and its block draws its own texture again. 26.1's menu slot casts its handler to a writable one when a screen fills its slots and the mod's list only spoke the read-and-insert form, so opening a backpack threw a `ClassCastException` before a slot was drawn. The block also named its texture in the item atlas, which a block model cannot sample, so the placed pack was invisible; the texture sits in the block folder now, at the same 32x32 size the model was drawn against.
- A dedicated server starts again. Two classes a server loads named client-only types outright - the leaf pile asked the client's block-colour list for a tint, and the falling-leaf packet did the same while drawing - so block registration and then packet registration stopped with `NoClassDefFoundError` and the run ended in "Mod loading has failed". The tint sits behind a hook the client fills in during its own setup and the packet hands its drawing to a client-only class, so a server generates and saves a world with no error; leaves still fall in the colour of the oak overhead.
- A fireplace no longer loses the food it could not hand back. Its "output full" test read the first slot of the *input* handler, the result was checked loosely against a hard-coded 64, and the write could still come up empty after the ingredients were taken. The check and the write come from one plan now: the output slots are simulated first, two results share a slot only when both fit there, nothing leaves the inputs until a result has a slot waiting, and a named dish keeps its name.
- The trinket table crafts trinkets again. 26.1 asks a slot whether it accepts an item before reporting how much room it has, and the table's output slot answered no to everything, so the table judged its own output full and never finished a recipe - which is why repairing still worked. A trinket, a potion and an ammolite come out of it again, the potion leaves an empty bottle behind, and the trinket keeps its effect.
- The backpack's tank keeps its contents between empty and full whatever it is handed: a negative amount is clipped rather than passed to a handler that refuses one and would have thrown inside the block entity's tick, an amount over capacity is clamped, and emptying goes through the same clamp.
- A fluid tank's level rises from the floor of the gauge. Each sixteen-pixel row was placed at the top of its own tile, so a part-filled row floated up to fifteen pixels above the tank's base and left a gap under it; rows are placed by their bottom edge now, so the level touches the floor and the rows meet without a seam.
- The key ring stores one key per slot, because it merged keys against a fixed 64 while every key stacks to one and its slot holds one. It also picks up keys lying in your inventory: it skipped every key carrying components, which is every key with a code, and now stows any key of the mod's key tag, leaving the key in hand alone.
- All 55 spawn eggs show a sprite again instead of the missing-model checkerboard. 26.1 dropped the two-layer egg template and its pair of tints, so every egg named a model the game no longer has. The images are painted during data generation from art the mod already has: the egg shape, filled with that mob's own 16x16 entity icon pulled towards the colour the egg registered with, a rim of that colour, and shading worked out from the egg itself. The eggs still fall into the groups the old tinted ones did - the horde, the ring-bearers, the beasts, the little things, the free peoples and the bosses - and the two horses, which have no icon, get a face drawn from plain shapes. A new egg still needs only its one line in the entity registration.
- Every item the mod registers on its own has its name back in English and Chinese. A registry-wide check found 46 showing a raw translation key: the backpack, the camouflage spawner, the sixteen sleeping bags and their lore line, the seven torches, the seven sign and seven hanging sign items and the seven trinket bases, plus the seven wall torches and the three armour trim materials. Each is registered as an item rather than as a block item, so the block's name never covered it.
- The Chinese names are tidier too. The Mirkwood torch was 阴郁木火把 - a second translation of "mirk" - while that wood's planks, signs and boat are all 黑森林木, so the torch, its wall block and its item are 黑森林木火把 now. The bark-on-all-sides block is 瑁珑木 rather than 瑁珑木木头, its stripped form 去皮瑁珑木, and a barrel 瑁珑木桶 rather than 瑁珑木木桶, for all seven woods; planks keep 木板, which is how vanilla writes 橡木木板.
- The placard's item names itself and shows its gold lore line, in English and Chinese. The Ring of Aglaral sits in the quest tab now, beside the keys it belongs with.
- The seven trinkets wear in Curios and its window lists their slots. Curios grants no slot on its own and the mod granted only the backpack's, so the trinkets had nowhere to go while the backpack had a home. Players carry head, necklace, body, hands, belt, charm and ring slots now, and the amulet is tagged `curios:necklace` - the slot Curios defines - instead of one no version has.
- The mod's fog is back in the biomes that asked for it. The woods, the barrow downs and Forochel looked as clear as a plain, because the ported handler halved a far plane that 26.1 defaults to 1024 blocks. Fog now comes from each biome's own fog start and end distances: the Misty Mountains at 38 blocks in a pale mist, Fangorn at 80 in the grey of old woodsmoke, the Old Forest and both Mirkwoods at 96, the barrow downs at 64, Forochel and its slopes at 96 in snow-blind white, with the sky and cloud ends following the same distance so a mist closes overhead and not just along the ground.
- The frozen villages of Forochel build their houses. The frozen house is six blocks deeper than the human house whose numbers it borrowed, so its doorway marker fell well inside its own walls, and a marker that deep can never find a street without growing into the house - the house was chosen and then dropped. It carries its marker one block past its own porch now.
- The four lantern families and the two watcher statues bake again. 26.1 decides a face's translucency from the piece of texture its UVs cover and refuses the model when that rectangle falls outside the image, which the lanterns' glass box and the watchers' wing planks did; both are inside 0..16 now. The lanterns' top and bottom faces also point at the glass of their four sides instead of a texture variable the model never declared, so looking down on one no longer shows the checkerboard.
- The fallen-leaf piles generate in all six woods again, and lie on the ground they belong to. The structure-avoidance modifier answered with no positions at all, and a pile then took its height from a heightmap that counts leaves - the canopy - so the soil test beneath it failed. The modifier passes every position through, since the landmark system it consulted is gone, and a pile finds its own floor by walking down to the first soil or water.
- Mordor's rubble, rock piles, stone spikes and dead trees generate again, and a rock pile can be placed by hand there. All of them asked for grass, which dark stone is not; they accept bare rock as ground now, brambles take root in it, and the dead trees skip the sapling check that only lets a tree stand where a sapling would survive.
- Mordor's stone spikes stand up out of the ground instead of hiding inside it, stand on all of the biome's rock, and rise out of its lakes. The spike only wrote into cells that already held rock or soil, which is every cell above the surface, so an attempt spent itself replacing nothing. It now finds the ground under plants and snow and builds upward through the air, fills the water or lava it passes so its foot plugs into the rock under a lake, and counts cobbled and smooth dark stone as ground alongside plain dark stone.
- The Misty Mountains no longer shed dwarven maple leaves; snow is the only thing that falls there.
- Trees in Fangorn and the Old Forest no longer stand on a floating disc of podzol. The mod brings its own ground decorator, which scans down and replaces only the highest solid block of a column when that block is soil, stepping over grass, a flower or a snow layer and stopping at a trunk log or a leaf.
- The camo keystone offers all three of its redstone modes again, with the numbers that go with them. A left click on the mode button landed on 26.1's empty click hook, so the button never stepped and the lever, stone button and pressure plate looked like missing options. A left click steps it on now, every toggle in the mod's screens steps with it, and the screen opens on the mode the keystone is actually set to.
- A camouflage spawner starts the way a vanilla spawner behaves: a player within sixteen blocks wakes it, it respects the mob's own spawn requirements, it does not wait for redstone and it shows its flames. Four of its toggles read backwards, each showing the opposite of the setting it changed, and the redstone and spawn-as-group choices were never written to the save. A spawner read from an older save now falls back to those vanilla numbers instead of zero ranges and zero delays.
- A backpack's magnet gathers without its screen being open. The scan only looked for a bag the player wore or carried, so a placed bag had nothing to fill, and a worn bag without a magnet could be chosen ahead of the one in hand and stop the scan at its zero reach. It reads both hands as well as the inventory now, steps past a bag with no magnet and falls back to the nearest placed bag whose reach covers the player, and only loaded chunks are searched. The bag the key and the button flip is the same one the scan fills.
- A carried backpack shows its sleeping bag and campfire buttons again, and pressing one puts the bed or fire in front of the player instead of leaving the upgrades looking missing. The screen's upgrade window opens closed, and the magnet's button sits in the toggle row between the campfire and upgrade buttons instead of hanging over the panel's edge.
- Breaking a placed backpack in creative gives the backpack back, contents and all. The drop was built from a stack of the block, which has no block item, so it came out empty and evaporated with whatever the pack held.
- A field that was drawn twice over itself is drawn once now: the milestone's name field, the key and keystone code fields and the lockable chest's code field.
- A camo spawner's spawn-egg tooltip no longer throws on a configured spawner: 26.1 stores a spawner's entity data as a different kind of component, and the old read had turned into an unchecked cast that compiled but would have failed the moment it ran.

## [4.4.83-Final] - 10.22.2026

### Added

- Mithril, morguliron and ammolite trims show on the armour icon. Those three are the mod's own materials, so vanilla's sprite atlas has no cut for them and a piece trimmed with one fell back to its plain icon; the datagen now bakes a trim sprite for each material and armour slot by putting vanilla's trim shape through the material's own colour palette - the same swap the game applies to vanilla trims - and each piece selects it by material just like the vanilla ones.
- The fell beast glides. While it was on the wing its animation file's `glide` clip was never asked for, so a beast soaring with nothing to chase beat its wings exactly like one in a fight; it now holds `glide` whenever it is flying without climbing, keeps `fly` for the climb, and still stoops with `dive` when it drops on prey.
- The fell beast really flies. Its own AI was holding it at exactly the height its navigator asked for, so it could never sink or climb on its own; it now beats its wings to climb at about two blocks a second, coasts between beats, and sinks slowly while it glides, holding its path with a wingbeat roughly every second. When its path leads well below it, it commits to a fast stoop instead of easing down. A player riding the beast still steers its height with their pitch.
- The fell beast's wingbeats are heard. Its flap sound was only ever played by a wingbeat routine nothing called, so a beast flying under its own AI moved in silence; each downstroke of its flight now sounds, quieter than the one that lifts a rider and at most about once a second, so a long climb does not turn into a siren.
- A stooping fell beast is heard coming. Its dive is silent, so a beast dropping out of the sky gave no sign of it; the air over its wings now plays while it stoops, and a rider's flight wind is driven by height as well as ground speed, so it no longer fades exactly when the beast drops straight down instead of flying flat.
- The dive wind has a sound of its own, captioned "The beast dives." It borrowed the elytra's wind, so with subtitles on a stooping fell beast read as an elytra whooshing; the beast now carries its own wind event, built from the mod's gust samples.
- The dive wind loops for the length of the stoop. Its gusts are three seconds long but were replayed on a one-second timer, so every one was cut off mid-gust and the seam was audible; the wind is now a purpose-made 7.25 second loop, assembled from the loud middles of those gusts and joined end to start inside a crossfade, recorded at the depth the dive plays at, that follows the beast and ends when the dive does.
- The fell beast's wings are heard while a player rides it. A rider's flight is simulated on their own client, so the beast's AI wingbeat never ran for it and the ride was silent; the wings now beat at the same configured volume and rate while it is being flown, and the beat is played from the server so players nearby hear it too, not just the rider.

### Changed

- The mithril, morguliron and ammolite trim palettes are proper ramps. Each is eight steps from a highlight down to a shadow, and the old strips jumped between extremes - mithril ran bright yellow, near-black, then three whites - so a trim built from them read as speckle rather than metal. The colours now also keep clear of the armour they sit on: mithril's trim is a deeper blue than its silver plate, morguliron's a cold green against its warm black, and ammolite's a blue that separates from its green. The trim icons and sprites are regenerated from the new palettes; the same strips are what the trim colours elsewhere read from.
- The fell beast's wingbeat can be tuned in the mod config. Its volume and the shortest gap between two beats were fixed in code; they are now `fellbeastWingbeatVolume` (default 2.0) and `fellbeastWingbeatInterval` in ticks (default 15, and 20 ticks is one second), read on every beat, so the loudness and cadence can be adjusted by ear without a new build.
- The dive wind's volume can be tuned in the mod config. It was fixed at 1.6 in code; it is now `fellbeastDiveWindVolume` (default 1.6), read while the wind plays, so unlike the wingbeat it can be changed without a new dive. Its pitch is `fellbeastDiveWindPitch` (default 1.0, since the loop is recorded at the depth the dive should sound) alongside it, so the wind can be made deeper or sharper by ear. The wind is heard on each client, so both are the listener's own settings rather than the server's.
- The Dwarven Mining enchant is Dwarven Excavation now, and its description says what it does: it breaks a slice across the face you struck, 3x3 at level I up to 9x9 at level IV, and sneaking breaks only the block you hit. It used to promise a vein of matching blocks, which the effect has not done since it was rewritten. Both languages carry the new wording; the enchantment's id is unchanged, so existing gear keeps it.
- The Dwarven Mining effect's field is `haste` rather than `range`. It was never the area - the area follows the enchantment's level - it is the amplifier on the mining speed buff the effect also hands out, so a datapack that set `"range"` on the effect wants `"haste"` now (nothing the mod ships sets either).
- A rider's dive is quiet. The wings beat for as long as a player flew the beast, even with its nose down; the beat now stops once the beast is dropping more than a block a second and steeper than `fellbeastDiveAngle` (default 45 degrees below horizontal), and comes back as soon as it levels out.
- `homeRadius` can be set as low as 8. Its floor was equal to its default, so the only direction the setting allowed was up.
- The config descriptions for `disableFakePlayer` and `paymentType` say what the two options actually do. One claimed a default it did not have, and the fake-player option was written as though turning it off disabled the effect rather than the fake players.

### Removed

- The `number_of_blocks` option is gone from the mod config. It set a range for the Dwarven Mining enchant, which takes its area from the enchantment's own level now - 3x3 at level 1 through 9x9 at level 4 - so nothing had read it since that change.

### Fixed

- Trimmed armour wears its trim on the icon again. 1.21.4 chooses the trimmed sprite with a `select` on the piece's trim material; the datagen still wrote the old `trim_type` predicates into the model's `overrides` array, which the game no longer reads, so every trimmed piece kept its plain icon however it was trimmed. Each of the twelve pieces now has a select definition over the eleven materials 1.21.4 ships, and resin - new in this version and missing from the old list - has its models too.
- Molten mithril and molten morgul iron are named when placed. The liquid blocks are registered as `mithril` and `morguliron`, while the language file only named `mithril_fluid` and `morguliron_fluid`, so a placed pool showed a raw key.
- Spawn eggs are drawn as eggs again, each in its creature's own two colours. 1.21.4 paints a spawn egg with vanilla's two-layer egg model, using the colours an item model definition tints it with, and the datagen was still writing the older model that named that template with no colours at all - so every egg in the game came out a flat untinted shape. Each of the 55 eggs now gets a definition carrying the two colours it was registered with, and the 55 model files that only pointed at the template are gone.
- Wall signs and hanging wall signs are named again. The blocks are registered as `sign_wall_<wood>` and `hanging_sign_wall_<wood>`, but the language file only named `wall_sign_<wood>` and `wall_hanging_sign_<wood>`, so a sign placed against a wall - and the title of the hanging sign screen you open on it - showed its raw key instead of its name; all seven woods carry the names the blocks actually ask for now.
- Boats and chest boats are named. Their items are `boat_<wood>` and `chest_boat_<wood>`, while the language file called them `<wood>_boat` and `<wood>_chest_boat`, so every boat in an inventory showed a raw key; the item names and both boat entity names follow the real ids now.
- Potted flowers are named. The blocks are `potted_flower_<flower>` and the language file used `potted_<flower>`, so a flower in a pot showed a raw key; the language helper now takes the potted block itself rather than rebuilding its name from the flower, which is what let the two drift apart.
- The second decay bloom block, pipeweed crops, the Arda portal, the Morgul crystal, the six projectiles, the simple trap and the two molten fluid types all have names now; none of them was in the language file at all.
- The camo spawner says which mob it selected or replaced. Both of those chat messages were missing from the language file, so the spawner printed raw keys at the player.
- The Chinese translation covers everything the English one does. The orc and southron villagers with all their trades, four biomes (Nurn, Forochel, the Forochel slopes and the Blue Mountains) and two spawn eggs had been left out, and every name above was missing from it too; the two files now hold the same 1719 keys.
- The fell beast's fireball and the boulder no longer crash the game when they are drawn in flight. Each kept the angles it sits at once it lands in a field that started empty, and its renderer read that field on every frame, grounded or not, so the first frame either one was on screen threw a NullPointerException and took the client down; the field starts at zero now.
- The milestone's dimension cost is a setting of its own again. It and the minimum tick speed were both saved under `minimum_tick_speed`, so the two could not be set apart and the dimension button's lowest allowed cost was whatever the tick speed was set to; the option is `dimension_cost` now.
- Milestones charge levels when the payment method is experience. That branch checked for and spent the configured payment item instead, so a jump priced in levels was refused for lack of coins, or paid with coins while the levels went untouched; it checks and spends levels now.
- The milestone list shows every milestone. It skipped whichever milestone came first in the list, its page count was one short whenever the total was just past a multiple of seven, and scrolling moved the page number without redrawing the list.
- The milestone's experience cost readout keeps its number. Clicking it clamped the cost shown to 1, because its bounds had been copied from the fixed payment icon beside it.
- Hallowed Earth hurts the undead again. Its `disableFakePlayer` option was read as a plain off switch that skipped the damage for every entity, not as a guard against fake players, so with the option on by default the block did nothing at all; it now spares fake players and hurts everything else it was always meant to.
- A milestone refuses a jump it cannot charge for. When the configured payment item no longer resolves to an item, the item branch skipped the charge and the teleport was free; the jump is refused with the usual payment message now.
- The fell beast roars, stoops and swings as it moves. Its animation file carries `roar`, `dive` and three `walk_attack` swings that no controller ever asked for, so the beast opened fights in silence and swung the same standing animation at a run; a bout of aggression now opens with `roar`, losing height on the wing plays `dive`, and a swing made on the move plays one of the `walk_attack` clips.
- The elemental golem's tornado, the web trap and the thrown cobweb are drawn again. None of the three had a client renderer registered, so the tornado and the cobweb ball were invisible and a web that hit the ground left nothing behind; each now registers its GeckoLib renderer, the cobweb draws from the model and texture that were already in the jar, and its idle animation is the one the animation file actually contains.
- The mallorn hanging wall sign is listed with the hanging signs. Its registration named the plain mallorn wall sign where the hanging wall sign was meant, so the game's hanging sign block entity claimed a block that is not one and never listed the one that is; the mod's own sign block entity already covered every hanging sign, so a placed sign was never affected.
- The tornado's funnel animates while it is on the field. Its animation clock was pinned to zero, so the rings never turned and the pillar stayed hidden below the spawn point; the entity reports its own age now, so the funnel rises and its rings spin through the strike.
- The fell beast's attacks use the animations its file has. Its ranged attack asked for `ranged` and its swimming movement and a leftover swim controller asked for `swim` and `swim_fast`, none of which the beast's file defines, so those controllers were left with nothing to play; the ranged attack now plays the beast's `fire`, the beast keeps beating its wings with `fly` while it swims, and the redundant swim controller is gone.
- The mithril and morguliron golems' ranged attack plays a swing. Both asked their animation file for `ranged`, which it does not contain, so the controller reset onto nothing and the golem stood still through the shot; they play their attack animation for it now.
- Gollum's swing no longer blanks his pose. His attack controller asked for an `attack` animation his file does not have, which reset the controller onto nothing and dropped him to his rest pose; he holds his idle pose through the swing instead.
- Villagers sit down properly. Their controller asked for a `sitting` animation that only the dwarf file carried, so an elf, hobbit, human, orc or southron villager who sat lost its pose and snapped back to idle; all six merchant animation files define it now.
- The swamp hag's attack and heal animations play. Her combat controller returned `STOP` in the same tick it set either animation, which makes GeckoLib throw the animation away, so she cast without moving; each branch keeps the controller running until the cast is done.
- Shadowfax's jump plays. The jump branch set the animation and then fell through to the controller's `STOP`, dropping it on the tick it started; the jump now runs for its length, after which running and walking take over again.
- The rock golem and the Istari stop holding a finished attack. Their combat controllers returned `CONTINUE` on every tick, so after a punch, rage, attack or heal ended the controller stayed live on its last frame and the mob kept that pose through idle and walk; both now hand the controller back once the animation is done, and the Istari's attack and heal play once instead of looping.
- The uruk-hai and mordor orcs animate their shots. Their ranged branch waited on the weapon flag, which is set on the server and never synced, so the client could not see it; it now reads the synced shooting flag the bow goal publishes.
- The tree ent and the swamp hag animate their throws. Their attack controllers keyed the throw on the weapon flag, which is an ordinary field set only on the server (and only for a bow in the main hand), so the client never saw it and the throw animation never played; the ranged goals now publish a synced shooting flag that both controllers read.
- Mobs that fight at range are marked as engaged while they throw. The game's throwing goal never touched the mod's aggressive flag, which only the melee and bow goals set, so a snowball, boulder or potion thrower fought with the flag stuck off and the attack animations waiting on it never played; the throwing goals now set it while they run and clear it when they stop.
- The fell beast breathes fire again, in the three parts its file was authored in. Its fire animation waited on the weapon flag, which is set only on the server and only for a bow in hand, neither of which the beast has, so its attack controller could not see it; the breath now runs off the beast's synced breathing flag and plays `fireStart` to wind up, `fireLoop` for as long as the breath lasts, and `fireEnd` once as it stops.
- The uruk-hai and mordor orcs shoot with the uruk bow they carry. The weapon check only looked for a vanilla bow in the main hand, and theirs is a mod bow in the off hand, so no archery goal was ever installed and the bow was decoration; a bow in either hand now chooses the bow goal. They fight as archers from then on, as any bow-carrying monster does.
- Hostile mobs animate their walk while they chase. Their walk controllers also required the mob not to be aggressive, and that flag stays on for as long as the attack goal runs, so a creature running down a target slid along in a static pose; walk now depends only on movement, and the fell beast keeps the faster prowl its file provides for a beast on the hunt.
- Hostile mobs keep breathing while they fight. Every hostile idle controller also required the mob not to be aggressive, so a creature that held a target stood frozen in its rest pose the moment it paused between swings; the idle animation now plays whenever it stands still, aggressive or not.
- The thrown cobweb breathes as it flies. Its animation controller stopped itself right after asking for the idle animation, which makes GeckoLib drop the animation and draw the web on its rest pose; the controller keeps running now, so the web pulses the way its file describes.

## [4.3.81 - Final] - 2026-10-21

### Added

- Added the Blue Mountains, a temperate mountain biome shaped like the Iron Hills but with the mod's mountain stone where vanilla stone would be, and with teal grass and water (#59B6BD).
- Mountain stone counts as stone for ore generation, so the Blue Mountains' underground carries the same ores as any other biome's.
- The Blue Mountains grow dwarven maples, and dwarven villages now start there as well as in the Iron Hills.
- Mountain stone also forms andesite-like veins through the Blue Mountains' stone, so the rock still shows in worlds that never apply the biome's surface rules, such as a single-biome world.
- Human NPCs spawned in a cold climate, such as Forochel, wear the winter clothing textures; humans spawned anywhere warmer keep their normal clothes. The outfit is chosen once, from the biome they spawned in, so it does not change if they later travel.
- Camouflage Smokers can be dyed: right-clicking with a dye tints the smoke a third of the way to that colour, another click with the same dye takes it to two thirds, and a third to the full colour, after which the next click returns it to plain smoke. A different dye starts again at a third. The dye is not used up.

### Changed

- The generated resources are written in the forms the game now uses, so a data run no longer rewrites the same eight hundred files: recipe ingredients carry the short `"item"` or `"#tag"` entry, biomes list their carvers flat and their music as weighted entries with a music volume, trim materials drop the retired item model index, village templates are saved at the current data version with the current block entity and attribute names, and leaf pile models state the UVs the game fills in anyway.
- Data generation runs as the client data run, the run NeoForge 21.4 provides for writing both assets and data; its classpath carries the client library the generator needs to boot. The generator now produces the 800 item model definitions, the fourteen boats included, instead of them being mirrored by hand.
- Blockstates and item models are written by the mod's own generator, since the NeoForge model generators this datagen relied on no longer exist; the generated JSON keeps the same shape.
- Armour sets are now described the way the game expects: each of Mithril, Morguliron and Ammolite has an equipment asset that names its worn texture, so the sets render on players and horses from one definition and the full-set effects are still recognised.
- Spawn eggs are rebuilt on the vanilla item with their two colours moved into the item model, and every mob still registers under its own entity id (the chest boats no longer share the plain boat's id).
- Tools and weapons are built from the game's tool materials again, so every mithril, morguliron and ammolite tool keeps its durability, enchantability, mining speed and damage, and is repaired by the ingot or gem it is made of; the shears keep their three durabilities and repair the same way. The elven and uruk bows stay enchantable even though they never wear out, and the trinkets still take the name of the potion inside them.
- Food and drink effects are carried by the consumable component, so drinks still tip the bottle back and foods still keep their bonuses.
- Tag, sound, particle and village-template datagen no longer asks for the file helper the modding API dropped.
- Recipe datagen builds its recipes through the game's recipe-provider instance, so crafted, smelted and stonecut items keep the recipe ids and unlock conditions they had before.
- Fireplace and trinket table recipes are built on the game's current recipe definitions: each ingredient now carries its own nested entry with its count, so the fireplace still smelts bread from three wheat, the trinket table still shows its fuel in JEI, and both still unlock on the same conditions.
- Potions are registered with the name the game now requires on them, so potions, splash potions and lingering potions keep the names they are shown with.
- Blocks and items are registered with the ids the game now reads while it builds them, so every block keeps its loot table and its name, block items show the name of their block, and items keep their inventory model.
- The clover and moss patches are drawn through the game's own model loader again, and camouflage blocks still take on the look of the block next to them.
- Block entity types are built directly, and the wood wall torches name the floor torch's loot table themselves, so torches still drop the way they did before.
- Creature renderers and models now use the per-frame render state the game switched to, so mobs, boats and thrown projectiles draw the same way they did before.
- Item colours and model properties are registered the way the game now reads them: a trinket's gem layer still takes the colour of the potion inside it, a bow still shows its three drawn frames at the same draw points, and a coin pouch or key ring still swaps its icon as it fills. Item model definitions now sit in `assets/tolkienmobs/items` and name those properties, replacing the old in-model predicates.
- Item model definitions are generated for every item: the model generator now writes `assets/tolkienmobs/items/<item>.json` for each item the mod registers, pointing at the item's model by default, and the bows, pouch, key ring and trinkets declare their dispatch and gem tint in the generator rather than in hand-written files.
- Loot is built on the game's current loot API: block drops keep their shears and silk-touch behaviour, the extra coin a loot modifier adds to a chest or mob drop is still rolled by its chance, and a trinket dropped from a table still picks a helpful potion when the loot is rolled.
- Human NPC winter clothing now follows the `tolkienmobs:climate/cold` biome tag instead of a hard-coded temperature, so a datapack can decide which biomes dress humans for the cold. Iron Hills, Barrow Downs and Hithaeglir count as cold alongside Forochel.
- Mob effects tick on the server now that the game only ticks them there, so the burning, drowning, fear, terror, nimble, sleep, elven life, flying and Eru effects still apply their damage, extra effects, particles and step height, the blacksmith and corrosion effects still repair and wear the player's gear, the lightning effect still calls a bolt down on the afflicted, and paralysing fear still stops the player's movement keys.
- Mobs now deal and take damage through the game's server-side damage hook, so every boss reaction still fires: Shelob still calls her spiders, the golems still apply their elemental effects, and the fell beast still puts a shield on cooldown. Mobs the bosses summon arrive as reinforcements or breeding results rather than anonymous spawns.
- Block entities work on the game's current APIs: a camouflage spawner still reads back the mobs stored in it and shows the chosen one turning above the block, a fireplace still accepts only fuel and takes its burn times from the game's fuel values, trinket tables and fireplaces still find their recipes, the lockable chests still name the key you open them with, and a milestone still charges the item configured as payment.
- Every container screen draws through the game's current widget pipeline: the chests, fireplace, trinket table, backpack, milestone, well, pouch and key screens keep their panels, progress arrows and fluid gauges, the camouflage spawner still turns its chosen mob on the mouse, and the greyed-out buttons still dim when they are switched off.
- Blocks answer the game's current neighbour-change hook, so waterlogged blocks still refill when a neighbour changes, wall torches, lanterns, wall mushrooms, decay blooms, patches and the Arda portal still break when their support is removed, dirt paths still revert to cobble, and a sleeping bag's two halves still link up and still bounce you when you land on them.
- The lightning bug and locust displays draw their parts through the renderer, so the bug body still draws cutout and the lightning bug's glow still overlays it as a separate translucent pass.

### Fixed

- The client gets past its loading screen again. The morgul crystal's renderer asked the vanilla end crystal model for glass, cube and base parts, which this version renamed and moved, so building the renderer threw and the game sat on its splash forever; the crystal now bakes its own copy of the layer it draws.
- Mobs that follow food no longer crash the game the moment one of them ticks. The game added a tempt range attribute that their follow goal reads, and the squirrel, rat, frog, gecko, thrush, crebain, goat, the two horses, the Istari and every heard animal now declare it.
- The molten mithril and morguliron blocks no longer name themselves as their own model parent. The model loader reads that as a loading loop and reported it sixty-four times while the game started; both models now carry nothing but the break particle, the way vanilla's water and lava do, since the fluid renderer draws the liquid itself.
- The Arda Staff draws its GeckoLib model again. Its item model still named the retired `builtin/entity` parent, so the staff could not be drawn at all; it now uses the game's special item model, which hands the staff to the GeckoLib renderer that was already registered for it, with the display transforms and break particles kept on the model underneath.
- The lightning bug and locust blocks no longer ask for a model parent the game dropped. Their block models named `builtin/entity`, which 1.21.4 no longer has, so the model could not be loaded and the block fell back to the missing model; the models are now bare like vanilla's chest - they draw nothing of their own and keep the block's break particles - while the block entity still draws the bug.
- Wooden ladders hang on the vanilla ladder model again. Their generated models named the model's parent without the `block/` folder, which no longer exists in the game's files, so every one of the seven ladders would have come out as the missing model.
- Data generation runs again. The port's last compile errors are gone: enchanted books are built through the game's enchantment helper, tag lookups, registry lookups, carver steps, the solid-render check, the fluid fog parameters and the JEI subtype interpreter all use the shapes the game now has, so the model, loot, tag and recipe providers can be regenerated from the build.
- Entity loot generation completes. The eight NPCs (humans, dwarves, elves, hobbits, Southrons, orcs and their traders) get the empty loot table vanilla gives villagers, and thrown ammo, the trap and the boats stop claiming a loot table they never had.
- The three shears attach their repair tag and tool rules while the item registers rather than in the item's static properties, so a data generation run, where the item registry is already frozen, no longer fails on them. The trinkets' default potion component is attached the same way.
- Barrels open towards the side they are placed against. The generator's direction variants rotate a model whose front is drawn on its top face (a barrel's lid) 180 degrees from vanilla's convention, so the nine barrels now ask for that offset and their blockstates match vanilla's.
- Wall blockstates are written in vanilla's order - the post first, then the four low sides north to west, then the four tall ones - rather than in whatever order the map of wall sides happened to iterate, so regenerating no longer rewrites every wall file.
- JEI shows the fireplace's recipes again. The game stopped sending recipes to the client, so the server now hands the fireplace recipe type to the client when it syncs a datapack, and the recipe category shows the recipes that arrive.
- The Blue Mountains no longer come out as bare stone: their floor keeps its grass, and mountain stone starts in the block beneath it instead of a band of dirt.
- The Camouflage Chest now works as a chest instead of being a plain block: it holds 27 slots, opens on right-click with the player's inventory below them, reads on a comparator, works with hoppers and pipes, and drops its contents when broken.
- Goats warn before they charge again: their melee hook had stopped being called, so the head-down warning never played.

## [4.3.81]  - 2026-10-18

### Added

- Added Forochel and Forochel Slopes biomes, cold snowy plains and mountain biomes based on vanilla snowy plains and snowy slopes.
- Added frozen house village structure (ttmhouse_frozen) that generates in the Forochel biome with human NPCs as residents.

### Fixed

- Generated entity loot tables had a potion id baked back into them, so a table's trinkets could only ever carry the one potion written into the file. The tables ship without the id again and pick a potion when the loot is rolled.

### Removed

- The spider tree's old `ttmspidertree` structure template is gone; nothing referenced it once the structure became `ttmtree_spider`.

## [4.2.80] - 2026-10-17

### Changed

- Barrows rise in clusters instead of standing alone. Barrow-downs now asks for one cluster every ten chunks, and each cluster seats one to five mounds on a single circle, a block or three apart, with its own random facing; a cluster that cannot seat a mound simply comes out smaller.
- The build number and the local changelog entry are written only when a jar is built, so loading the game for testing - client, server or data generation - no longer bumps the version or edits the changelog.

## - 2026-10-17
* Initial automated test build setup

## [4.2.054] - 2026-10-17

### Changed

- The dwarven, human and Nurn villages lay the mod's stone path block where a vanilla village lays a path. Dwarven and human streets, the stubs that close a road off and the fountain plaza still behave the way vanillas do - a tenth of a street still grows over, and street crossing water is still bridged - and the driveway and the porch at each house's door are the block outright. Nurn's roads, its road stubs and the plaza around its well walk on the block too, where the desert village they are built from walked on smooth sandstone; its walls, well and lamps stay dark stone.

### Fixed

- A Nurn lake no longer writes outside the chunks it is allowed to write, which filled the log with `Detected setBlock in a far chunk` errors as the player moved and left the edge of the lake and its grass shore cut off. The whole lake, water and shore, now stays inside the feature's write window.

### Entity AI and behavior

- Changed `meleeGoal` and `bowGoal` visibility from `private` to `protected` in `TolkienMonsterEntity` to allow subclass access.
- Adjusted flying behavior for `GreatEagleEntity` and `GwahirEntity` to fly more frequently by:
  - Reducing `StartFlyingBirdGoal` chance from 120 to 60 (2x more frequent takeoff)
  - Increasing `LandBirdGoal` chance from 240 to 480 (2x less frequent landing)
  - FellBeast behavior modified to save spawn location
  - Fixed bug with Witch King spawning with Fell Beast

### Villages

- Converted desert, dwarf, elven, hobbit, and human houses into villages with connected paths and driveways, wells or fountains, bells, farms, decorations, animals, and villagers. Existing Tolkien inhabitants are retained.
- Villages generate in Haradwaith, Iron Hills, Lorinand, Shire, and Gladden Fields respectively, using one shared placement grid instead of independent house clusters.

### Flying mobs

- Fixed actual takeoff and airborne movement for crebain, thrushes, great eagles, and Gwahir, including flight controls, navigation conflicts, and landing behavior.
- Enforced a 64-block horizontal flight radius and a ceiling 100 blocks above the spawn origin. Flight origin and flying state persist across saves; sitting stops flight.
- Added gliding and eagle dive attacks, updated flight animations, and made great eagles and Gwahir take off more often and land less often.

### Dwarven Miner

- Set the maximum enchantment level to 4: levels 1–4 mine full 3×3, 5×5, 7×7, and 9×9 areas.
- Mining follows the targeted face. Wall patterns extend one block below the target, with the remaining rows above; floor and ceiling patterns are centered on the target.
- Restored normal harvesting, drops, experience, durability, and cancellable break events. Added tool-tier and unbreakable-block checks; sneaking disables area mining, and mining stops when the tool breaks.

### Other entity and data updates

- Moved the ambient animal base to `TamableAnimal` and updated its subclasses; added synchronized gliding and aggression states.
- Added melee combat goals to several hostile mobs, adjusted goblin goal priorities, and exposed shared melee/bow goals to subclasses.
- Fixed the Goblin King's snowball projectile launch and configured its ranged combat independently of held bows.
- Updated potion assignments in ten generated entity trinket loot tables and reordered biome tag entries without changing their membership.
- Fixed the Witch King, the nazgul and the Mordor orc being rendered standing on their mounts: all three now play a sitting pose while riding, and the animation moves the bones their rigs actually have.

### World generation

- Rockpiles dot a biome instead of carpeting it: one placement now fills a 1-3 block patch rather than every surface column, and each chunk gets six placements instead of ten.
- Random rubble, stone spikes and stumps are sporadic decoration. Rubble makes half as many attempts per placement and drops to six placements per chunk; spikes and stumps pass a one-in-six chunk filter and place one or two, and are positioned from their own column's surface instead of the chunk corner's.
- Rock piles recognise a neighbour by block type instead of exact state, so grass carrying snow counts as grass.
- Mirkwood webs hang from the tree canopy. They read a heightmap that never contains a placed tree and ran in the decoration step ahead of the mirkwood trees, so nearly every attempt found open air, rock or sky; they now read the live heightmap and run after the trees, and four placements do the work of twenty.
- Mordor and the Ash Mountains no longer hold water. The dimension's aquifer fills those biomes under the sea level and in pockets high above it, which no biome setting can change, so chunks now turn that water into lava as they load. Terrain generated before this change is converted too.
- Nurn is the watered half of that country: dark stone ground broken up by cobbled patches, tan water, a full 1.0 of rainfall, and no lava of its own anywhere in the biome. Grass grows only near water - on the low shores and islands a few blocks above the water table, and on the meadow a lake lays around its own water - so the biome reads as dry ground with green at its edges.
- Nurn's lakes are dug by a feature of their own, at most two chunks across, and asked for once every 18 chunks so the lowland carries the occasional lake rather than a chain of them. Each one lays the grass meadow around its water, up to sixteen blocks out, and the whole lake stays inside the chunks a feature is allowed to write to, so nothing is left with a missing edge.
- Nurn's villages are the vanilla desert village rebuilt in dark stone. Every sandstone and terracotta shape is swapped for the dark stone block of the same kind, the farm rims that were sandstone stairs become deadwood log fences, the crops stay the desert crops, and the roads get their own terminator pool, because the desert pieces that close a road off are raw smooth sandstone and do not pass through the street templates. A Nurn village only starts on dry ground.

### Trinkets and loot

- A trinket's potion is chosen when its loot is rolled instead of while the loot tables are being built, so the trinkets from one table no longer all carry the same potion. A potion id written into a table by hand still pins that potion.
- Trinket loot only hands out benevolent potions: harming, poison, slowness, turtle master's slowness and the mod's own elemental, corrosion and terror potions no longer turn up on worn belts, rings and amulets.
- Generated biome tag files are written in a stable order, so two data generation runs in a row no longer rewrite them.

### Trinket table

- The table mends damaged gear as well as crafting trinkets. A damaged tool in the trinket slot, its repair material in the potion slot and an ammolite gem in the gem slot undo up to 1000 damage per gem and material, repairing the tool where it sits rather than moving it to the output slot.
- Which material repairs what follows the anvil's own rule, so mod tools take their own ingots, and the mod's shears - the one tool the anvil gives no material at all - now take their tier's repair item.
- The damage one gem and one material undo is a config value, `trinket_repair_amount`, defaulting to 1000.
- What repairs what is a data pack's decision too: an entry under `data/<pack>/tolkienmobs/repair_material/` names the tools, by item or by tag, and the material that mends them. An entry covering a tool replaces the anvil's rule for that tool, so a pack can hand the table a material the anvil would refuse or take away the one it would use.
- The mod ships no entries of its own and the anvil's rule stays the fallback, so an unmodified game repairs exactly as before. Entries are read when the world loads.

- **A sign post is four blocks, and none of them can be taken away.** The plinth is the foot and asks for solid
  ground under it; a shaft stands on a plinth or on another shaft, so a post is as tall as the road needs; the
  cap, two pixels of it, goes up only on a shaft; and an arm hangs off a shaft or a cap with the post behind it.
  All four carry bedrock's own strength, `strength(-1.0F, 3600000.0F)`, keep no loot table and have no recipe,
  so a post planted as scenery stays planted - there is no way to break it, and no way to get one back as an
  item, short of creative.
- **An arm stands in one of four steps of its cell.** The board is four pixels high and its block state carries
  a `level` of 0 to 3, one step for each four pixel band of the block, with a model lifted by the matching
  number of pixels, so arms stack tight up the post and four boards can hang at four heights from one shaft.
  The height is taken from where the player clicked the post, and the direction from the face they clicked, so
  a board placed on a shaft's north face points north with the post behind it.
- **An arm says where it points and how far that is.** Right-clicking one steps it through the places this world
  has actually planted - the catalogue of `Places`, filtered to those with a place in this world, with a blank
  first so a board can be left empty - and shift-right-clicking steps it back. The distance is measured on the
  server from the post to the place's centre and saved with the board, never taken from the player, and the two
  lines are drawn turned to face whoever is looking, as a fingerpost's lettering is read from the road; a board
  pointing nowhere draws nothing at all.

- **The sign post's four items had no model definition and so drew nothing.** 26.1 does not hand an item to the
  renderer through `models/item` alone: it reads `assets/<mod>/items/<name>.json` for the definition, which
  points at the model. The four new items had the model and the parent but not the definition, so the game
  logged `Missing item model for location tolkienmobs:sign_post_plinth` four times at startup and drew the
  plinth, shaft, cap and arm in the inventory as nothing at all - while the blocks themselves, placed in the
  world, drew their textures as they should. The provider writes a plain definition for every item in the mod's
  namespace, so the four are now in the generated pack beside the rest; a datagen run reproduces them.

- **A bed of spikes, in stone and in wood, hurt whoever stands on it.** One block, `SpikeBlock`, is registered twice:
  `spike_stone` and `spike_wooden` share the artist's geometry - eight plates stepping from the full block at the
  floor to four pixels at the top - and differ only in the model that draws them, the stone reading one half of the
  `spikes.png` sheet and the wood the other half. The block is in the cactus's family rather than a trapdoor's: its
  collision is the steps themselves, so a walker goes up and over it, and anything inside it takes two points of
  general damage. The game's own hurt timer spaces that to a hit every half second, so a bed is a nuisance crossed
  quickly and a hazard stood on for long, never an instant kill. Both break as their material and drop themselves.

- **An arm's board now reaches back into the post's cell, so it meets the shaft it hangs on.** The post is two
  pixels wide in a cell of its own, so its face sits eight pixels inside the cell a board hangs in; a board drawn
  from the cell boundary outward therefore hung in mid air beside the post, with the same gap on every arm, laid
  by hand or generated. The plank starts at z -8 rather than 0 now, so the model runs 12.5 pixels out and eight
  back into the post, and the block's own shape follows the model outside its cell - which vanilla allows and
  draws as a board attached to the post. The three lifted models are generated from the changed one, so levels
  1-3 carry the same reach, and the two broad faces got longer uv slices so the wood keeps one texel per pixel
  instead of stretching across the extra length.

- **An arm now takes its facing from the post, so the flat end always sits against the shaft.** Where the board
  points used to come from the face the player clicked or the way they stood, a board could arrive with its arrow
  at the post and its flat end out - which is what a hand-placed arm did. The placement now looks at the four
  sides of the cell for a shaft or a cap and points the board away from it, so the arrow goes out along the road
  whichever face was hit; a cell with no post beside it refuses the arm outright, which is the only real
  condition on placing one.
- **The arm's lettering is painted in black on the board itself.** The destination and the distance used to be
  drawn as a white label turned to face the camera and hung in the air in front of the post, which washed out
  against the sky and read at nothing but close range. They are now set onto the board's own two broad faces, in
  black, turned with the block's model so the words lie along the wood; two lines are scaled so both fit the
  board's four pixel height, a long name shrinks further so a line cannot run off the end, and both faces are
  lettered because which one a traveller reads depends on the way they came.

- **A double arm carries two destinations down one road.** A cell holds one block, so a post that sends a traveller
  two ways along the same road needs one block that draws both boards: `sign_post_arm_double` is the artist's arm
  and a copy of it four pixels higher, eight pixels together, sitting in the lower half of the cell at `level` 0
  (model `sign_post_arm_double`) or the upper half at `level` 2 (model `sign_post_arm_double_2`). Its block entity
  keeps a destination for each board, measured and saved apart from the other - the second under `destination2`
  and `distance2`, so an arm placed before this change keeps the place it had - and each board is lettered on
  both faces in black, four pixels apart. Placing one works as a single arm does, the facing coming from the post;
  a plain click steps the lower board's place on and a crouching one steps the upper, so the two are set one at a
  time.

- **The arm's lettering lost its shadow and starts at the shaft end of the board.** The drop shadow that made the
  words look embossed - and doubled them at a distance - is off, so the ink is flat like paint. The pose is now
  set down at the board's shaft end, two pixels in from where it meets the post, rather than at the middle of the
  board, so a name uses the board's whole length and the words run out toward the arrow; the two faces read along
  the board in opposite directions, so the face whose letters run back toward the shaft is drawn from the end of
  the string instead, and both faces then hug the same end of the wood.

- **The arm's lettering sits up on the board, and its two lines closer together.** The first line started where the
  board's middle was, so with a distance under it the pair sank through the lower edge of the wood; the block of
  text now starts three pixels higher and the lines are set eight pixels apart rather than the font's nine, so both
  lines lie inside the board with a pixel of wood above and below.

- **The sign post models were redrawn, and the level models follow them.** The plinth, shaft, cap, single arm and
  double arm were re-exported from the artist's files; the derived models are generated from those bases rather
  than edited by hand - `sign_post_arm_1` to `_3`, the single arm lifted four, eight and twelve pixels so each
  level of the cell has its board, and `sign_post_arm_double_2`, the double pair lifted eight for the upper half
  of a cell. A change to the arm now reaches every level in one step; a re-export still needs the generator run,
  which is written down in `docs/next-up.md`. Their spans still match what the code assumes (the single arm four
  pixels high and running from eight pixels inside the post's cell to twelve and a half beyond, the double eight),
  so no block shape needed changing. The generated pack was re-run alongside: the item definitions, the two spike
  loot tables and the language file now carry the provider's own output rather than the hand-written copies.

- **The Gundabad and Hithaeglir fog is back to its own distances.** Those two biomes had been pulled in to 44 blocks
  start to finish so screenshots could be taken in them, and the generated files carried that tuning; a data run
  puts them back to the 512 to 1024 the provider gives every other mountain, so the fog no longer crowds the
  halls when a player is walking them for real.
- **The village templates no longer carry `structure_void` filler.** The twelve village houses had a thousand or
  more `minecraft:structure_void` markers apiece, standing in for the air around the build; the generator now
  writes only the blocks the template actually places. The house is unchanged - fifteen by twelve by sixteen, the
  same air, logs, planks, stairs, grass, dirt and wool in the same counts - it is simply a smaller file, and a cell
  absent from a template is left alone when it is placed, exactly as a void marker is.
- **A data run settled the rest of what the sources had drifted from.** The dead bodies of Arda are placed four to
  the chunk as the provider asks rather than two, the dark tower and the warg pit carry the beard-thin terrain
  adaptation their provider gives them, and the dungeon key's chest loot table is gone: nothing points at it and
  it was written by hand, so the generator had been calling it stale on every run.

- **The sign post lettering sits where the board actually begins.** The plank's shaft end is eight pixels behind its
  own cell, half a block further back than the lettering assumed, so the words started half a block along the wood
  with a stretch of blank board out toward the arrow. Anchoring them at the true end carries the ink half a block
  toward the post and off the arrow, which is where a fingerpost's words belong.

- **The old forest road is written down from the walk that scouted it.** A hundred and one ammolite marks were read
  back out of the scouting world and chained into one line: from the mark on Glóin's camp's own ground at 8231, -3641
  east to the last one at 11842, -3877, thirty-eight hundred blocks, with every mark a bend so the road goes where the
  walk went rather than where a search would have guessed. Two of them are bank pairs rather than bends: 8481 and 8520
  either side of the Old Ford, whose water stands at 186 across thirty-three columns with its bed eleven under it, and
  11736 and 11775 either side of the Forest River, water at 167 across the same thirty-three and its bed eleven under
  that. The pass carries only its own three middle columns over water, and only while its grade is above the line, so
  both crossings come out as low fords of the country's own surface rather than as spans. It is laid five wide of the
  wild mix, like every other walked line. Nothing is laid yet: `/tmroad build` standing on the first mark lays it.

- **Water a road is told about is crossed the way it was surveyed.** A route can now name its crossings in
  `Roads.WATERS`: two banks on one axis, the height of the water, the height the road is laid at across it, and a kind -
  `FORD` for a bed laid at the water's own height with the water left standing over it, `SPAN` for a deck carried over
  the water with a pier every fourth station drawn down to the river's floor. The pass lays the whole band across a
  surveyed crossing instead of only its own three middle columns, which is the difference between a narrow causeway
  nobody asked for and a ford or a deck somebody did; a road that names no crossings behaves exactly as before. Both of
  the old forest road's are spans, because eleven blocks of water is not a wade: the Old Ford is decked at 188 over
  water at 186, and the Forest River at 168 over water at 167. Nothing has been laid, so what a deck two over the
  Bruinen's kind of water looks like from the tow is still unseen.

- **The road's grade is settled on halves, and the slab is the step.** The profile used to move in whole blocks because
  a station's height was floored to one, so every riser a road had was a block a walker had to jump, and a terrace had to
  be edged afterwards with a slab to break it into two half steps - a pass of its own that walked every column of every
  road, and that only edged the sides of a terrace where a neighbour on its own axis stood exactly a block lower, leaving
  the corners and diagonals of each step open. The grade is now quantised to half a block after it settles, the step it may
  take between stations is half a block, and a column's surface is read from its own height: the road's own block, plus a
  bottom slab on top where the height falls between two blocks. Nothing compares a column with its neighbour any more, so
  there is nothing to miss at a corner, and the lip stage is retired - it stays in the stage list only so a pass saved by
  a version that had one still finishes instead of stalling. The slab goes over the block rather than into it, which is the
  one thing this had to get right: a slab sunk in the column's own cell is half a block lower than the block beside it, so
  a station the profile puts half a block higher would have been laid half a block lower and every rise built as a fall.
  None of it is laid yet: the old forest road is what will show whether a five-wide stair of half steps reads as a road.

- **The hardened leaves are leaves, and the road stops climbing into them.** `TolkienHardenedLeavesBlock` is a plain
  block rather than a leaves block, so nothing put it in `minecraft:leaves`; the heightmap the road reads its ground
  from ignores that tag, so a wood's hardened canopy was counted as ground and the old forest road climbed into
  Mirkwood and ran along the top of the trees. All nine hardened leaves are now in the tag beside their own leaves,
  which is what the no-leaves heightmap and the road's own cover list both read.
- **A surveyed crossing is laid before the ground is read.** The pass asked what a column's ground was first, and only
  then whether a crossing covered it - so a river column answered with water, a lily pad standing on the water, or the
  bed under it, the crossing never fired, and the grade followed the water down: both of the old forest road's decks
  came out as a road diving into the river and climbing out the other side. The crossing is now asked for before the
  ground, and its height is the grade's own both ways - every station on one is put at the surveyed walk height as a
  pin before the steps settle, so the bank sweeps up onto the deck at half a block a station instead of cutting down to
  the water. A road already laid keeps what it has: `forget` and then `build` lays it again under both fixes.

- **The two crossings of the old forest road are bridges, not ribbons.** The pass already builds spans - two abutments,
  the middles between them and oak-log piers drawn to the bed - but only for a `Route.Crossing`, and the old forest road
  is a `Riverless` line, so both rivers came out as whatever the road's own grade did in the water: at the Old Ford a
  holey ribbon of road one block over the water, at the Forest River the road down the bank and up the far one. The Old
  Ford and the Forest River are now crossing routes of their own beside it, `OLD_FORD_BRIDGE` and
  `FOREST_RIVER_BRIDGE`, written from their east bank west because the pieces are stood from the near bank down the
  span's axis: decks at 188 and 168, the banks' own heights, with the abutments based a block under and the middles two
  under. Nine wide, the width the pieces come in, though the road either side of them is five.
- **A road leaves the water to the span, and arrives on its deck.** What a river column answers is water, or a lily pad
  standing on it, and either was read as ground and graded from - which is how a road came to dive in where it had a
  bridge. The pass now asks the surveyed crossings before it grades a column: a bridge's water is left untouched and the
  road stops at the bank, and every station of the road standing in that water is pinned to the crossing's walking
  surface before the steps settle, so the bank sweeps up onto the deck half a block a station. The crossings themselves
  answer from the routes and not from a table kept beside them, so a span is written once - and a column counts as
  standing on one within twelve blocks of the line its pieces are stood on, because the line is the scout's and the road
  is a search between marks: matched within three, neither crossing was ever met at all.

- **Both new bridge decks come up a block.** Laid at the bank's own height the Old Ford's deck read as sunk into the
  ground either side of it, and the Forest River's the same, so each span is raised one: the Old Ford's deck 189 with
  its abutments based at 188 and its middles at 187, the Forest River's 169 with 168 and 167 under it. The road follows
  without being told where to go - its arrival is pinned to the crossing's own walking surface - so a re-grade steps it
  up onto the deck instead of running into it level. The old pieces are cleared by the sweep that opens a span, so a
  forgotten and rebuilt bridge is not two decks stacked.

- **The campaign's state is written down where the next session looks first.** `docs/next-up.md` is rewritten around
  what is actually left rather than what was next a month ago: the old forest road laid and seen, both crossings standing
  as bridge routes with their decks raised a block and not yet rebuilt, the three rebuild commands in the order they want
  running, the Gundabad roads still unstarted, chapter two's eleven pages wired but never walked, chapter three's plan
  and what it needs before any code, and the loose ends outside the quests - the sign posts, the spikes, the seam
  scene's owed marks and faces, the gate that no pin raises, the biome audit waiting on a decision, and the ford path
  kept in the code with no route using it. `docs/HANDOFF.md` takes a dated paragraph pointing at it, so the long history
  and the running state are not the same file drifting in two directions.

- **A gate already standing is now left alone on two answers, not one.** `GundabadGate.autospawn` read a single
  netherrack course in the west wing, and breaking that one block would have set the whole gate laying again on the next
  start - which writes eight templates' air over whatever had been built on the footprint since. It now also asks the
  build itself through `Sites.standsAt` on the first piece's corner, so a gate whose marker has been mined out is still a
  gate that stands.
- **The keep is not a pin a hand may move.** `/tmpin build keep` wrote a pin at the caller's feet for the keep's
  template, which would move a hold whose corner is written down in `ArdaMap`; it refuses now and names `/tmgate keep`.
  The name `gundabad` no longer means the keep either - the gate is eight pieces with a plan of its own, and calling the
  keep the whole location is how that pin came to look reasonable.

- **The keep is the hold the note walked, and the captain moved with it.** `ArdaMap` carried the first plan -
  `KEEP_X` 5471, `KEEP_Z` -7100, `KEEP_FLOOR` 176 - whose floor top of 178 stood eleven blocks under the passage the hold
  opens off, while the passage's own floor top is 189 and the box measured for the keep is x 5516…5637 by z -7112…-6930.
  It now reads 5516/-7112/187: floor top 189, flush with the hall, its south wall at -6930 where `keepWall()` puts the
  end of the passage. The captain and the wraith are placed from `keepThrone()` and `keepThroneWatch()`, so they follow
  the plan to (5546, 204, -7036) and (5546, 204, -7039); the owner's slab-and-carpet mock-up of the seat at
  (5501, 192, -7024) is a mark rather than part of the build and is left where it stands.
- **The keep's "already standing" answer is the build's own blocks.** It asked whether the doorway's floor was
  `deepslate`, and Arda's rock under the mountain is deepslate too, so every world answered yes and the hold was never
  laid - measured in the test world, nothing stands in either plan's box. It reads the template back through
  `Sites.standsAt` now, opening the hold's box first: an unvisited build is read rather than assumed missing, which is
  the difference between a keep and eight templates' air laid over one.

- **The hall out of the gate is carried to the keep's wall every pass, not once.** `keepWall()` is the keep's number,
  so moving the hold moved the wall it opens off: a world whose gate already stood had the chamber ending at the older
  wall -6930 short by twelve blocks, and `extendHall` only runs when the gate is laid, which a standing gate is not.
  `GundabadKeep.autospawn` now carries it every time: it writes only where the cell is air, so a hall already at the
  wall is nothing to do, and a hold that moves takes its hall with it.

- **The spikes are settled as decoration.** Seen in game and called right: eight stepped plates that drop themselves and
  take two points of general damage on the game's hurt timer, which is the piece doing its job rather than a hazard to
  be balanced - a spike that does nothing is a texture. Nothing was raised about the stepped shape or the stone half of
  the sheet, so both stay as they are and nothing is owed here.

- **The biome audit is recorded as settled, not as waiting.** `docs/arda-biome-canon.md` had its verdict at the foot -
  eleven findings applied, the Barrow-downs' deadwood kept on purpose - while its own opening still read "nothing in this
  file is applied", and `docs/next-up.md` carried that line as an open decision. Both now say what stands, checked
  against the generated biomes: Lorinand gold-green at `#7e8b3e` over `#9da24a`, the Shire a mid green at `#77b255`,
  Emyn Muil down to wolves. The one thing still owed is the owner's eye on the four new leaf textures.

- **The four new leaf textures are final, and the biome audit is closed whole.** The conifer, the fir, the holly and
  Ithilien's southern evergreen were drawn for the shape of each tree and were left in `docs/arda-biome-canon.md` as
  awaiting the owner's eye; seen, they are right as they are - nothing to redraw, nothing to shade differently. With the
  Barrow-downs' deadwood kept on purpose and the other eleven findings carried into the world, every line of that audit
  is now answered.

- **The captain of the Dourhands is stood once, not once per pass.** His seat is filled from page 2-7, and that page stays
  in the player's book until Gloin takes it - so the moment Dugorim was killed he was missing from a page still in hand,
  and the hunt stood another. Walking the line in game showed it plainly: quests through 2-7 worked, and the captain kept
  coming back. He now carries a mark of his fall on the hand that ended him, `DUGORIM_DOWN_TAG`, which is the answer the
  wraith behind him has always had - a scoreboard tag saved with the player - so the seat stays empty until it is paid
  for, and a second player's own hunt is untouched. Confirmed in game: felled once after the fix, he did not come back.

- **The keep's placement is on hold until a world is drawn for it.** Seen in game and called off: the hold at
  5516/-7112/187 does not read right, but the world it was looked at in was built around the older plan - the gate and
  its hall end where 5471/-7100/176 put them, the owner's mock-up of the seat sits forty-five blocks west of the new
  hold, and no keep stands at either plan - so nothing measured there can say whether the new one sits true. It is left
  exactly where the note walked it and read from a brand-new world generated for the quest walk, whose ground the
  current `ArdaMap` draws and whose hold autospawns at that plan; moving a hold twice is cutting a hall twice. Two
  stale lines went with it: `docs/next-up.md` still described both bridge decks as raised and unseen and still gave the
  keep's old numbers as the road's destination.

- **Chapter two is walked to its Gundabad pages, and the seam and Chinese are settled.** Played in the test world from
  the chapter-one seam at page 25 through 2-7, working as written: the courtyard scene, Gloin's camp, Ulmug's fires, the
  hold raised from its page, the vault, Gimli at the well and the captain below the stair. The seam between 25 and 26 was
  already staged and ran smoothly, so it is finished rather than owed - `docs/departure-scene.md` is a record, not a task.
  Chinese is parked whole until a full public release instead of being deferred line by line. Four pages are left to
  walk: 33 to 36. The inner keep's spiral stair stays open for now, a design decision rather than a defect, and no door
  is hung on a stair whose hold is waiting on a freshly drawn world.

- **The keep's stair is final and open, and the rest of chapter two is paused at Carn Dum.** The spiral at the hall's
  well has no door over it and is not getting one: the hold is a place to explore rather than a lock, and page 29 stays
  the dialogue it was, so the last decision open at Gundabad is closed. Pages 33 to 36 - Steps of Evil I and II, Evil
  Tidings, Fire & Ice - are all Carn Dum ground and are paused for now; Carn Dum is what quests go to next, with its
  border camps and the wight's hall the parts nobody has looked at. Two records were corrected with it: Gimli stands in
  his own cave east of the gate on `GundabadCave.post()` rather than beside the well, as the code has had him since the
  hold was walked, and page 31's ground now says so.

- **Only the captain was ever missing his down mark.** The wraith behind him was walked in game on 15 November 2026 and
  needed nothing - killed once, it stayed gone - which confirms the answer Dugorim's fix borrowed was sound all along,
  and that the fault was one seat stood from a page with no mark of its fall, not two.

- **The notes are cut back to a status-first handoff.** `docs/next-up.md` had grown into a mix of history, recipe and
  plan, with settled decisions listed as open ones, a "walk 26 to 36" that the same section paused, and a seam said to
  be both staged and not staged. It is now where we stopped, an eight-row table of what is next, paused, on hold or not
  started, then the detail for each - roads final with their crossings, the chapter's bodies and coordinates, the
  settled list so nothing is re-opened by accident, and what unblocks each hold. `docs/HANDOFF.md` gains the Gundabad
  paragraph it was missing - the gate's doubled answer, the keep's plan and guard, the captain's down mark - and stops
  claiming the raised decks were never seen.

- **The cutter writes its piece table, and can be told to leave named blocks out.** `cut_world.py` streamed a build
  into 48-cubes and then died at the last line: `parts` carries seven fields - the file, its offset and its own size -
  but the table was formatted for four, so every cut it made ended without a `.parts` and `Sites` had nothing to place
  it by. It writes `name dx dy dz wide deep tall` now, which is exactly what `CityCommands.parts` reads and what
  `TolkienStructureProvider.readCut` tolerates. It also takes `--without`, a comma-separated list of blocks to leave
  out, for the one thing in a cut that is not the build: the scout's ammolite markers. Nothing else is ever dropped -
  no material is swapped for air and no layer is thinned - which is the difference between cutting a map and deciding
  what part of it was worth keeping. Checked on a 48-cube holding one marker: 87,089 non-air blocks in the volume, one
  of them ammolite, 87,088 in the tile, and nothing else missing.

- **The Shire map is cut and waiting on a location.** The player's `Hobbiton_Divici` world holds a hand-built Shire in
  its overworld, bracketed by four ammolite markers at (-456, 6, 276), (94, 6, 276), (-456, 6, 743) and (94, 6, 743) -
  551 by 468 across, base y 6, content to y 95 - and every block of it is vanilla, so nothing needs remapping. It is cut
  whole into `run/hobbiton_art`: 143 pieces of 48-cube and a `hobbiton.parts` table, 14 MB, keeping everything except
  the four markers, checked against an independent count of the world (5,079,392 distinct positions on both sides) and
  rendered top-down to see the village, the pond and the field strips come across whole. Nothing ships and nothing is
  placed: it waits on a pin in Arda's Shire, whose cells are x -1024 to 1023 and z -512 to 1023.

- **A cut that is not the city can be laid from a corner.** `CityCommands` stands Rivendell up and knows it by heart -
  the folder a re-cut is worked out in, the table the pieces are listed in, the jar path they are read from and the
  dressing that settles the valley's ground to its walls are four constants with the city's name on them - so any other
  build, cut the same way into 48-cubes with a `.parts` table beside it, could only be looked at by renaming it to
  `rivendell` and dropping it into a copy of a save, which also dragged in the drape, the apron and the outflow pass: three
  passes tuned to that one city and answerable to nothing about this one. `CutCommands` is the plain hand for the plain
  case. `/tmcut list` says what cuts a world is holding; `/tmcut table <cut>` reads one's table and reports its pieces,
  its bands and its box without laying a block; `/tmcut place <cut> [x y z]` lays every piece of one from a corner,
  lowest band first, with the corner read as the cut's own low corner the way a pin is - so a table whose offsets start
  at zero and one whose offsets start forty blocks in both land their north-west bottom block on the block named. Given
  no corner it uses the caller's feet on the ground, which suits a shed and not a map, and says so. A cut is a folder of
  pieces and a `<cut>.parts` table, read from the save's own `tolkienmobs/cuts` so a build under test travels with its
  world, and from the jar's `data/tolkienmobs/structure` beside the city's so a shipped cut needs nothing beside it, jar
  first so a folder left over from an older cut cannot quietly win. It plants no marker, dresses no ground, stands nobody
  and opens no road: what it lays is the blocks that were cut, which is what makes it the tool for putting a map down to
  look at a question of rivers rather than for shipping one. Compiles; nothing of it has been run in game.

- **The Shire's pin is pinned to the channel's own level.** The plan's y 52 came from the fifty-two the Shire's cells are
  painted at, which is not where the ground there stands: read in game at (-631, -436) the bank is grass at 102 and the
  block under the channel's surface is dirt at 100, so the drawn river's water is at 101. The build's water sits 9 over
  its base and its grass line 16, so **y 92** puts the village's river flush with the channel and leaves its grass line
  6 over the bank - a raised terrace above the river rather than a sunken village. The corner moves with it, to
  **(-700, 92, -512)**, which puts the west arm's mouth four blocks off the channel's east bank; its cost is 249 of the
  channel's cells buried under the plateau's north-west strip, since the drawn river runs diagonally across any box that
  touches it, and moving the box east trades that burial for a dug canal (80 cells and 84 blocks at x -620, 32 and 104
  at x -600). The method that produced those counts is a script against the river mask and the cut's own water, not a
  world: the join itself is still unseen.

- **`/tmcut clear` takes the country's own ground back off the top of a cut.** A build laid into Arda arrives interred: a
  hill over the village's grass, a stand of trees over its roofs and, where a drawn channel crosses the footprint, water
  standing in the air over a yard. None of that can be read against the build until it is off, and `/tmcut` had no word
  for it. `clear` takes the cut's name and the same corner `place` was given, and for every column inside the footprint
  cuts from the country's own surface down to the highest block the cut puts in that column and no lower - the cut's
  roads, roofs and trees stay where they were cut, and the country is cut away rather than filled, which leaves the
  wall and the scar that were asked for. The ceiling is read from the cut's files rather than from the world, because a
  world column now holds both: each palette's block list is walked once, air skipped by name, and the per-column answer
  is kept for the session. The Shire cut's ceilings run 95 to 166 at the pin at y 87 - the pond's water at the low end,
  its tallest tree at the high - and every one of its 257,868 columns has one. Compiles; not yet run in a world.

- **`/tmcut clear` now takes the country out of the box as well as off the top of it.** Clearing what stood above a
  build was half the job and looked like it: Arda's ground survived *inside* the footprint wherever it fell below the
  cut's own top, which under a tree's canopy is nearly everywhere, and its gravel survived where an old riverbed
  reached the build's height. Both read as stone standing in a village. The second half of the pass walks every column
  from the cut's top down to its bottom and takes the country out of every position the cut leaves empty, keeping the
  cut's own blocks exactly as they were cut and never touching the ground below the cut's base. The shape it reads
  from is the cut itself: each piece's palette and block list walked once, a bit per position, with each column's own
  span kept so the walk is bounded - 257,868 columns and 5,079,392 positions for the Shire map, held for the session.
  Compiles; not yet run in a world.
- **The city's drape now dresses any cut, and `/tmcut drape` calls it.** The pass that settles the ring of ground
  outside a build to the build's own outline was written for Rivendell and read its pieces through `CityCommands.raw`,
  which knows the city's folder and the cut in the jar - so a build laid by `/tmcut` from the save's own `cuts` folder
  could not be dressed without renaming it to the city. `Dressing` takes a `Pieces` source now: the city supplies one
  bound to its cut, `/tmcut` supplies one bound to the named cut, and everything after the reading - the outline, the
  ring, the water left alone, the holes filled - is the same pass for both, because two drape rules for two builds is
  how a country ends up with two ideas about where a wall stops. Compiles; not yet run in a world.
- **The Shire's drawn river is rerouted round the Hobbiton footprint.** The channel crossed the build's north-west
  corner and the village's own river left on two edges that met nothing: one dead-ending in the hillside, one against
  the far wall. `rivers.png` now bends the channel west before the footprint - down at x -762, clear of the west wall
  by 20 to 44 blocks - and rejoins the old course at z -160, and a tributary runs from the village's south-arm mouth
  (x -586…-436 at z -45) west along the foot of the terrace to meet it at x -752. Drawn at eight blocks to the pixel
  with the same falloff the map's own channels have, checked against the mask's 30% water threshold: the rendered
  before-and-after shows the river running outside the west edge and the tributary along the south, where both used to
  run through and dead-end. Only chunks generated after the edit carry it, so the test world that already holds the old
  course has to be replaced to see it.

- **The drape reads a village's outline from the ground under its trees.** The outline a drape works to is the highest
  block the cut puts in each column, which for a city is its own stone and for a village is a canopy: the ring was built
  up to the leaves, one spire of stone beside every tree along the edge, and the ground the trees stand on was never
  seen. `Dressing` now takes a `trees` flag - leaves, logs, stems and hyphae are skipped while the outline is read - set
  by `/tmcut drape` and left off for the city, whose cut has no trees and whose outline is unchanged. Compiles; not yet
  run in a world.
- **Both of the village's river mouths are joined to the rerouted channel.** The cut's water leaves on two edges, and
  the first mask pass met neither: the west mouth is at world z -203…-176 on the wall at x -700 while the bypass runs at
  x -762, and the south mouth is at world x -234…-178 on the edge at z -45 while the tributary stopped at x -470 - a
  hundred blocks short. `rivers.png` carries a spur east from the bypass at z -190 into the west mouth, its water
  reaching x -680, twenty blocks past the wall, and the tributary is carried east to x -165 so the whole south mouth has
  water in front of it. Both mouths were read off the cut rather than guessed: the edge columns were clustered, and the
  single-column hits every edge carries - waterlogged blocks - were ignored. Only chunks generated after the edit carry
  it. Drawn and measured against the mask's 30% water threshold; not yet seen in a world.

- **The river mask is back at the drawing's own course, and the reroute is parked.** The rerouted channel did what it was
  drawn to do in the mask - clear of the west wall by 20 to 44 blocks, a spur into the west mouth reaching x -680, a
  brook carried along the terrace to x -165 for the south mouth - and read as a canal in the world, because the drawn
  channel runs diagonally, leaves a wide gap at both mouths, and the joins that close the gap run straight across Arda's
  ground for worldgen to carve straight. `rivers.png` is restored to the blob it had before the change (`8ea4f0f`,
  checked byte for byte), and the rerouted version is kept in `run/river_reroute` with the polylines, the widths, the
  banks it produced and a drawer (`draw_mask.py`) that paints a candidate route into a copy of the mask and prints the
  channel's banks at the footprint's wall, so the next route can be measured before it is shipped. The next question is
  the footprint's place against the channel rather than the channel's place against a fixed footprint.

- **The Shire's river is rerouted round the west of the village, a branch is cut out of its south mouth, and the two are
  brought together in a Y south of it.** The drawn channel used to cross the Hobbiton footprint's north-west corner -
  its east bank 116 blocks inside the wall at z -472 - and leave by the west wall; it is cut at z -600 and redrawn as a
  bend west to x -744, easing back to its own course at z -204, so its east bank now stands 20 to 28 blocks clear of
  the wall across the whole crossing and meets the west arm's mouth on the river's own course, where the water is 95
  against the village's 96. A branch runs from the south mouth - 56 wide at the wall narrowing to 36 at the fork - and
  the drawn river, which stopped dead at z 40, is carried on south from its own end to meet it at (-770, 246), the two
  running on as one channel south-west down the vale into the head of the bay at (-1000, 508). The branch needed a
  level of its own: the country outside the south wall is drawn at 105 to 113 against the village's water at 96, and a
  drawn river is laid at the ground, so the arm would have ended against its own wall thirteen blocks under a river
  running past it. `ArdaMap` now reads the branch's water from the village - `SHIRE_WATER` 96 less one block per
  `SHIRE_GRADE` 40 of run along its course, 96 at the mouth down to 80 at the fork, where the drawn river's own is 81 -
  and takes it as a minimum within `SHIRE_REACH` 64 of that course, so nothing else in the world moves; the bed is cut
  under the surface as it already was for every drawn river, which makes the branch a gorge thirteen deep at the wall
  that opens to two at the fork. The bypass stands up to seven blocks over the river it leaves, because the ground west
  of the channel is 5-9 blocks over the channel's own line, and the level reach cuts that into a short chute at the
  corner north of the build - the fall was chosen rather than found. The route, its widths, its levels and the reading
  of it as a plan and a profile are in `run/river_reroute` (`shire_y_interpretation.png`, `shire_y_mask.png` and the two
  scripts). Nothing of it has been seen in a world, and it needs one made after the mask change.

- **The drape takes the ground out of the way of a mouth, and the Shire's branch is cut a valley instead of a slot.**
  The save at Hobbiton's main junction showed four to six blocks of land lying across the water where the village's west
  arm meets the drawn channel. The mouth's own columns were left open on purpose, but every column of the drape's ring
  in front of a mouth clamps to the mouth's own column and so read as water with it, which left the country's bank
  standing eight blocks out. `Dressing.openMouth` now takes the ring's ground down to `CityCommands.MOUTH_DEPTH` (3)
  below the water's top wherever the ground stands over that bed, skipping any column that is already the drawn river so
  the join is the drawing's channel meeting the build's; it is a drape change, so running the drape again clears the bar
  in a world that already exists. The same save showed the village's southern branch as a canal in a trench - its bed is
  cut under water the code reads from the village, and nothing brought the hillside down to it, because a drawn river
  normally stands at the height of the ground it is drawn on and its banks are the country. `ArdaMap.shireBank` is the
  other half of that cut: a four-block shelf at water + 1, then the hillside at half a block per block out to
  `SHIRE_BANK` (96) blocks, with a little roll, so the branch lies in a valley that closes at the country's own height
  rather than between two sheer walls. Measured against the drawn ground, the sections at (-270, 40), (-450, 140) and
  (-650, 210) come down from 105, 112 and 97 to the water's edge and climb back to the country's 108, 109 and 94. The
  bank grading is world generation and needs chunks made after it; neither change has been seen in a world.
- **Bree is sited, and its hill is the world's.** The owner's cut of the town measures 496 by 672 by 108 - markers at
  y 14, highest block 121, the town's ground at 19 to 35 and its roofs at 63, with Bree-hill filling the eastern 166
  blocks and cut off square at the markers. It is pinned at **(1024, -256)**: box x 1024..1519 by z -256..415, in cells
  12/19 and 12/20 only, on ground 101 to 130 (a drop of 28, the Shire village's own), dry, the stream 58 east at its
  southern end, and the drawn cell middle 1280, 256 - where the East-West Road and the Greenway meet - inside it. The
  town is seated on its own grass line, five over the marker course (`BREE_DECK_LIFT`), so the pin's 117 puts it on 122.
  `ArdaMap.breeHill` lays the rock that carries the build's east edge into Eriador: its foot four blocks inside the
  footprint at the build's own top for that depth (read along the edge from the save, 68 to 118), the flank falling a
  block for a block to the drawn country and no further, its corners eased over 32 blocks, the map's folded noise faded
  in off the foot, and drawn river and lake columns left unraised so the stream at the town's foot survives. Highest
  rock 220 at 1515, 30; it meets the country between x 1545 and 1607. `Bree` raises the cut with Hobbiton's pass -
  ground a chunk a tick, pieces at `breePlan()`, the footprint cleared in bands, then `Dressing` with the town's deck and
  its trees left out of its shape - on `/bree raise`, at a start, or when a player is within 192 blocks. Nothing is
  raised yet: there is no `bree` cut, and the note says which tool makes one.
