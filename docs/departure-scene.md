# The departure scene — the chapter-one seam, recovered

The cutscene that carries the player out of chapter one and into chapter two was built in the old
TolkienCraft world, not in this pack. It was recovered on 10 November 2026 from

    G:\GitHub\TolkienCraft-3-Map\world\customnpcs

and this note is what a later session needs so it is not scanned for twice. **Nothing of it is built in
this pack yet.** The plan it belongs to is `docs/chapter-two-last-refuge.md`; chapter two's state is
`docs/chapter-two-state.md`; the seam's parked note in `docs/HANDOFF.md` is superseded by this file.

## What it is, and what it is not

It is **not** a camera scene. The old pack has no camera, no cinematic and no letterbox anywhere in it —
`grep -ril "cutscene\|cinematic"` and `grep -ril "camera"` over the whole customnpcs tree return nothing.
What it has is CustomNPCs **scenes**: a name, a tick budget, and a list of `tick command …` /
`tick move to x y z` lines owned by one NPC and run on it. The player stands in the courtyard in first
person and watches the household appear, speak and walk off.

So a port needs no camera override, no input lock and no title-card layer. What it does need is listed
under *What the pack is missing*.

## The three scenes

| Scene | Runs on | Ticks | What it does |
|-------|---------|-------|--------------|
| `FellowshipDepart1` | Gandalf | 1138 | spawns the household, speaks the departure, starts the eight walks, hands over to `ElrondBye` |
| `ElrondBye` | Elrond | 88 | five lines after they are gone, starts quest 180, teleports the player, clears the bodies |
| `FellowWalk1`–`8` | Boromir, Gimli, Legolas, Aragorn, Frodo, Samwise, Merry, Pippin | 461–875 | one line each: `move to 14387 44 5835`, then remove the body and reset |

The eight budgets differ (Aragorn 461, Boromir 533, Gimli 574, Legolas 641, Frodo 687, Samwise 764,
Merry 826, Pippin 875) so the exits stagger. Literally read, each body is deleted 22 ticks after its own
walk starts — either a brisk exit or a near-instant vanish, depending on how CNPC's `move to` behaved. It
has not been watched in the old world, so that is unresolved rather than decided.

### `FellowshipDepart1`, beat by beat (ticks at 20 per second)

- **2** — spawn ten clones in Elrond's courtyard, all at y 42:
  Elrond `14323,42,5902`; Bilbo `14323,42,5907`; Aragorn `14329,42,5908`; Gimli `14330,42,5902`;
  Legolas `14331,42,5906`; Boromir `14331,42,5904`; Merry `14328,42,5903`; Pippin `14328,42,5906`;
  Samwise `14328,42,5904`; Frodo `14328,42,5905`.
- **5, 7, 12 — Gandalf** (bold): the household of Elrond Halfelven gather to bid farewell to nine brave
  adventurers; entrusted with a secret task which could mean the salvation or doom of Middle-earth;
  "I, Gandalf the Grey, must lead them along dark roads…"
- **16 — Frodo** (italic): everything seems to be ready, we're about to leave, hopefully I'll see you again
  once this is all done with.
- **22, 25, 28, 30 — Bilbo and Frodo:** "You take care of yourself, Frodo. Adventuring is dangerous
  business!" / "It is, but look at all the help I'll have!" / "You're right, Frodo." / "You can't go wrong
  with a Took, a Brandybuck, and a Gamgee! Solid hobbits all."
- **35, 38, 41, 45, 48 — Aragorn:** keep an eye upon these lands while we are away; there will be danger
  behind us too; my footsteps will be easier if I know you remain behind; my own journey will carry me far;
  Andúril will go to Minas Tirith, but my road follows that of the Company for many leagues.
- **53, 56, 59, 64 — Samwise:** "Samwise, you dunder-head! You've gone and forgotten to bring a bit of rope
  with you." / "I knew there was something you'd forgotten!" / hullo adventurer, this is it, we're about to
  head out, check in on my old Gaffer; Lor' knows when we'll be done with this whole business.
