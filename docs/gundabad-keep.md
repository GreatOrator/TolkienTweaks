# The keep under the gate

The plan for the next build at Gundabad, worked out from the world rather than from the drawing. Every
number below was read out of `run/saves/Mod Test` (`tolkienmobs/arda`) with `tools/grab_build.py`, or out
of the structure files in the pack, so it is what the ground actually is.

The keep is **underground and dwarven**: the chamber carried north out of the gate is its approach, and
the keep is the hall that chamber opens into. So this file is about lining the two up, not about a building
on the skyline.

## What the ground behind the gate is

| where | what it is |
|---|---|
| the courtyard | floor **187**, level to the block, from the gate's back (z −6877) north to about **z −6908**; clear of rock roughly **x 5535 … 5632** at z −6900, wider where the pass opens. |
| the passage | **x 5563 … 5591**, floor top **189**, ceiling course **200** (interior 190 to 199, ten tall), carried **34 blocks** north from the rear face, ending at **z −6911**. |
| the rise behind it | from 187 at z −6908 to **267 at z −6941**: about **2.4 blocks per block**, which is `PASS_STEP` 2.5 doing what it was written to do. |
| the crown | **exactly 267** from z −6941 to **z −7109**, across **x 5496 … 5656** - 160 wide and 168 deep, held there by `Math.min(shaped, PASS_EDGE_TOP)`. Walled by the drawn country at **298 – 299** west, east and north. |
| the rock under the crown | **solid**, from the floor to 267. Sampled across the keep's whole proposed box at y 190 … 211: **0 % air, no cavern, nothing to find**. |
| above the crown | 267 to about 279 at the back, so the mountain's own lid over the keep is 18 blocks at the south wall rising to **55 under the crown**. |

## Where the keep sits

The template is **`key_structures/gundabad.nbt`: 122 wide, 26 tall, 183 deep**. Its rooms are **lifted thirteen
courses over its base**: `gundabad_keep.py` cuts the spiral's stair under its own hall, so the five surface rooms
are written thirteen up (`LIFT` 13) to stand on one level with it, and the thirteen courses under them are
`deepslate` fill. A room's floor block is therefore **local y 13**. The passage's floor's top is 189 and its
interior starts at 190 - which is the whole alignment in one line: **the keep's floor top must be 189, so the
pin's y is 176.**

| | |
|---|---|
| pin | **(5528, 176, −7112)** - the template's own north-west bottom corner, no rotation |
| floor | rooms' floor block **189**, walked on **190**, level with the passage's floor and interior |
| box | **x 5528 … 5649**, **z −7112 … −6930**, roof course **201** |
| across | twelve east of centred, so the south wall's 43..47 opening stands at world 5571..5575 inside the passage (5563..5591); the box is still inside the crown's x 5496..5656 with seven blocks to spare |
| south wall | **z −6930**, where the rock stands **230 … 249** over it - the shallowest cover anywhere on the box, and still 18 blocks above the roof |
| north wall | **z −7112**, three blocks past the crown's own end, where the drawn ground is 279 |
| gap to the passage | **none.** The passage is carried to the wall: 53 blocks out from the rear face, its last course on **z −6930** |

Two numbers are choices rather than measurements:

- **z −6930, not −6911.** Flush with the passage's old end, the keep's south 15 to 20 blocks would have their
  upper courses **above the rise**, because the ground there is only 192 – 217 and the keep is 26 tall. It
  would break out of the slope as a stone roof. Nineteen blocks further north puts the whole box under 230
  or more, and the passage was carried those nineteen blocks (`HALL_INTO_ROCK` gives way to the plan:
  `ArdaMap.keepWall()`).
- **x 5528, not 5516.** A 122-wide template cannot centre on a 29-wide passage to the block, and centring it put
  none of its south-wall openings in the passage at all: at 5516 the nearest of the four shared one column with it and
  the one the first plan was written around stood twenty-nine blocks east. Twelve east puts the 43..47 opening at
  5571..5575, which is the door, and leaves seven blocks of crown to the east.

