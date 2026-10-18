"""Amulet Map Editor find-and-replace operation that understands modded blocks.

Target: Amulet Map Editor 0.10.x (amulet-core 1.9.x, tested against 0.10.44).

Why this plugin exists
----------------------
The editor builds its block pickers from PyMCTranslate, which only knows the
vanilla blocks of the version being edited. A modded block therefore cannot be
chosen in the stock Replace operation: the namespace dropdown offers only
"minecraft" and the block list for any other namespace is empty.

This plugin uses the same picker widgets as the stock Replace operation
(namespace dropdown, search box, block list, property panel and the "pick block
from the world" button) and splices the modded namespaces into those two lists,
so the screen looks and behaves like Replace but has tolkienmobs blocks in it.

Block names for a modded namespace come from MOD_BLOCK_REGISTRY below, extended
at runtime with every modded block already present in the loaded world, so a
block added by a newer mod build shows up as soon as the world contains it.
Anything else can still be chosen: the search box always offers what you typed
as a quoted entry, which is how the stock picker handles unknown names.

How the replacement works
-------------------------
Amulet stores ids from the level block palette in each chunk's block array and
writes those ids back out through PyMCTranslate when saving. For a block it does
not know, PyMCTranslate passes the block through unchanged (logging a warning),
so a modded block round-trips as-is. The operation therefore replaces whole
chunk slices in one numpy pass rather than calling get_version_block and
set_version_block for every coordinate, which is the difference between seconds
and minutes on a large selection.
"""

from __future__ import annotations

from typing import Dict, List, Mapping, Optional, Sequence, Tuple, TYPE_CHECKING

import amulet_nbt
import numpy
import wx

from amulet.api.block import Block
from amulet.api.data_types import Dimension
from amulet.api.level import BaseLevel
from amulet.api.selection import SelectionGroup

from amulet_map_editor.api.wx.ui.base_select import EVT_ITEM_CHANGE, EVT_PICK
from amulet_map_editor.api.wx.ui.block_select import (
    EVT_PROPERTIES_CHANGE,
    BlockSelect,
    PropertySelect,
)
from amulet_map_editor.api.wx.ui.simple import SimpleScrollablePanel
from amulet_map_editor.api.wx.ui.version_select import EVT_VERSION_CHANGE, VersionSelect
from amulet_map_editor.programs.edit.api.operations import DefaultOperationUI

if TYPE_CHECKING:
    from amulet_map_editor.programs.edit.api.canvas import EditCanvas

MOD_NAMESPACE = "tolkienmobs"

