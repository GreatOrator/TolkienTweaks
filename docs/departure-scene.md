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