**The join is the hold's own door, 21 November.** The passage runs 53 blocks at floor 189 / ceiling 200 and its last
course is the keep's south wall. That wall carries **four five-wide, seven-tall openings** at local x 13..17, 43..47,
74..78 and 104..108 - read from the template itself, not guessed - and behind them it is two courses deep over most of
the passage's twenty-nine columns, twelve at local 58..63 where an inner mass stands. The plan is written twelve east of
centred so the **43..47 opening stands at world 5571..5575**, inside the passage's 5563..5591; that is the way in. The
gate used to cut a hole the passage's own width, fourteen courses deep, through that wall on every start - which on a
first start the template then wrote over and on any later start took the doorway with it - so **it no longer cuts one**:
the passage stops at the wall and `clearMouth` dresses the ground in front of the door.

The keep's plan lives in `ArdaMap` beside the gate's - `KEEP_X` 5528, `KEEP_Z` -7112, `KEEP_FLOOR` 176, `KEEP_WIDE`
122, `KEEP_TALL` 26, `KEEP_DEEP` 183, `keepPlan()` and `keepWall()` - because the passage's end is the keep's wall in
every world and must not be measured off rock that the hall itself changes. It read 5471/-7100/176 until 15 November
2026, the first plan; x and z moved to the box this file was measured for, and y stayed 176. It was then read as 187
on 15 November, taking the template's bottom courses for the floor the rooms stand on - and a world built with that
number shows exactly what it costs: the keep's rooms on **200** with the chamber's floor on **189**, the chamber
opening into the void under them and the keep's floor standing as the chamber's ceiling, so a hold with no way in.

**Settled, 18 November 2026, at `KEEP_FLOOR` 176.** The 187 was the template's base read as its floor: a fresh
world drew the keep exactly at that plan and had no way in, with the rooms on **200** and the chamber's floor on
**189** - the chamber opening into the void under them and the keep's floor standing as its ceiling. `9f88c9a0`
dropped the eleven courses, so the constants read `KEEP_X` 5528 (5516 until 21 November), `KEEP_Z` -7112,
`KEEP_FLOOR` **176**: the rooms' floor 189, level with the passage, which is the plan this file's own numbers give.
**Walked 21 November:** the floor was right and the entrance was not - see *The join* above. **The check still owed**
is one walk up the passage in a world made after the shift.

**Two things followed the move.** The captain and the wraith are placed from `ArdaMap.keepThrone()` and
`keepThroneWatch()` - the seat is thirty blocks in from the plan's corner, seventeen up and seventy-six back - so they
now stand at (5546, 204, -7036) and (5546, 204, -7039) rather than at the old plan's (5501, 193, -7024). And the
owner's mock-up of that seat, the dark-stone slab at (5501, 192, -7024) with red carpet over it, is left standing
forty-five blocks west and twelve south of the hold: it is a mark, not part of the build, and can be taken out.

**The guard that kept the hold out of every world is fixed with it.** `GundabadKeep.autospawn` asked whether the
doorway's floor was `deepslate` - and the mountain's own rock is deepslate, so it answered "already standing" always.
It now reads the template's own blocks back through `Sites.standsAt`, with the box opened first so an unvisited hold is
read rather than assumed missing.

**And a world that already holds the hold keeps it (21 November).** That guard asked only for the plan this build writes,
so a world laid at the older x would have been answered "no hold here" and had a second one laid twelve east - 110 of
its 122 columns the same rock, the old south wall standing through the new rooms and two doorways facing the passage.
`ArdaMap.keepOlderPlans()` is asked as well: 5516 at `KEEP_FLOOR` 176, and 5516 at the 187 the plan used between 15 and
18 November. A world holding either keeps its hold and only has its mouth dressed, so the twelve-east shift is what a
world made after 21 November gets; it is not a migration.

## Two passes that would wreck it

Both are in `Sites.build`, and both are right for a building on the surface and wrong for one buried:

1. **Seating.** A pinned build's height is *measured*: `plane(...)` reads the ground under the footprint
   with `level.getHeight(MOTION_BLOCKING_NO_LEAVES, …)` and returns `high - sink`. Underground that answers
   **267, the mountain's surface**, so the keep would be laid on the crown - seventy-eight blocks above the
   passage, on the shelf, in the open.