# Every block id tolkienmobs registers, read out of the mod's block registry
# (the BLOCKS.register / registerBlock calls in TolkienBlocks and TolkienFluids),
# so it is complete even for the blocks that have no blockstate asset of their
# own. Add further namespaces here ({"othermod": ("block_a", "block_b")}) to
# give them a picker list too.
MOD_BLOCK_REGISTRY: Dict[str, Sequence[str]] = {
    MOD_NAMESPACE: (
        "ancient_dead_watcher", "ancient_watcher", "angainor", "angainor_bricks",
        "angainor_bricks_button", "angainor_button", "arda_portal", "backpack",
        "barrel_culumalda", "barrel_deadwood", "barrel_dwarven_maple",
        "barrel_fangornoak", "barrel_lebethron", "barrel_mallorn", "barrel_mirkwood",
        "barrel_mithril", "barrel_morguliron", "block_ammolite", "block_bloom_decay",
        "block_camo_chest", "block_camo_fluid", "block_camo_glowstone",
        "block_camo_smoker", "block_camo_spawner", "block_decay_bloom",
        "block_hallowed", "block_key_stone", "block_mithril", "block_morguliron",
        "block_piggybank", "block_stone_path", "chameleon_block",
        "chiseled_angainor_bricks", "chiseled_angainor_bricks_button",
        "chiseled_dark_stone_bricks", "chiseled_dark_stone_bricks_button",
        "chiseled_dwarven_stone_bricks", "chiseled_dwarven_stone_bricks_button",
        "chiseled_elven_marble_bricks", "chiseled_elven_marble_bricks_button",
        "chiseled_iron_stone_bricks", "chiseled_iron_stone_bricks_button",
        "chiseled_mountain_stone_bricks", "chiseled_mountain_stone_bricks_button",
        "clover_patch", "cobbled_angainor", "cobbled_angainor_button",
        "cobbled_dark_stone", "cobbled_dark_stone_button", "cobbled_dwarven_stone",
        "cobbled_dwarven_stone_button", "cobbled_elven_marble",
        "cobbled_elven_marble_button", "cobbled_iron_stone",
        "cobbled_iron_stone_button", "cobbled_mountain_stone",
        "cobbled_mountain_stone_button", "cracked_angainor_bricks",
        "cracked_angainor_bricks_button", "cracked_dark_stone_bricks",
        "cracked_dark_stone_bricks_button", "cracked_dwarven_stone_bricks",
        "cracked_dwarven_stone_bricks_button", "cracked_elven_marble_bricks",
        "cracked_elven_marble_bricks_button", "cracked_iron_stone_bricks",
        "cracked_iron_stone_bricks_button", "cracked_mountain_stone_bricks",
        "cracked_mountain_stone_bricks_button", "culumalda_button", "dark_stone",
        "dark_stone_bricks", "dark_stone_bricks_button", "dark_stone_button",
        "deadwood_button", "door_culumalda", "door_deadwood", "door_durin",
        "door_dwarven_maple", "door_fangornoak", "door_lebethron", "door_mallorn",
        "door_mirkwood", "door_mithril", "door_morguliron", "dwarven_maple_button",
        "dwarven_stone", "dwarven_stone_bricks", "dwarven_stone_bricks_button",
        "dwarven_stone_button", "elven_lantern", "elven_marble", "elven_marble_bricks",
        "elven_marble_bricks_button", "elven_marble_button", "fangornoak_button",
        "fence_culumalda", "fence_deadwood", "fence_dwarven_maple", "fence_fangornoak",
        "fence_gate_culumalda", "fence_gate_deadwood", "fence_gate_dwarven_maple",
        "fence_gate_fangornoak", "fence_gate_lebethron", "fence_gate_mallorn",
        "fence_gate_mirkwood", "fence_lebethron", "fence_mallorn", "fence_mirkwood",
        "fireplace", "flower_aeglos", "flower_alfirin", "flower_athelas",
        "flower_brambles", "flower_elanor", "flower_lissuin", "flower_mallos",
        "flower_mirkwood", "flower_niphredil", "flower_simbelmyne",
        "flower_swamp_milkweed", "flower_valley_lilly", "hanging_sign_culumalda",
        "hanging_sign_deadwood", "hanging_sign_dwarven_maple",
        "hanging_sign_fangornoak", "hanging_sign_lebethron", "hanging_sign_mallorn",
        "hanging_sign_mirkwood", "hanging_sign_wall_culumalda",
        "hanging_sign_wall_deadwood", "hanging_sign_wall_dwarven_maple",
        "hanging_sign_wall_fangornoak", "hanging_sign_wall_lebethron",
        "hanging_sign_wall_mallorn", "hanging_sign_wall_mirkwood",
        "hardened_leaves_culumalda", "hardened_leaves_dwarven_maple",
        "hardened_leaves_fangornoak", "hardened_leaves_lebethron",
        "hardened_leaves_mallorn", "hardened_leaves_mirkwood", "iron_stone",
        "iron_stone_bricks", "iron_stone_bricks_button", "iron_stone_button",
        "ladder_culumalda", "ladder_deadwood", "ladder_dwarven_maple",
        "ladder_fangornoak", "ladder_lebethron", "ladder_mallorn", "ladder_mirkwood",
        "leafpile_culumalda", "leafpile_dwarven_maple", "leafpile_fangornoak",
        "leafpile_lebethron", "leafpile_mallorn", "leafpile_mirkwood",
        "leaves_culumalda", "leaves_dwarven_maple", "leaves_fangornoak",
        "leaves_lebethron", "leaves_mallorn", "leaves_mirkwood", "lebethron_button",
        "lightningbug", "living_roots", "lockable_chest_block",
        "lockable_double_chest_block", "lockable_double_treasure_chest_block",
        "lockable_treasure_chest_block", "locust", "log_culumalda", "log_deadwood",
        "log_dwarven_maple", "log_fangornoak", "log_lebethron", "log_mallorn",
        "log_mirkwood", "mallorn_button", "milestone_block", "mirkwood_button",
        "mithril", "mithril_bars", "mithril_button", "morgul_lantern", "morguliron",
        "morguliron_bars", "morguliron_button", "moss_patch", "mossy_angainor_bricks",
        "mossy_angainor_bricks_button", "mossy_cobbled_angainor",
        "mossy_cobbled_angainor_button", "mossy_cobbled_dark_stone",
        "mossy_cobbled_dark_stone_button", "mossy_cobbled_dwarven_stone",
        "mossy_cobbled_dwarven_stone_button", "mossy_cobbled_elven_marble",
        "mossy_cobbled_elven_marble_button", "mossy_cobbled_iron_stone",
        "mossy_cobbled_iron_stone_button", "mossy_cobbled_mountain_stone",
        "mossy_cobbled_mountain_stone_button", "mossy_dark_stone_bricks",
        "mossy_dark_stone_bricks_button", "mossy_dwarven_stone_bricks",
        "mossy_dwarven_stone_bricks_button", "mossy_elven_marble_bricks",
        "mossy_elven_marble_bricks_button", "mossy_iron_stone_bricks",
        "mossy_iron_stone_bricks_button", "mossy_mountain_stone_bricks",
        "mossy_mountain_stone_bricks_button", "mountain_stone", "mountain_stone_bricks",
        "mountain_stone_bricks_button", "mountain_stone_button", "mushroom_bloom_decay",
        "mushroom_decay_bloom", "ore_ammolite", "ore_deepslate_ammolite",
        "ore_deepslate_mithril", "ore_deepslate_morguliron", "ore_end_ammolite",
        "ore_end_mithril", "ore_end_morguliron", "ore_mithril", "ore_morguliron",
        "ore_nether_ammolite", "ore_nether_mithril", "ore_nether_morguliron",
        "pane_ammolite", "pipeweed", "placard", "planks_culumalda", "planks_deadwood",
        "planks_dwarven_maple", "planks_fangornoak", "planks_lebethron",
        "planks_mallorn", "planks_mirkwood", "potted_flower_aeglos",
        "potted_flower_alfirin", "potted_flower_athelas", "potted_flower_brambles",
        "potted_flower_elanor", "potted_flower_lissuin", "potted_flower_mallos",
        "potted_flower_mirkwood", "potted_flower_niphredil", "potted_flower_simbelmyne",
        "potted_flower_swamp_milkweed", "potted_flower_valley_lilly",
        "potted_mushroom_bloom_decay", "potted_mushroom_decay_bloom",
        "potted_sapling_culumalda", "potted_sapling_deadwood",
        "potted_sapling_dwarven_maple", "potted_sapling_fangornoak",
        "potted_sapling_lebethron", "potted_sapling_mallorn", "potted_sapling_mirkwood",
        "pressure_plate_angainor", "pressure_plate_angainor_bricks",
        "pressure_plate_chiseled_angainor_bricks",
        "pressure_plate_chiseled_dark_stone_bricks",
        "pressure_plate_chiseled_dwarven_stone_bricks",
        "pressure_plate_chiseled_elven_marble_bricks",
        "pressure_plate_chiseled_iron_stone_bricks",
        "pressure_plate_chiseled_mountain_stone_bricks",
        "pressure_plate_cobbled_angainor", "pressure_plate_cobbled_dark_stone",
        "pressure_plate_cobbled_dwarven_stone", "pressure_plate_cobbled_elven_marble",
        "pressure_plate_cobbled_iron_stone", "pressure_plate_cobbled_mountain_stone",
        "pressure_plate_cracked_angainor_bricks",
        "pressure_plate_cracked_dark_stone_bricks",
        "pressure_plate_cracked_dwarven_stone_bricks",
        "pressure_plate_cracked_elven_marble_bricks",
        "pressure_plate_cracked_iron_stone_bricks",
        "pressure_plate_cracked_mountain_stone_bricks", "pressure_plate_culumalda",
        "pressure_plate_dark_stone", "pressure_plate_dark_stone_bricks",
        "pressure_plate_deadwood", "pressure_plate_dwarven_maple",
        "pressure_plate_dwarven_stone", "pressure_plate_dwarven_stone_bricks",
        "pressure_plate_elven_marble", "pressure_plate_elven_marble_bricks",
        "pressure_plate_fangornoak", "pressure_plate_iron_stone",
        "pressure_plate_iron_stone_bricks", "pressure_plate_lebethron",
        "pressure_plate_mallorn", "pressure_plate_mirkwood", "pressure_plate_mithril",
        "pressure_plate_morguliron", "pressure_plate_mossy_angainor_bricks",
        "pressure_plate_mossy_cobbled_angainor",
        "pressure_plate_mossy_cobbled_dark_stone",
        "pressure_plate_mossy_cobbled_dwarven_stone",
        "pressure_plate_mossy_cobbled_elven_marble",
        "pressure_plate_mossy_cobbled_iron_stone",
        "pressure_plate_mossy_cobbled_mountain_stone",
        "pressure_plate_mossy_dark_stone_bricks",
        "pressure_plate_mossy_dwarven_stone_bricks",
        "pressure_plate_mossy_elven_marble_bricks",
        "pressure_plate_mossy_iron_stone_bricks",
        "pressure_plate_mossy_mountain_stone_bricks", "pressure_plate_mountain_stone",
        "pressure_plate_mountain_stone_bricks", "pressure_plate_smooth_angainor",
        "pressure_plate_smooth_dark_stone", "pressure_plate_smooth_dwarven_stone",
        "pressure_plate_smooth_elven_marble", "pressure_plate_smooth_iron_stone",
        "pressure_plate_smooth_mountain_stone", "raw_mithril_block",
        "raw_morguliron_block", "rockpile", "sapling_culumalda", "sapling_deadwood",
        "sapling_dwarven_maple", "sapling_fangornoak", "sapling_lebethron",
        "sapling_mallorn", "sapling_mirkwood", "sign_culumalda", "sign_deadwood",
        "sign_dwarven_maple", "sign_fangornoak", "sign_lebethron", "sign_mallorn",
        "sign_mirkwood", "sign_wall_culumalda", "sign_wall_deadwood",
        "sign_wall_dwarven_maple", "sign_wall_fangornoak", "sign_wall_lebethron",
        "sign_wall_mallorn", "sign_wall_mirkwood", "silmaril_lantern", "slab_angainor",
        "slab_angainor_bricks", "slab_chiseled_angainor_bricks",
        "slab_chiseled_dark_stone_bricks", "slab_chiseled_dwarven_stone_bricks",
        "slab_chiseled_elven_marble_bricks", "slab_chiseled_iron_stone_bricks",
        "slab_chiseled_mountain_stone_bricks", "slab_cobbled_angainor",
        "slab_cobbled_dark_stone", "slab_cobbled_dwarven_stone",
        "slab_cobbled_elven_marble", "slab_cobbled_iron_stone",
        "slab_cobbled_mountain_stone", "slab_cracked_angainor_bricks",
        "slab_cracked_dark_stone_bricks", "slab_cracked_dwarven_stone_bricks",
        "slab_cracked_elven_marble_bricks", "slab_cracked_iron_stone_bricks",
        "slab_cracked_mountain_stone_bricks", "slab_culumalda", "slab_dark_stone",
        "slab_dark_stone_bricks", "slab_deadwood", "slab_dwarven_maple",
        "slab_dwarven_stone", "slab_dwarven_stone_bricks", "slab_elven_marble",
        "slab_elven_marble_bricks", "slab_fangornoak", "slab_iron_stone",
        "slab_iron_stone_bricks", "slab_lebethron", "slab_mallorn", "slab_mirkwood",
        "slab_mithril", "slab_morguliron", "slab_mossy_angainor_bricks",
        "slab_mossy_cobbled_angainor", "slab_mossy_cobbled_dark_stone",
        "slab_mossy_cobbled_dwarven_stone", "slab_mossy_cobbled_elven_marble",
        "slab_mossy_cobbled_iron_stone", "slab_mossy_cobbled_mountain_stone",
        "slab_mossy_dark_stone_bricks", "slab_mossy_dwarven_stone_bricks",
        "slab_mossy_elven_marble_bricks", "slab_mossy_iron_stone_bricks",
        "slab_mossy_mountain_stone_bricks", "slab_mountain_stone",
        "slab_mountain_stone_bricks", "slab_smooth_angainor", "slab_smooth_dark_stone",
        "slab_smooth_dwarven_stone", "slab_smooth_elven_marble",
        "slab_smooth_iron_stone", "slab_smooth_mountain_stone", "sleeping_bag_black",
        "sleeping_bag_blue", "sleeping_bag_brown", "sleeping_bag_cyan",
        "sleeping_bag_gray", "sleeping_bag_green", "sleeping_bag_light_blue",
        "sleeping_bag_light_gray", "sleeping_bag_lime", "sleeping_bag_magenta",
        "sleeping_bag_orange", "sleeping_bag_pink", "sleeping_bag_purple",
        "sleeping_bag_red", "sleeping_bag_white", "sleeping_bag_yellow",
        "smooth_angainor", "smooth_angainor_button", "smooth_dark_stone",
        "smooth_dark_stone_button", "smooth_dwarven_stone",
        "smooth_dwarven_stone_button", "smooth_elven_marble",
        "smooth_elven_marble_button", "smooth_iron_stone", "smooth_iron_stone_button",
        "smooth_mountain_stone", "smooth_mountain_stone_button", "stairs_angainor",
        "stairs_angainor_bricks", "stairs_chiseled_angainor_bricks",
        "stairs_chiseled_dark_stone_bricks", "stairs_chiseled_dwarven_stone_bricks",
        "stairs_chiseled_elven_marble_bricks", "stairs_chiseled_iron_stone_bricks",
        "stairs_chiseled_mountain_stone_bricks", "stairs_cobbled_angainor",
        "stairs_cobbled_dark_stone", "stairs_cobbled_dwarven_stone",
        "stairs_cobbled_elven_marble", "stairs_cobbled_iron_stone",
        "stairs_cobbled_mountain_stone", "stairs_cracked_angainor_bricks",
        "stairs_cracked_dark_stone_bricks", "stairs_cracked_dwarven_stone_bricks",
        "stairs_cracked_elven_marble_bricks", "stairs_cracked_iron_stone_bricks",
        "stairs_cracked_mountain_stone_bricks", "stairs_culumalda", "stairs_dark_stone",
        "stairs_dark_stone_bricks", "stairs_deadwood", "stairs_dwarven_maple",
        "stairs_dwarven_stone", "stairs_dwarven_stone_bricks", "stairs_elven_marble",
        "stairs_elven_marble_bricks", "stairs_fangornoak", "stairs_iron_stone",
        "stairs_iron_stone_bricks", "stairs_lebethron", "stairs_mallorn",
        "stairs_mirkwood", "stairs_mithril", "stairs_morguliron",
        "stairs_mossy_angainor_bricks", "stairs_mossy_cobbled_angainor",
        "stairs_mossy_cobbled_dark_stone", "stairs_mossy_cobbled_dwarven_stone",
        "stairs_mossy_cobbled_elven_marble", "stairs_mossy_cobbled_iron_stone",
        "stairs_mossy_cobbled_mountain_stone", "stairs_mossy_dark_stone_bricks",
        "stairs_mossy_dwarven_stone_bricks", "stairs_mossy_elven_marble_bricks",
        "stairs_mossy_iron_stone_bricks", "stairs_mossy_mountain_stone_bricks",
        "stairs_mountain_stone", "stairs_mountain_stone_bricks",
        "stairs_smooth_angainor", "stairs_smooth_dark_stone",
        "stairs_smooth_dwarven_stone", "stairs_smooth_elven_marble",
        "stairs_smooth_iron_stone", "stairs_smooth_mountain_stone",
        "stripped_log_culumalda", "stripped_log_deadwood", "stripped_log_dwarven_maple",
        "stripped_log_fangornoak", "stripped_log_lebethron", "stripped_log_mallorn",
        "stripped_log_mirkwood", "stripped_wood_culumalda", "stripped_wood_deadwood",
        "stripped_wood_dwarven_maple", "stripped_wood_fangornoak",
        "stripped_wood_lebethron", "stripped_wood_mallorn", "stripped_wood_mirkwood",
        "torch_culumalda", "torch_deadwood", "torch_dwarven_maple", "torch_fangornoak",
        "torch_lebethron", "torch_mallorn", "torch_mirkwood", "trapdoor_culumalda",
        "trapdoor_deadwood", "trapdoor_dwarven_maple", "trapdoor_fangornoak",
        "trapdoor_lebethron", "trapdoor_mallorn", "trapdoor_mirkwood",
        "trapdoor_mithril", "trapdoor_morguliron", "trinket_table", "wall_angainor",
        "wall_angainor_bricks", "wall_chiseled_angainor_bricks",
        "wall_chiseled_dark_stone_bricks", "wall_chiseled_dwarven_stone_bricks",
        "wall_chiseled_elven_marble_bricks", "wall_chiseled_iron_stone_bricks",
        "wall_chiseled_mountain_stone_bricks", "wall_cobbled_angainor",
        "wall_cobbled_dark_stone", "wall_cobbled_dwarven_stone",
        "wall_cobbled_elven_marble", "wall_cobbled_iron_stone",
        "wall_cobbled_mountain_stone", "wall_cracked_angainor_bricks",
        "wall_cracked_dark_stone_bricks", "wall_cracked_dwarven_stone_bricks",
        "wall_cracked_elven_marble_bricks", "wall_cracked_iron_stone_bricks",
        "wall_cracked_mountain_stone_bricks", "wall_dark_stone",
        "wall_dark_stone_bricks", "wall_decay_bloom", "wall_dwarven_stone",
        "wall_dwarven_stone_bricks", "wall_elven_marble", "wall_elven_marble_bricks",
        "wall_iron_stone", "wall_iron_stone_bricks", "wall_mithril", "wall_morguliron",
        "wall_mossy_angainor_bricks", "wall_mossy_cobbled_angainor",
        "wall_mossy_cobbled_dark_stone", "wall_mossy_cobbled_dwarven_stone",
        "wall_mossy_cobbled_elven_marble", "wall_mossy_cobbled_iron_stone",
        "wall_mossy_cobbled_mountain_stone", "wall_mossy_dark_stone_bricks",
        "wall_mossy_dwarven_stone_bricks", "wall_mossy_elven_marble_bricks",
        "wall_mossy_iron_stone_bricks", "wall_mossy_mountain_stone_bricks",
        "wall_mountain_stone", "wall_mountain_stone_bricks", "wall_mushroom_brown",
        "wall_mushroom_red", "wall_smooth_angainor", "wall_smooth_dark_stone",
        "wall_smooth_dwarven_stone", "wall_smooth_elven_marble",
        "wall_smooth_iron_stone", "wall_smooth_mountain_stone", "wall_torch_culumalda",
        "wall_torch_deadwood", "wall_torch_dwarven_maple", "wall_torch_fangornoak",
        "wall_torch_lebethron", "wall_torch_mallorn", "wall_torch_mirkwood",
        "well_block", "wood_culumalda", "wood_deadwood", "wood_dwarven_maple",
        "wood_fangornoak", "wood_lebethron", "wood_mallorn", "wood_mirkwood",
    ),
}


