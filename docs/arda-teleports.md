# Arda teleports

One point per country of the drawing, at the middle of its largest block of cells, with the height the
reference masks give that column plus eight blocks so the landing is above the ground.
`/tmtp <region>` stands you on these same points without a coordinate typed - see `docs/commands.md`.
Run them from anywhere; the dimension is `tolkienmobs:arda`. Each y is the mask height plus eight, so
the landing is a few blocks above the ground. Minhiriath’s strip is drawn where the mask keeps only a
narrow shelf of land, so its point is the first dry column of it rather than the middle of the patch.

```
/execute in tolkienmobs:arda run tp @s <x> <y> <z>
```

| Region | x | y | z | mask height |
| --- | --- | --- | --- | --- |
| Angmar | 4864 | 204 | -7168 | 196 |
| Ash Mountains | 15360 | 176 | 1280 | 168 |
| Barrow Downs | 1536 | 104 | 1792 | 96 |
| Belfalas | 7680 | 182 | 6400 | 174 |
| Blue Mountains | -2816 | 155 | -3584 | 147 |
| Brown Lands | 11520 | 179 | 1024 | 171 |
| Cardolan (patch grid) | 256 | 113 | 1792 | 105 |
| Dagorlad | 11520 | 158 | 2304 | 150 |
| Dale | 13056 | 191 | -6912 | 183 |
| Dorwinion (patch grid) | 14976 | 237 | -4992 | 229 |
| Dunland | 3328 | 147 | 2560 | 139 |
| Eastern Waste | 18432 | 75 | 0 | 67 |
| Emyn Muil | 10240 | 185 | 2048 | 177 |
| Enedwaith (patch grid) | 2816 | 134 | 1792 | 126 |
| Ered Mithrin | 9728 | 196 | -7168 | 188 |
| Eregion | 4608 | 162 | -2048 | 154 |
| Eriador | 768 | 148 | -3840 | 140 |
| Eryn Vorn | -1280 | 139 | -256 | 131 |
| Ettenmoors | 3840 | 159 | -5888 | 151 |
| Fangorn | 7936 | 185 | 256 | 177 |
| Firien | 4608 | 169 | -1024 | 161 |
| Forochel | -3840 | 124 | -6912 | 116 |
| Forochel Slopes | -2304 | 171 | -6912 | 163 |
| Forodwaith | 7680 | 149 | -8448 | 141 |
| Gladden | 8192 | 203 | -3328 | 195 |
| Gondor | 6912 | 204 | 4608 | 196 |
| Harad | 12800 | 168 | 6912 | 160 |
| Hithaeglir (the Misty Mountains, mid-range) | 5632 | 180 | -4608 | 172 |
| Iron Hills | 14592 | 190 | -6656 | 182 |
| Ithilien | 11264 | 175 | 3840 | 167 |
| Khand | 17920 | 163 | 5120 | 155 |
| Lindon | -4096 | 120 | -4096 | 112 |
| Lorinand | 7168 | 231 | -1536 | 223 |
| Marshes | 9728 | 210 | 1792 | 202 |
| Minhiriath (patch grid) | -768 | 103 | 1280 | 95 |
| Mirkwood | 10496 | 223 | -4096 | 215 |
| Mirkwood Spooky | 11008 | 185 | -1280 | 177 |
| Mordor | 15872 | 173 | 2816 | 165 |
| Nurn | 15616 | 230 | 4352 | 222 |
| Old Forest | 1024 | 109 | 1280 | 101 |
| Rhudaur | 3840 | 176 | -4864 | 168 |
| Rhun | 13824 | 169 | -3584 | 161 |
| Rohan | 8448 | 187 | 1280 | 179 |
| Shire | 0 | 116 | 256 | 108 |
| Trollshaws | 4352 | 166 | -2816 | 158 |
| Umbar (patch grid) | 9216 | 100 | 7936 | 92 |
| White Mountains | 6656 | 183 | 3072 | 175 |
| Wild Wood | 11264 | 184 | 512 | 176 |

## Lakes

These are the lake mask's nine sheets: six inside the world frame, and three beyond its eastern and
southern edges that the bake crops away. Each sheet is one water level, taken from the lowest ground the sheet is drawn over, so the water fills the
sheets rather than water following the rise and fall of the ground under them. Ground standing more than four
basin the drawing gives it; ground standing over that level is left as land rather than cut away, so a sheet
ends against the hill it lies under. Only low shore is graded - a face standing more than six blocks over the
water keeps its own - and the water
grades from one block deep at the mask's edge down to the full six inside it, so a sea shallows into its
shore instead of showing terraces of bed through the water, and the land round the sheet - a headland in
the middle of one included - stands a block over the water where it meets it and a block higher for each
block further out, so a drawn hill ends in a beach rather than a cliff. The spread of each
sheet over its own level is logged once at load, for tuning. A point is the middle of the
drawn sheet and the span is the extent of it; the two great seas are in the east and
the south, not where the 48 by 36 grid draws its hand-painted sea cells.

