# The War of the Three Fronts — the West's line

The campaign script: what is said, where it happens, and what the player has to do. It is written to
be read aloud by NPCs the way the older quest pages were, one page per quest, so a stage can be built
from a page without inventing anything.

**One faction, for now.** This is the West's questline and nobody else's. Every hall the player is
sent to is the West's, every speaker answers to the West, and the standing it pays is the West's. The
East and Harad lines come later by retooling this one — the spine, the acts and the courage ladder
stay, and only the halls, the speakers, the countries and the items the enemy is moving change. The
note at the foot of this file lists exactly what a retool touches, so the second and third lines are
a rewrite rather than a design.

**The spine.** The war has three fronts and the player holds one of them: the West's. The enemy is not
three armies but one will behind them — a Rider who was never thrown down at the Ford — and every act
ends by showing that the front just cleared was only a diversion. Act I teaches the player fear (the
`PARALYSING_FEAR` pin). Act II turns that pin into slowness, so the fear still costs something without
stopping the fight. Act III is where a player who has done the work can stand in the arena and not
flinch at all — the courage ladder is the story's own progression, and the Witch-king's refusal is
earned rather than granted.

**The Rider cannot be killed, and the game lets you kill him anyway.** A Nazgûl is not a dead man, it
is a will that has been given a shape; nothing a player carries can end one, and in the tales they are
not ended at all until the Ring is. So page 5's Rider takes damage, runs out of health, and falls like
anything else — and then he is standing on the next page, unharmed, unimpressed, and mildly interested
that you tried. The mechanic is a lie the game tells with a straight face, and it is the truest thing
in the campaign.

**What each page gives.** A speaker, the ground they stand on, the words, the errand, what the errand
pays, and which of the mod's quest kinds can express it today. Where a page needs machinery the mod
does not have yet — an ordered chain, a world state, a staged build — it says so in **Needs**, so the
mechanic can be built for the page rather than guessed at.

---

## The Nine

The Ringwraiths, named once, so a page can name one instead of calling him "the Rider" for three acts.
Most of these names are not Tolkien's - they come from the old campaign material that gave the Nine
names at all - so they are furniture that can be moved, not scripture. Two are canon and stay: **the
Witch-king of Angmar**, who is the will behind the whole war, and **Khamul the Easterling**, the Shadow
of the East, who was the wraith sent to look for the Shire, which is exactly the errand this line puts
him on.

- **The Witch-king of Angmar** - page 12. He is the war, and he is fought once, at the end.
- **Khamul the Easterling**, the Shadow of the East - the Rider of pages 5 and 11. Act I's shape is his
  errand in the North: he was sent to find a country and count its halls, and he is doing it slowly
  because he has all the time there is.
- **Murazor** - the Witch-king's own name in the same table, not a tenth body. This file calls him by
  Angmar's name, and keeps this one for a page that wants him named without saying "the Witch-king".
- **Akhorahil**, **Hoarmurath** - the other two on page 11, and the reason that page's three lines are
  written without names: in a dark court the player cannot tell them apart, which is the point.
- **Dwar**, **Ji Indur**, **Adunaphel**, **Ren**, **Uvatha** - held back. The East's and Harad's lines
  want wraiths of their own, and five is enough for two fronts.

**What the names are for:** nothing the mechanic depends on. A page names a Rider in its stage
direction and the stage spawns that one. The rule the line keeps is that a wraith is never killed
permanently, so a named Rider can stand on more than one page without the campaign explaining itself.

## Act I — The Green Way

The West's front. The country is Eriador's roads and the Shire's edges: quiet ground, and that is the
point, because the first thing the war does is make quiet ground frightening.

**Outline.**

- **1. The wrongness.** Maelgrin sends you out to a late scout. You find a cold in a ditch and
  breathing where nothing stands. *Cost:* the walk. *Reveals:* nothing yet - this one is bait.
- **2. The night.** You go back out with the scout, and the fear lands for the first time: pinned,
  blind, helpless. *Cost:* the pin. *Pays:* the "fear withstood" deed.
- **3. The theft.** Aldwyn's beasts went whole, the fence went with them, and there are lights on the
  barrows. *Cost:* an ordinary person's livelihood, on screen. *Reveals:* the war is local now.
