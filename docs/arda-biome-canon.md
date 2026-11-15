# Arda's biomes against canon — the audit, and what it would cost to fix

Every country's biome was read against what Tolkien actually writes about that region: the generated file in
`src/generated/resources/data/tolkienmobs/worldgen/biome/` (temperature, downfall, precipitation, grass and foliage
tints, fog, features, spawns), the four `arda_*` biome tags and the modifiers that use them (`plants/arda_cover.json`,
`tree/arda_broadleaf.json`, `tree/arda_woodland.json`, `tree/arda_mallorn.json`), the surface floors in
`TolkienSurfaceRules`, and the tree and spawn lists in `TolkienBiomeModifiers`/`BiomeMaker`.

**This file is the record of a decision already taken** - the verdict at its foot, 9 November 2026, is what stands, and
the code has carried it since: `lorinand` is gold-green, `emyn_muil` spawns wolves and nothing else, `shire` is a mid
green rather than lime. The findings above the verdict are kept as the reasoning, so the survey is never redone. Items
are ordered by how loud the mismatch was, not by how hard it was to fix.

## What already stands

Most countries pass. Those checked and left alone, with the passage they match:

- **Enedwaith** — fens and grasslands, wooded in the south-east, fisher-folk on the coast (*Unfinished Tales*,
  "The Port of Lond Daer"). `wetFloor()`'s mud through the grass is the fen; the broadleaf tag is the southern woods.
- **Dunland** — "a green and pleasant country" in the north, hills at the mountains' feet (*The Return of the King*,
  "Many Partings"). Highland floor, herdsmen's animals, no towns.
- **Angmar** — canon gives position and history, not scenery (*Appendix A*). Thin cold soil over broken stone is a
  fair reading and is what the surface rule lays.
- **Ettenmoors, Rhudaur, Cardolan's downs** — stony upland, deadwood, rubble, wolves; the grey highland floor.
- **Brown Lands, the Dagorlad, the Eastern Waste** — "long, formless slopes with a withered look … without trees or
  even grass" (*Fellowship*, "The Great River"). Coarse dirt and gravel, grass only within reach of water, no trees.
- **Mordor and the Ash Mountains** — "broken peaks and barren ridges … grey as ash" (*The Return of the King*,
  "The Land of Shadow"). Dark stone, brambles, no ground cover that survives.
- **Nurn** — "great slave-worked fields" by "dark sad waters" (*The Return of the King*, "The Steward and the King").
  Dark stone shores, grass only where water reaches, its own lake feature.
- **Forochel, the Slopes, Ered Mithrin** — ice, packed ice and snow over stone, Lossoth, polar bears; the one region
  Tolkien describes at length in winter (*Appendix A*, Arvedui).
- **The Blue Mountains and the White Mountains** — mountain stone under grass, and snow over stone and gravel; both
  ranges are named for their look and get it.
- **Haradwaith, Khand, Umbar** — sand and sandstone, olive hills where Umbar's cells rise. See "beyond canon" below.
- **Dale, the Shire, Lindon, Belfalas, Gondor, the Old Forest, Fangorn, Lorinand's trees** — the broadleaf country,
  pipeweed, mallorn and elanor, Fangorn's own oak. The species exist and are where canon puts them.

## Findings, in order of how wrong they look

1. **The Barrow-downs grow trees.** `add_tree_deadwood` names `MORDOR` *and* `BARROW_DOWNS`, and the Downs' feature
   list carries fallen deadwood. Tolkien: "There was no tree nor any visible water: it was a country of grass and short
   springy turf … crowned with green mounds" (*Fellowship*, "Fog on the Barrow-downs"). *Fix:* drop the Downs from the
   modifier and its feature list. Small.

2. **Lórien wears Mirkwood's palette.** `lorinand` is grass and foliage `#0b4431` with fog `#5b5f61` — the exact
   values of `mirkwood`, `mirkwood_spooky` and `marshes`. Canon's Golden Wood is the opposite of that: mallorn leaves
   turning gold, "a light that was golden", elanor and niphredil on Cerin Amroth (*Fellowship*, "Lothlórien"). *Fix:*
   gold-green tints and a light fog. Small, and the loudest of the cheap ones.