def collect_mod_blocks(
    world: BaseLevel,
    translation_manager,
    platform: str,
    version_number: Tuple[int, int, int],
    force_blockstate: bool = False,
) -> Dict[str, Sequence[str]]:
    """Block names per namespace that PyMCTranslate does not know.

    The baked registry is the authoritative list, so it can offer blocks that
    have not been placed in this world yet. Any other namespace in the level's
    block palette is added from the palette, which covers blocks of this mod
    (or another mod) that are already in the world.
    """
    mod_blocks = {namespace: set(names) for namespace, names in MOD_BLOCK_REGISTRY.items()}
    try:
        known = set(
            translation_manager.get_version(
                platform, version_number
            ).block.namespaces(force_blockstate)
        )
    except Exception:  # an unsupported version should not break the picker
        known = {"minecraft"}

    try:
        palette = world.block_palette
    except Exception:
        palette = ()

    for block in palette:
        namespace = block.namespace
        # The editor registers universal_minecraft:air at palette index 0. That
        # is not a namespace any version knows, so it must not become a picker
        # entry of its own.
        if namespace.startswith("universal_") or namespace in known:
            continue
        mod_blocks.setdefault(namespace, set()).add(block.base_name)

    return {
        namespace: tuple(sorted(names))
        for namespace, names in sorted(mod_blocks.items())
    }