- **4. The duty.** The barrow wight was *guarding* a key, not hoarding it. *Reveals:* the enemy set the
  dead to a task. *Pays:* the key fragment, and the Trembling rung.
- **5. The tower.** You fight the Rider, you beat him, and he falls. Then he is on the next page,
  unharmed, and the act turns on the player realising what that means. *Cost:* the fight you thought
  you had won. *Reveals:* you cannot end him, and he has been counting your halls for years.

**Settled for this act:** the Rider is killable and reconstitutes. An unkillable mob reads as a bug to
a player who has been taught to hit things, and a mob that dies reads as a win until it walks back in.
The second sentence is the one the act needs.

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

*Place:* the top of the dark tower. He is killable here. It changes nothing.

> **The Rider:** "You came up the stair. They always come up the stair."
>
> 1. "You were thrown down at the Ford."
> 2. "Then say your word and be done."
>
> "The river took a horse. Rivers are patient and rivers are stupid. I have been in your West since
> the ice went out, walking your roads, counting your halls, and I have not been counting for myself.
> Blow your little horn, Man of the West. It will bring help, and help is what I came for."
>
> *He fights, and he is beatable — the corpse falls and the tower's stores burn behind you as you
> leave. Then, on the next page, he is in a doorway with the same patient voice, and the ledger has
> already forgotten the kill.*

**Errand:** Beat the Rider, then find him again. **Built from:** a real Nazgûl fight
(`entityttm_nazgul`) that resolves normally — drops, death message and all — with the identity held
outside the entity, so the same Rider stands at the next stage. **Pays:** courage deed for the Nazgûl
killed (5 deeds), and the act's turn.

**Needs:** a rider that outlives its own corpse. The clean way is a campaign holder (a per-player stage,
below) that spawns the Rider fresh at each stage with the same name and no memory of dying; the mob
needs no special code at all, only the campaign needs to know that killing him does not advance the
story. Cheap, and the lie holds because the player never sees the respawn happen.

---

## Act II — The Iron Road

The East's front, seen from the West's side: the player is still the West's, and this act is the West's
expedition east. The ground is Mirkwood's eaves, the Dagorlad, and the road the enemy's smiths use to
feed Mordor.

**Outline.**

- **1. The wagons.** Guthlac, a dwarf of the Iron Hills who answered the West's summons: forty-one
  wagons east, coming back empty but not light. You break the warg pit and take the plan out of its
  cache. *Cost:* the road's garrison. *Pays:* the fortress map.
- **2. The plan.** It was drawn from inside the fortress. Either there is a friend, or the enemy is
  handing plans to travellers. *Reveals:* the bait, and the player knows it is bait.
- **3. The quiet front.** Maelgrin's letter: the West has been silent for a month, and that is what
  frightens him. *Reveals:* a war that stops on one front has moved, not stopped.
- **4. What the wagons fed.** You follow the road into Nurn and count what has been built. The
  overseer's scroll is addressed to the captains of the southern road: you will be glad when you are
  no longer needed. *Reveals:* the arena. *Pays:* the Stout-hearted rung.
- **5. The scroll home.** The last line is in a smaller hand: *"Run."* You carry it back to Maelgrin,
  and the Witch-king is named for the first time - and not fought. *Turns the act.*

**The open question for this act:** the fear is meant to become slowness here, but the player has had
the full pin already in Act I, so Act II's fear must be *newly* worse in a way that is not "again, but
slower" - otherwise the change reads as the game being kind rather than the enemy being clever.

### 6. Smiths on the Iron Road — Guthlac, a dwarf of the Iron Hills

*Place:* the West's camp on the Iron Road (`wild_tent`), east of Mirkwood.

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
`FETCH` the fortress map from the pit's cache. **Pays:** standing with the West, coin.

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
overseer. **Pays:** standing with the West, the Act II rung (Stout-hearted, 40 deeds).

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

**Errand:** Carry the scroll home to Maelgrin at the West's hall. **Built from:** `FETCH` the scroll
to the West's hall (`faction_hall_west`). **Pays:** courage deed for the Witch-king's name spoken (no
fight yet), and the Act II turn.

---

## Act III — The Arena

The end of the West's war. Two builds the mod now carries: the fortress (`boss_castle`) and the arena
beneath it (`boss_arena`). This act is where the courage ladder pays out — at Unbowed the `dread_aura`
is refused outright, and a player who has done the work walks into the arena and simply does not
flinch.