- **67, 70 — Meriadoc:** this is it, wish us luck; we'll lend Frodo all the help we can and do whatever
  Gandalf needs.
- **74–81** — start `FellowWalk1`…`FellowWalk8` for the eight members.
- **82, 86 — Gandalf:** the number of the Company has been chosen and any more would threaten the success
  of its mission; if the errand fails, Eriador will have need of heroes.
- **89** — Gandalf moves to the road mark `14387,44,5835`.
- **95** — start `ElrondBye`. **96** — remove Gandalf's body.

### `ElrondBye`

- **2, 5, 8, 11, 15 — Elrond:** "It is done, and they are beyond my aid." / "For all of my wisdom, I
  cannot see the end of their road." / "May they come to it in safety and return to Rivendell in victory."
  / "And so the Company departs. May the stars shine upon their road's end." / "Let us go back,
  Adventurer. There is nothing more we can do for them: the Company must now rely upon their own strength
  and wisdom."
- **22** — start quest 180. **23** — teleport everyone within 16 blocks of the courtyard to
  `14936,142,6081`. **24** — reset all nine scenes. **25, 26** — remove Bilbo and Elrond.

## The cast and the faces

The nine are Frodo, Samwise, Meriadoc, Peregrin, Gandalf, Aragorn, Boromir, Legolas and Gimli. Bilbo and
Elrond are the hosts, not of the nine. Old clone sizes: 3 for the five hobbits (Bilbo included), 4 for
Gimli, 5 for Elrond, Gandalf, Aragorn, Boromir and Legolas.

| Character | Clone's texture | In this project |
|-----------|-----------------|-----------------|
| Elrond | `Elrond.png` | **present** — `middle-earth/elrond.png` |
| Legolas | `Legolas.png` | **present** — `CustomSkins/Guy/legolas.png`, and in the pack |
| Gimli | `Gimli.png` | **present** — `CustomSkins/Hobbit/gimli.png`, and in the pack |
| Bilbo | `BilboOld.png` | missing |
| Gandalf | `GandalfGrey.png` | missing (near-match `CustomSkins/Guy/GandalfTheGrey3.png`) |
| Aragorn | `AragornWild.png` | missing (near-matches `Guy/Aragorn.png`, `MiddleEarth/Aragorn (1).png`) |
| Boromir | `Boromir.png` | missing |
| Frodo | `FrodoBaggins.png` | missing |
| Samwise | `SamwiseGamgee.png` | missing |
| Merry | `MerryBrandybuck.png` | missing |
| Pippin | `PippinTook.png` | missing |

**Eight faces are still to be sourced:** `bilbo`, `gandalf`, `aragorn`, `boromir`, `frodo`, `samwise`,
`merry`, `pippin` (as the pack will name the files — see below). The clone JSONs agree with this list.

What the audit found, so it is not redone:

- The clones reference **28** textures and **26** of them are absent from `TolkienTweaks/CustomSkins`. The
  folder moved to the project is a 330-file skin library, but it is not the set the old map resolved
  against: even generic references such as `male 5.png`, `male 8.png`, `rangerlord 4.png`, `layles.png`,
  `darkmage3.png` and `watcherspirit.png` are missing. If the real set is found, take it wholesale.
- The project's own copies of the clones (`run/saves/Arda/customnpcs/clones/4` and the backup beside it)
  reference the same 28 names with the same gaps — there is no second local source.
- **Every PNG in `CustomSkins` is the legacy 64×32 layout.** The pack's models are all 64×64
  (`geckolib/models/merchant/{human,hobbit,dwarf,elf,gimli}.geo.json`), so old skins need the
  legacy-to-modern conversion. PIL 13.0.0 is available for that; new 64×64 skins need nothing.
- Models only sample the base layer: hats and overlays must be painted on the base layer to show.

## The old world's coordinates are not ours

