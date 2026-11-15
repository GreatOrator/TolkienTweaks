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
ammolite — the gate, the keep and all nine orc pieces were checked — so every ammolite block in a save is a
mark somebody left.

`OrcPieces` is the table those marks are written down in: the template, the block that marks its footprint
(its middle, or an edge with a push), and which way the piece is turned. It is raised from `/tmplace orcs` and again as the server
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

## The pit, the rams and the farm

Six more marks close the camp, and three of them are not middles: a pair of blocks marks the **front edge** of
a ten-wide ram, because ten has no middle block, and another pair the **back edge** of the farm. `Piece`
carries a **push** for those — how far the footprint moves off the mark once it has been centred on it —
written beside the mark as it was judged rather than measured out at the laying.

| mark | piece | box | base |
|---|---|---|---|
| (5578, 186, −6813) | `orc_lava_pit`, `NONE`, drop 0 — the mark **is** the base | x 5568–5589, z −6823…−6802 | **186**, mouth 200 |
| (5610, 189, −6830) + (5611, 189, −6830) | `orc_battle_ram`, `NONE`, push 15 south | x 5606–5615, z −6823…−6806 | **186** |
| (5617, 189, −6830) + (5618, 189, −6830) | `orc_battle_ram`, `NONE`, push 15 south | x 5613–5622, z −6823…−6806 | **186** |
| (5614, 189, −6860) + (5615, 189, −6860) | `orc_farm`, `CLOCKWISE_180`, push 14 north | x 5607–5622, z −6887…−6860 | 188 |

The pit's mark is the only one in the pack that is **buried** — a block at 186 under the stone lip at 189 —
and it turns out to be the piece's own **base course**, so it is the one mark read as a base rather than a
middle: drop 0. The template's first course is its ground — 22 by 22 of grass and dirt round 21 cells of lava —
and everything above it is a one-block perimeter wall round a hollow shaft, 6140 air cells in all. So the shaft
its mouth stands at **200**, twelve courses over the courtyard floor, and its air carves the whole 22-by-15 box
out of the ground around it, the bank rising to the south included. It went in twice lower than this first
(base 172, then 174 with its rim at the courtyard floor) and was raised each time.

The rams stand unturned (`NONE`) with their bases at **186**, and the face that carries the template's corner
glowstone is their front: it is the box's **south** face, so their front edges stand at **−6806** — the block
the builder pointed at — and their 18-deep boxes run north from there to −6823, seven courses off the gate wall.
They first went up turned right round with a base of 188 and were judged too far back, so they were turned
about, dropped two and moved six south: the marks did not move, only the push. Their 10-wide grass pads overlap
by three blocks — their centres are 7 apart — and their 7-wide bodies do not.

The farm's back sits on its mark and it runs north to −6887, five courses short of the huts, with its gated
cobblestone wall — the template's high-z end — looking at them. It went up right the first time, and all three of
these pieces are now **settled as final**: the farm first time, the rams after two goes at their depth and one at
their place, the pit after three at its height. Nothing here is to be moved again on a guess.

## The war camp in front of the gate

Twelve tents stand in the open in front of the gate. They are the only pieces in this table that **no builder
marked**: they answer a measured question instead — how much ground the gate's own front leaves. Counted on the
surface, within two courses of level, four clear courses over it and nothing already standing on it:

| where | open ground | raw tent footprints |
|---|---|---|
| x 5548–5606, in front of the hall's doorway, sixty-four deep | 3087 cells | 17.0 |
| x 5563–5591, the doorway's own width | 1339 cells | 7.4 |
| the whole front, x 5492–5656, sixty-four south of the wall | 5860 cells | 32.2 |

A tent is 13 by 14 and carries **no air**, so it never erases a neighbour; the only thing it needs is footprint.
Packed 13 by 14 with **four blocks between** neighbours, twelve fit across the whole front — seven of them in the
stretch in front of the hall's doorway — and the eight-wide corridor in front of that doorway stays empty. Three
between would buy one more and start to look like a barracks rather than a camp; six would lose two.

