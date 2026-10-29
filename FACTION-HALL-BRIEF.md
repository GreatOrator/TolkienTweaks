# Faction hall — build brief

One hall per alliance, standing on the village square and built once in every village of the
styles that have one. This is the place a player hands in faction coin and tokens, the place the
envoy stands, and the place a hostile alliance's guards muster.

## Where it goes, and why that decides the size

A hall is a **landmark**, not a house. A house is drawn from a street's sockets, and a street is
11–16 blocks long, so a plan wider than that touches what is already placed and the game drops it
nearly every time — which is why the human village's church (31 blocks across) was never seen until
it became a landmark. A landmark instead fronts the **village square**, the meeting point paved out
to 21 × 21 (`PLAZA_MARGIN = 6` on each side of vanilla's 9 × 9 centre), and its socket sits at the
**middle of one side** of that square, one block above the paving.

So:

| | |
| --- | --- |
| **Footprint** | **17–19 blocks wide, 9–13 deep.** Stay at or under 19 so three landmarks can share a village — a hall on the east or west side is flanked by whatever already stands north and south. |
| **Height** | One storey, 5–7 blocks, or a low two with the upper floor inside the roof. Village houses sit about that tall; a hall should read as bigger without towering. |
| **Ground floor** | Level with the paving outside. No basement, no sunken steps — the piece is placed against the square's edge, and anything below that level is cut off. |
| **Headroom** | At least 3 blocks clear inside, and a 2-wide path from the door to the counter. |

## The door

The front door goes **in the middle of the south wall**, in the build's own frame, facing south —
the same convention the human village's plans use. The game turns the piece to face whichever side
of the square it stands on, so build it once in that orientation and let the rotation do the rest.

Mark it with a jigsaw block placed **one block outside the south wall**, at the outermost column in
the direction it faces (the mod's `requireFrontEdge` check fails `runData` if the entrance sits
inside the footprint). Name and target `minecraft:building_entrance`, joint `aligned`. I will set
the pool, which is what makes it the hall's own landmark pool.

Nothing else in the build should carry a jigsaw. A second entrance — a back door, a side gate —
has to be a plain doorway, not a socket, or the game will try to grow a street out of it.

## The markers

Use **ammolite blocks**, one each, sitting on the floor where the thing belongs. I convert them
during extraction, exactly like the chests in the house builds.

| marker | where | becomes |
| --- | --- | --- |
| **Envoy** | Inside, behind the counter, on the side away from the door | The alliance's envoy entity, placed by the structure so the hall is never empty |
| **Muster** | Outside the front door, 2–3 blocks clear of the threshold, on the paving | Where guards form up when a hostile player comes near |
| **Chests** | One behind the counter, optionally one in a back room | Stamped with the `faction_hall` loot table |
| **Banner** | On the wall behind the envoy, 2 blocks up | The alliance's emblem — the one block swapped per alliance |

Corners of the whole build marked with ammolite in opposing corners as usual, ground level at the
floor, outside air set to void so terrain is not replaced around it. Inside air stays air.

## What's inside

- A **counter or desk** across the envoy's marker, so the hand-in reads as a transaction rather
  than a conversation with someone loitering in a hall.
- **Seating or benches** along one wall — a hall, not an office.
- **A hearth or brazier**, unlit or lit; it is the one warm light in the room and it makes the
  interior worth walking into.
- **Storage**: the chest behind the counter.
- **No beds.** Villagers do not sleep in them and a bed in a village hall invites players to try to
  set their spawn in a structure that may rotate.

## Recolouring per alliance

Build it once in a **neutral palette** and let a processor list swap the load-bearing blocks per
alliance, the way Nurn rebuilds the desert house in dark stone and the Harad lamppost is redone in
sandstone. Keep these five roles visually distinct in the build so the swaps are clean:

| role | build in | West | East | Harad |
| --- | --- | --- | --- | --- |
| **walls** | stone brick | stone brick + lebethron frame | dark stone (Mordor) | smooth sandstone |
| **roof** | wood stairs and slabs | lebethron | deadwood | smooth sandstone |
| **floor** | planks | lebethron planks | deadwood planks | sandstone tile |
| **frame / beams** | logs | lebethron log | deadwood log | mirkwood log |
| **banner** | white wool | blue | black | yellow-and-red |

The banner is the one block whose only job is to say which alliance the hall belongs to, so it is
worth putting somewhere it can be seen from the door.

## Naming and wiring (mine, once the build exists)

- Template: `tolkienmobs:village/<style>/landmarks/faction_hall`
- Plan name in `TolkienVillageProvider.VILLAGES_BY_STYLE`: `faction_hall`, added to the style's
  `houses` list and to its `landmarks` list on a free side, with the entrance coordinate written
  against **this** template.
- Pool: `village/<style>/landmarks/faction_hall`, one element, `placement_priority: 1` — which is
  what makes it stand exactly once per village.
- Processor list per alliance, applied to the element, so the recolour reaches the jigsaw's
  `final_state` too.

Villages that can take a hall today: **human** and **dwarven**, the only two styles with a square.
The Shire, elven, desert, Nurn, orc and frozen villages have no plaza and no landmark machinery, so
a hall there waits until those styles get a square of their own — the same job the human and dwarven
villages already had done.

## What I need from you

1. One hall build in a test world, ammolite corners, ammolite markers for envoy, muster and banner.
2. The world name and the build's coordinates.
3. A yes on the palette table above, or the colours you would rather see.

Then I extract it like the houses, add the three recolourings, wire the landmark and the pool, and
we place a village to see it stand.