`14323,42,5902` is the old world's Rivendell, and `14936,142,6081` is where the old scene dumped the
player afterwards. Our Rivendell is the same `Rivendell_Imladris` build pinned at the cut's low corner
`5200,75,-3370`, so the courtyard's *relative* layout is what carries over — the cluster above is 9 blocks
across and 7 deep, and the road mark is 64 east and 67 south of Elrond. The cut already names local NPC
spots in `data/tolkienmobs/structure/rivendell/rivendell.npcs`: legolas `200,83,151`, elrond `308,112,233`,
glorfindel `414,175,159` (offsets inside the 554×512 cut). Elrond's own mark is the natural gathering point
until the owner's markers arrive.

## Pack-side facts a port has to use

- **Skin lookup.** A named body is looked for as `textures/entity/middle-earth/<slug>.png`, where the slug
  is the body's own name lowercased with accents flattened (`HumanRender.slugOf`; `Glóin` → `gloin`).
  A missing file is silent and the body wears its folk's clothes.
- **Named skins are only asked for when the body qualifies**: a human needs the giver tags (any
  `Alliance.giverTag()`, which makes `giverSkin > 0`) or `Sites.KEEPER_TAG`. `DwarfRender` has its own
  named lookup. **`HobbitRender` and `ElvesRender` have none today** — hobbits will need the same
  name-to-file lookup added to wear their own faces.
- **Bodies.** Keepers are `Mob`s made with `TolkienEntities.ENTITY_TTM_{HUMAN,DWARF,ELVES,HOBBIT}.create`,
  `snapTo`, `setPersistenceRequired`, tags, a gold `setCustomName`, then `Keepers.remember(level, tag,
  body)`; `Keepers.keeper(level, tag)` finds them again. `Campaign.keepGloin` (about line 1750) is the
  template to copy.
- **Nothing walks.** There is no keeper movement anywhere in the pack; `Keepers` is identity and
  persistence only. The walk-off is the one genuinely new mechanic, and it is a straight line in the old
  scene.
- **Pins.** `Pins.at(level, key)`; `Sites` already uses `.end`, `.keeper` and `.body` suffixes.
  **`.scene` does not exist yet** and would be the key a scene reads for its gathering point and its exit.
- **Tick hook.** `FactionEvents.onPlayerTick` already calls `Campaign.tick`, `Places.tick` and
  `QuestGivers.tick`, which is where a beat driver belongs.
- **Commands.** `@EventBusSubscriber` class with `onRegisterCommands(RegisterCommandsEvent)` and
  `Commands.literal("tm…")`, as `RoadCommands` and `PinCommands` do.
- **Trigger.** `Campaign.noteHanded` already branches on `page.target().equals(Quest.personTarget(GLOIN))`
  to pitch `Sites.GLOIN_CAMP` — that is page 26 being handed over by Elrond, the moment the old scene
  played. The old chain (dialogs `108`–`111`, *Onward Misty Mountains I–IV*) is gated on quest 25 active
  and 26 not done, and `111` is the page that starts quest 26.
- **The one wiring gap.** Nothing in the old pack's dialogs or quests starts `FellowshipDepart1`: quest
  25's `QuestCommand` is empty and only the clone files name the scene. It was fired from the world (an
  NPC scene button or a command block), so the pack cannot tell us what the trigger was meant to be.

## What the pack is missing

1. Eight faces (table above) and, for the hobbits, a name-based lookup in `HobbitRender`.
2. A scene beat table and a tick driver: spawn, say, walk, remove, hand over.
3. A keeper that can walk to a mark.
4. A `.scene` pin (gathering) and an exit pin, and the marker legend the owner is preparing.
5. The trigger and the once-per-player record.

## Open decisions

- **Marker legend.** The owner is placing a set of markers and will name who each one is for, plus the
  road the nine leave by. Nothing should hardcode a courtyard offset until then.
- **Pacing.** The old lines land 2–3 seconds apart, which is fast to read. Slower (5–6 s) with the
  speaker styled as our chat lines are is the recommended change; it is not agreed yet.
- **Bilbo in or out.** He is a host in the original and is not one of the nine.
- **The old teleport.** The scene used to dump the player at the pass afterwards. The pass road now
  exists (`GLOIN_ROAD`, `GLOIN_BRIDGE`, `GLOIN_ROAD_EAST`, commit `87f2217a`), so walking is possible
  instead. Recommended: drop the teleport and let Elrond hand page 26.