**Outline.**

- **1. The muster.** Maelgrin calls the West's companies and their allies to one gate: three tokens,
  three errands, and the politics of the whole line cashed in. *Cost:* the running about.
- **2. The court.** Three Riders in the fortress, staggered, and the horn is the answer to each - and
  killing any of them is exactly as permanent as it was the first time. The Fearless rung lands
  between the second and the third, so the third is fought with the fear already refused.
- **3. The floor of sand.** The arena, the `dread_aura`, and a Witch-king who tells you he has seen to
  it that you can keep your feet - because he wants you standing for this. At Unbowed the aura is
  refused outright, and the joke is on him. *Pays:* the Unbowed rung, the West's war end.
- **4. (Not yet written.) The accounting.** What the war cost the West, and what the East and Harad
  lines will open on.

**The open question for this act:** three Riders in one build is a lot of boss for one room. The
alternative is one Rider per level of the fortress as a descent - court, gallery, keep - which is
more work to stage but gives the act its own shape instead of a second arena.

### 10. The Last Muster — Maelgrin

*Place:* the West's hall.

> **Maelgrin:** "You have brought us a scroll that says we are not the war. I have read it to the
> hall twice. The first time they laughed. The second time nobody did, and now they are sharpening
> things they had put away, and I do not know whether to thank you or put you out."
>
> 1. "Then let them sharpen. I will go first."
> 2. "Three fronts were a lie. One fortress is the truth. Give me what you have."
>
> "What I have is a name and a road. The fortress stands where the wagons were going, and beneath its
> yard there is a floor of sand that no one lays for grain. Take the horn. Take whoever will follow a
> fool. Blow it at the gate and do not stop blowing until the gate is behind you."

**Errand:** Muster the West's three companies, each of which sends its token. **Built from:** `FETCH`
three Western tokens (`ITEM_TOKEN_WESTERN_ALLIANCE`) to the muster site — three errands, one per
company. **Pays:** courage deed per token, standing with the West.

### 11. The Court of the Fortress — three Nazgûl

*Place:* inside `boss_castle`.

> **The first:** "He said you would come up the stair."
> **The second:** "He said you would bring the horn."
> **The third:** "He did not say what you would do with it."

**Errand:** Three Riders, met one at a time in the fortress's court. The horn is the answer to the
fear — a player who blows it at the right moment keeps their feet. **Built from:** three staggered
Nazgûl (`entityttm_nazgul`) encounters. **Pays:** courage deed per Rider (5 each), the Fearless rung
(75 deeds) between the second and third.

**Needs:** the same holder as page 5 — the Riders are spawned by the campaign at each stage, so their
deaths are the campaign's business rather than the world's.

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
courage deed 20 (the Unbowed rung at 130 deeds lands here or just before), the West's war end text.

**Needs:** the boss arena staged under the castle — both are placed builds now, and this is the
campaign's job to place them together, not the generator's.

---

## The marks over their heads

Every giver carries a mark on its nameplate, and it says one of three things:

- **!** in blue - an errand here, not yet taken.
- **!** in gold - the mainline's next step, not yet taken.
- **!** in grey - work taken, not yet done. The same glyph dimmed, because it is the same promise.
- **?** in blue - an errand done, hand it in.
- **?** in gold - the mainline's page done, hand it in.

The ink is the line and the shape is the state, so the two are told apart without reading either: blue
is anybody's errand, gold is the campaign's own. A giver is the mainline's by carrying the scoreboard
tag `tolkienmobs:campaign` - the herald and Maelgrin will, an ordinary traveller never will - so the
colour lives on the NPC rather than on the quest, and a giver tagged mid-play turns gold on the next
sync.

The mark is per player: two players standing in front of one giver see different glyphs, and a player
with no business there sees a bare name. **This one is built.** It is drawn onto the nameplate from the
quest sync, so it costs no texture, no extra render pass and no per-tick work, and it appears on every
giver the mod spawns - the campaign's and the ordinary ones alike. For the mark to follow the right
body, a quest now remembers the id of the NPC that handed it out, not only the name it was given.