3. **The Gladden is a tan plain with a wood on it.** `gladden` is grass `#deb887` (a sand colour), sits in
   `#arda_woodland` (the mod's three grown trees), carries human spawns, and has no marsh plants in its features.
   Canon: "marshes and islets thick with reeds and rushes, and great beds of yellow irises" (*Unfinished Tales*,
   "The Disaster of the Gladden Fields"). **Chapter two's Glóin camp is sited in this biome**, so this is the ground the
   player stands on at that camp. *Fix:* marsh palette, `shore_reeds`/`waterlily` and an iris flower, no woodland tag,
   no humans. Medium.

4. **The Mark is wooded.** `add_tree_culumalda` names `ROHAN`, so culumalda stands across the Mark. Canon: open
   pasture, "the grass swelled like a green sea" (*The Two Towers*, "The Riders of Rohan"), the Wold treeless. *Fix:*
   drop `ROHAN` from the modifier. Small.

5. **Mirkwood is one wood.** All of `mirkwood`, `mirkwood_spooky` and `wild_wood` grow the same `hardened_mirkwood`
   tree. Canon splits the forest three ways: closed broadleaf gloom with ivy and lichen in the north (*The Hobbit*,
   "Flies and Spiders"), **beech** in the east — "most of the trees were beeches … straight grey trunks … deep red
   carpets" — and "a forest of dark fir" over the stony height of Dol Guldur (*Fellowship*, "Lothlórien"), with the
   Emyn Duir's fir slopes in *Unfinished Tales*. *Fix:* a beech belt and a fir south is a new tree feature and a new
   palette per belt. Large — worth doing only if a Mirkwood chapter is planned.

6. **Eryn Vorn is broadleaf.** It is in `#arda_woodland` and its ground features are Fangorn oak's fallen logs. Canon's
   only evidence is Tolkien's own annotation on the Baynes map: "Eryn Vorn [= Black Forest] a forest region of dark
   [pine?] trees" — the question mark is his. *Fix:* a dark conifer over the cape. Medium (the pack has no pine).

7. **Emyn Muil and Khand carry the wrong people.** `emyn_muil` spawns elves, sheep, pigs, chickens, cows, horses,
   donkeys and aurochs; canon gives grey stony ridges and gullies, and no settlement (*The Two Towers*, "The Taming of
   Sméagol"). `khand` spawns Harad's list — Haradrim, mûmakil, a southron — where canon names Variags of Khand and
   gives the land no beasts (*Appendix A*). *Fix:* two spawn lists. Small.

8. **Cardolan and Minhiriath spawn hobbits.** Both list `hobbit` among their creatures. Minhiriath was "almost entirely
   deserted" after the Great Plague and Cardolan emptied by war and plague (*Appendix A*; *Unfinished Tales*,
   Appendix D); hobbits lived in the Shire and Bree-land. Minhiriath was already corrected once for its trees and tints
   and its people were missed. *Fix:* two spawn lists. Small.

9. **Eregion has no holly.** Eregion is in `#arda_broadleaf` (culumalda and lebethron) over the highland floor. Canon
   names the country for holly: "a low ridge crowned with ancient holly-trees whose grey-green trunks seemed to have
   been built out of the very stone of the hills", and the two great hollies at Moria's West-door (*Fellowship*,
   "The Ring Goes South"). The pack has no holly tree at all. *Fix:* one holly feature over Eregion. Medium, and it is
   the most memorable plant in the lands next to chapter one.

10. **Ithilien is generic broadleaf.** Canon's "garden of Gondor" is a Mediterranean wood — fir, cedar, cypress,
    tamarisk, terebinth, olive, bay, ilex, ash, oak, "fragrant herbs and flowers" (*The Two Towers*, "Of Herbs and
    Stewed Rabbit"). We give it culumalda and lebethron. *Fix:* palette and a southern tree. Medium, and far from any
    chapter yet written.

11. **Five biomes share a neon green.** `shire`, `firien`, `hithaeglir`, `iron_hills` and `white_mountains` all use
    grass and foliage `#22f927`, a near-pure green that reads as lime in game; `blue_mountains` uses a teal `#59b6bd`.
    The Shire wants a rich mid-green, and Firien is grass country a chapter will cross. *Fix:* recolour. Tiny.

12. **The Misty Mountains are a glacier.** `hithaeglirSurface` lays `PACKED_ICE` and `ICE` on steep faces. Canon's
    high range is snow over bare rock, and Caradhras is "tipped with snow like silver, but with sheer naked sides, dull
    red as if stained with blood" (*Fellowship*, "The Ring Goes South"). Ice belongs in the highest hollows only.
    *Fix:* snow over stone at height, ice rare. Small; the High Pass is where chapter two's camp sits.

## Beyond canon — do not "fix" these

Tolkien does not describe the ground of **Harad**, **Khand** or **Umbar's hinterland** at all; "Near Harad is desert,
Far Harad is jungle" comes from maps and later atlases, not the text. The only support for heat is his own note that
elephants and camels "would be in place in the blank squares of Harad". **Far Harad's dark forests** exist only as a
simile ("Orcs sprang up them like apes in the dark forests of the South"), and **Khand** has no scenery whatsoever.
Likewise canon is silent on **Angmar's** palette, the **Wild Wood**'s trees (we call it unwitched beech country, which
is a design choice, not a reading), and **Nurn's** grass. These are inventions that do not contradict anything; leave
them as decisions rather than defects.

## The verdict, 9 November 2026

- **1, the Barrow-downs' dead trees: kept.** Not canon, but the downs read as an empty field without them. Closed as
  a choice, not a defect.
- **2, 4, 7, 8, 11, 12: applied.** Gold Lorinand, no culumalda in the Mark, the right peoples in Emyn Muil, Khand,
  Cardolan and Minhiriath, the neon greens recoloured, and snow over rock instead of a glacier on the Misty Mountains.
- **3, the Gladden: applied.** Marsh palette, out of the woodland tag, no humans, and the marsh planted: a reed block
  (sugar cane's plant, the owner's model) standing three high along the water, and a Gladden iris in beds of its own.
- **5, 6, 9, 10: applied.** A dark conifer over Eryn Vorn and Mirkwood's fir south, holly over Eregion, and a tall
  southern evergreen over Ithilien, each with its leaves, its hardened twin and its own biome modifier. The leaf
  textures were drawn for the shape of each tree and were seen and called right on 15 November 2026 - conifer, fir,
  holly and the southern evergreen are final, and nothing is owed on them.
