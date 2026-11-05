# Arda teleports

One point per country of the drawing, at the middle of its largest block of cells, with the height the
reference masks give that column plus eight blocks so the landing is above the ground.
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

These are the six the lake mask draws. The water is laid at the ground the mask gives it, so a point is
the middle of the drawn sheet and the span is the extent of it; the two great seas are in the east and
the south, not where the 48 by 36 grid draws its hand-painted sea cells.

| Lake | x | y | z | drawn span |
| --- | --- | --- | --- | --- |
| Sea of Rhun, the great eastern water | 17580 | 84 | -212 | x 16336..18800, z -1400..1168 |
| Sea of Nurnen, the southern water | 15460 | 72 | 7140 | x 14312..16600, z 6488..7648 |
| Northern lake, above Angmar | 524 | 127 | -4860 | x 248..768, z -5176..-4600 |
| Lake at the Bruinen's head | 2828 | 137 | -3588 | x 2656..3024, z -3736..-3448 |
| Far-eastern lake | 18836 | 179 | -4236 | x 18608..19048, z -4368..-4096 |
| North-eastern lake, above Mirkwood's eaves | 11484 | 164 | -4916 | x 11424..11560, z -5120..-4696 |
| Lake Evendim (Nenuial), at Annuminas - drawn by hand | -2500 | 103 | -2990 | x -2922..-2074, z -3224..-2720 |

Lake Evendim is the one the mask has nothing for. Its water is a hand-drawn ellipse at Annuminas filled
to a level, so what you see is the land's own contour at 95 rather than the ellipse: it came out 570
by 505 blocks, every block of shore on rising ground.

## Marshes

Read off the countries the map paints, so these are the drawn wetlands rather than anything the masks
say. Both marsh countries are low ground with standing water and drowned trees.

| Marsh | x | y | z | drawn cells |
| --- | --- | --- | --- | --- |
| Nindalf, the Wetwang, and the Dead Marshes | 9728 | 210 | 1792 | cols 27-30, rows 22-24 |
| Gladden Fields, the Anduin's northern vale | 8192 | 203 | -3328 | cols 25-26, rows 6-20 |
| Gladden Fields, the east bank | 11520 | 178 | -3328 | cols 31-33, rows 11-15 |
| Dol Guldur, the southern Mirkwood | 11008 | 185 | -1280 | cols 30-32, rows 16-18 |

Moria: the surface point above the halls is the Misty Mountains entry. The halls are hollow now, so the
point below lands inside the chamber; the rock over it is the mountain's own ground.

```
/execute in tolkienmobs:arda run tp @s 5632 20 -3200
```