**The herald stands in every hall, with nothing to say yet.** Each hall carries a third body, the herald,
cut by `tools/HallVariants.java` beside the warden and the envoy: a human in all three halls, because the
skin he wears is the quest-giver art of his alliance, tagged as a giver, as the alliance of his hall and
as the campaign, so his clothes and his gold mark are right from the first load. What is still owed is the
gate. Nothing should appear over his head until the player is a **stranger** to that alliance - the first
rank earned above outsider, 100 points of ordinary hall work - and then a gold **!** appears and the
campaign has somewhere to begin. A rank rather than a page, because the line should open for someone who
has already done the country a favour, not for whoever walks through the door first. Until the stage
counter below exists and a page is written for him to hand out, right-clicking him answers that there is
no work, which is the truth.

Once the stage counter below exists, the herald stops holding the whole line in one hand: his mark
becomes the one that reflects the stage - **!** for the next page, **?** for a page finished and unread
- while the hall's ordinary givers keep spawning as they do now. That is also what keeps the campaign
from being a wall of text at one NPC. The herald hands out the thread; the world does the rest.

**His clothes.** Only the giver standing in a hall is dressed for the work:
`faction_west/quest_giver_west.png`, `faction_east/quest_giver_east.png` and
`faction_harad/quest_giver_harad.png`, one per alliance and all three human-sized, so any race the mod
ships can be pointed at one. The skin follows a scoreboard tag, `tolkienmobs:quest_giver/<alliance>`,
read on the server and copied into synched data, so a hall only has to tag its own giver. A giver met in
the wide world carries no alliance tag and keeps the clothes of its own folk: the folk who walk a country
are ordinary people, and the one dressed for the work is the one who stands still in a hall. Everything
else in the campaign can stay an ordinary mob in an ordinary skin. A giver is also **invulnerable** as
well as persistent - a mob that wanders into the scene cannot end a quest by killing the one thing that
can hand it in - and that is applied on the way into the world, so a giver already standing in a saved
world is protected on its next load.

## What is buildable now, and what is not

| Page | Today | Missing |
|---|---|---|
| every giver | the mark over their head: **!** for work to take and **?** for work to hand in, blue for an errand and gold for the mainline, grey while the work is in hand | nothing - it is drawn from the quest sync |
| 1–4, 6–8, 10 | `HUNT` / `FETCH` / `FACTION` at a named giver, courage deeds on hand-in and on fear | a fixed giver at a known place rather than a spawned one |
| 5, 11 | `HUNT` the Rider — a normal fight, a normal drop, a normal death message | a rider that outlives its corpse: a campaign-spawned Nazgûl, respawned fresh at the next stage |
| 9, 12 | `FETCH` to the West's hall; `HUNT` the boss | a per-player campaign stage, so page 9 does not read as a random scroll |
| 10 | three `FETCH` errands | — |

The gap is the same one in every row: **the mod has quests, not a questline.** A stage counter per
player (which page they are on) turns every page above into ordinary work the existing pool already
knows how to pay, and it is the smallest thing that makes the campaign a campaign rather than a
handful of good errands. It also happens to be the thing that makes the Rider work: a page number is
what lets the same Nazgûl be met twice.

---

## Retooling the line for the East and for Harad

What a second line changes, and nothing else:

- **The halls.** `faction_hall_east` / `faction_hall_harad` where this line says the West's, and the
  muster sends that alliance's token.
- **The speakers.** Maelgrin becomes a captain of that alliance; the farmer in page 3 becomes a
  crofter of that country and the ground becomes a village of that folk (`ttmhouse_orc`,
  `ttmhouse_desert`, `ttmhouse_nurn`). Guthlac stays Guthlac in the East's line only if he would work
  for them, and becomes someone else in Harad's.
- **The ground.** Act I's barrows and old forest become that country's own ruins
  (`ttmruin_large`/`ttmruin_small`), and the tower stays the same tower for all three lines, because
  there is one Rider and he goes where he likes.
- **The things the enemy is moving.** This line's wagons carry iron to the arena. The East's line can
  carry the same iron for a different reason; Harad's should carry something else entirely, or the
  second line reads as the first one repainted.

What stays fixed: the five beats of each act, the fear's progression act by act, the courage rungs
landing in the same places, the Rider dying and coming back, and the arena.

**Free with the retool:** the marks over the heads. They are keyed on a giver id rather than on the
alliance it serves, so a second line gets the whole ladder for nothing, and the herald's gate
becomes that alliance's first rank, which is the same number for all three.
