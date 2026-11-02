# Chapter one — what each page hands over besides standing and coin

The reference gives most pages a purse or an object. Coin and experience are already paid; this is the
list of everything else, decided page by page from what the document says the stage *is*, and drawn from
the pack's own gear, food, drink, potions and trinkets, plus what vanilla offers.

Two rules came out of the first draft of this list, and both are worth keeping:

- **A reward has to do something.** `item_fancysword`, `item_fancypick`, `item_wornsword`,
  `item_wornarmor`, `item_fancyshield` and their kin are props: they look like gear and are not. The real
  ones are the tiered sets - `sword_ammolite`, `pickaxe_*`, `axe_*`, `shovel_*`, `hoe_*`,
  `helmet/chestplate/leggings/boots_*`, `elven_bow`, `dwarven_hammer` - and vanilla's own sword, pickaxe
  and shield where a page wants something plain.
- **Trinkets are rationed.** `faction_charm_west`, `faction_charm_east` and `faction_amulet_harad` are
  powerful objects to be able to hold, so the whole of chapter one gives exactly one, at the end. Ores and
  the herb are out: `item_pungentherb` does nothing on its own, and ore is the least interesting thing a
  chapter can pay.

The pool the decisions are drawn from:

| kind | items |
|---|---|
| food | `food_cram`, `food_lembas`, `food_honeycake`, `food_honey`, `brambles_berry`; vanilla bread, cooked beef, golden apple |
| drink | `drink_miruvor`, `drink_grog`, `drink_ent_draught`, `drink_elf_blessing`, `drink_elf_vitality`, `drink_eru_blessing` |
| potions | vanilla potion of healing, regeneration, swiftness, fire resistance (`PotionContents.createItemStack`) |
| gear | `sword_ammolite`, `sword_mithril`, `pickaxe_ammolite`, `axe_ammolite`, `shovel_ammolite`, `helmet_ammolite`, `elven_bow`; vanilla iron sword, diamond pickaxe, shield |
| trinkets | `faction_charm_west`, `faction_charm_east`, `faction_amulet_harad` - **one in the chapter** |

Enchanted books are not in the table yet. They are the obvious answer to the cave hand-in's "random
enchantment", but a book needs an enchantment written onto it at the moment it is paid, and that is a
small piece of work rather than a table entry.

## The page-by-page list

| page | the reference pays | this line pays | why that |
|---|---|---|---|
| 1 - The Troubles of Rivendell | Bread | **Cram x4** | A hall is sending a player it does not know out along the East Road; cram is the North's travel bread, which is what a hall has to give. |
| 2 - Where is the Horse and the Rider? | Bread | **Lembas x2** | Elrond is sending them to his sons in the Trollshaws, and waybread is what an Elvish house gives for a journey. Two, not a stack: a gift, not a ration. |
| 3 - The Missing Rider I | Bridle of the Black Steed | **the bridle** (already paid, `item_darksaddle`) | The page's object is the proof of the report; nothing should sit beside it. |
| 4 - The Missing Rider II | a piece of equipment or ores | **Ammolite Sword x1** | Elladan has heard the report and is arming someone to go back into the channels. A real sword, not a prop: ammolite is the tier below the mithril the West's own soldiers carry, which is the right size for a blade handed to a stranger. |
| 5 - Wisdom of Lord Glorfindel I | - | - | |
| 6 - the beryl at the ford | - | - | The beryl itself is the page's gift, lent and given back. |
| 7 - Wisdom III | - | - | |
| 8 - Regaining the Trail | - | - | |
| 9 - Hiding in the Dark | a cave completion token for each chamber; the reference's separate 1-8d pays "random equipment, enchantment, ore" | **tokens** (already paid) + **Potion of Healing x1** | The tokens are the proof of three chambers emptied. What a camp gives someone who has just come up out of the dark is a potion, and healing is the one a player is short of rather than a trinket they may not need. |
| 10 - Knowledge of Giants | - | - | |
| 11 - the Giant | 4 silver, 16 copper | **already paid** | |
| 12 - Aid of Mirkwood I | - | - | |
| 13 - Aid of Mirkwood II | - | - | |
| 14 - Matuna | 8 silver, 40 copper | **already paid** | |
| 15 - Hasten Their Departure | 2 silver, 20 copper, an earring | **the West's charm x1** | The pack has no earring; what it has is a worn charm of the West, and a charm is the same idea - a small thing from a house that now counts you its own. It is the only trinket chapter one gives. |

## How it is paid

`Campaign.PAGE_GIFTS` holds the table above, keyed by the page's own title key, and `payGift` hands the
item over where the page is settled - beside `payPurse`, so a page is never worth its coin and not its
gift. Each entry builds its own stack, so a potion can be built with its contents rather than named as an
id, and the pack's own constants are used rather than strings.

The gift is not drawn in the book yet: the leaf shows coin and experience from the page. That is the next
thing to do if a player should be told what else a page carries before taking it.