In front of the hall itself there is no room, and it is a measurement rather than a preference: the hall stands in a
**covered courtyard**, a chamber whose floor is 188 to 190 under rock at 200 to 203, ten clear courses where a tent
is twelve tall — and its south face, open across 28 of its 35 cells, has twelve blocks of floor between it and the
far tower row, less than a 13-by-14 footprint deep. The chamber round it is the one the huts and the farm stand in
already.

## The outside camp, settled

Twenty-eight pieces stand outside the keep, and every one of them is **final**:

| | how many | where |
|---|---|---|
| towers | 8 | two ranks of four, on the gate's own footprint |
| huts | 4 | the covered plaza behind the gate, one west of the hall and three east |
| farm, pit, rams | 1, 1, 2 | the courtyard and the gate's front, laid from marks |
| tents | 12 | the open ground in front of the gate, laid from the survey above |

The hall itself is the one piece outside the keep that this table never carried: it was placed by hand, and when it
was needed it was found by matching its fourteen wall torches against the world (x 5533–5567, y 187–220,
z −6912…−6891, unturned, 14 of 14).

**No lamp is left hanging anywhere.** The cut tool left a glowstone floating in the corner of every template's box —
one block, nothing beside it, part of no building — and it has been cut out of all five files that carry one: the
tower's (11, 21, 0) first, then the farm's (15, 15, 0) and the ram's (9, 6, 0), and now the tent's (12, 11, 0) and
the hut's (19, 18, 0). Because the huts and the tents were laid before the last two were cut, `OrcPieces` carries
their corners and sweeps them at every raise: the cell is cleared only where it still holds glowstone, so a piece
that has been built around is left alone. Each of those templates keeps the glowstone set into its own base course —
only the one hanging in the sky goes.

Both `orc_farm` and `orc_battle_ram` carried a **floating glowstone** at a corner of their box, (15, 15, 0) and
(9, 6, 0): the cut tool's residue, the same as the one taken out of `orc_tower` at (11, 21, 0). Both are cut.

## The keep

The dwarven hold was written down in `ArdaMap` as a **plan** long before anything laid it: `Sites.GUNDABAD` was
pitched by the campaign's own page, and the numbers - 122 by 26 by 183 - were the template's, with a corner nothing
could be stood on because the corner is inside solid rock. What *can* be stood on is the mark the owner walked: an
ammolite on the chamber's floor at **(5577, 189, -6918)**, at the foot of the hold's own doorway. That mark is the
plan now, read backwards through the template.

| | |
|---|---|
| floor as the template carries it | fourteen courses of deepslate, its **top course the floor a player stands on**, rooms over it, doorways from course fourteen up |
| the mark's course | that top course, local y 13, so the base is **176** |
| the doorway | the far one of the south wall, local x 104-108, its middle **106**, five wide and seven tall |
| the wall's course | local z 182, the last course of the depth |
| the plan | **x 5471, y 176, z -7100**, unturned |
| what that puts where | the doorway's middle on the mark's x 5577, its floor on the mark's course 189, the wall on the mark's z -6918, and the rooms in the chamber's own section, 190 to 200 |

The body runs the rest of its 183 courses west and north into the mountain. Every corner of the box is solid stone at
176 to 201, with 244 to 315 of rock over it, so the hold is buried by design and only its doorway shows - the way the
owner's own example had a doorway flush with the floor and the wall running away from it either side.

**Order is the other half of it.** The gate's hall is carried north to `ArdaMap.keepWall()` - the keep's south wall -
and cleared fourteen courses through where that wall will stand, so the gate lays first and the keep lays over it: the
keep's own wall and its own five-wide doorway are then written whole, and the clearing the hall made is air the keep's
rooms already hold. Laid the other way round the hall opens the wall across the chamber's whole width, and the hold
has no door at all. `GundabadKeep.autospawn` runs after `GundabadGate.autospawn` for exactly that reason, and tests the
doorway's own floor cell to leave a keep that is already standing alone.

`/tmgate keep` lays it by hand at the same plan - no position, no facing, because there is one box the hold fits. The
box's chunks are asked for by hand before the template is placed: they begin at chunk x 341, z -444, past anything a
world keeps loaded for its spawn, and a `hasChunkAt` test there answers no on a first start, which is how a keep that
was never laid looked exactly like one that was already standing.

