# The mod's commands

Every command the pack registers, in the order a world-runner reaches for them. All seven roots are for
arranging a world rather than for playing in one: each asks for **creative, or a gamemaster-level operator
(op 2)**, and none of them is how a player plays the campaign. Nothing here reaches the world by a menu, so
this file is also the only list of what the pack can be told to do.

The last section is the walkthrough for standing the hand-built Rivendell up in a world.

## `/tmcity` — the hand-built city

The build's own hand, for standing the city up while the cut still lives in a folder rather than in the jar.
It reads `run/rivendell_art` beside the game directory: `rivendell.parts` (each piece and its offset),
`rivendell.npcs` (the city's people), and `_laid.txt` (where the corner was planted, how far it has been
laid, and in which dimension).

| hand | what it does |
|---|---|
| `origin` | Plants the cut's lowest corner at the caller's feet, in the dimension they stand in, forgetting any earlier plant. Answers with the ground the city covers and how many bands it has. |
| `summon` | `origin`, every band in one word, and the city's people after them. One long stall: about two thousand chunks and forty million blocks. |
| `folk` | Stands Elrond, Legolas and Glorfindel at the marks the build gives them, the fifteen unnamed walkers at theirs, and the rest of a crowd of twenty-four wherever the city has ground. Bodies are counted by tag, so it is safe to repeat and a killed one is put back. |
| `band [count]` | Lays the next band, or `count` of them (1–8), and stops. The last one also blends the shoulder. Answers with pieces laid, which bands went down, pieces left out above the sky, and bands still to come. |
| `relay` | Puts the counter back at the bottom of a city already standing, keeping the corner, so a re-cut can be laid over it. Adding is safe; removing is not — a piece that lost blocks leaves the old ones standing. |
| `apron` | Blends the shoulder by hand and reports what it came to: columns cut down, columns built up, and columns left standing because they were more than six blocks off the city's base. |
| `goto` | Stands the caller in the middle of the city. |
| `status` | The city's ground, its middle, which band it is on, and whether the shoulder is blended. Also refuses politely when the corner was planted in another dimension. |

The marker lives in the game directory rather than in a save, so it survives being carried between worlds;
the dimension is written down with it for the same reason.

## `/tmquest` — work, given by hand

| command | what it does |
|---|---|
| `page <kind> <target> [goal]` | Writes one page of the chosen kind against the named target, goal 1–64 (default 1). Kinds: `faction`, `fetch`, `hunt`, `location`, `dialogue`, `survey`. |
| `<targets> give [count]` | Fills the targets' books with work drawn as a giver draws it, from the country they stand in. |
| `<targets> quit` | Takes the work back off the targets without paying for any of it. |
| `<targets> spawn` | Stands a giver beside each target, holding work of its own. |
| `<targets> complete [count]` | Settles what the targets carry, up to `count` pages, without earning it. |
| `<targets> campaign_giver <alliance>` | Stands the campaign's own herald for `west`, `east` or `harad`. |

## `/tmplace` — ground the world knows by name

A place is a circle of ground with a name and a radius, not a border. It is what makes a road announce
itself, and what the beryl's survey counts as ground.

| command | what it does |
|---|---|
| `add <id> [radius]` | Sets a catalogue place at the caller's feet, radius 4–512 (default 32). |
| `remove <id>` | Takes a name back off the ground, so nothing is introduced there any more. |
| `list` | Every catalogue name, where it stands in this world, or that it has not been set. |
| `where` | Which place the caller is standing in — how a radius is checked by hand. |
| `forget <targets>` | Clears what the targets have been shown, so the banners on a road can be walked again. |

The catalogue: `greenway`, `barrows`, `hobbiton`, `bree`, `weathertop`, `ford`, `tharbad`, `rivendell`,
`thorenhad`, `barachen`, `gloin`, `gundabad`, `vault`, `keystone`, `carndum`, `oldforest`.

## `/tmfaction` — standing with the three alliances

| command | what it does |
|---|---|
| `set <targets> <west \| east \| harad> <amount>` | Puts every target at one standing, clamped to the same bounds a hand-in or a kill is held to. |
| `get <targets> [alliance]` | Reads a standing back: one alliance, or all three with their ranks. Answered to the caller only, so asking about a player is not an announcement. |

`/tmsetfaction` and `/tmgetfaction` are the same two branches under flat names, for anyone who would rather
type the verb first.

## `/tmcourage` — where a player is on the climb

| command | what it does |
|---|---|
| `<targets> get` | What each target has earned, and the stage that makes them. |
| `<targets> <deeds>` | Sets courage to an exact number of deeds, 0 to the ceiling. |

Courage is earned in play; this exists because the rungs are a long climb by design, and trying what the
fourth rung does to a Nazgul should not cost an afternoon.