| Lake | x | y | z | drawn span |
| --- | --- | --- | --- | --- |
| Sea of Rhun, the great eastern water | 17580 | 84 | -212 | x 16336..18800, z -1400..1168 |
| Sea of Nurnen, the southern water | 15460 | 72 | 7140 | x 14312..16600, z 6488..7648 |
| Lake Evendim (Nenuial), the mask's northern lake | 524 | 127 | -4860 | x 248..768, z -5176..-4600 |
| The smaller lake south-east of Evendim | 2828 | 137 | -3588 | x 2656..3024, z -3736..-3448 |
| Far-eastern lake | 18836 | 179 | -4236 | x 18608..19048, z -4368..-4096 |
| North-eastern lake, above Mirkwood's eaves | 11484 | 164 | -4916 | x 11424..11560, z -5120..-4696 |

Evendim is the mask's own bigger blob in the north-west, with the smaller blob to its south-east; the
hand-drawn ellipse that stood in for it is gone, and the lake is the drawn one like the rest. Each named sheet is its own biome - `lake_evendim`,
`sea_of_rhun`, `sea_of_nurnen`, `long_lake` - while any other drawn lake is `lake`. Rivers are `river`,
except the four the river mask's own components can be named without guessing: `river_anduin`,
`river_celduin`, `river_gwathlo` and `river_lune`. The three
sheets outside the frame are at x 19833..20070 z -1495..-1295, x 22411..22707 z 11446..11646 and
x 9957..10158 z 13724..13908.

## Marshes

Read off the countries the map paints, so these are the drawn wetlands rather than anything the masks
say. The wetwang and the Midgewater are the one marsh country - low ground with standing water and drowned
trees. The Dead Marshes are their own country and their own biome: the same water, the pools and all, under a fog
that closes at forty blocks, with fell spirits laid in it by the generation and none walking the bank. Its pools are dug to the middle
ground of the fen, so the water sits in the marsh rather than on top of it, and the bodies are laid in
whatever water a chunk actually ends up with.
Standing in them lays a dread on you - the slowness and the weakness of fear, with no wither and no
blindness, because nothing there is looking at you. It deepens the longer you stay: nothing for the first
half minute, one step after thirty seconds, and two after ninety, which is where it holds. It says so when it steps
up - a line on the action bar and a low sound - so a player can feel the marsh taking hold and choose to
leave before it has them. It fades a moment after you leave, and begins again from nothing if you come back.
The marsh is also not quiet: corpse-lights stand on the pools within twenty blocks of a player, a cry comes
out of the water every fifteen seconds or so, and the spirits under it stir every few seconds - bubbles and
a low sound through the water, and a slow roll of the body where it lies.
Stay in the marsh for a minute and the dead start standing out in the fog: a figure eighteen to twenty-six
blocks off, turned to face you, that is gone the moment anyone gets within six blocks of it - not only the
player it was shown to - and that never survives a save: a figure read back from disk is dropped on its first
tick rather than standing in the marsh after a restart. It comes more often
the longer you linger - once every twenty-five seconds after the first minute, every eighteen after two,
every twelve after three - and the count resets the moment you step out.

| Marsh | x | y | z | drawn cells |
| --- | --- | --- | --- | --- |
| Nindalf, the Wetwang | 9216 | 210 | 1792 | cols 27-28, rows 22-24 |
| The Dead Marshes, its own biome | 10240 | 208 | 1792 | cols 29-30, rows 22-24 |
| Midgewater, the fen east of Bree | 2048 | 210 | -768 | cols 13-14, row 18 |
| Gladden Fields, the Anduin's northern vale | 8192 | 203 | -3328 | cols 25-26, rows 6-20 |
| Gladden Fields, the east bank | 11520 | 178 | -3328 | cols 31-33, rows 11-15 |
| Dol Guldur, the southern Mirkwood | 11008 | 185 | -1280 | cols 30-32, rows 16-18 |

Moria: the surface point above the halls is the Misty Mountains entry. The halls are hollow now, so the
point below lands inside the chamber; the rock over it is the mountain's own ground.

```
/execute in tolkienmobs:arda run tp @s 5632 20 -3200
```

