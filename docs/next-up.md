# Next up

The working state of the campaign, written down so a fresh session does not have to guess at it. Read this
first, then `docs/chapter-three.md` for the chapter that comes next.

**Last touched:** chapter two through page 2-7, and the chapter-three write-up.

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
