# The keep above the gate

The plan for the next build at Gundabad, worked out from the world rather than from the drawing. Every
number below was read out of `run/saves/Mod Test` (`tolkienmobs/arda`) with `tools/grab_build.py`, or
out of the structure files in the pack, so it is what the ground actually is.

## What the ground behind the gate is

The gate's rear face opens into a **courtyard**, and behind the courtyard the ground is not a mountain to
tunnel into - it is a **cliff up to a flat shelf**.

| where | what it is |
|---|---|
| the courtyard | floor **187**, level to the block, from the gate's back (z −6877) north to about **z −6908**. Clear of rock roughly **x 5535 … 5632** at z −6900, and wider where the pass opens; the mountain's foot rises at its edges, thirty-nine blocks inboard on the west and twenty-four on the east. |
| the passage | **x 5563 … 5591**, floor top **189**, ceiling **200**, carried **34 blocks** north from the rear face, ending at **z −6911** in rock. It is a dead end: the rock ahead of it is solid, and its own roof is the last course at 203. |
| the cliff | from **187 at z −6908** to **267 at z −6941**: eighty blocks of rise over thirty-three of run, about **2.4 per block**. This is `PASS_STEP` 2.5 doing what it was written to do. |
| the shelf | **exactly 267**, from the cliff's top to **z −7109** (`PASS_HEAD_Z` − 232), across **x 5496 … 5656**. That is **160 wide and 168 deep**, level everywhere, and it is *shaped* rather than drawn - `Math.min(shaped, PASS_EDGE_TOP)` clamps the apron to 267 inside the pass's own width, so the shelf is a terrace the pass built for exactly this.
| the shoulders | the drawn country stands at **298 – 299** west of x 5496, east of x 5656, and north of z −7109. The shelf is a level cut between higher ground, walled by the mountain on three sides and by the cliff on the fourth. |
| the surface | stone 717, snow block 533, the pack's own rockpile 337, ice 65 at the top course, sampled across the shelf. Cold, high, and nothing grown on it. |
| the gate's crown | **267** - the towers reach the shelf's own height, so a keep standing on the shelf reads as the gate's continuation rather than a second thing on the skyline. |

The one line to take from the table: **the shelf is the keep's site, and nobody has to cut anything to
make it.** It is level, it is as wide as the gate, and it is held at the gate's crown.

## Where the keep sits

The keep the campaign already knows is the shipped template **`key_structures/gundabad.nbt`: 122 wide,
26 tall, 183 deep**, and it fits the shelf almost exactly.

| | |
|---|---|
| floor | **267**, the shelf's own top - the keep's base course is the shelf, with no bedding pass needed |
| width | 122, centred on the pass's axis **x 5576**, so **x 5515 … 5636** - nineteen blocks of shelf to spare on each side |
| front | **z −6946**, five blocks in from the cliff's lip, which leaves a parapet walk along the drop |
| back | **z −7128** - fifteen blocks past where the shelf ends, so the keep's back stands **into the drawn country** at 299, the way the gate's ends stand into the mountain |
| top | 267 + 26 = **293**, below the shoulders at 298: the keep sits in the notch rather than on the skyline |

So the keep is placed, not cut: its front on the shelf, its back in the higher ground behind, its floor
on the terrace the pass built. The only question the laying itself raises is what the fifteen blocks of
back rock do to a template whose own walls are already there - the same question the gate's ends answered
by carrying rock instead of air.

## The way up

The passage ends at **189**; the shelf is at **267**. Seventy-eight blocks of rise, and the only place to
put them is inside the cliff.

1. **A stair in the rock, as the passage is built (recommended).** A shaft at the passage's end, 13 to 15
   wide, spiralling or switchbacking up the seventy-eight - four turns of a radius-six spiral, or three
   flights of twenty-six with two landings. It is cleared as it is laid and faced with the gate's own
   rock, which is the pattern `GundabadGate.extendHall` already uses, and it touches the shelf only where
   it comes through the floor, which is a doorway and not a cut. The walls of the shaft are the cliff
   itself.
2. **A stair on the cliff face.** Additive, standing against the face rather than cut into it: an
   eighty-block rise needs three flights on the face at 1:2 with landings, and it would be the keep's
   public approach. It leaves the rock alone entirely, but it stands in front of the shelf's lip and
   changes the courtyard's silhouette.

Either way the courtyard stays the courtyard: nothing is cut down, and the shelf is left level.

## Which orc pieces belong around it

Sizes are the templates' own, read from the `.nbt` files: **35 wide × 34 tall × 22 deep** for `orc_hall`,
and so on. The shelf's margins are nineteen blocks wide, which is room for a tower and not for a hall, so
the eight pieces divide by where there is ground for them:

| piece | size (w × h × d) | where it belongs, and why |
|---|---|---|
| `orc_hall` | 35 × 34 × 22 | **on the shelf beside the keep** if the margin is widened, otherwise the keep *is* the hall and this stands at the back of the shelf (z −7080) where the drawn country walls it. Thirty-four tall: it would break the 293 roofline by eleven, which is a thing to look at before committing. |
| `orc_hall_2` | 15 × 14 × 20 | the guard house at the **stair head**, inside the parapet on the west side of the landing: the first thing met coming up, and small enough for the lip. |
| `orc_tower` | 12 × 22 × 13 | a pair, **flanking the stair head** on the cliff's lip at x 5540 and x 5610 - the only place a twenty-two-tall watch post reads as part of the gate's own crown. |
| `orc_butcher` | 23 × 20 × 29 | **the courtyard below**, against the east foot of the cliff (z −6900, x 5600): the camp's working block, where the army is, not on the shelf. |
| `orc_hut` | 20 × 19 × 19 | **the courtyard**, west side, in a row with the butcher - barracks for the camp at the gate's foot. |
| `orc_farm` | 16 × 16 × 28 | **the courtyard's west edge**, where the level 187 runs furthest: the one piece that wants open flat ground and the one the courtyard has room for. |
| `orc_tent` | 13 × 12 × 14 | **the courtyard**, beside the passage's mouth (x 5600, z −6885): the camp's own tents, which is what a tent is for. |
| `orc_battle_ram` | 10 × 7 × 18 | **the courtyard**, on the axis under the gate - a ram is kept where it is used. |

Read as one picture: **the keep on the shelf, its guard at the stair head, and the war camp in the
courtyard at the gate's foot.** The shelf is a fortress's floor and the courtyard is its yard, and that is
also how the pass was shaped - `PASS_PLAZA` is level "because an army stands on it", and the plaza is
where these pieces go.

## What is decided, and what is not

Settled by the ground: the shelf at 267 is the site, the keep's floor is 267, the axis is x 5576, the
front is z −6946, and the camp is on the courtyard at 187.

Not settled, and worth looking at in a world before anything is written down:

- **The stair.** Inside the rock, or on the face. This is the only real build in the plan; everything else
  is placing.
- **`orc_hall`'s height.** Thirty-four blocks above 267 is 301, eleven over the keep's roofline and three
  over the shoulders. Either it stands at the shelf's back where the mountain hides it, or it is the
  silhouette the keep is meant to have.
- **The nineteen-block margins.** If the shelf is to hold outbuildings as well as the keep, it wants to be
  wider - `PASS_HEAD_HALF` is what sets it, and widening it moves the gate's end walls.
- **The fifteen blocks of back rock.** The keep's back is in the drawn ground at 299. What the template
  does there, and what the rock does to it, is the first thing a hand-placed test will show.