def as_game_version(translation_manager, platform: str, version_number):
    """The game version tuple for a level wrapper's version number.

    A Java world's wrapper reports its level's data version, an int such as 3465,
    while a Bedrock world's reports a game version tuple. The editor's version
    picker only lists game version tuples: handed an int it matches nothing and
    silently falls back to the newest version instead of the world's own, so
    resolve the data version through PyMCTranslate first.
    """
    if not isinstance(version_number, int):
        return version_number
    try:
        return tuple(
            translation_manager.get_version(platform, version_number).version_number
        )
    except Exception:  # a version the translations do not know
        return version_number


def _snbt(value) -> str:
    """A property value as SNBT text, whichever form it arrived in."""
    if hasattr(value, "to_snbt"):
        return value.to_snbt()
    try:
        return amulet_nbt.from_snbt(value).to_snbt()
    except Exception:
        return str(value)


def block_matches(
    block: Block,
    namespace: str,
    name: str,
    properties: Mapping[str, str],
) -> bool:
    """Does this universal block match what the find picker is showing?

    ``properties`` holds SNBT strings as the picker produced them; a value of
    ``"*"`` is the picker's wildcard and matches any value. An empty mapping
    matches every state of the block.
    """
    if block.namespace != namespace or block.base_name != name:
        return False
    for property_name, wanted in properties.items():
        actual = block.properties.get(property_name)
        if wanted == "*":
            continue
        if actual is None or _snbt(actual) != _snbt(wanted):
            return False
    return True


