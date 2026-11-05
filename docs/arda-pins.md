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
| a **building** - a template raised for the player who was sent to it | the template id: `tolkienmobs:key_structures/wild_tent`, `tolkienmobs:ttmruin_large_2` | `Sites.survey` |

## The table

A line is a key and a block:

```java
"thorenhad", new BlockPos(4864, 157, -3328),
```

The y is the surface the scout stood on: a note, not a measurement, because a building is still seated by
the laying. The x and z are taken as given. `/tmpin` prints exactly this line, which is why a pin kept for
every world is a copy rather than a transcription.

Empty is the table's normal state and a working one - a location with no pin is surveyed from the drawing
as it always was - so nothing here has to be filled in for the campaign to be walked.