- **Trigger point.** Page 25 settling in the library, or page 26 being handed over by Elrond. The old
  pack did it after Elrond's words, which is the page-25 settle.

## Verified

Nothing. No code, no assets and no markers have been added by this note; every fact above comes from
reading the old pack's JSON and this pack's source. `./gradlew compileJava` was not run for a docs-only
change, and none of the old scene has been watched in motion.

## The cast's skins have landed - 10 November 2026

All ten of the scene's faces are now in the mod's own texture folder,
`src/main/resources/assets/tolkienmobs/textures/entity/middle-earth/`, lower-cased and
named by slug: `gandalf.png`, `aragorn.png`, `boromir.png`, `legolas.png`, `gimli.png`,
`bilbo.png`, `frodo.png`, `samwise.png`, `merry.png`, `pippin.png`, beside the ones
already there (`elrond.png`, `glorfindel.png`, `elladan.png`, `gloin.png`, `ranger.png`).

That is the folder `HumanRender` and `DwarfRender` already look in by name, so the tall
folk and Gimli needed nothing but the keeper tag the scene already sets. Hobbit bodies
had no named lookup at all - `HobbitRender` went straight to the sixteen village
variants - so it gained one: a named hobbit asks for `middle-earth/<slug>.png` first
and falls back to its variant when no file is drawn, the same rule and folder as the
tall folk.

Elrond is the one body the scene does not spawn: he is the campaign's own, stood in the
valley, and `middle-earth/elrond.png` exists but `ElvesRender` has no named lookup yet.
His lines are spoken as chat either way.

## Rehearsing without markers - 10 November 2026

`/tmscene play` gathers the real cast, with the real offsets and the real script, round
the caller's feet, and takes it away at the end without answering the note or opening
chapter two. `/tmscene stop` takes it away early, `/tmscene note` hands over the summons,
`/tmscene forget` clears the note and the having-seen, `/tmscene skip` answers the seam
outright, and `/tmscene` alone reports the stage. So placement and pacing can be looked
at on unpinned ground: what is seen there is what the pinned courtyard will get.

## The plaza is marked - 10 November 2026

Eleven marker blocks, one body each, set down on the plaza; all of them read where they lie,
none of them typed in:

| Block | Body | Pin |
| --- | --- | --- |
| Morgul iron block | Gandalf | `rivendell.scene.gandalf` |
| Mithril block | Elrond | `rivendell.scene.elrond`, and `rivendell.scene` |
| Ammolite block | Aragorn | `rivendell.scene.aragorn` |
| Dwarven stone | Gimli | `rivendell.scene.gimli` |
| Elven marble | Legolas | `rivendell.scene.legolas` |
| Mountain stone | Boromir | `rivendell.scene.boromir` |
| Angainor stone | Bilbo | `rivendell.scene.bilbo` |
| Culumalda planks | Frodo | `rivendell.scene.frodo` |
| Lebethron planks | Samwise | `rivendell.scene.samwise` |
| Deadwood planks | Pippin | `rivendell.scene.pippin` |
| Dwarven maple planks | Merry | `rivendell.scene.merry` |

`/tmscene marks [radius]` walks the cube round the caller - sixteen blocks unless told otherwise,
and the nearest block of each kind wins - and pins the first air over each mark, so a block set
into the floor and a block laid on top of it both give the ground a body stands on. Elrond's mark
doubles as the gathering mark: it is where the note's scene begins, where a rehearsal is now held,
and where his keeper is stood in the valley, so he is in the courtyard with the rest rather than
at his old post forty blocks off.

The old pack's offsets are still there, and are what a body nobody has marked stands on. Nothing
of this is written into the build: these are world pins, so a mark moved by one block is read
again by running the command again, and another world keeps its own plaza.

## Elrond stands in the departure - 10 November 2026

