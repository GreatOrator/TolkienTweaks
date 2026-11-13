# Terrain that looks like the game's own mountains

A note on how 1.18+ Minecraft builds mountain ground, what of it Arda already does, and what to
reach for when a new pass, valley, notch or hillside is cut into the map. Written 13 November 2026
from a note the maintainer supplied on vanilla's mountain geometry, checked against this pack's own
generator rather than taken on trust.

Read it before changing anything in `ArdaMap` that decides where the ground stands.

## In one line

Vanilla grows mountains out of noise and shapes them with splines; Arda **draws** them and then cuts
detail into the drawing. The look the two share comes from four things - ridges made by folding noise,
heights eased by curves rather than multiplied, faces steeper than any slope around them, and surfaces
dressed by slope and altitude - and only the last two are cheap here.

## The four mechanics, and what Arda does with each

| mechanic | in vanilla | in Arda |
|---|---|---|
| **multi-noise controls** - continentalness, erosion, peaks & valleys / weirdness read at every column | one 3D noise field, sampled once per column, decides everything | a **drawn map**: 48 × 36 cells of 512 blocks, each a `Country` with a height and a relief, over a 16-bit heightmap mask (`ArdaMasks.ground`) read at 8 blocks to the pixel, lifted toward the crown by the mountain mask (`ArdaMasks.lift`, `rock`) |
| **ridged noise** - `1 - abs(noise)`, squared, summed over octaves | sharp crests, flat-bottomed valleys, chains of peaks rather than cones | `ArdaMap.ragged()` does exactly this, twice: `1 - abs(fractal(..128..))` plus half again at 32 blocks, amplitude `CRAG * rock²` so a range is ragged and the plain beside it is untouched |
| **splines** - non-linear curves mapping noise to height | a spike where erosion is low and peaks are high | `smooth()` (smoothstep) blends between a site's own shape and the drawn ground; the per-site shaping (`valley`, `ridge`, `climb`, `dale`, `gundabadPass`) is where the hand-tuned curves live |
| **3D density and domain warping** - `density = height(x,z) - y + noise3(x,y,z) * jaggedness` | overhangs, arches, cliffs that bulge | **not here, and not possible as it stands**: `ArdaChunkGenerator.reshape` retargets every column to one height - ground above it is cleared, ground below filled - so Arda has no overhangs, no floating rock and no undercut cliffs. Caves are the carvers', cut from the drawn ground, and `seal` puts the drawn water back after them |

## Rules that follow from that

- **The height is a function of the column alone.** `ArdaMap.sample(x, z)` must answer the same thing
  for a column whoever asks, because two chunks that meet agree at their shared border only if it
  does. Anything that reads a neighbouring column to decide this one's height is a seam waiting to
  happen - which is why the river, lake and vale passes are all distance-fields, not neighbour walks.
- **Fold noise, don't smooth it.** A raw `fractal` value is a dune; `1 - abs(fractal)` is a ridge.
  `ragged` is the pattern to copy, including reading two scales and weighting by how much rock the
  mask puts at the column.
- **Never one cone.** Vanilla's mountains are chains along ridge lines. A radial bump around a point
  is the shape a blurred brightness makes on its own, which is why `ragged` exists at all and why the
  ranges have spurs and gullies instead of a smooth gradient.
- **Ease, don't step.** Every blend in `ArdaMap` is a `smooth()` of a share clamped to 0..1, and the
  run lengths are chosen, not inherited: a hillside wants a hundred blocks or more of run, a rock
  face wants ten to twenty. A hard step between two heights is a wall of stone with a straight top,
  which is the single most "not Minecraft" thing terrain can do.
- **A floor is level where a build stands on it, and rolls where it is walked.** `PASS_ROLL` is two
  blocks, faded to zero across a build's own footprint, because a floor course lifted one block off
  the ground is a gap under the whole build.
- **Faces are dressed by slope, not by hand.** `TolkienSurfaceRules` already asks
  `SurfaceRules.steep()` for bare stone on the faces and keeps snow, ice and grass on the gentler
  ground (`highlandSurface`, `gundabadSurface`, `whiteMountainsSurface`). A new surface rule should
  read the same way: steep means stone, altitude means snow, and `materials()` is the one place the
  mod's own stone, dirt and grass live.
- **Shape after `ragged`, not before.** `groundHeight` lifts the drawn height, breaks it into crags
  and hands it on; a site's own shaping has to come last so it cuts the crags as well as the drawing.
  That is why `vale` and `gundabadPass` are applied where they are.

## What the source note suggests that this generator cannot do

- **Overhangs, arches, undercut cliffs.** They need a 3D density field; Arda's whole contract is one
  height per column (`ArdaChunkGenerator.reshape`). A build can be cut into a face - the gate is -
  but the terrain itself will never overhang.
- **Erosion-driven variety.** Vanilla's `erosion` is a noise; here the country's `relief` is a number
  in the `Country` table and only applies where the heightmap has no ground. Turning one knob does
  not roughen a whole range - the drawn mask decides, and `ragged` adds the detail on top.
- **Splines as the engine has them.** Vanilla's splines live in the noise router, which Arda throws
  away: `fillFromNoise` keeps the noise's caves and aquifers and replaces its height outright. Any
  spline we want is a curve we write, in `smooth()` terms.

The Python sample that came with the note is illustrative of the *idea*, not of the engine: its
`noise2` API is the pre-1.18 style, and its constants (0.4, 1.8, 140, 14) are made up to show a
shape. Read it for the shape of the maths, not for numbers to copy.

## Checklist before committing terrain

1. **Where in `groundHeight`?** After `lift` and `ragged`, before the `Sample` is built, so it cuts
   crags and drawing alike.
2. **Is it a function of (x, z) only?** If not, chunk borders will disagree.
3. **Are the blends smooth, and are the runs deliberate?** Face 10-20 blocks, hillside 100+, courtyard
   easing 250-400.
4. **Is it a cone?** Fold something into it - `ragged` does this for ranges, but a hand-cut bowl still
   wants a ridge or a spur.
5. **Is the ground under any build level, and does the build's own edge height mean anything?** The
   gate's notch stands the walls at 267 because that is the top of the gate's wings; a wall that
   started at the floor would leave the build in a trench.
6. **Do the surface rules still make sense there?** Snow over rock, bare stone on the faces, grass
   below the snow line - and no rule that reads only its own column being asked for a distance.
7. **Verify by measuring, not by looking at the code.** `./gradlew compileJava -x
   createMinecraftArtifacts -q --no-build-cache`, then mirror the function in Python against
   `src/main/resources/assets/tolkienmobs/arda/height.png` and `mountains.png` and read the numbers
   off it - that is how the gate's notch was checked before it was ever generated.
8. **Say it is unverified.** Terrain can only be judged in a world, in a fresh chunk.

## Where the code is

| | |
|---|---|
| `ArdaMap.groundHeight` | the whole height of a column: mask, lift, `vale`, `ragged`, then any site shaping |
| `ArdaMap.ragged` | the folded ridged noise, and the pattern to copy |
| `ArdaMap.fractal` / `hash` | the map's own value noise, 0..1, seeded by a salt |
| `ArdaMap.vale`, `ridge`, `climb`, `dale`, `gundabadPass` | the hand-shaped sites: Rivendell's valley and the gate's notch |
| `ArdaMasks.ground`, `rock`, `lift` | the drawn heightmap and the mountain mask |
| `ArdaChunkGenerator.reshape`, `seal` | the retarget to the drawn height, and the drawn water after the carvers |
| `TolkienSurfaceRules` | what the ground is made of, by biome and by slope |
