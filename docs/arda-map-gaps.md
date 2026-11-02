# Arda's drawing against the reference map — the cells that are off

`ArdaMap.draw()` is the whole of Arda's geography: every country's cells, its anchor, every place the
catalogue surveys and every site the campaign pitches. It was laid out from `middleearth.jpeg` (10000 x
5455, untracked, kept in the repo root for reference) by eye, and the east and south of the drawing sit a
few cells east and south of where that map puts them. None of it touches chapter one, which is laid in
the Trollshaws — three cells wide, against the Misty Mountains, unaffected by any of the shifts below — so
the drawing was left alone rather than moved under every anchor for no chapter's benefit.

This file is the list of what is off, measured cell by cell off a 512-block grid laid over the reference
map, so that a chapter which walks into one of these countries can fix that country before it starts
surveying ground in it. The grid is the map's own: column 0 is the west edge and each cell is
`ArdaMap.CELL` (512 blocks) across.

## The shifts, in cells

"Reference" is what the map shows; "drawn" is what `draw()` fills today. The measured error is within
one cell either way, because the map's own cell edges are not quite the drawing's.

| country | drawn (columns, rows) | reference (columns, rows) | off by |
|---|---|---|---|
| Mirkwood | 27–33, 5–18 | 25–31, 5–18 | 2 east |
| Sea of Rhûn | 40–43, 12–15 | 33–36, 12–15 | 7 east |
| Iron Hills | 35–41, 6–7 (and 35–36, 11–12) | 30–34, 6–7 | 5 east |
| Rohan | 23–29, 20–24 | 20–25, 20–24 | 3 east, 1 too wide |
| Mordor (Gorgoroth) | 37–44, 24–26 | 32–38, 24–26 | 5 east, 1 too wide |
| Nurn | 37–43, 27–29 | 34–37, 27–29 | 6 east, 7 wide against 4 |
| White Mountains | 11–34, 25–26 | 18–27, 25–26 | 7 too far west, 7 too far east |
| Ered Luin (Blue Mountains) | 3–5, rows 4–21 | 3–5, rows 2–9 | runs 12 rows too far south |
| Harad's eastern strip | 44–47, 4–35 | nothing there | invented |

The pattern is one offset, not nine mistakes: everything drawn east of the Misty Mountains is a couple of
cells east of the map, and the further east it lies the further it has drifted — two cells at Mirkwood,
five at the Iron Hills and Mordor, seven at the Sea of Rhûn. The south of the drawing is low by two or
three rows in the same way. There are two shape errors as well as the shifts: the White Mountains are a
24-cell wall where the map draws a 10-cell one, and Ered Luin runs the length of Eriador's coast instead
of stopping at the Gulf of Lune.

The eastern Harad strip is the one place the drawing invented land: `fill(map, Country.HARAD, 44, 4, 47,
35)` paints four columns down the whole eastern edge, so the far east of the world is Haradwaith from
Forodwaith to the southern sea. It is there because Rhûn's own fill stops at column 43, and something has
to be east of it; the map draws open country and the sea there.

## What fixing one of these has to carry

Nothing is pinned in any of these countries yet — every hard location the campaign has is in the
Trollshaws, and Angmar's anchor (4352, -6912) is unaffected by any of the above — so a country can be
moved today without moving a single build. A move still has to carry four things:

1. **The fills themselves.** Most of these are one `fill(...)` in `draw()`; the White Mountains' width and
   Ered Luin's length are a changed range rather than a shift, and the Harad strip is a line to delete.
   Countries that share a border have to move together (`RHUN` and its sea, `MORDOR`, `NURN` and the Ash
   Mountains' ring, `ROHAN` and the White Mountains), or a shift opens a one-cell seam of sea between them.
2. **Every place canon'd to the country.** `Places.CANON` maps a name to a `Country`; none of these
   countries holds a name today, so this is empty work until a chapter adds one.
3. **Every site pinned to the country.** `Sites.CANON_CELL` holds a cell per build; likewise empty for
   these countries. A pinned cell is an absolute column and row, so it does not follow its country: a
   country that moves leaves its pins behind, and the build lands in whatever now stands at that cell.
4. **The comparison itself.** Re-cut the gridded crops of `middleearth.jpeg` and re-render the
   side-by-side before and after, or the next reader cannot tell whether a number is the map's or the
   drawing's.

## Which chapter will walk into each

- **Rohan and the White Mountains** — the Mark, whenever its chapters are written; the notes' Rohan and
  Gondor beats both cross this ground.
- **Mordor and Nurn** — the East front and the campaign's last acts. The castle and arena the campaign
  raises stand in Angmar, so nothing has needed Mordor's cells yet.
- **The Sea of Rhûn and the Iron Hills** — the East front, whose three pages name Rhûn's marches and
  Erebor's levy.
- **Mirkwood** — a Mirkwood chapter (Legolas' hunt, Matuna's lair). Chapter one names Legolas and never
  walks his country, which is why its two-cell shift has not shown.
- **Harad's eastern strip** — the Harad front, whose pages name the sun and Umbar.
- **Ered Luin** — nothing yet; it is the wall between Lindon and Eriador, and Annúminas (chapter seven)
  is the first thing that may want the coast to read correctly.

Nothing here is started. It is written down so that the first chapter to walk into one of these countries
opens with a decision about the ground rather than a discovery about it.
