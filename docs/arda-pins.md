# Pinned ground

Where a location stands, when somebody has walked the world and said so.

The hands-on guide - every command, what a pin does to placement, the ground still to scout and the traps
worth knowing - is [`arda-pinning.md`](arda-pinning.md). This file is the table itself, and why it exists.

## Why this exists

The campaign places ground by asking the drawing: a country's middle, then a cell's middle, then the
nearest column inside that search whose footprint will sit level and dry. That answers "somewhere in
this part of the country", which is the right answer for a world nobody has looked at - but it is not
an answer to "here".

A pin is that answer written down: the block a location stands on, chosen in a world whose hills have
already been made. `Pins.SOURCE` holds one for every world, and both things that place ground read it
before they read the drawing:

| what was scouted | key | asked by |
| --- | --- | --- |
| a **name** - a circle of ground announced on arrival, with folk standing in it | the place id: `rivendell`, `thorenhad`, `barachen`, `ford` | `Places.ensure` |
| a **building** - a template raised for the player who was sent to it | the template id: `tolkienmobs:key_structures/barachans_camp`, `tolkienmobs:key_structures/ttmruin_large_2` | `Sites.survey` |

## The table

A line is a key and a block:

```java
Map.entry("thorenhad", new BlockPos(4864, 157, -3328)),
```

The y is the surface the scout stood on: a note, not a measurement, because a building is still seated by
the laying. The x and z are taken as given. `/tmpin` prints exactly this line, which is why a pin kept for
every world is a copy rather than a transcription.

Empty is the table's normal state and a working one - a location with no pin is surveyed from the drawing
as it always was - so nothing here has to be filled in for the campaign to be walked.

## Where a pin lands on a building

A pinned build is laid from its **north-west bottom corner** unless it is on the front-corner list, and the
height is measured off the ground under the footprint rather than taken from the pin - `Pins`' own note
says so, and it is why a pin a block or two out in y is not an error.

The orc pieces are the exception. They are placed by hand against a wall or the back of a yard, so they
are pinned at their **front**: the pin is the front's south-west corner, the piece runs back from it by its
own depth, and it sits **one course below** the pin rather than being seated on the ground. The two lists in `Sites` are
`FRONT_CORNER` and `ON_THE_PIN`, and they carry the same eight templates.

The corner came out of the owner's own placing rather than from a guess: an `orc_hall` pinned at
**(5533, 188, −6891)** was laid by hand at **(5533, 188, −6912)** - the pin 21 north, which is the hall's
depth of 22 less one. Laid by the world, the same pin had put the hall at **(5533, 201, −6891)**: the right
corner, but seated thirteen blocks up on the courtyard's surface, which is why the pin's own height is used
at all.

The y is then ours, not the ground's: the base course sits **one below the pin**, so a pin taken in the block
a builder stands in puts the floor on the ground they stood on. (`FRONT_DROP = -1`.) The owner's by-hand hall
is one course above where this rule places it, and the rule is the one they asked for.

`orc_hall`'s four faces are all open at ground level - it is a pillared hall on a grass floor rather than a
walled box - so "front" is a convention here and not a doorway. The convention is +z: the face at the
greater z is the front, which is the face that meets the yard it was built against.

## The orc pieces bring no draping

They are on `Sites.SELF_DRESSED` as well, so the world does not dress the ground around them: no `sweep` -
the pass that carries the ground out to the build's roofline and cuts it down inside the footprint - and no
`underpin` under the base. The yard they are pinned on is the yard the builder chose, and turning the ground
over is the pass undoing the placing. The same switch also means no keeper, no steed and no travel stone at
them, which is right for pieces being tried; the day one becomes a place the campaign sends anyone to, it
comes off the list and gets its own dressing.

## A mark instead of a pin

Some spots are easier to stand on than to type: a builder marks the middle of a footprint with a block of
`tolkienmobs:block_ammolite`, and the spot is read out of the world. No template in the pack carries
ammolite — the gate, the keep and all eight orc pieces were checked — so every ammolite block in a save is a
mark somebody left.