def drop_block_entities(chunk, slices, replaced: "numpy.ndarray") -> None:
    """Remove block entities sitting on blocks that were just replaced.

    A replaced chest or sign would otherwise leave its contents behind at a
    coordinate that no longer holds that block.
    """
    if not chunk.block_entities:
        return
    # x and z in ``slices`` are chunk relative, y is an absolute block height.
    min_x = slices[0].start + chunk.cx * 16
    min_y = slices[1].start
    min_z = slices[2].start + chunk.cz * 16
    for coord in list(chunk.block_entities.keys()):
        dx = coord[0] - min_x
        dy = coord[1] - min_y
        dz = coord[2] - min_z
        if (
            0 <= dx < replaced.shape[0]
            and 0 <= dy < replaced.shape[1]
            and 0 <= dz < replaced.shape[2]
            and replaced[dx, dy, dz]
        ):
            del chunk.block_entities[coord]


class ModdedBlockSelect(BlockSelect):
    """The stock block picker with extra namespaces in the dropdown.

    ``BlockSelect`` reads its namespaces and block names straight from
    PyMCTranslate. Overriding the two populate methods is enough to add the
    modded entries; search, selection and the quoted free-text entry are
    inherited unchanged.
    """

    def __init__(self, *args, mod_blocks: Optional[Mapping[str, Sequence[str]]] = None, **kwargs):
        self._mod_blocks: Dict[str, Sequence[str]] = {}
        super().__init__(*args, **kwargs)
        self.set_mod_blocks(mod_blocks or {})

    def set_mod_blocks(self, mod_blocks: Mapping[str, Sequence[str]]):
        self._mod_blocks = {
            namespace: tuple(sorted(names)) for namespace, names in mod_blocks.items()
        }
        namespace = self.namespace
        name = self.name
        self._populate_namespace()
        self.set_namespace(namespace)
        self._populate_item_name()
        self.set_name(name)

    def _populate_namespace(self):
        version = self._translation_manager.get_version(
            self._platform, self._version_number
        )
        namespaces = list(version.block.namespaces(self._force_blockstate))
        for namespace in self._mod_blocks:
            if namespace not in namespaces:
                namespaces.append(namespace)
        self._do_text_event = False
        self._namespace_combo.Set(namespaces)

    def _populate_item_name(self):
        version = self._translation_manager.get_version(
            self._platform, self._version_number
        )
        try:
            names = list(version.block.base_names(self.namespace, self._force_blockstate))
        except Exception:  # a namespace PyMCTranslate has never heard of
            names = []
        for name in self._mod_blocks.get(self.namespace, ()):
            if name not in names:
                names.append(name)
        self._names = names
        self._list_box.SetItems(names)


