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