2. **Sweeping.** `sweep(...)` carries the ground around a build to its roofline and, inside the footprint,
   cuts it down to the build's own floor. Buried, it would cut a **122 × 183 pit from 267 down to 176** -
   the entire keep quarried out of the mountain and left in a hole.

The mechanism for both already exists: `Sites.ON_THE_PIN` takes the pin's own height with no question asked
of the ground, and `Sites.SELF_DRESSED` skips the underpinning and the sweep. The shelter camp is in both,
for the opposite reason - it is cut into a hill and must not be tidied - and the keep belongs in both for
its own: **the pin is the measurement, and the rock is what it is in.** `clearMouth` is safe either way: it
returns immediately for a build with no doorway in its seat.

## The pin itself

The keep's plan is written down (`ArdaMap.keepPlan()`), so it needs no pin at all: it is a fixed piece of one
location, like the gate. `/tmpin build <name>` - which pins the caller's feet, and takes them as the
template's **corner** - stays as it is for everything a builder can walk to.

`/tmpin build <name>` pins **the caller's feet**, and the pin is the template's **corner**, not its centre.
That is a problem here that is not a problem anywhere else: the corner at (5528, 176, −7112) is inside solid
rock, so there is nowhere to stand. The three ways out:

- **Coordinates on the command** - `/tmpin build keep 5528 176 −7112`. The smallest change, and the only one
  that makes a buried build pinnable at all without digging to it first.
- **A plan of its own**, the way the gate has `ArdaMap.gatePlan()` and `/tmgate site` - the keep is a fixed
  piece of one location, so it can be written down once rather than measured per world.
- **Digging a shaft** to the corner in creative and standing in it. Works, and leaves a shaft in the rock
  that is not part of the design.

## Which orc pieces belong around it

Sizes are the templates' own: **35 wide × 34 tall × 22 deep** for `orc_hall`, and so on. With the keep
underground, the nine pieces divide between the hold below and the camp at the gate's foot.

| piece | size (w × h × d) | where it belongs, and why |
|---|---|---|
| `orc_hall` | 35 × 34 × 22 | **underground, off the keep's own floor**: floor 189, roof 222, with 45 blocks of rock over it under the crown. A second vaulted chamber of the hold, reached from the keep's side - the one piece big enough to be a room of its own rather than an outbuilding. |
| `orc_hall_2` | 15 × 14 × 20 | **underground**, a guard room beside the doorway at the keep's south wall: the first room met coming up the passage. |
| `orc_tower` | 12 × 22 × 13 | a pair **on the crown at 267**, flanking the pass's axis about x 5540 and x 5610: the hold's watch posts, 22 tall over the mountain's own lid. |
| `orc_butcher` | 23 × 20 × 29 | **the courtyard below**, against the east foot of the rise (z −6900, x 5600): the camp's working block, where the army is. |
| `orc_hut` | 20 × 19 × 19 | **the courtyard**, west side: barracks for the camp. |
| `orc_farm` | 16 × 16 × 28 | **the courtyard's west edge**, where the level 187 runs furthest - the one piece that wants open flat ground. |
| `orc_tent` | 13 × 12 × 14 | **the courtyard**, beside the passage's mouth (x 5600, z −6885). |
| `orc_battle_ram` | 10 × 7 × 18 | **the courtyard**, on the axis under the gate, where a ram is used. |
| `orc_lava_pit` | 22 × 15 × 22 | **the courtyard**, sunk into its floor: a hole ringed with iron and skulls, its bottom 21 cells of lava with water standing over it. The only piece of the nine that **carries air** - 6140 of its 7260 entries - so laying it takes its whole 22 × 22 box out first and nothing standing inside that box survives it. |

Read as one picture: **a dwarven hall under the mountain, its guard room at the door, two watch posts on
the crown above it, and the war camp in the courtyard at the gate's foot.** `PASS_PLAZA` is level "because
an army stands on it", and that is where six of the nine pieces go.

## What is settled, and what is not