class ModdedBlockDefine(wx.Panel):
    """``BlockDefine`` with modded namespaces in the picker.

    Same interface as the editor's BlockDefine (platform, version_number,
    namespace, block_name, str_properties, properties, block, universal_block)
    so the operation can use it exactly like the stock Replace screen does.
    """

    def __init__(
        self,
        parent: wx.Window,
        translation_manager,
        mod_blocks: Mapping[str, Sequence[str]],
        platform: str,
        version_number: Tuple[int, int, int],
        force_blockstate: Optional[bool] = None,
        namespace: Optional[str] = None,
        block_name: Optional[str] = None,
        properties: Optional[Mapping[str, str]] = None,
        wildcard_properties: bool = False,
        show_pick_block: bool = False,
    ):
        super().__init__(parent)
        self._translation_manager = translation_manager
        self._sizer = wx.BoxSizer(wx.VERTICAL)
        self.SetSizer(self._sizer)

        self._version_picker = VersionSelect(
            self,
            translation_manager,
            platform,
            as_game_version(translation_manager, platform, version_number),
            force_blockstate,
        )
        self._sizer.Add(self._version_picker, 0, wx.EXPAND)
        self._version_picker.Bind(EVT_VERSION_CHANGE, self._on_version_change)

        self._picker = ModdedBlockSelect(
            self,
            translation_manager,
            self._version_picker.platform,
            self._version_picker.version_number,
            self._version_picker.force_blockstate,
            namespace,
            block_name,
            show_pick_block,
            mod_blocks=mod_blocks,
        )
        self._sizer.Add(self._picker, 1, wx.EXPAND | wx.TOP, 5)
        self._picker.Bind(EVT_ITEM_CHANGE, self._on_picker_change)

        self._property_picker = PropertySelect(
            self,
            translation_manager,
            self._version_picker.platform,
            self._version_picker.version_number,
            self._version_picker.force_blockstate,
            self._picker.namespace,
            self._picker.name,
            dict(properties or {}),
            wildcard_properties,
        )
        self._sizer.Add(self._property_picker, 1, wx.EXPAND | wx.TOP, 5)
        self._property_picker.Bind(EVT_PROPERTIES_CHANGE, self._on_property_change)

        self.SetSizerAndFit(self._sizer)
        self.Layout()

    def _on_version_change(self, evt):
        self._picker.version = (
            evt.platform,
            evt.version_number,
            evt.force_blockstate,
        )
        self._update_properties()
        evt.Skip()

    def _on_picker_change(self, evt):
        self._update_properties()
        evt.Skip()

    def _on_property_change(self, evt):
        self.Layout()
        evt.Skip()

    def _update_properties(self):
        self._property_picker.version_block = (
            self._version_picker.platform,
            self._version_picker.version_number,
            self._version_picker.force_blockstate,
            self._picker.namespace,
            self._picker.name,
        )

    @property
    def platform(self) -> str:
        return self._version_picker.platform

    @property
    def version_number(self) -> Tuple[int, int, int]:
        return self._version_picker.version_number

    @property
    def force_blockstate(self) -> bool:
        return self._version_picker.force_blockstate

    @property
    def namespace(self) -> str:
        return self._picker.namespace

    @property
    def block_name(self) -> str:
        return self._picker.name

    @property
    def str_properties(self) -> Dict[str, str]:
        return self._property_picker.str_properties

    @property
    def properties(self):
        return self._property_picker.properties

    @property
    def block(self) -> Block:
        return Block(self.namespace, self.block_name, self.properties)

    @block.setter
    def block(self, block: Block):
        self._picker.set_namespace(block.namespace)
        self._picker.set_name(block.base_name)
        self._update_properties()
        self._property_picker.properties = block.properties

    @property
    def universal_block(self) -> Tuple[Block, None]:
        try:
            return self._translation_manager.get_version(
                self.platform, self.version_number
            ).block.to_universal(self.block, None, self.force_blockstate)[:2]
        except Exception:
            # A modded block has no translation. For Java 1.13+ its version form
            # already is the universal form, so hand it back unchanged.
            return self.block, None

    @universal_block.setter
    def universal_block(self, universal_block: Tuple[Block, None]):
        block, block_entity = universal_block
        try:
            version_block = self._translation_manager.get_version(
                self.platform, self.version_number
            ).block.from_universal(block, block_entity, self.force_blockstate)[0]
        except Exception:
            version_block = block
        if isinstance(version_block, Block):
            self.block = version_block


