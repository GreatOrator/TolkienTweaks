from amulet.api.block import Block
from amulet.api.level import BaseLevel
from amulet.api.selection import SelectionGroup
from amulet.api.data_types import Dimension

# =========================================================================
# 1. YOUR TOLKIENMOBS REGISTRY DUMP
# Paste your generated list of block names here. 
# Do not include the 'tolkienmobs:' prefix.
# =========================================================================
MOD_BLOCK_REGISTRY = [
    "morgul_iron_ore",
    "mithril_ore",
    "mallorn_log",
    "amroth_leaves",
    # <-- Paste the rest of your extracted list here
]

def tolkienmobs_replace_operation(
    world: BaseLevel, 
    dimension: Dimension, 
    selection: SelectionGroup, 
    options: dict
):
    # Retrieve the input values out of the 0.10.44 options layout
    find_ns = options.get("Find Block Namespace", "minecraft")
    find_name = options.get("Find Block Base Name", "stone")
    replace_name = options.get("Select TolkienMobs Replacement", "")

    # Basic safety checks
    if not find_name or not replace_name:
        print("Error: Missing target block fields. Make sure inputs are filled.")
        return

    # Build the required Amulet block validation objects
    target_find_block = Block(find_ns, find_name)
    modded_replace_block = Block("tolkienmobs", replace_name)
    replace_count = 0

    # Iterate through every block location in the active selection box
    for box in selection:
        for x in range(box.min_x, box.max_x):
            for y in range(box.min_y, box.max_y):
                for z in range(box.min_z, box.max_z):
                    
                    # Fetch the block present at the coordinate
                    current_block = world.get_version_block(
                        x, y, z, 
                        dimension, 
                        world.level_wrapper.platform_version
                    )
                    
                    # Compare block details
                    if (current_block.namespace == target_find_block.namespace and 
                        current_block.base_name == target_find_block.base_name):
                        
                        # Swap out with the modded replacement block definition
                        world.set_version_block(
                            x, y, z, 
                            dimension, 
                            world.level_wrapper.platform_version, 
                            modded_replace_block
                        )
                        replace_count += 1

    print(f"Operation complete! Swapped {replace_count} blocks into the world.")

# Explicitly typed layout mapping required for older v0.10.xx versions
export = {
    "name": "TolkienMobs Find & Replace",
    "operation": tolkienmobs_replace_operation,
    "options": {
        "Find Block Namespace": ("str", "minecraft"),
        "Find Block Base Name": ("str", "stone"),
        "Select TolkienMobs Replacement": ("str_choice", MOD_BLOCK_REGISTRY)
    }
}