The scene used to speak Elrond's five closing lines over a courtyard with no Elrond in it: the only body
of his in the world is the campaign's keeper, stood where the campaign keeps him. He is now one of the
scene's own eleven - spawned at his mark with the rest, named, keeper-tagged so he wears `elrond.png`,
and taken away when the last line is said. The library keeps its keeper for the audience and for the
tidings carried back, and the plaza gets its own Elrond for the departure; both can be in the valley at
once.

The plaza's marker blocks are a survey, not furniture: they exist in the world the plaza was walked in
and in no other. Once `/tmscene marks` has printed the eleven lines it reads, the positions go into the
build's own pins, so a map with no mithril block in it still stands the Company in the right places - and
a world that does its own survey still overrules the build, so a plaza can be nudged in a working map
without a rebuild.

## The road out - 10 November 2026

The walk off needs a first waypoint, and the courtyard already carries it. Elrond's mark is the middle
and the other ten are the half-ring in front of him, so the open side of the ring is the way out: the
bearing from Elrond through the mean of the Company, stepped out to three blocks past the outermost body,
dropped onto the ground at that column. `/tmscene marks` reports it as `The road out begins at x, y, z`.

It is derived rather than surveyed, so it costs no twelfth block and cannot go stale: move a body's mark
and the road's start moves with it. Moving Elrond's mark moves it too, because the bearing is his. A
plaza with fewer than two marked bodies has no bearing to read and reports nothing - one body beside
Elrond is not a direction.

## The plaza's numbers - 10 November 2026

Read off the marker blocks on the staging plaza - 5117, -3409, the thirteen-block disc at the north end
of the high pass road - on the evening of 10 November 2026, all eleven found at y=77:

| Body | Standing spot |
| --- | --- |
| Elrond | 5117, 77, -3407 |
| Gandalf | 5117, 77, -3412 |
| Aragorn | 5115, 77, -3413 |
| Boromir | 5119, 77, -3413 |
| Gimli | 5113, 77, -3411 |
| Legolas | 5121, 77, -3411 |
| Bilbo | 5115, 77, -3407 |
| Frodo | 5116, 77, -3410 |
| Samwise | 5118, 77, -3410 |
| Pippin | 5114, 77, -3410 |
| Merry | 5120, 77, -3410 |