class TolkienMobsReplace(SimpleScrollablePanel, DefaultOperationUI):
    def __init__(
        self,
        parent: wx.Window,
        canvas: "EditCanvas",
        world: BaseLevel,
        options_path: str,
    ):
        SimpleScrollablePanel.__init__(self, parent)
        DefaultOperationUI.__init__(self, parent, canvas, world, options_path)
        self.Freeze()

        options = self._load_options({}) or {}

        if self._world_is_java_blockstates():
            wrapper = world.level_wrapper
            self._mod_blocks = collect_mod_blocks(
                world,
                wrapper.translation_manager,
                wrapper.platform,
                as_game_version(
                    wrapper.translation_manager, wrapper.platform, wrapper.version
                ),
            )
        else:
            # Universal block names only line up with version block names from
            # Java 1.13 on, which is what makes a modded block writable without
            # a translation entry. Older formats get the vanilla pickers only.
            self._mod_blocks = {}

        self._find_block = self._add_define(
            "Find blocks",
            options.get("find"),
            wildcard_properties=True,
            default_block=("minecraft", "stone"),
        )
        self._find_block.Bind(EVT_PICK, lambda evt: self._on_pick_block_button(1))

        self._match_properties = wx.CheckBox(
            self,
            label="Match block properties (otherwise every state of the block is replaced)",
        )
        self._match_properties.SetValue(bool(options.get("match_properties", False)))
        self._sizer.Add(self._match_properties, 0, wx.LEFT | wx.RIGHT | wx.TOP, 8)

        self._replacement_block = self._add_define(
            "Replace with",
            options.get("replace"),
            wildcard_properties=False,
            default_block=self._default_replacement(),
        )
        self._replacement_block.Bind(EVT_PICK, lambda evt: self._on_pick_block_button(2))

        self._run_button = wx.Button(self, label="Run Operation")
        self._run_button.Bind(wx.EVT_BUTTON, self._run_operation)
        self._sizer.Add(
            self._run_button, 0, wx.LEFT | wx.RIGHT | wx.BOTTOM | wx.EXPAND, 8
        )

        self.Layout()
        self.SetupScrolling()
        self.Thaw()

    @property
    def wx_add_options(self) -> Tuple[int, ...]:
        return (1,)

    def _world_is_java_blockstates(self) -> bool:
        wrapper = self.world.level_wrapper
        if wrapper.platform != "java":
            return False
        version = as_game_version(
            wrapper.translation_manager, wrapper.platform, wrapper.version
        )
        return isinstance(version, tuple) and version[:2] >= (1, 13)

    def _default_replacement(self) -> Tuple[str, str]:
        names = self._mod_blocks.get(MOD_NAMESPACE)
        if names:
            return MOD_NAMESPACE, names[0]
        return "minecraft", "stone"

    def _add_define(
        self,
        title: str,
        saved: Optional[Sequence],
        wildcard_properties: bool,
        default_block: Tuple[str, str],
    ) -> ModdedBlockDefine:
        wrapper = self.world.level_wrapper
        translation_manager = wrapper.translation_manager
        platform = wrapper.platform
        version_number = as_game_version(
            translation_manager, platform, wrapper.version
        )
        box = wx.StaticBoxSizer(wx.VERTICAL, self, title)
        try:
            define = ModdedBlockDefine(
                self,
                translation_manager,
                self._mod_blocks,
                *(saved or (platform, version_number, None, *default_block)),
                wildcard_properties=wildcard_properties,
                show_pick_block=True,
            )
        except Exception as exc:
            # Saved options are per plugin and survive world/version changes, so
            # they can be stale. Fall back to a sane default rather than fail.
            print(f"Find & replace: could not restore saved {title.lower()} settings ({exc})")
            define = ModdedBlockDefine(
                self,
                translation_manager,
                self._mod_blocks,
                platform,
                version_number,
                None,
                *default_block,
                wildcard_properties=wildcard_properties,
                show_pick_block=True,
            )
        box.Add(define, 1, wx.EXPAND | wx.ALL, 5)
        self._sizer.Add(box, 1, wx.EXPAND | wx.ALL, 6)
        return define

    def _on_pick_block_button(self, code: int):
        self._show_pointer = code

    def _on_box_click(self):
        if not self._show_pointer:
            return
        x, y, z = self._pointer.pointer_base
        block = self.world.get_block(x, y, z, self.canvas.dimension)
        if self._show_pointer == 1:
            self._find_block.universal_block = (block, None)
        else:
            self._replacement_block.universal_block = (block, None)
        self._show_pointer = False

    def _save_define(self, define: ModdedBlockDefine) -> Tuple:
        return (
            define.platform,
            define.version_number,
            define.force_blockstate,
            define.namespace,
            define.block_name,
            define.str_properties,
        )

    def disable(self):
        try:
            self._save_options(
                {
                    "find": self._save_define(self._find_block),
                    "replace": self._save_define(self._replacement_block),
                    "match_properties": self._match_properties.GetValue(),
                }
            )
        except Exception as exc:
            print(f"Find & replace: could not save the options ({exc})")
        super().disable()

    def _run_operation(self, _):
        self.canvas.run_operation(self._replace)

    def _replace(self):
        world = self.world
        dimension: Dimension = self.canvas.dimension
        selection: SelectionGroup = self.canvas.selection.selection_group

        find_namespace = self._find_block.namespace
        find_name = self._find_block.block_name
        find_properties = (
            dict(self._find_block.str_properties)
            if self._match_properties.GetValue()
            else {}
        )
        replacement, _block_entity = self._replacement_block.universal_block

        if not find_name:
            print("Find & replace: no block selected to find.")
            yield 1.0
            return
        if not replacement.base_name:
            print("Find & replace: no replacement block selected.")
            yield 1.0
            return

        replacement_id = world.block_palette.get_add_block(replacement)
        matches: List[int] = []
        scanned_palette_size = -1
        replaced = 0
        total = sum(1 for _ in world.get_coord_box(dimension, selection))

        for index, (chunk, slices, _box) in enumerate(
            world.get_chunk_slice_box(dimension, selection), 1
        ):
            # Chunks load as the operation walks the selection and can bring new
            # blocks into the palette, so rescan whenever it grew.
            if len(world.block_palette) != scanned_palette_size:
                scanned_palette_size = len(world.block_palette)
                matches = [
                    block_id
                    for block_id, block in world.block_palette.items()
                    if block_matches(block, find_namespace, find_name, find_properties)
                ]

            if matches:
                blocks = numpy.array(chunk.blocks[slices])
                mask = numpy.isin(blocks, matches)
                if mask.any():
                    blocks[mask] = replacement_id
                    chunk.blocks[slices] = blocks
                    replaced += int(mask.sum())
                    drop_block_entities(chunk, slices, mask)
                    chunk.changed = True

            yield min(index / total, 1.0) if total else 1.0

        print(f"Find & replace: swapped {replaced} block(s).")


export = {
    "name": "TolkienMobs Find & Replace",
    "operation": TolkienMobsReplace,
}
