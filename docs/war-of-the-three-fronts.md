# The War of the Three Fronts

The campaign script: what is said, where it happens, and what the player has to do. It is written to
be read aloud by NPCs the way the older quest pages were, one page per quest, so a stage can be built
from a page without inventing anything.

**The spine.** Three alliances (West, East, Harad) each hold a front, and the war is lost or won on
whatever front the player chooses to stand. The enemy is not three armies but one will behind them —
a Rider who was never thrown down at the Ford — and every act ends by showing that the front just
cleared was only a diversion. Act I teaches the player fear (the `PARALYSING_FEAR` pin). Act II turns
that pin into slowness, so the fear still costs something without stopping the fight. Act III is where
a player who has done the work can stand in the arena and not flinch at all — the courage ladder is
the story's own progression, and the Witch-king's refusal is earned rather than granted.

**What each page gives.** A speaker, the ground they stand on, the words, the errand, what the errand
pays, and which of the mod's quest kinds can express it today. Where a page needs machinery the mod
does not have yet — an ordered chain, a world state, a staged build — it says so in **Needs**, so the
mechanic can be built for the page rather than guessed at.

---

## Act I — The Green Way

The West's front. The country is Eriador's roads and the Shire's edges: quiet ground, and that is the
point, because the first thing the war does is make quiet ground frightening.

### 1. A Rider on the Greenway — Maelgrin, Ranger of Eriador

*Place:* the West's hall (`faction_hall_west`), in Eriador.

> **Maelgrin:** "You have come a long road to stand in a cold hall. Sit, then — but not long. I have
> riders out on the Greenway and two of them are late, and lateness on that road has not meant
> anything good in thirty years."
>
> 1. "Late riders are often only tired."
> 2. "What is on the Greenway that frightens a Ranger?"
>
> "Travellers, mostly. Farm carts, a pedlar with too many knives. And three nights ago, a shape that
> my scout swore was a horseman and would not swear was a man. He has not sworn anything since; he
> came back at a walk with his horse lathered and would not say what he saw, only that he heard
> breathing where nothing stood. Go out to the road, @p, and find what he found. Take the horn — if
> you must blow it, blow it once and run."

**Errand:** Go to the Greenway and find the scout's marker. **Built from:** `FETCH` the scout's token,
placed in a wild tent (`wild_tent`) beside the road. **Pays:** standing with the West, and the war horn
if the player does not yet carry one.

**Needs:** a placed tent is a structure the campaign builds, not one the generator draws — stage it.

### 2. Breathing Where Nothing Stands — Maelgrin, and the scout, Halvard

> **Halvard:** "I will say it once and then I will not speak of it again. There was a cold in the
> ditch that had nothing to do with the weather. My lamp went thin. And something leaned close enough
> to my ear that I felt the weight of it on the air, and it said a word I did not know, and I have not
> slept since."
>
> **Maelgrin:** "He is not mad, @p, and that is the trouble. The Nine were broken at the Ford, but the
> river took eight horses. Eight. Go back out with him, and this time do not go alone."

**Errand:** Hold ground at the ditch through the night. **Built from:** `HUNT` — the night's visitors
are the mod's orcs and a warg (`ttmwargpit` is their kennel; break it). **Pays:** standing, courage
deed `fear withstood` for standing in the fear aura and not breaking.

**Needs:** an aura source at the site — the `dread_aura` effect on a hidden marker, or the Nazgûl
itself passing through on a scripted path.

### 3. The Drover's Tale — Aldwyn Thistlewood, a Shire farmer

*Place:* a Shire village (`ttmhouse_hobbit`), the house furthest west.

> **Aldwyn:** "You'll be the one from the hall. Good. Sit — no, don't sit, there's no time to sit.
> Six of my beasts went in the night and I'll not say a wolf did it, because a wolf leaves a mess and
> a wolf leaves tracks and there are neither. Whatever took them took them *whole*, and it took the
> fence as well, and the fence was oak."
>
> 1. "Beasts do not walk through oak."
> 2. "Show me where the fence stood."
>
> "That's what I said. And my lad saw a light out on the barrows two nights running, which I told him
> to keep to himself, and now I'm telling you. I'm not a brave hobbit, @p, and I'd like to go on not
> being one. Do whatever it is you do out there, and let me sleep."

**Errand:** The barrows (`ttmbarrow`) are being opened from the inside. **Built from:** `HUNT` the
wights that come out of them; `FETCH` the dropped key fragment. **Pays:** coin, standing with the
West, courage deed for the quest handed in.

**Needs:** nothing beyond the barrow structure, which already generates.