These are written into `Pins.SOURCE` as `rivendell.scene` (Elrond's, which is also the gathering mark)
and `rivendell.scene.<name>`, so a map that has never held a mithril block stands the Company in exactly
this courtyard. The blocks themselves were a survey: they were for reading the plaza once, and no map
needs them again. A world that lays its own and runs `/tmscene marks` still overrules the build.

With the ring as it stands, the walk out's first waypoint derives to column 5117, -3416 - nine blocks
north of Elrond, three past the outermost body, on the plaza's north rim where the climb leaves.

## How they leave - 10 November 2026

Nobody leaves until everybody has spoken. The last line of the script is Elrond's, and only then do the
nine who go - Gandalf, Aragorn, Boromir, Legolas, Gimli, Frodo, Samwise, Merry and Pippin - turn and walk
off the plaza in lanes of their own, abreast across the bearing the ring gives. Elrond and Bilbo stay
where they were stood until the nine are off the plaza, and the two of them are taken away with the scene.

The walk is driven by the scene, not navigated by the bodies: a body standing in a cutscene has no AI
worth asking, and nothing of its own should take it somewhere the courtyard did not say. Each is stepped
a walking player's pace - 0.2 blocks a tick - toward the far end of its lane, twenty-four blocks out
from the gathering mark, and its ground is re-read from the heightmap on every step so the rim's easing
never leaves it walking in air. A body is taken away on arrival, or after thirty seconds if it has not
arrived, so no showing can leave one standing about.

What the walk does not do yet is leave the plaza: it ends where the courtyard does, twenty-four blocks
out on the open side. Where the road runs after that is a mark of its own and a separate step.

## Facing and small movements - 10 November 2026

The cast used to be turned to the gathering mark, which was right for the ring and wrong for the two
at its edge: Elrond's own mark is the gathering mark, so he faced out of his own scene, and Bilbo,
standing level with him, faced Elrond's shoulder rather than the Company. Everybody now turns to the
mean of the eleven marks - the middle of the courtyard - which puts Elrond and Bilbo among the rest
and leaves the ring looking inward as before.

On top of the standing yaw, two small movements, both stepped on every tick by the scene:

- **A breath.** Four degrees of sway on a six-second cycle, each body on its own phase, so eleven of
  them are never in step. Nobody stands like a signpost.

- **A turn to the player.** While a body's own line is being said, that body turns to whoever is being
  spoken to, clamped to seventy degrees either side of where it stands so nobody spins on the spot if
  the player walks behind him. The sway resumes when the line passes.

Both are made on the body's own yaw. That is deliberate: the head follows the body through
`adjustModelBonesForRender`, so a body that turns is a body that looks, and it needs nothing from the
rigs. A head that turns on its own while the body stays put, or an arm raised as if to shake hands, is
work inside `HumanModel`, `HobbitModel` and `DwarfModel` - a bone moved from the render state, or an
animation controller and its file - and is the next piece rather than this one.

## Heads and hands - 10 November 2026

The whole-body turn to the player is gone: a body now keeps the ground it was stood on and turns its
head, which is what looking at somebody actually is. The scene's part in it is two synched flags on
`TolkienVillagerEntity` - `SCENE_BODY` and `SPEAKING` - because tags are a server fact the client never
sees, and the renderer is where a bone moves.

The renderer's part is in `HumanRender.sceneData` and `HumanRender.sceneBones`, shared by the human,
hobbit and dwarf rigs:

- **The head.** `headYaw` is the player's bearing taken from the body's own facing, clamped to seventy
  degrees and eased a quarter of the way per frame, so it is never a snap. That is the frame the bone
  lives in, which is why the body's yaw is subtracted before the bone is turned.

- **The arm.** `rightArm` comes up to eighty degrees while `SPEAKING` holds, eased in and out. The three
  rigs were read rather than guessed at: `head`, `rightArm` and `leftArm` are the bone names in
  `human.geo.json` and `hobbit.geo.json`, `dwarf.geo.json` has the same pair at a lower pivot, and no arm
  carries a rest rotation, so a raise can be written as a rotation of its own rather than a correction.
  Every other body in the world keeps the head rule it had.

Two numbers to look at in game: `HEAD_SIGN` in `HumanRender` (flip it to -1 if heads turn away from the
player rather than toward), and the eighty degrees of arm raise (negative X, forward and up - if the arm
goes behind the back, the sign of `raised` is what to change).

## The courtyard names itself - 10 November 2026

Walking into the plaza with the note unanswered puts a banner on screen: **Meeting of The Fellowship**,
with **The Gathering** under it. It uses the packets the places already use - the animation
`(15, 50, 20)`, then the subtitle, then the title - and the same bold italic gold the campaign's own
names are set in, so it reads as a place being named rather than as a scene beginning.

It is sent from forty blocks out - wider than the twenty-four that begin the scene - so the banner lands
as the plaza comes into view and is already fading when the Company speaks. The words are
`campaign.tolkienmobs.scene.plaza` and `campaign.tolkienmobs.scene.plaza.line`.

A tag - `tolkienmobs:plaza_named` - keeps it to one showing, since that check runs once a second and the
scene would otherwise be introduced to itself on every tick. `/tmscene forget` removes the tag with the
note and the having-seen, so a re-walk of the seam gets the banner again.

## Reading the route - 10 November 2026

`/tmscene route [radius]` reads ammolite laid along the walk out - sixty-four blocks unless told
otherwise - and numbers it outwards from the gathering mark, pinning each mark as
`rivendell.scene.walk.<n>` at the ground above it. The plaza's own cast marks are matched by column and
skipped, since one of them is ammolite as well: the numbers that come back are the road's.

The numbers are the order the marks read in from the courtyard. A route that doubles back or runs round
the plaza's edge will not sort the way it was laid, so the printed coordinates are worth checking against
the legend before they are baked into the build.
