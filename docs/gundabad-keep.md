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

The template is **`key_structures/gundabad.nbt`: 122 wide, 26 tall, 183 deep**. Its floor is three solid
courses (y 0 … 2, with a few openings in the lowest), so its floor's **top is y 2**, and its interior starts
at y 3. The passage's floor's top is 189 and its interior starts at 190 - which is the whole alignment in one
line: **the keep's floor top must be 189, so the pin's y is 187.**

| | |
|---|---|
| pin | **(5516, 187, −7112)** - the template's own north-west bottom corner, no rotation |
| floor | 187 … 189, level with the passage's floor, interiors both starting at **190** |
| box | **x 5516 … 5637**, **z −7112 … −6930**, roof course **212** |
| across | centred on the passage's axis (x 5577) to within half a block, with ~20 blocks of rock to the west and ~19 to the east inside the crown's 160 |
| south wall | **z −6930**, where the rock stands **230 … 249** over it - the shallowest cover anywhere on the box, and still 18 blocks above the roof |
| north wall | **z −7112**, three blocks past the crown's own end, where the drawn ground is 279 |
| gap to the passage | **none.** The passage is carried to the wall: 53 blocks out from the rear face, its last course on **z −6930** |

Two numbers are choices rather than measurements:

- **z −6930, not −6911.** Flush with the passage's old end, the keep's south 15 to 20 blocks would have their
  upper courses **above the rise**, because the ground there is only 192 – 217 and the keep is 26 tall. It
  would break out of the slope as a stone roof. Nineteen blocks further north puts the whole box under 230
  or more, and the passage was carried those nineteen blocks (`HALL_INTO_ROCK` gives way to the plan:
  `ArdaMap.keepWall()`).
- **x 5516, not 5517.** A 122-wide template cannot centre on a 29-wide passage to the block; 5516 leaves
  twenty blocks of rock west and nineteen east.

**The join is built.** The passage runs 53 blocks at floor 189 / ceiling 200, its last course on the keep's
south wall, and a **29 wide × 10 tall** doorway is cut through that wall. The template has **no door on any
of its four faces** - all four are wall at floor level, which is what an interior level of a dwarven hold
looks like - so the doorway is ours, and it is cut **fourteen courses deep**, which is the deepest the wall
goes across the passage's own columns (one course at the face, one behind most of the eastern half, three to
nine on the west, fourteen where an inner mass stands just inside it). It is cleared every time the hall is
laid, so **raising the keep and then laying the gate again is what opens the door**; until a keep stands
there it is a pocket in the rock.

The keep's plan lives in `ArdaMap` beside the gate's - `KEEP_X` 5516, `KEEP_Z` −7112, `KEEP_WIDE` 122,
`KEEP_TALL` 26, `KEEP_DEEP` 183, `keepPlan()` and `keepWall()` - because the passage's end is the keep's
wall in every world and must not be measured off rock that the hall itself changes.

## Two passes that would wreck it

Both are in `Sites.build`, and both are right for a building on the surface and wrong for one buried:

1. **Seating.** A pinned build's height is *measured*: `plane(...)` reads the ground under the footprint
   with `level.getHeight(MOTION_BLOCKING_NO_LEAVES, …)` and returns `high - sink`. Underground that answers
   **267, the mountain's surface**, so the keep would be laid on the crown - seventy-eight blocks above the
   passage, on the shelf, in the open.
2. **Sweeping.** `sweep(...)` carries the ground around a build to its roofline and, inside the footprint,
   cuts it down to the build's own floor. Buried, it would cut a **122 × 183 pit from 267 down to 187** -
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
That is a problem here that is not a problem anywhere else: the corner at (5516, 187, −7112) is inside solid
rock, so there is nowhere to stand. The three ways out:

- **Coordinates on the command** - `/tmpin build keep 5516 187 −7112`. The smallest change, and the only one
  that makes a buried build pinnable at all without digging to it first.
- **A plan of its own**, the way the gate has `ArdaMap.gatePlan()` and `/tmgate site` - the keep is a fixed
  piece of one location, so it can be written down once rather than measured per world.
- **Digging a shaft** to the corner in creative and standing in it. Works, and leaves a shaft in the rock
  that is not part of the design.

## Which orc pieces belong around it

Sizes are the templates' own: **35 wide × 34 tall × 22 deep** for `orc_hall`, and so on. With the keep
underground, the eight pieces divide between the hold below and the camp at the gate's foot.

| piece | size (w × h × d) | where it belongs, and why |
|---|---|---|
| `orc_hall` | 35 × 34 × 22 | **underground, off the keep's own floor**: floor 187, roof 220, with 47 blocks of rock over it under the crown. A second vaulted chamber of the hold, reached from the keep's side - the one piece big enough to be a room of its own rather than an outbuilding. |
| `orc_hall_2` | 15 × 14 × 20 | **underground**, a guard room beside the doorway at the keep's south wall: the first room met coming up the passage. |
| `orc_tower` | 12 × 22 × 13 | a pair **on the crown at 267**, flanking the pass's axis about x 5540 and x 5610: the hold's watch posts, 22 tall over the mountain's own lid. |
| `orc_butcher` | 23 × 20 × 29 | **the courtyard below**, against the east foot of the rise (z −6900, x 5600): the camp's working block, where the army is. |
| `orc_hut` | 20 × 19 × 19 | **the courtyard**, west side: barracks for the camp. |
| `orc_farm` | 16 × 16 × 28 | **the courtyard's west edge**, where the level 187 runs furthest - the one piece that wants open flat ground. |
| `orc_tent` | 13 × 12 × 14 | **the courtyard**, beside the passage's mouth (x 5600, z −6885). |
| `orc_battle_ram` | 10 × 7 × 18 | **the courtyard**, on the axis under the gate, where a ram is used. |

Read as one picture: **a dwarven hall under the mountain, its guard room at the door, two watch posts on
the crown above it, and the war camp in the courtyard at the gate's foot.** `PASS_PLAZA` is level "because
an army stands on it", and that is where five of the eight pieces go.

## What is settled, and what is not

Settled by the ground and the templates: the keep's floor top is 189, its pin is **(5516, 187, −7112)**, its
box is 122 × 26 × 183 running from z −7112 to −6930, its south wall meets the passage after 19 more blocks,
and it is buried under 18 to 55 blocks of rock with a doorway to cut in a wall that has none.

Not settled:

- **The pin route.** Coordinates on `/tmpin build`, a plan like the gate's, or a dug shaft.
- **The doorway's shape.** 29 wide and ten tall is the passage's own section; a dwarven door might be
  narrower with the full width opening into a hall just inside.
- **Whether the crown is used at all.** Two watch posts at 267 is a skyline the pass's shaping never asked
  for; if the hold is meant to be entirely underground they belong down in the courtyard instead.
- **`orc_hall`'s 34 blocks.** Only 47 of rock stand over it at the back of the crown, and less towards the
  south wall. Where exactly it tunnels off the keep decides how much of the mountain is left over it.