### The doorway's mouth

A wall is not a way in. With the hold laid, the ground south of its wall still stood a course high: course 190 is
solid across the chamber's whole width, so at the height a player walks the five-wide doorway showed as a **one-block
slot at its middle column**, and east of it the mountain's rock came down from 196 over the other half of the opening.
`GundabadKeep.clearMouth` takes a box out around the door:

| | |
|---|---|
| columns | the doorway's five (5575-5579) and four more each side, **5571-5583** |
| out from the wall | **eight courses**, z -6917 to -6910, which is where that ground ends and the chamber is already open |
| height | the floor's course **189** is never touched; **190 to 196** is cleared, the door's own seven |
| measured | 728 cells in the box, **378 taken out** - 283 stone, 84 snow, 6 ice, 5 rockpile |

It is dressed on every pass, whether the hold was just laid or has stood for weeks, so a door re-cut by a later gate
is dressed again. The floor stays where it is, which is what makes the apron flush with the hold's own floor instead
of a trench outside it.

## The keep, settled

The hold is final: one location, one way onto the ground, and one way for a page to find it.

| | |
|---|---|
| plan | **x 5471, y 176, z -7100**, unturned, 122 by 26 by 183 |
| doorway | x 5575-5579 (middle **5577**), courses **190-196**, floor course **189** |
| south wall | **z -6918**, the plane the mark was walked on |
| rooms | 190-200, the gate chamber's own section |
| buried | solid stone at every corner course, 244 to 315 of rock over the top |

**How it comes onto the ground.** `GundabadKeep.autospawn` runs in `MilestoneHandler` straight after
`GundabadGate.autospawn` and before `OrcPieces.raise`: the gate's hall is carried north to `ArdaMap.keepWall()` and
cleared through where the keep's wall will stand, so the gate goes first and the keep's own wall and doorway are
written whole over that clearing. The hold lays itself on the first start of a world, loads its own chunks (the box
begins at chunk x 341, z -444, past anything a world keeps for its spawn), dresses its doorway's mouth, and is left
exactly as it is on every start after that - the test is the doorway's own floor cell, which answers with the
template's deepslate once the hold is up. `/tmgate keep` is the same laying by hand, and it reports the count.

**How a page finds it.** `Pins.SOURCE` carries the plan, so `Pins.at(level, Sites.GUNDABAD)` answers the corner in
every world. `Sites.ON_THE_PIN` holds it, which is the rule that the pin's own height is the height - a box 122 by 183
cannot be seated by measuring the ground under it, because the ground under it is the massif. And it is
`SELF_DRESSED`: the mountain is the hold's roof, so the sweep that carries a camp's ground to its roofline would open
the massif over it. What that costs is the travel stone, which the pack puts at every named building; the hold is
reached by the campaign's own page and by `/tmgate keep` instead.

**Who stands here.** Gimli, and only Gimli. He is the campaign's one keeper at the hold - `keepGimli` stands a
`tolkienmobs:dwarf` beside the well at the head of the spiral stair, his post being the middle of the hold plus
(38, 61) across and (with `GIMLI_LIFT`) thirteen courses up, which is **5570, 190, -6948**: local (99, 152), a room
column with the well's shaft at x 87-97 and the spiral's steps at x 94-95 beside it. He is tagged
`tolkienmobs:campaign_gimli` and named in gold, and he is the turn-in for *Last Refuge I* and the giver of
*Last Refuge II*. Glóin gives and takes every Gundabad page but stands at **his camp**, not here; Dugorim is the
Dourhands' captain and the hunt target *below* the stair, an enemy rather than a giver. No camp keeper and no steed:
`CAMP_OWNERS` holds only Elladan's and Barachen's, and `bodyFor(GUNDABAD)` answers none.

**Its stone.** The ammolite set into the gate's facade at **(5584, 190, -6830)** is where the hold's travel stone
stands, named **Gundabad**: `MilestoneHandler` lays it on every server start, which is why a world whose gate is long
built still gets it, and `Stones` replaces the marker rather than leaving it beside the stone. It is the gate's stone
rather than the keep's because the facade is what a player walks up to; the hold itself is a doorway in a chamber.