## `/tmtp` — the quick map

A jump to any of the drawing's 49 countries, so a region can be walked while no build is pinned.

| command | what it does |
|---|---|
| *(no argument)*, or `list` | Every region name, which is also what tab completion offers. |
| `<region>` | Stands the caller in Arda at that country's middle, eight blocks over its ground - or over its water, for the three seas - moving them into the dimension from wherever they were. |

The point is not typed: it is `ArdaMap.anchor`, the middle of the cells the country was drawn in, with the height read off the drawing itself. The table in `docs/arda-teleports.md` is the same set of points worked out by hand before the command existed, so the two agree - but only the command follows a fill that moves.

A country whose build is settled wants the build, not its middle. Add one line to `LOCKED` in
`TeleportCommands` - `"bree", new BlockPos(x, y, z)` - and that name lands on the build exactly, with no
other change anywhere. That is how the quick map becomes a map of the world rather than of the poster.

## `/tmpin` - pinning a location to the ground it was scouted on

Stand where a build or a named place belongs and say which it is; the caller's feet become the pin, and
the exact line for `Pins.SOURCE` is printed back, so a spot can be kept for every world by copying rather
than by retyping coordinates out of a debug screen.

| command | what it does |
|---|---|
| *(no argument)*, or `list` | What this world has pinned, and what the build pins for every world. |
| `checklist` | The fifteen locations the two chapters ask for - six names, nine buildings - each marked `this world`, `every world` or `not pinned`, with the count at the top. Places nothing; it is what scouting still owes. |
| `place <id>` | Pins a catalogue name at the caller's feet - one of the sixteen `/tmplace` lists. |
| `build <name>` | Pins a template at the caller's feet: `camp`/`elledancamp` (Elladan's camp), `tent` (the camp errand's), `ruin`, `cave`/`cave1`, `cave2`, `cave3`, `gloincamp`, `gundabad`/`keep`, `weathertop`, `castle`, `arena`, or a full template id. |
| `remove <name>` | Forgets this world's pin for that name. A pin written into the build is in the jar, not in the world, and this does not touch it. |

A pin outranks the drawing. What each kind does to placement, the ground still to scout and the traps
are all in [`arda-pinning.md`](arda-pinning.md), which is the one place they are written down.

Arda only: the pin is read by the campaign's own placement, so the caller has to be standing in Arda.

# Standing Rivendell in a world

What it needs: the cut in `run/rivendell_art/` (584 pieces, `rivendell.parts`, `rivendell.npcs`), a world
with cheats, and creative or op 2. The city is **554 x 244 x 512**, and its lowest corner is planted at the
caller's feet, so **the feet must be at y 76 or below**: above that the top bands are left out with a
message rather than laid.

1. **Pick the ground.** In Arda, Rivendell is pinned at `5200, 75, -3370` (`Pins.SOURCE`): the low corner of
   the 554 by 512 footprint, on ground the city was scouted against. No floor is cut for it any more
   (`PLANTED = false` in `ArdaMap`), so the drawn ground is what it stands on. To look at it, `/execute in
   tolkienmobs:arda run tp @s 5477 100 -3114` and let yourself fall. You do not have to stand on the corner:
   in Arda the pin decides where the city goes, so `/tmcity origin` puts the cut's low corner on
   5200, 75, -3370 from whatever block you are on: the 554 by 512 footprint covers x 5200..5753 by
   z -3370..-2859, the crown at 318, one block under the ceiling. In any other world, pick flat ground with 554 x 512 clear and
   feet at or below 76.

2. **Plant it.** `/tmcity origin`. Whatever ground was there stays around it; the city replaces its own box
   and underpins the bottom band with dirt.
3. **Raise it.** `/tmcity summon` for the whole city in one word (long stall), or `/tmcity band 1` six times
   to watch it come up band by band from the bottom.
4. **Stand its people.** `summon` does this already; otherwise `/tmcity folk`.
5. **Blend the shoulder.** Automatic on the last band. `/tmcity apron` re-runs it and reports the counts —
   ground "already standing on its line" was level enough to need nothing, and a shoulder that moved nothing
   on ground that was not level is one that did not run.
6. **Walk it.** `/tmcity goto`, then `/tmcity status` for which band it is on and whether the shoulder is done.
7. **After a re-cut.** Replace the folder, `/tmcity relay`, `/tmcity band 8`, and `/tmcity apron` again.

**What is not there yet.** The cut is outside the jar and nothing in the pack generates this city: the valley
page no longer builds anything at all, `/tmcity` is the only way to raise the city, and the page that names
the valley dresses whatever is planted there - the build's own marks for the Elves, and its crowd. A world
that is to have Rivendell without a command needs the cut packed into the mod and a placement written for
it - the same ground the map draws, raised by the generator or by the page that names the valley.
