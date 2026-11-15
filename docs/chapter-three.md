# Chapter three — Fires in the South

The write-up of the second chapter after Gundabad: what the pack's own chapter says, how it divides into
pages, and what has to stand in the world before the first of them can be handed over.

This is the plan, not the code. Nothing here is in `Campaign` yet.

## Where it comes from

- Quests: `CH3-Fires in the South` — ids **56** and **60** to **69**, eleven in all.
- Dialogs: `CH3-Fires in the South` — ids **200** to **228**, twenty-eight of them.
- The pack numbers a chapter's quests and writes its dialogs as separate files, so the two are tied together
  by the ids the quests name (`CompleterNpc`) and by the order the dialogs were written in — dialog 202 is
  Elrond saying "speak with Aragorn", and quest 56 is the walk that follows it.

The pack's chapters are numbered where the mod's pages are: chapter two became pages 2-1 to 2-11, and this
chapter becomes **3-1 to 3-11**.

## The chapter in one paragraph

Angmar's shadow has moved: Elrond sends the player to Aragorn, a wounded falcon brings word from Mitheithel
that a Ranger thought lost is alive at a village east of Moria, and the man — Manwyn — tells of a company
that went into Khazad-dûm and never came back. The road west runs through **the Watchers**, statues that
break the will of anyone who comes near them, and the only way past them is to take a whole stone-heart out
of the fortress of Dol Guldur and turn the statues' own power against them. The chapter ends inside Moria,
at a dwarf camp, with the question the pack leaves hanging: how did you pass the stones and not perish?

## The pages

| Page | Pack quest | Title | Kind | Giver | Turn-in | Ground / body |
|---|---|---|---|---|---|---|
| 3-1 | 56 | Turning to the South I | DIALOGUE | Elrond | **Aragorn** | the Last Homely House, Rivendell |
| 3-2 | 60 | Cromhes and Moria | DIALOGUE | Aragorn | **Guthwine** | Mitheithel, the Rangers' refuge in the South Downs |
| 3-3 | 61 | Against His Leader | DIALOGUE | Guthwine | **Manwyn** | Stoneyford, a village of Men east of Khazad-dûm |
| 3-4 | 62 | Secrets of the Watchers I | LOCATION (gives the rubbing) | Manwyn | — (settles itself) | the Watchers, at the eastern gate of Moria |
| 3-5 | 63 | Secrets of the Watchers II | FETCH `item_rubbing` | Manwyn | Manwyn | back to Stoneyford |
| 3-6 | 64 | Heart of the Watchers I | LOCATION (gives the cracked heart) | Manwyn | — (settles itself) | a lifeless watching-stone, east of Stoneyford, toward Dol Guldur |
| 3-7 | 65 | Heart of the Watchers II | FETCH `item_stone_heart` (cracked) | Manwyn | Manwyn | back to Stoneyford |
| 3-8 | 66 | The Priest's Doom I | HUNT "Narmos" | Manwyn | Manwyn | the ritual at Dol Guldur |
| 3-9 | 67 | The Priest's Doom II | FETCH `item_stone_heart` (whole) | Manwyn | Manwyn | back to Stoneyford |
| 3-10 | 68 | Challenging the Watcher | HUNT "The Ancient Spirit" | Manwyn | Manwyn | the Watchers again, with Manwyn's charm |
| 3-11 | 69 | Into the Darkness of Khazad-dûm | DIALOGUE | Manwyn | **Rurain** | a dwarf camp inside Moria |

Two shapes recur and are worth naming before the pages are written:

- **The object pages** — 3-4/3-5, 3-6/3-7, 3-8/3-9 are each one errand split in two the way the pack splits
  them: the walk that gets the thing, and the walk that brings it back. The walk settles itself and hands the
  object over on the spot (the way the Key-stone's vault does now); the second is a fetch carried to Manwyn.
- **The named kills** — Narmos and the Ancient Spirit are one body each, so both are named pages (see
  `namesBody` in `Campaign`), not a count of a folk.

## What each page says

Prose below is written to the pack's own words, in the voice chapter two is written in. Title keys are given
so nothing has to be renamed later; `place` and `closing` are the two lines the book and the leaf print.

### 3-1 — Turning to the South I

- `campaign.tolkienmobs.page.south`
- Place: **the Last Homely House, in the valley of Imladris**
- Closing: *"Angmar grows with each passing day, and without a means to stem its evil we have no hope of
  winning this war. Aragorn waits for you in the main house, out on the porch, looking the way he always
  looks — west. Go to him, and hear what the falcons have brought."*

### 3-2 — Cromhes and Moria

- `campaign.tolkienmobs.page.mitheithel`
- Place: **Mitheithel, the hidden refuge of the Rangers in the South Downs**
- Closing: *"A falcon came to Rivendell this morning with a message from Mitheithel, and another came to
  Mitheithel out of the north — gravely wounded, carrying word from a kinsman the Rangers had given up for
  dead. Aragorn cannot go: his road lies north and east. He asks you to go in his stead and speak with
  Guthwine, who sent the bird."*

### 3-3 — Against His Leader

- `campaign.tolkienmobs.page.stoneyford`
- Place: **Stoneyford, a village of Men in the Gladden Fields, east of Khazad-dûm**
- Closing: *"Years ago a captain named Cromhes took a company south into Moria against Aragorn's edict, and
  none of them were heard from again. Now Manwyn, one of that company, writes from a village called
  Stoneyford that he has been dwelling there since — and that the message would not wait. Guthwine marks the
  road for you. Go east, and hear what kept a Ranger from home for so long."*

### 3-4 — Secrets of the Watchers I

- `campaign.tolkienmobs.page.rubbing`
- Place: **the Watchers, the statues at the eastern gate of Khazad-dûm**
- Closing: *"Cromhes' company was broken at a vale full of statues, and Manwyn never learned how. The runes on
  the pedestals may say what the stones are, so go carefully up to one of the Watchers and take a rubbing of
  the face of its pedestal. Carefully: the stones are not blind, and what they do to a will that comes too
  near is not a thing a man walks away from twice."*

### 3-5 — Secrets of the Watchers II

- `campaign.tolkienmobs.page.rubbingback`
- Place: **Stoneyford**
- Closing: *"The rubbing is in your pack, and it is worse than a curiosity: the runes are Elvish in mode and
  Black Speech in tongue, and Manwyn can read only a little of it. What he can read is one word repeated —
  a heart, inside the stone. Bring it to him and let him look at it in the light."*

### 3-6 — Heart of the Watchers I

- `campaign.tolkienmobs.page.heart`
- Place: **a lifeless watching-stone, east of Stoneyford on the road to Dol Guldur**
- Closing: *"There are stones east of the village that have gone dark, and a heart can be taken from one of
  those without fear — the runes say so, and Manwyn believes them. Search the dead stones on the way to Dol
  Guldur, and bring back whatever the crack in the stonework is hiding."*

### 3-7 — Heart of the Watchers II

- `campaign.tolkienmobs.page.heartback`
- Place: **Stoneyford**
- Closing: *"The heart came out of the stone easily enough, and the unease that came with it passed as
  quickly as it came. It is cracked, though, and a cracked heart is no use to anyone — which is why Manwyn
  will want to see it himself before he decides what the village does next."*

### 3-8 — The Priest's Doom I

- `campaign.tolkienmobs.page.narmos`
- Place: **Dol Guldur, the fortress south-east of the dead stones**
- Closing: *"Some of the villagers came back from a hunt with word of a ritual being worked at Dol Guldur,
  and the sorcerer working it carries a whole stone-heart. His name is Narmos, and he is of Angmar — a
  steward of Khazad-dûm the people here call the False King's priest. Break the ritual and take the heart off
  him, and go quickly: if he is making another watching-stone, there will not be a second chance."*

### 3-9 — The Priest's Doom II

- `campaign.tolkienmobs.page.narmosback`
- Place: **Stoneyford**
- Closing: *"The heart is whole. That is the good of it, and the whole of the good: Manwyn will not touch it,
  and what he says when he sees it is worse than the stone itself. Carry it back to him anyway — he asked for
  it, and he is owed the truth of what he asked for."*

### 3-10 — Challenging the Watcher

- `campaign.tolkienmobs.page.spirit`
- Place: **the Watchers**
- Closing: *"It is no spell that gives the statues their power: a heart like this one was prepared to receive
  a spirit of an elder Age, the same kind that inhabits the wights that serve Angmar. Manwyn has put a charm
  on you that should give you the will to pass the stones — and it may draw one of the spirits out of the
  stone to try you. If it does, put it down, and come back and tell him the charm held."*

### 3-11 — Into the Darkness of Khazad-dûm

- `campaign.tolkienmobs.page.khazaddum`
- Place: **a dwarf camp inside Khazad-dûm**
- Closing: *"You have proven the stones can be beaten — that their oppression is only as great as you allow
  it. Manwyn's own spirit was broken too long ago to walk with you, so go west alone, pass the Watchers, and
  look for a dwarf camp somewhere inside Moria. The village says there are dwarves in there. Find them, and
  find what is left of Cromhes' company."*

## What chapter three needs that is not standing

### Ground

| Ground | State | What it needs |
|---|---|---|
| The Last Homely House, Rivendell | standing (the valley) | a stand point for Aragorn on the porch, added to `VALLEY_STANDS` |
| Mitheithel | nothing | a Ranger camp — a new `Sites` template (`key_structures/mitheithel_camp`), pitched when 3-2 is taken, the way Glóin's camp is |
| Stoneyford | nothing | a village of Men in the Gladden Fields — a new `Sites` template, or a pinned set of the pack's own village pieces; it is the chapter's home fire and four pages end there |
| The Watchers | nothing | the statues at Moria's eastern gate: a new template, and the thing the two "Challenging" pages happen at. This is the chapter's signature set piece and the one most worth building properly |
| The dead stones | nothing | lifeless copies of the same statues, away from the road, toward Dol Guldur |
| Dol Guldur | nothing | a fortress for the ritual — `boss_castle` and the orc halls (`orcs/orc_hall`, `orc_tower`, `orc_lava_pit`) are the nearest pieces in the pack and could be laid as a yard rather than a new build |
| The dwarf camp in Moria | nothing | the chapter's last stop; `Sites` template, pitched when 3-11 is taken |

The `tolkienmobs:moria` biome already exists in the dimension, so Moria is ground the world has; it is only a
camp that has to be raised on it.

### Bodies

| Body | Role | Notes |
|---|---|---|
| **Aragorn** | gives 3-2, receives 3-1 | a named human keeper in the valley, like Elrond; needs his own skin file `middle-earth/aragorn.png` if the pack has one |
| **Guthwine** | receives 3-2, gives 3-3 | a Ranger at Mitheithel |
| **Manwyn** | the chapter's spine: gives 3-4 to 3-11, receives most of them | a Ranger at Stoneyford, standing at the village's fire; the pack's dialogs 210–228 are all his |
| **Narmos** | 3-8's quarry | a sorcerer of Angmar; the mod has no sorcerer body, so either a `FactionGuardEntity` of the East dressed as one, or a `WitchKingEntity` with the health and the bar turned down |
| **The Ancient Spirit** | 3-10's quarry | the pack's own answer is the wight — `entityttmbarrow` — named "The Ancient Spirit" and stood at the Watchers while the page is in hand |
| **Rurain** | receives 3-11 | a dwarf at the camp in Moria, and the chapter's last voice |

### Objects

- `item_rubbing` — the watching-stone rubbing (3-4 hands it over, 3-5 carries it back).
- `item_stone_heart` — one item, two states: cracked (3-6/3-7) and whole (3-8/3-9). Either two items or one
  with a component; two is the smaller change and reads better in the book.
- Manwyn's charm — best as a player tag rather than an item: the Watchers are a scene, not a code, and the
  charm only has to be remembered between 3-10 and 3-11.

### Ladder

Standing and courage at the tenth the notes are read at, along the line the chapter climbs:

| Page | 3-1 | 3-2 | 3-3 | 3-4 | 3-5 | 3-6 | 3-7 | 3-8 | 3-9 | 3-10 | 3-11 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| Standing | 50 | 50 | 40 | 60 | 30 | 40 | 60 | 90 | 40 | 90 | 70 |
| Courage | 4 | 4 | 4 | 5 | 4 | 4 | 5 | 6 | 4 | 7 | 6 |

## Open questions

1. **Stoneyford and the Watchers are both new builds.** Neither is in the pack's `key_structures`, and both
   are needed before the chapter can be walked. The Watchers are the chapter's whole identity; Stoneyford is
   a village that four pages end in. Are these to be built here, or does the map's own world already have
   them at the places the pack used?
2. **The wight is named rather than tagged.** 3-10 says "the spirit" and the pack's own statue is not a body;
   standing a named barrow-wight at the Watchers is the smallest answer and matches how Matuna is done. The
   alternative — a new spirit body — is an asset decision, not a story one.
3. **Narmos.** A sorcerer of Angmar has no body in the mod. Reuse a `FactionGuardEntity` of the East, or the
   Witch-king with his bar muted? The pack gives him no fight of his own beyond the ritual.
4. **The charm.** As a tag, nothing has to be equipped or lost; as an item, it can be worn and can be asked
   for by later pages. The tag is the smaller change.
5. **Where the chapter hands over.** Chapter two ended on Elrond's ears (2-11 turns in at Elrond), and the
   pack's 202 is Elrond saying "speak with Aragorn", so 3-1 needs nothing new to open it: the same Elf hands
   it. If chapter three is to open from a herald instead, that is a different beat.