## The cave east of the gate

The second mark is a cave rather than a build: **(5693, 186, -6676)**, on the shelf where the mountain begins to climb.
`GundabadCave` cuts it, and nothing is placed but the dais - the mouth, the walls and the ceiling are the mountain's own
rock.

| | |
|---|---|
| mouth | **2 wide by 2 up**, facing west, its floor the shelf's own ground course **186** |
| tunnel | **six blocks** through the slope's lip, x 5693-5698 |
| room | **7 across by 9 deep**, x 5699-5707, z -6679 to -6673, air from **187** |
| ceiling | **5 courses clear** at its lowest, ragged a course or two higher column by column |
| dais | **3 by 3**, one course up, `angainor_bricks`, x 5702-5704, z -6677 to -6675 |
| the cut | **412 blocks**, all inside chunk (356, -418) |
| buried by | at least **8 courses** of rock over the highest part of the ceiling |

The tunnel is six blocks rather than three for a measured reason: the shelf's foot runs diagonally - the rock begins at
x 5693 on the mark's own line and at x 5697 five blocks south - so a room starting three blocks in broke out along its
southern corner, where the surface is 186 and the ceiling is 193. Six blocks in, the shallowest column the room covers
has 199 of rock over it.

It goes up with the hold, on every start, and is left alone once it stands - the test is the room's own first cell,
since the mouth's cell answers air either way, the marker having stood on the shelf with the sky over it. `/tmgate cave`
re-cuts it on demand and reports the count, and the marker itself is taken up into stone once the cave is cut, the way
`Stones` takes up the gate's.

### The mouth

The way in is a wall of the hillside's own stone, not a door: `GundabadCave.seal` fills the mouth's own two-by-two cells
and `open` takes them out again, the pair the goblin cave's ashen scroll drives. What opens it here is a player:

| | |
|---|---|
| reach | **64 blocks** from the mark, and they have to be carrying one of the hold's pages |
| which pages | the walk to the hold (2-3), Gimli's stair (2-6), the captain below it (2-7) - `Campaign.onHoldPage` |
| the wall | **two courses deep** (x 5693-5694) so it reads as rock from outside, with the tunnel's hollow left behind it |
| closing | asked once a second within **128** blocks, so the mouth is shut again before the last player is gone - never while somebody is within twelve blocks of the room's middle |
| a fresh cut | sealed as it is cut, so a hollow nobody was sent to is shut |

A world that existed before the cave simply gets it: `keep` cuts it on the first approach if the mountain has no hollow
behind the mark, which is the same path the autospawn takes on a new world.

### The keeper

Gimli is met here now. His post is the room's dais - `GundabadCave.post()`, (5703, 188, -6676) - rather than the spiral's
well up in the hold, because page 2-6 sends the player down and the step down is this cave. The reach is unchanged: he is
stood when a player carrying that page is within ninety-six blocks of the dais, or inside the hold, which keeps a world
that already holds him at the well to one body - `recall` walks him down rather than a second dwarf being stood. A new
body is only placed when the player is at the cave, so nobody is popped into the rock from the far side of the massif.

### His gaze

He is a planted giver, not a walker: `QuestGivers.plant` gives him no AI and a home area the width of the room, and
`QuestGivers.onPlayerTick` turns him toward whoever is nearest every two ticks, six degrees at a time - the same slow
turn the questing halls' staff have. The one difference is reach: a hall's giver watches its room from **twelve**
blocks, and the cave's keeper watches **twenty-four**, because the dais stands behind a six-block tunnel and a player
coming in through the mouth should find him already facing them.

### The captain

Page 2-7 is a hunt of `tolkienmobs:entityttmdwarf`, so any of the pack's dwarves settles it - but the captain the prose
names is the stage's boss, and he holds the keep's own throne while the page is in hand: `ArdaMap.keepThrone()`, read
back from the owner's mark as (5501, 193, -7024), the dark stone slab on its carpet, standing as he would with the hall
in front of him. He is **not** a giver: a giver is made invulnerable on the way into a world, which is the one thing a
hunt cannot have, so he only carries `tolkienmobs:campaign_dugorim` and is found by it afterwards, which is how a body
already felled stays down.