### 4. What the Dead Were Keeping — Maelgrin

> **Maelgrin:** "A key that opens nothing in a barrow that should open nothing at all. And you say the
> wight was *guarding* it, not hoarding it. That is not how a wight behaves, @p. A wight keeps what it
> loved. This one was set to a duty."
>
> 1. "Set by whom?"
> 2. "Then someone wanted it found — or wanted it kept."
>
> "Both, I think, and that is the shape of a trap. There is a tower in the old forest that no one built
> in my lifetime and no one has entered in anyone's. If a Rider is gathering the West's dead to his
> hand, that is where the hand is. Take the key. Do not take the key if you are not ready to use it."

**Errand:** Enter the dark tower (`ttmdark_tower`). **Built from:** `FETCH` the key fragment into the
tower's door — the campaign's first hand-placed gate. **Pays:** the Act I courage rung (Trembling,
6 deeds), a mithril coin.

### 5. The Rider's Word — the Nazgûl

*Place:* the top of the dark tower. One conversation, and the player does not get to win it.

> **The Rider:** "You came up the stair. They always come up the stair."
>
> 1. "You were thrown down at the Ford."
> 2. "Then say your word and be done."
>
> "The river took a horse. Rivers are patient and rivers are stupid. I have been in your West since
> the ice went out, walking your roads, counting your halls, and I have not been counting for myself.
> Blow your little horn, Man of the West. It will bring help, and help is what I came for."
>
> *The Rider does not fight. He leaves, and the tower's stores burn behind him.*

**Errand:** Survive the tower as it burns; the Rider is not killable here. **Built from:** a scripted
encounter — the Nazgûl (`entityttm_nazgul`) with the player unable to finish it. **Pays:** courage deed
for the Nazgûl encounter (5 deeds), and the act's turn.

**Needs:** a non-lethal encounter state — the mob must leave at a threshold rather than die. This is
the one mechanic Act I cannot be built without.

---

## Act II — The Iron Road

The East's front, and the act where the fear changes its grip. The ground: Mirkwood's eaves, the
Dagorlad, and the road the enemy's smiths use to feed Mordor.

### 6. Smiths on the Iron Road — Guthlac, a dwarf of the Iron Hills

*Place:* the East's hall (`faction_hall_east`).

> **Guthlac:** "You want the short version? Wagons. Wagons going east that come back empty, and
> wagons are heavy things to move for no reason. I put a lad on the road to count them and he counted
> forty-one before he stopped counting, and he stopped counting because he was dead."
>
> 1. "Forty-one wagons of what?"
> 2. "What does an empty wagon weigh, Guthlac?"
>
> "Now that's the right question. Nothing. So they are not empty, they are *unloaded*, and a wagon
> unloaded at a fortress is a wagon feeding a fortress. There is a pit out past the wood with a warg
> in it and worse things than a warg. Break it, and the road goes quiet, and I will believe you when
> you tell me what you found in the mud."

**Errand:** Warg pit (`ttmwargpit`) and the road beside it. **Built from:** `HUNT` wargs and orcs;
`FETCH` the fortress map from the pit's cache. **Pays:** standing with the East, coin.

### 7. The Map That Should Not Exist — Guthlac

> **Guthlac:** "That is a fortress plan, @p, and it is *good*. Whoever drew this drew it from inside.
> Look at the smithy flue — no one knows a flue who has not stood under it. So either we have a friend
> in that fortress or the enemy is handing out plans to travellers, and I know which of those I would
> bet a beard on."
>
> 1. "Then it is bait."
> 2. "Then we have a friend, and we should find them."
>
> "Aye. Bait. But bait is only bait if you take it, and I would rather know the shape of the walls
> than not. Keep it. And @p — the wagons are not carrying iron. Iron does not need forty-one wagons."

**Errand:** Read the plan where the enemy cannot see you doing it — a ruin (`ttmruin_large`) away from
the road. **Built from:** `FETCH` the map to a marked spot; a scouting timer. **Pays:** courage deed,
standing.

### 8. What the Wagons Carried — Maelgrin's letter, read by Guthlac

> **Guthlac:** "Your Ranger writes a fine hand for a man who lives in a ditch. He says the West has
> been quiet for a month and that quiet has *frightened* him, and I have read that sentence four times
> and it frightens me too. A war that stops on one front has not stopped. It has moved."
>
> 1. "Moved where?"
> 2. "Then we are the diversion."
>
> "We are the *noise*, and the noise is what he wants us to make. The wagons went south. Every one of
> them. Go and look at what they were feeding, and come back and tell me I am wrong."