Settled by the ground and the templates: the rooms' floor top is 189, its pin is **(5528, 176, −7112)**, its
box is 122 × 26 × 183 running from z −7112 to −6930, its south wall meets the passage after 19 more blocks,
and it is buried under 18 to 55 blocks of rock with a doorway to cut in a wall that has none.

The pin route is settled by the plan (`ArdaMap.keepPlan()`), so nothing has to be walked into the rock.

Not settled:

- **The doorway's shape - settled 21 November.** The passage's own section is 29 wide and ten tall, and the way in is
  the template's five-wide door at 5571..5575 with wall either side of it.
- **Whether the crown is used at all.** Two watch posts at 267 is a skyline the pass's shaping never asked
  for; if the hold is meant to be entirely underground they belong down in the courtyard instead.
- **`orc_hall`'s 34 blocks.** Only 47 of rock stand over it at the back of the crown, and less towards the
  south wall. Where exactly it tunnels off the keep decides how much of the mountain is left over it.

## The marker the auto-placement reads

`GundabadGate.autospawn` stands the gate once per world at `ArdaMap.gatePlan()` and answers "already
standing" from one cell of netherrack in the west wing. The offsets (`MARK_X`, `MARK_Y`, `MARK_Z`) are
counted from the **box's own corner** — `base(plan, Rotation.NONE)`, which is `gatePlan()` less half the
width, i.e. (5496, 187, −6877) — and not from `gatePlan()` itself, which is the middle of the box's north
row. Read from the plan point the same numbers land on (5593, 188, −6852), stone, so the guard answered
"not standing" every time the server came up and re-laid the whole gate: its template's air took the half of
anything standing on the footprint that was inside the box. The marker cell that answers correctly is
**(5513, 188, −6852)**, and it is netherrack in the test world.

## 21 November 2026 - the entrance, settled in a world

The owner walked `Work Verification` up the gate's passage. The floor was right - the rooms on 189, level with the
passage, the number 18 November settled - and the way in was not: the hold's south wall reached the chamber with wall
over twenty-six of its twenty-nine columns, and the doorway the plan was written around stood twenty-nine blocks east
of the passage. The template's own file settled the shape of it: four five-wide openings at local x 13..17, 43..47,
74..78 and 104..108, wall two courses deep behind them, twelve at local 58..63. The plan moved **twelve east** to
`KEEP_X` 5528, which puts the 43..47 opening at 5571..5575 in the chamber, `DOOR_X` moved with it (106 to 45) so the
mouth is dressed at the door that is used, and the gate stopped cutting its own hole through the wall. **Not seen:**
the walk into the rooms and the hold's first room in a world made after this.

## 21 November 2026 - the way in, walked again

A second fresh world stood the keep at its plan and put the doorway the plan was written around in the chamber, and
the passage still did not reach it. The hall carried its floor, its roof and its two walls the fifty-three blocks to
the wall, but **never cleared its own inside**: the air was written through `base.offset(x, y, z)`, where y and z
are already world numbers, so it went to the cell the gate's corner plus 190 to 199 and -6878 to -6930 gives -
about 377 courses up and seven thousand blocks north. The mountain therefore went on standing in the passage, and
what separated the hold's mouth from the gate's hall was **thirteen blocks of its rising ground, z -6910 to
-6922**. `clearMouth`'s eight blocks out from the wall were the only rock ever taken out of the passage, which is
what made the two read as a tunnel stopping short.

`extendHall` now builds the cell from the corner and the passage's own x - `new BlockPos(base.getX() + x, y, z)` -
so the inside is cleared from the rear face to the wall. The same double offset sat in `undressBack`, the strip
behind the gate that a world laid before the drape learned to keep off the back needs once, and is fixed there with
it. Nothing in the plan moves: the passage still runs 53 blocks at floor 189 / ceiling 200 and its last course is
the wall at -6930, with the inside cleared to the course before it - the wall itself is never cleared, since the way
in is the template's doorway in it and a hole the passage's width would take the door out on every start after the
first. A world already holding the hold gets its passage cut on the next start, because the hall is carried on
every pass; no world needs to be made again.

**Not seen:** the cleared passage in a world. The next start of a world holding the hold is the check, and the clear
logs nothing - the check is the walk, past the sweep's eight blocks and through the door into the rooms.