`OrcPieces` is the table those marks are written down in: the template, the block that marks the middle
of its footprint, and which way the piece is turned. It is raised from `/tmplace orcs` and again as the server
comes up, after the gate, because the towers stand on the gate's own footprint. A piece is laid with half its width and depth out from the mark and its **base one block
below** the mark, which is the same rule the orc pins follow, and `/tmplace orcs` raises every marked piece
that is not already standing. The check is `Sites.wholeAt`, the strict reading of `standsAt`: every sampled
block, not a majority, so running it twice raises nothing twice while a piece that something else has laid
over — a gate's template air cutting the half of it inside the gate's footprint — is laid whole again.

The eight towers on the gate are its first entries, in two ranks of four. The near rank's marks are the
ammolite blocks **inside the gate's footprint** — x 5496 to 5655, z −6877 to −6830, on the courtyard floor at
188:

| mark | piece's box |
|---|---|
| (5539, 188, −6831) | x 5534–5545, y 187–208, z −6837…−6825 |
| (5557, 188, −6831) | x 5552–5563, y 187–208, z −6837…−6825 |
| (5598, 188, −6830) | x 5593–5604, y 187–208, z −6836…−6824 |
| (5628, 188, −6830) | x 5623–5634, y 187–208, z −6836…−6824 |

The far rank stands five blocks north of the gate's back face, at **z −6882**, on ground one course lower at
187 — two west of the passage's mouth, two east of it, closing the same building from behind:

| mark | piece's box |
|---|---|
| (5539, 187, −6882) | x 5534–5545, y 186–207, z −6888…−6876 |
| (5558, 187, −6882) | x 5553–5564, y 186–207, z −6888…−6876 |
| (5598, 187, −6882) | x 5593–5604, y 186–207, z −6888…−6876 |
| (5628, 187, −6882) | x 5623–5634, y 186–207, z −6888…−6876 |

## The huts on the plaza

Four more marks stand on the plaza, whose middle is (5576, 187, −6893):

| mark | body (13 wide) | base | door |
|---|---|---|---|
| (5548, 187, −6899) | x 5542–5554, z −6905…−6893 | 186 | east, `COUNTERCLOCKWISE_90` |
| (5599, 187, −6899) | x 5594–5606, z −6904…−6892 | **187** | **south, `NONE`** |
| (5613, 187, −6899) | x 5608–5620, z −6904…−6892 | **187** | **south, `NONE`** |
| (5627, 187, −6899) | x 5622–5634, z −6904…−6892 | **187** | **south, `NONE`** |

The three east of the hall were laid a course low and facing west first; the builder marked their
doorways with ammolite at **(5593, 187, −6898)**, **(5607, 187, −6898)** and **(5621, 187, −6898)** — the
west edge of each body, exactly where a player stands to walk in — and judged them: a course higher, and
turned to look at the gate's own front (south). Those marks are the evidence for both, so they are left in
the world as the record.

The marks the builder first left were 10 to 19 blocks apart, which put all four hut bodies inside each other;
these are spread across the strip the plaza actually leaves open. That strip is measured, not guessed: the
hall runs **x 5563–5591**, the mountain's foot closes it at **5538** west and **5633** east, and the tower
row takes **z −6888** south, so the open ground is x 5538–5633 by z −6889…−6909 — 24 blocks west of the hall
and 41 east. Four 13-wide bodies need 52 plus gaps, so they go one west and three east, with **one block
between neighbours**; a two-block gap would need two more blocks than the east strip has.

A hut is not four walls: it has **one opening**, a single column two courses high in its **south face** at local
x 10, with its stairs cut into the base below and a lintel over it. The turn is written beside each mark
rather than worked out when it is laid, and `Sites.wholeAt` turns the sampled blocks with it, so the check for
"already standing" is asked of the piece in the turn it was laid in. A quarter clockwise turns that opening
from south to **west**; a quarter anticlockwise turns it to **east** — so the hut west of the hall is the one
turned the other way, and every door looks at the plaza's middle (5576, 187, −6893).

The 20-by-19 boxes still overlap — a 19-wide box on 14-block centres always will — but the **bodies** do
not: they are 13 wide and stand 14 apart, so each hut is its own building with one block of air between its
neighbour's wall. Nothing is erased either way: `orc_hut` places **no air** (its palette names air, but none
of its block entries use it), so a box overlapping a neighbour leaves it alone.