### The fight

He is armed, and the fight is written in `DugorimFight` rather than in a goal list, because the body the chapter stands
him in cannot have one: the pack's dwarf is a `Villager`, whose movement belongs to its brain - it walks to a work site
and panics when hurt, and an attack goal added beside the brain would simply be walked away from. So the driver runs on
`ServerTickEvent.Post`, which is after every body has ticked and therefore the last word each tick: he wakes for a
player within twenty-four blocks of the seat, turns to them, closes to arm's length and swings for six, once a second,
with a shove behind it. He carries an iron axe (`armCaptain` puts it in his hand and sets the attack up to six and his
health to forty, since a dwarf arrives with a farmer's one point and a villager's twenty), and a home area of sixteen
blocks keeps him in the room between fights. He is killable: he is the one body in the line that carries no giver tag,
so nothing makes him invulnerable on the way into a world.

### The wraith

A `tolkienmobs:entityttmnazgul` stands four paces down the carpet from the seat, at `ArdaMap.keepThroneWatch()`
(5501, 193, -7021), also while 2-7 is in hand - and unlike the captain he is not planted: he is given a home area the
width of the hall and left to fight.

**The page is not paid for the dwarf alone.** When the player carries 2-7 back to Glóin with the count met but no mark
of the wraith's fall, `Campaign.nazgulHeldBack` refuses it and says why. The mark is the scoreboard tag
`tolkienmobs:nazgul_down`, written on the player the moment they fell a body tagged `tolkienmobs:campaign_keep_nazgul` -
a player tag rather than a note in code, because a player tag is saved with them: no second wraith is stood for
somebody who has already killed one, and Monday's kill is still a kill on Tuesday.

### The gate

The way into the hall is the coal lattice the owner built across it: seven columns (5575-5581) and ten courses (190-199)
at z -6856, in a checker - odd courses solid, even courses coal at the odd columns, and the bottom course closed with
dark oak stairs set top-half facing north. Its holes are one block wide each way, which is why it stops a player.

`GundabadDoor` lifts it while anybody within twelve blocks **carries the Key-stone** (`item_keystone`, anywhere in the
pack), one course per two ticks, bottom course first, so the screen shrinks up into the lintel the way a portcullis
rises; when nobody carries the stone it comes back down the same way, and never while a player stands in the opening.
Nothing is stored between sessions - the shape is written down from the owner's four corner marks, and how open the door
is gets read off the door itself on the first ask of a world, so a server stopped mid-lift finishes lifting or shutting
rather than leaving a half-open gate.


His face is `textures/entity/middle-earth/dugorim.png`, copied straight from the old pack -
`customnpcs/textures/customskins/dwarves/dwarfwarrior 4.png`, 64x64, nothing to convert. `DwarfRender` looks a named
dwarf's art up by the name on his plate (`middle-earth/<slug>.png`), so the custom name is what dresses him; a save
holding him before this change gives him the art on his next load, since the lookup is per frame and not per spawn.

### The words

Both Last Refuge pages are read aloud now rather than summarised: they are in `Campaign.SPOKEN`, and the leaf is spoken by
whoever hands the page over, which is why the old chapter's four dialog nodes land where they do.

| our page | handed over by | says | then |
|---|---|---|---|
| 2-6 Last Refuge I | Glóin, at the camp | dialog `184`'s two halves - the Key-stone has opened the back way, and he sends Gimli ahead with the stone and the lore | dialog `185` - the way is marked in the journal, speak to Gimli at the stair |
| 2-7 Last Refuge II | Gimli, in the cave | dialog `186` - go now into the mountains and finish Dugorim and the last of the Dourhands | dialog `187` - good luck, may the battle be glorious |

The two replies on each leaf are the pack's own options, kept word for word: *I am ready now. What do I need to do?* and
*Let me come back when I am better prepared.* The closings were already written and are unchanged; the `place` lines say
where each leaf is heard. `zh_cn` has no chapter-two keys at all, so Chinese falls back to the English for these pages,
as it already did for their titles.
