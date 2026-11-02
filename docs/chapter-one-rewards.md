# Chapter one — what each page hands over besides standing and coin

The reference gives most pages a purse or an object. Coin and experience are already paid; this is the
list of everything else, decided page by page from what the document says the stage *is*, and drawn from
the pack's own food, drink, tools and trinkets. Ores are deliberately not used: the reference offers
"equipment or ores" twice, and a handful of ore is the least interesting thing a chapter can pay.

The pool the decisions are drawn from — all of it already in the pack, none of it new content:

| kind | items |
|---|---|
| food | `food_cram`, `food_lembas`, `food_honeycake`, `food_honey`, `brambles_berry` |
| drink | `drink_miruvor`, `drink_grog`, `drink_ent_draught`, `drink_elf_blessing`, `drink_elf_vitality`, `drink_eru_blessing` |
| herbs | `item_pungentherb` |
| tools | `item_fancysword`, `item_fancysword2`, `item_fancypick`, `item_lockpick`, `item_wornsword`, `item_wornpick` |
| trinkets | `faction_charm_west`, `faction_charm_east`, `faction_amulet_harad` |

Enchanted books are not in the table yet. They are the obvious answer to the cave hand-in's "random
enchantment", but a book needs a random enchantment written onto it at the moment it is paid, so it waits
for a page that actually wants one rather than being guessed at now.

## The page-by-page list

| page | the reference pays | this line pays | why that |
|---|---|---|---|
| 1 · The Troubles of Rivendell | Bread | **Cram x4** | A hall is sending a player it does not know out along the East Road. Cram is the North's travel bread, which is exactly what a hall has to give. |
| 2 · Where is the Horse and the Rider? | Bread | **Lembas x2** | Elrond is sending them to his sons in the Trollshaws, and waybread is what an Elvish house gives for a journey. Two, not a stack: it is a gift, not a ration. |
| 3 · The Missing Rider I | Bridle of the Black Steed | **the bridle** (already paid, `item_darksaddle`) | The page's object is the proof of the report; nothing should be added to it. |
| 4 · The Missing Rider II | a piece of equipment or ores | **Fancy Sword x1** | Elladan has heard the report and is arming someone to go back into the channels. A blade is the equipment; ore is the version of this that nobody remembers. |
| 5 · Wisdom of Lord Glorfindel I | — | — | |
| 6 · the beryl at the ford | — | — | The beryl itself is the page's gift, lent and given back. |
| 7 · Wisdom III | — | — | |
| 8 · Regaining the Trail | — | — | |
| 9 · Hiding in the Dark | a cave completion token for each chamber | **tokens** (already paid), **Miruvor x2** at the camp | The three tokens are the proof; the camp's own thanks after three chambers in the dark is a drink, and miruvor is the one in the pack that puts a traveller back on their feet. This is the reference's 1-8d page, which is the same page as the search. |
| 10 · Knowledge of Giants | — | — | |
| 11 · the Giant | 4 silver, 16 copper | **already paid** | |
| 12 · Aid of Mirkwood I | — | — | |
| 13 · Aid of Mirkwood II | — | — | |
| 14 · Matuna | 8 silver, 40 copper | **already paid** | |
| 15 · Hasten Their Departure | 2 silver, 20 copper, an earring | **the West's charm x1** | The pack has no earring. What it has is a worn charm of the West, which is the same idea — a small thing given by a house to someone it now counts as its own. |

## How it is paid

`Campaign.PAGE_GIFTS` holds the table above, keyed by the page's own title key, and `payGift` hands the
item over where the page is settled - beside `payPurse`, so a page is never worth its coin and not its
gift. Ids are resolved through `BuiltInRegistries.ITEM` with a `containsKey` guard, because that registry
is defaulted in 26.1 and a typo would otherwise pay air.

The gift is not drawn in the book yet: the leaf shows coin and experience from the page. That is the next
thing to do if a player should be told what else a page carries before taking it.
