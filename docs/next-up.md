# Next up

The working state of the campaign, written down so a fresh session does not have to guess at it. Read this
first, then `docs/chapter-three.md` for the chapter that comes next.

**Last touched:** the four sign post blocks (plinth, shaft, cap, arm) and the arm's destination label.

## First thing in the morning

1. **The roads from Glóin's camp to Gundabad.** Asked for on 15 Nov, not started. `Roads.ROUTES` stops at the
   camp: `GLOIN_ROAD_EAST` runs 6493, -3662 → 8234, -3590 and the camp's own spur is at 8215..8242,
   -3674..-3553. Nothing goes from the camp west or north toward the hold at `ArdaMap.keepPlan()`
   (5471, 176, -7100), whose gate approach is around 5577, -6830. Page 2-1 to 2-3 is a walk a player takes,
   so the walk needs a road: a new `Route` (or legs of one, with a `Route.Crossing` for any water on the way),
   pinned in `Pins.SOURCE` if it needs a mark. `/tmroad` is the tool. **Work out the line from the map, do not
   guess it from these numbers.**
2. Then walk the chapter in game: camp → cave (2-5's vault, the stone) → camp (2-6) → cave (Gimli, 2-7) →
   the coal lattice at z -6856 → the keep → the captain and the wraith → camp (turn in; Glóin takes the
   stone back).

## Where chapter two stands

Done, committed, and **not yet seen in game**:

- The keep stands at its plan; the cave east of the gate is cut and dressed, its mouth gated on a player within
  64 blocks holding a hold page (`Campaign.onHoldPage`).
- Gimli at the cave dais (5703, 188, -6676), the captain on the throne (5501, 193, -7024), the wraith three
  blocks south of him.
- The coal lattice door at x 5575-5581, y 190-199, z -6856 lifts for a player within 12 blocks carrying
  `item_keystone`, one course every 2 ticks.
- The captain fights (`DugorimFight`): 40 health, iron axe, closes and swings for 6 once a second, red boss bar
  within 32 blocks, plate "Dugorim, Captain of the Dourhands".
- Page 2-5 is the vault in the cave beside Gundabad: the place `keystone` is pinned at the cave's dais, the
  cave's mouth opens for that page, arriving hands the stone over, and Glóin takes it back at the 2-7 turn-in.

**Unverified in game, and worth watching for on the walk:**

- whether the captain's own chase beats the villager brain (`DugorimFight` re-issues navigation every tick);
- whether the iron axe shows in the dwarf model's hand;
- the boss bar's fill as he takes damage, and whether 32 blocks is the right reach in the halls;
- whether 2-5's place circle (16 blocks around the dais) is entered from the cave floor without stepping
  outside it;
- the gate door's timing (about four seconds) and whether the wrapper around it reads as a door.

## Sign posts

Four blocks, `sign_post_plinth`, `sign_post_shaft`, `sign_post_cap` and `sign_post_arm`, all bedrock-hard with
no recipe and no drop. The models under `models/block/sign_post_*.json` are the artist's and each is one block
tall: the plinth and the shaft fill the cell, the cap is two pixels at the bottom of its cell (so it reads as
the shaft's crown), and the arm is one pixel thin, four high and runs from z -8 (eight pixels back inside the
post's own cell, so board and post meet) out to 12.5; it is drawn pointing south, so an arm facing south has its
post to the north. The block's shape follows the model outside its cell, and the three lifted level models are
generated from the base, uv slices included.

Block states: the three post parts have one state each. The arm carries `facing` and `level` (0-3), sixteen
states over four models, `sign_post_arm` and `sign_post_arm_1..3` lifted by four pixels each - the lifted
models were generated from the first, so if the artist re-exports the arm the other three must be regenerated
too. Not yet seen in game, and worth watching:

- whether the two lines are legible inked in black on the board's own faces at the pinned scale, and whether
  the lettering sits on the wood rather than clipping through it;
- whether the arm's `y` rotations are the four intended ones (south 0, east 90, north 180, west 270), and
  whether the automatic facing puts the flat end against the shaft from every face one can click;
- that a cell holds one arm: `level` slides that arm up and down within its own cell in four pixel steps, so
  boards in cells above and below can be brought flush, but two boards inside one cell would need a model
  that draws both;
- whether the cap's two pixels line up with the shaft's top of 16 rather than sitting in the cell below it;
- whether the plinth's own post stub (10 to 16) meets the first shaft's foot without a seam;
- whether a board can be placed against a cap as well as a shaft, and whether it breaks when the post goes.
- the four item definitions live in `src/generated/resources/assets/tolkienmobs/items/`; without them the items
  draw as nothing and the log says `Missing item model`. A datagen run writes them for every item, so they are
  only hand-written because that run has not happened since the blocks were added.

## Spikes

`SpikeBlock`, registered as `spike_stone` and `spike_wooden`: one class, one shape of eight stepped plates taken
from the models, two models off the two halves of `spikes.png`. Both drop themselves (loot tables written into
the generated pack and `dropSelf` added to the loot provider) and both hurt anything inside them for two points
of general damage, spaced by the game's own hurt timer. Not yet seen in game: whether two points a half second
reads as a nuisance rather than a wall (it is a heart every second against a 10 point bar), whether the stepped
shape is walkable rather than a trip hazard, and whether the stone half of the sheet lines up on the faces as the
artist drew it.

## Chapter three

`docs/chapter-three.md` is the plan: eleven pages (3-1 to 3-11) from the pack's `CH3-Fires in the South`
(quests 56 and 60-69, dialogs 200-228), with prose written for each. Nothing of it is in `Campaign` yet.

What it needs before any code: Mitheithel, Stoneyford, **the Watchers**, the dead stones, Dol Guldur, and the
dwarf camp in Moria; five bodies (Aragorn, Guthwine, Manwyn, Narmos, Rurain) and the wight as the Ancient
Spirit; two props and one charm. The chapter's own open questions are at the foot of that file, and the first
of them - whether Stoneyford and the Watchers are built here or already stand in the map's world - decides
how much of the rest is work.

## The rules this project is walked by

- One `CHANGELOG.md` bullet per change, in the long `- **Claim.** …` prose.
- Commit to `main` from `TolkienTweaks/`; **never push**.
- "Verified" means `./gradlew compileJava -x createMinecraftArtifacts -q --no-build-cache` exits 0, and the
  answer always says what was **not** seen in game. Never claim a playtest.
- Java files are CRLF; patch through a `build/` script and delete it after.