**Errand:** Follow the road south into Nurn (`nurn`) and count what has been built. **Built from:**
`HUNT` the orc garrison of a Nurn village (`ttmhouse_nurn`); `FETCH` a scroll (`ITEM_SCROLL`) from the
overseer. **Pays:** standing with the East, the Act II rung (Stout-hearted, 40 deeds).

### 9. The Steward of the South — the Witch-king's voice, through a scroll

*Place:* a Nurn hall, after the garrison falls.

> **The scroll, read aloud, in a voice that is not the reader's:** "To the captains of the southern
> road. The West is patient and the East is proud and both are slow. Let them count their wagons. When
> the last of the iron is in the arena, I will need none of you, and you will be glad of it."
>
> 1. "An arena."
> 2. "Then the war was never on three fronts at all."
>
> *The last line is not in the same hand. It is smaller, and it was added later: "He is not wrong.
> Run."*

**Errand:** Take the scroll to whoever the player stands with — the campaign forks here by alliance.
**Built from:** `FETCH` the scroll to the player's best-standing hall. **Pays:** courage deed for the
Witch-king's name spoken (no fight yet), and the Act II turn.

---

## Act III — The Arena

The South's front, and the end of it. Two builds the mod now carries: the fortress (`boss_castle`) and
the arena beneath it (`boss_arena`). This act is where the courage ladder pays out — at Unbowed the
`dread_aura` is refused outright, and a player who has done the work walks into the arena and simply
does not flinch.

### 10. The Last Muster — the player's own alliance warden

*Place:* whichever hall the player has the highest standing with.

> **The warden:** "You have brought us a scroll that says we are not the war. I have read it to the
> hall twice. The first time they laughed. The second time nobody did, and now they are sharpening
> things they had put away, and I do not know whether to thank you or put you out."
>
> 1. "Then let them sharpen. I will go first."
> 2. "Three fronts were a lie. One fortress is the truth. Give me what you have."
>
> "What I have is a name and a road. The fortress stands where the wagons were going, and beneath its
> yard there is a floor of sand that no one lays for grain. Take the horn. Take whoever will follow a
> fool. Blow it at the gate and do not stop blowing until the gate is behind you."

**Errand:** Muster the three alliances' volunteers at the fortress gate. **Built from:** `FETCH` a
faction token of each alliance to the site (three tokens, three errands — the act's gathering phase).
**Pays:** courage deed per token, standing with each.

### 11. The Court of the Fortress — three Nazgûl

*Place:* inside `boss_castle`.

> **The first:** "He said you would come up the stair."
> **The second:** "He said you would bring the horn."
> **The third:** "He did not say what you would do with it."

**Errand:** Three Riders, met one at a time in the fortress's court. The horn is the answer to the
fear — a player who blows it at the right moment keeps their feet. **Built from:** three staggered
Nazgûl (`entityttm_nazgul`) encounters. **Pays:** courage deed per Rider (5 each), the Fearless rung
(75 deeds) between the second and third.

### 12. The Floor of Sand — the Witch-king

*Place:* `boss_arena`, below the fortress.

> **The Witch-king:** "You have come a long way to stand on sand. Do you know why I had it laid? Not
> for you. For the others — the ones who came before you, who were brave, and who learned in the first
> minute what their bravery was worth. You may keep your feet, @p. I have seen to it that you can. It
> will not help you."
>
> 1. "Then let us find out what it is worth."
> 2. *(Say nothing. Blow the horn.)*

**Errand:** The Witch-king (`entityttm_witchking`), in the arena, with `dread_aura` running. **Pays:**
courage deed 20 (the Unbowed rung at 130 deeds lands here or just before), the war's end text.

**Needs:** the boss arena staged under the castle — both are placed builds now, and this is the
campaign's job to place them together, not the generator's.

---

## What is buildable now, and what is not

| Page | Today | Missing |
|---|---|---|
| 1–4, 6–8, 10 | `HUNT` / `FETCH` / `FACTION` at a named giver, courage deeds on hand-in and on fear | a fixed giver at a known place rather than a spawned one |
| 5 | — | a Nazgûl that leaves instead of dying |
| 9, 12 | `FETCH` to a hall; `HUNT` the boss | a per-player campaign stage, so page 9 does not read as a random scroll |
| 10 | three `FETCH` errands | — |
| 11 | three staged Nazgûl | ordered encounters in one build |

The gap is the same one in every row: **the mod has quests, not a questline.** A stage counter per
player (which page they are on) turns every page above into ordinary work the existing pool already
knows how to pay, and it is the smallest thing that makes the campaign a campaign rather than a
handful of good errands.
