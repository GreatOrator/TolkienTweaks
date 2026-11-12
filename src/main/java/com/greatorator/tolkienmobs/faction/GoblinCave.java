package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.block.custom.entity.LockableChestBlockEntity;
import com.greatorator.tolkienmobs.containers.KeyItemContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The goblin cave page 27 hunts Ulmug in, cut into the hill behind a pinned door.
 * <p>
 * A cave is the one ground in the campaign that is not a template, so it is not placed: it is taken out
 * of the hill the world already made. The door is where a scout stood - {@code /tmpin place goblins} -
 * and everything else is measured from it. Which way the hill rises is read rather than given, so the
 * same pin in the same world always cuts the same cave and the cave always goes into the hill instead of
 * out of it.
 * <p>
 * Two kinds of room, on purpose. The hall is squared: it is the worked part, cut and floored by whoever
 * holds the pass. The chambers are not - they are built out of overlapping lobes, so their walls have
 * shoulders and recesses rather than one clean sweep, they swell and pinch round the compass and dome
 * over, and then rock is left standing in them: rubble on the floor, stalactites hanging and stalagmites
 * standing wherever the cell hash puts them. Two chambers flank the hall and a third, larger, sits
 * behind its far wall. A goblin hall is a room somebody made and the caves behind it are not. The sines
 * and the hash are the shape; they are the same ones {@code tools/cave_section.py} draws with, so the
 * drawing and the cut agree.
 * <p>
 * The one rule the plan keeps is that the cave never opens where nobody cut a mouth: every cell is held
 * {@link #ROOF} blocks under the ground above it, except the way in's own first few blocks, which are cut
 * through the surface because that is what a mouth is.
 */
public final class GoblinCave {
    /** The pin the door is read from. A place name, so /tmpin, /tmtp and the catalogue all know it. */
    public static final String PIN = "goblins";

    /**
     * The hall's own structure, which is the same room this cut leaves written out as a template so it can
     * be lived in: what the cut can do is take rock out, and a room somebody holds is floored, walled and
     * furnished. Laid over the hollow the cut has already made, its bottom course is the hall's floor and
     * its sides are the rock round it.
     */
    public static final String HALL = "tolkienmobs:goblin_hall";

    /**
     * Ulmug, the goblin king page 27 hunts, and the folk who keep the fires with him. The hall is ground
     * the world raises; the bodies in it are the campaign's, so a player who walks in before Gloin has
     * spoken finds the room and nobody home.
     */
    public static final String ULMUG = "Ulmug";
    /**
     * The tag Ulmug wears, so a second look at the hall does not stand a second king in it - and so page 27
     * knows him: the page names a goblin, and he is a goblin king, so only the tag makes his death count.
     */
    public static final String ULMUG_TAG = "tolkienmobs:goblin_hall_ulmug";
    /** The tag the hall's goblins wear: the fires the page counts, told from any goblin in the wild. */
    public static final String FOLK_TAG = "tolkienmobs:goblin_hall_folk";
    /** The page whose asking the fires answer: the pack's goblin, killed once in Ulmug's hall. */
    private static final String GOBLINS_TARGET = "tolkienmobs:entityttmgoblin";
    /**
     * How many goblins the fires are worth: enough to be a fire, few enough to walk out of. Page 27 asks
     * for these and the king beside them, so the number is the page's goal less one.
     */
    public static final int FOLK = 5;
    /** How near the door a player has to be for the hall's folk to be stood, in blocks. */
    private static final double FOLK_WAY = 64.0D;
    /** The tag one of the hall's goblins wears, so exactly one of them is carrying the key. */
    public static final String KEY_TAG = "tolkienmobs:goblin_hall_key";
    /**
     * The halls that have already given up their key, so a hall hands over one key and not a key per goblin
     * that takes the carrier's place. Kept for the session: a world restarted after the key was taken can arm
     * a carrier again if the hall is stood afresh, which is the one case this does not remember.
     */
    private static final Set<Long> KEYED = new HashSet<>();
    /**
     * The caves this world has already cut, so a player walking the hill cuts one and not one a pass. Kept for
     * the session: a restarted world asks the hall's own bench whether the room is there before cutting again.
     */
    private static final Set<Long> RAISED = new HashSet<>();
    /** How near the door a player has to be for the world to cut the cave, in blocks. */
    private static final double RAISE_WAY = 96.0D;

    private static final int DROP = 40;           // how far the way in falls, from the door's ground
    private static final int RAMP = DROP * 2;     // one block down in two, so twice as far as it falls
    private static final int HALL_BACK = RAMP + 6;        // where the hall starts, past the door
    private static final int HALL_FRONT = HALL_BACK + 42; // and where it ends
    private static final int HALL_SIDE = 8;       // half its width
    private static final int HALL_HEIGHT = 13;    // the hall's own headroom
    private static final int LANDING_HEIGHT = 5;  // the landing's
    private static final int LANDING_SIDE = 3;    // half the landing's width
    private static final int DOOR_BACK = 96;      // where the cave tunnels out of the hall's sides
    private static final int DOOR_FRONT = 116;    // and where they are into the chambers
    private static final int HALL_VAULT = 8;      // the hall's courses below this are its full width
    private static final int HALL_INSET = 2;      // and above it the ceiling is set in this far
    private static final int HALL_CUT = 3;        // its corners are cut at 45 degrees, this far in
    private static final int HALL_CLEAR = 2;      // rock kept between the hall's wall and a chamber
    private static final int TUNNEL_BACK = 134;   // where a flank tunnel leaves its own chamber
    private static final int TUNNEL_FRONT = 184;  // and where it is well into the back one
    private static final int TUNNEL_INSIDE = 22;  // and how far it stays off the hall's own line
    private static final int CHAMBER_TALL = 20;   // the tallest any lobe of a chamber gets
    private static final int FAR = 220;           // the plan's own far wall
    private static final int FAR_SIDE = 60;       // and how wide it reaches

    /** One lobe of a chamber: where it sits in the plan, how far it reaches, how tall it is. */
    private record Lobe(int a, int s, int ra, int rs, int height) {
    }

    /**
     * The chamber either side of the hall, as lobes rather than an ellipse: four overlapping ones, so
     * the outline has shoulders and recesses instead of one clean sweep. Mirrored, so this is one side.
     */
    private static final Lobe[] FLANK = {
            new Lobe(112, 28, 20, 18, 16),
            new Lobe(136, 36, 12, 11, 12),
            new Lobe(96, 40, 11, 10, 11),
            new Lobe(118, 46, 8, 8, 10),
    };

    /** The chamber behind the hall's far wall, larger than either flank. */
    private static final Lobe[] BACK = {
            new Lobe(172, 0, 26, 30, 20),
            new Lobe(196, -18, 15, 16, 15),
            new Lobe(200, 20, 14, 15, 14),
            new Lobe(150, 13, 13, 13, 13),
    };

    /**
     * The ways out of the hall's sides, as cave tunnels rather than doorways: two balls each, wandering
     * out of the wall and changing width, mirrored left and right like the chambers.
     */
    private static final Lobe[] DOORWAY = {
            new Lobe(98, 8, 3, 3, 7),
            new Lobe(103, 11, 3, 3, 6),
            new Lobe(108, 14, 3, 4, 6),
            new Lobe(113, 17, 4, 4, 6),
    };

    /**
     * The way from each flank chamber into the one behind the hall, as a string of small balls rather
     * than a bored corridor: the centres wander and the radii change, so it reads as a tunnel somebody
     * followed rather than a passage somebody drove. Mirrored with the flank, like the chamber is.
     */
    private static final Lobe[] TUNNEL = {
            new Lobe(140, -38, 4, 4, 6),
            new Lobe(146, -36, 4, 4, 5),
            new Lobe(151, -39, 3, 4, 5),
            new Lobe(156, -35, 4, 5, 6),
            new Lobe(161, -37, 3, 4, 5),
            new Lobe(166, -33, 4, 4, 6),
            new Lobe(171, -35, 3, 4, 5),
            new Lobe(176, -28, 5, 5, 6),
    };
    private static final int ROOF = 3;            // rock kept between the cave and the sky above it
    private static final int OPEN = 4;            // the way in's own blocks, cut through the surface
    private static final int OUTSIDE = 1;         // blocks cut in front of the door, so it is not walled in
    private static final int MOUTH_SIDE = 2;      // half the mouth's width, and the way in's
    private static final int MOUTH_HEIGHT = 7;    // and how tall both are
    /** The code the camp's chest and the key Gloin hands over both carry. */
    public static final String CAMP_CODE = "CampStorage";
    /** How near the mouth an ashen scroll has to be read, in blocks. */
    public static final int SCROLL_REACH = 16;
    /** What the player is told when the scroll is spent on the door. */
    public static final String OPEN_LINE = "campaign.tolkienmobs.cave.open";
    /** How far above the bore's head the wall is carried, at most, to meet the hillside beside it. */
    private static final int WALL_CAP = 8;
    /** The courses of the mouth that are cut through the surface itself, as {@link #carve} makes them. */
    private static final int MOUTH_COURSES = 2;
    private static final int SPUR_TOP = 16;       // of 256: how often a column of cavern keeps a stalactite
    private static final int SPUR_LOW = 14;       // and how often a stalagmite
    private static final int SPUR_LONG = 5;       // the longest of either, in blocks
    private static final double COARSE = 0.16;    // how far a wall wanders, as a share of its reach
    private static final double FINE = 0.06;      // and how much of that is per two blocks

    /**
     * The key page 2-2 hands out: a bronze key coded to the camp's chest, and worn out before it is given - zero
     * uses and no mode to spend them - so opening the chest never costs it. Its own fifteen slots are the camp's
     * stores, which is the only thing a key's inventory is: a few cram and the ashen scroll the mouth answers to.
     */
    public static ItemStack campKey() {
        ItemStack key = new ItemStack(TolkienItems.BRONZE_KEY.get());
        KeyItem.setKeyData(key, CAMP_CODE, 0, false);
        NonNullList<ItemStack> slots = NonNullList.withSize(KeyItemContainer.SLOTS, ItemStack.EMPTY);
        slots.set(0, new ItemStack(TolkienItems.CRAM.get(), CAMP_CRAM));
        slots.set(1, new ItemStack(TolkienItems.ITEM_SCROLL.get(), 1));
        key.set(TolkienDataComponents.ITEMSTACK_HANDLER.get(), ItemContainerContents.fromItems(slots));
        return key;
    }

    /**
     * Walls the mouth up with the hill's own stone, so a cave that has not been opened reads as unbroken rock
     * rather than as a hole nobody was meant to find. Cut into the hillside at the raise, and laid again when a
     * page is finished with it.
     */
    public static boolean seal(ServerLevel level) {
        return mouth(level, true);
    }

    /** Takes that wall back out, when the ashen scroll is read at the door. */
    public static boolean open(ServerLevel level) {
        return mouth(level, false);
    }

    /**
     * The mouth as a wall or as an opening, over the cells {@link #carve} opens at the way in - and no others.
     * <p>
     * The sill is the pin's own height rather than the ground read at the door: once the wall is standing, the top of
     * the column at the pin is the wall's own roof, so a sill measured there had climbed seven blocks by the time the
     * scroll was read and the opening cleared the sky above the plug instead of the plug. The shape is the cut's, not
     * a box drawn round the door: the ramp's first steps are cut above the ground behind the mouth, which is the open
     * trench a player can see over the wall, so a wall three courses deep left that trench looking straight through.
     */
    private static boolean mouth(ServerLevel level, boolean fill) {
        BlockPos pin = Pins.at(level, PIN);
        if (pin == null || !TolkienDimensions.ARDA.equals(level.dimension())) return false;
        Map<Long, Integer> cache = new HashMap<>();
        Door written = door(level);
        int sill;
        Direction into;
        if (written != null) {
            sill = written.sill();
            into = written.into();
        } else {
            // A world cut before the door was written down: the ground at the pin is the wall's own roof by now, so
            // this is a guess and only as good as the wall that stands. Re-cutting writes the door and settles it.
            Integer read = ground(level, pin.getX(), pin.getZ(), cache);
            if (read == null) return false;
            sill = read;
            into = steepest(level, pin.getX(), pin.getZ(), sill, cache);
        }
        int floor = sill - DROP;
        int stepX = into.getStepX();
        int stepZ = into.getStepZ();
        int acrossX = -stepZ;
        int acrossZ = stepX;
        int changed = 0;
        for (int a = 0; a <= OPEN; a++) {
            for (int s = -MOUTH_SIDE; s <= MOUTH_SIDE; s++) {
                int x = pin.getX() + stepX * a + acrossX * s;
                int z = pin.getZ() + stepZ * a + acrossZ * s;
                for (int y = floor; y <= sill + MOUTH_HEIGHT - 1; y++) {
                    if (!mouthCell(a, s, y, sill, floor)) continue;
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(at);
                    if (fill) {
                        if (!state.isAir() && !state.is(Blocks.CAVE_AIR) && !state.is(Blocks.VOID_AIR)) continue;
                        level.setBlock(at, skinOf(level, x, z, y, stepX, stepZ, acrossX, acrossZ), 2);
                        changed++;
                    } else {
                        if (!state.is(BlockTags.BASE_STONE_OVERWORLD)) continue;
                        level.setBlock(at, Blocks.CAVE_AIR.defaultBlockState(), 2);
                        changed++;
                    }
                }
                if (!fill) continue;
                // And the wall is carried up to the ground beside it. The ramp's first steps are cut down into the
                // hillside, and the hillside falls away above them, so the bore's own head is below the surface by
                // the time the way in is a few courses deep: a wall built to the bore's head left the trench open to
                // the sky behind it, which is the hole a player saw over the plug. Only air is filled, so the bank
                // itself is left exactly as it stands, and the sky over the trench becomes hillside.
                Integer beside = flankGround(level, x, z, acrossX, acrossZ, cache);
                if (beside == null) continue;
                int head = Math.min(beside, sill + MOUTH_HEIGHT + WALL_CAP);
                for (int y = sill + MOUTH_HEIGHT; y <= head; y++) {
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(at);
                    if (!state.isAir() && !state.is(Blocks.CAVE_AIR) && !state.is(Blocks.VOID_AIR)) continue;
                    level.setBlock(at, skinOf(level, x, z, y, stepX, stepZ, acrossX, acrossZ), 2);
                    changed++;
                }
            }
        }
        return changed > 0;
    }

    /** The pin a cut writes its own doorway under, headed by the way the hill rose from it. */
    private static String doorPin(Direction into) {
        return PIN + ".door." + into.getName();
    }

    /** The sill and the heading a cut wrote down, or null for a world cut before they were written. */
    @Nullable
    private static Door door(ServerLevel level) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos written = Pins.at(level, doorPin(side));
            if (written != null) return new Door(written.getY(), side);
        }
        return null;
    }

    /** What a cut knows about its own way in, and what a later scroll has to know to put it back. */
    private record Door(int sill, Direction into) {
    }

    /**
     * The cells the way in is made of: the wall's own three courses through the surface at the door, and the ramp's
     * first steps behind them, which are cut above the ground and so are the trench a player sees from outside. Taken
     * from {@link #carve} rather than drawn round the door, so the wall is the hole's shape and opening it back up
     * cannot take a block the cut never opened.
     */
    private static boolean mouthCell(int a, int s, int y, int sill, int floor) {
        if (Math.abs(s) > MOUTH_SIDE) return false;
        if (a >= 0 && a <= MOUTH_COURSES && y >= sill && y <= sill + MOUTH_HEIGHT - 1) return true;
        if (a >= 1 && a <= OPEN) {
            int low = floor + DROP - (a - 1) * DROP / (RAMP - 1);
            return y >= low && y <= low + MOUTH_HEIGHT - 1;
        }
        return false;
    }

    /** The ground beside a mouth column, higher of the two flanks, which is the hillside the mouth was cut into. */
    @Nullable
    private static Integer flankGround(ServerLevel level, int x, int z, int acrossX, int acrossZ,
                                       Map<Long, Integer> cache) {
        Integer left = ground(level, x + acrossX * (MOUTH_SIDE + 1), z + acrossZ * (MOUTH_SIDE + 1), cache);
        Integer right = ground(level, x - acrossX * (MOUTH_SIDE + 1), z - acrossZ * (MOUTH_SIDE + 1), cache);
        if (left == null) return right;
        if (right == null) return left;
        return Math.max(left, right);
    }

    /** The block the hill beside the mouth is made of, so the wall is the hillside rather than a plug in it. */
    private static BlockState skinOf(ServerLevel level, int x, int z, int y, int stepX, int stepZ,
                                     int acrossX, int acrossZ) {
        for (int side : new int[]{MOUTH_SIDE + 1, -MOUTH_SIDE - 1}) {
            BlockState beside = level.getBlockState(new BlockPos(x + acrossX * side, y, z + acrossZ * side));
            if (beside.is(BlockTags.BASE_STONE_OVERWORLD)) return beside;
        }
        return Blocks.TUFF.defaultBlockState();
    }

    /** Whether a key's own slots still hold the ashen scroll the mouth answers to. */
    public static boolean holdsScroll(ItemStack key) {
        ItemContainerContents contents = key.get(TolkienDataComponents.ITEMSTACK_HANDLER.get());
        if (contents == null) return false;
        for (int slot = 0; slot < contents.getSlots(); slot++) {
            if (contents.getStackInSlot(slot).is(TolkienItems.ITEM_SCROLL.get())) return true;
        }
        return false;
    }

    /**
     * Puts an ashen scroll back in a key's own slots. The scroll is spent on the wall, and the page can be walked
     * again in this world and the wall laid again when it is finished with: a key holding the dust of the last one
     * would leave the second walk with no way in, and the camp's chest has nothing else to give.
     */
    public static void stockScroll(ItemStack key) {
        if (holdsScroll(key)) return;
        ItemContainerContents contents = key.get(TolkienDataComponents.ITEMSTACK_HANDLER.get());
        NonNullList<ItemStack> slots = NonNullList.withSize(KeyItemContainer.SLOTS, ItemStack.EMPTY);
        if (contents != null) contents.copyInto(slots);
        for (int slot = 0; slot < slots.size(); slot++) {
            if (!slots.get(slot).isEmpty()) continue;
            slots.set(slot, new ItemStack(TolkienItems.ITEM_SCROLL.get()));
            break;
        }
        key.set(TolkienDataComponents.ITEMSTACK_HANDLER.get(), ItemContainerContents.fromItems(slots));
    }

    /** How many cram the key's own slots carry. */
    private static final int CAMP_CRAM = 4;

    /** What one cut came to, for the command to say. */
    public record Result(BlockPos door, Direction into, int floor, int ceiling, int cells, int dropped,
                         @Nullable BlockPos hall) {
    }

    private GoblinCave() {
    }

    /**
     * Cuts the cave behind this world's goblin pin. Nothing is cut when the pin is missing, or when the
     * ground under it has not been made yet: a cave is taken out of a hill, so there has to be one.
     */
    @Nullable
    public static Result cut(ServerLevel level) {
        BlockPos pin = Pins.at(level, PIN);
        if (pin == null || !TolkienDimensions.ARDA.equals(level.dimension())) return null;
        Map<Long, Integer> ground = new HashMap<>();
        Integer surface = ground(level, pin.getX(), pin.getZ(), ground);
        if (surface == null) return null;

        int doorX = pin.getX();
        int doorZ = pin.getZ();
        Direction into = steepest(level, doorX, doorZ, surface, ground);
        int stepX = into.getStepX();
        int stepZ = into.getStepZ();
        int acrossX = -stepZ;
        int acrossZ = stepX;

        // Every chunk the cave touches is asked for first: a block written to a chunk nobody is holding
        // is a block written to nothing.
        reach(level, doorX, doorZ, stepX, stepZ, acrossX, acrossZ);

        // The door's own height and heading are written down before a block is taken out: the sill is the ground at
        // the door column, which the cut then opens to the sky and the wall later puts its own roof over - so after
        // the cut there is nothing left in the world to measure it from, and a wall built from the pin's own y stood
        // as many blocks under the bore as the scout happened to be standing above or below it.
        Pins.set(level, doorPin(into), new BlockPos(doorX, surface, doorZ));

        int floor = surface - DROP;
        int cells = 0;
        int dropped = 0;
        // Every cell the cut opens, kept so the wear is done after the carving rather than during it: a patch
        // set into a wall the next row up has not been cut yet is a patch the cut takes back out again.
        List<BlockPos> opened = new ArrayList<>();
        for (int a = -OUTSIDE; a <= FAR; a++) {
            for (int s = -FAR_SIDE; s <= FAR_SIDE; s++) {
                int x = doorX + stepX * a + acrossX * s;
                int z = doorZ + stepZ * a + acrossZ * s;
                Integer top = ground(level, x, z, ground);
                if (top == null) continue;
                Spur spur = spur(a, s, floor);
                for (int y = floor; y <= surface + MOUTH_HEIGHT - 1; y++) {
                    if (!carve(a, s, y, surface, floor, spur)) continue;
                    // The mouth's own few blocks are the one place the cave is meant to open: they are cut
                    // through the surface, which is what makes a mouth a mouth rather than a buried room.
                    if (a > OPEN && y > top - ROOF) {
                        dropped++;
                        continue;
                    }
                    BlockPos at = new BlockPos(x, y, z);
                    // Every cell the plan opens is kept, whether the cut has to take a block out of it or the world
                    // already had it open: a cell the hill was hollow at before the cut is as much the cave as the
                    // rest of it, and it wants the same wear. Cells above the roof were turned away above, so what
                    // is kept here is cave rather than sky.
                    opened.add(at);
                    BlockState state = level.getBlockState(at);
                    if (state.isAir() || state.getFluidState().is(Fluids.WATER)) continue;
                    level.setBlock(at, Blocks.CAVE_AIR.defaultBlockState(), 2);
                    cells++;
                }
            }
        }
        // The hill weathers the cave before the hall is laid in it, and the hall's own walls after: a room cut
        // in a hill is still a room the hill has been in for an age, and the template's walls are the same stone
        // the caves round them are made of.
        AABB box = hallBox(level, pin, into, floor);
        wear(level, opened, box);
        BlockPos hall = dress(level, pin, into, floor);
        if (box != null) wearHall(level, box);
        // The mouth is walled as the last thing the cut does, so the hill reads unbroken until a scroll is read
        // at it; the hall is behind the wall rather than sealed away from it.
        seal(level);
        return new Result(pin, into, floor, floor + HALL_HEIGHT - 1, cells, dropped, hall);
    }

    /** The four ways a wall can stand from a cell, picked by the cell's own hash so it is the same wall each time. */
    private static final Direction[] SIDES = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    /**
     * Ages the cave the cut has just opened: cobble where a floor has broken, moss on the stone that has been
     * wet, and the odd block of glowstone set into a ceiling - so a hill that has been hollow for an age does not
     * read as a corridor somebody drove.
     * <p>
     * Everything is taken off the cell's own hash rather than from the world's random, so the same plan wears the
     * same way in every world and a save re-cut from the same seed is the same cave. Only the hill's own material
     * is worn - {@link BlockTags#BASE_STONE_OVERWORLD} - because anything else is somebody's work rather than the
     * hill's, and the hall's own box is left alone entirely: its floor is the template's.
     */
    private static void wear(ServerLevel level, List<BlockPos> opened, @Nullable AABB hall) {
        for (BlockPos air : opened) {
            if (hall != null && hall.inflate(1.0D).contains(air.getX() + 0.5D, air.getY() + 0.5D,
                    air.getZ() + 0.5D)) continue;
            int roll = hash(air.getX(), air.getY(), air.getZ());
            if (roll < 3) {
                BlockPos ceiling = air.above();
                if (hill(level.getBlockState(ceiling))) {
                    level.setBlock(ceiling, Blocks.GLOWSTONE.defaultBlockState(), 2);
                }
            } else if (roll < 46) {
                BlockPos under = air.below();
                if (hill(level.getBlockState(under))) {
                    level.setBlock(under, (roll & 2) == 0
                            ? Blocks.MOSSY_COBBLESTONE.defaultBlockState()
                            : Blocks.COBBLESTONE.defaultBlockState(), 2);
                }
            } else if (roll < 64) {
                BlockPos wall = air.relative(SIDES[(roll >> 3) & 3]);
                if (hill(level.getBlockState(wall))) {
                    level.setBlock(wall, (roll & 4) == 0
                            ? Blocks.MOSSY_COBBLESTONE.defaultBlockState()
                            : Blocks.COBBLESTONE.defaultBlockState(), 2);
                }
            }
        }
    }

    /** The hill's own material, which is the only thing the wear may change. */
    private static boolean hill(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD);
    }

    /**
     * Wears the hall's own walls once the template is down: the room crops up in a hill like every cave round
     * it, so its stone breaks into cobble and takes moss where the wall faces the room. The floor course is left
     * exactly as the template saved it - that is the ground the benches, the fire and the rack stand on, and a
     * hole worn in it is a hole in the room's own furniture.
     */
    private static void wearHall(ServerLevel level, AABB hall) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = (int) hall.minY + 1; y < (int) hall.maxY; y++) {
            for (int x = (int) hall.minX; x < (int) hall.maxX; x++) {
                for (int z = (int) hall.minZ; z < (int) hall.maxZ; z++) {
                    cursor.set(x, y, z);
                    if (!hill(level.getBlockState(cursor))) continue;
                    if (!facesRoom(level, cursor)) continue;
                    int roll = hash(x, y, z);
                    if (roll < 3) {
                        level.setBlock(cursor, Blocks.GLOWSTONE.defaultBlockState(), 2);
                    } else if (roll < 40) {
                        level.setBlock(cursor, (roll & 2) == 0
                                ? Blocks.MOSSY_COBBLESTONE.defaultBlockState()
                                : Blocks.COBBLESTONE.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    /** Whether a block stands in the room rather than in the hill: air beside it or over it. */
    private static boolean facesRoom(ServerLevel level, BlockPos at) {
        for (Direction side : SIDES) {
            if (level.getBlockState(at.relative(side)).isAir()) return true;
        }
        return level.getBlockState(at.above()).isAir();
    }

    /**
     * Lays the hall's structure into the room the cut has just made, so the hall is a room somebody made
     * rather than bare rock: the file carries the floor course the cut leaves to whatever the hill had, and
     * whatever has been saved into it since - a hall furnished by hand is a hall that gets placed.
     * <p>
     * The file is the same {@code hall()} this cut uses, written out by {@code tools/hall_structure.py}, so
     * it lands over the hollow rather than beside it. Its own axes are x across the way in and z into the
     * hill; here that is turned to face the way this world's hill happens to rise, because which way the
     * cave runs is read from the ground rather than given.
     */
    @Nullable
    public static BlockPos dress(ServerLevel level, BlockPos door, Direction into, int floor) {
        StructureTemplate hall = level.getStructureManager().get(Identifier.parse(HALL)).orElse(null);
        if (hall == null) return null;
        Rotation rotation = rotationFor(into);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setIgnoreEntities(false);
        // The corner the file is laid from is the near corner of the hall's floor course - one course below
        // the hall's own floor, which is the floor the file carries - and not the door, which is the ramp's
        // far end. The file's x runs from the far side of the room back towards the near one, so the corner
        // is taken across the way in rather than along it. The width is read from the file rather than from
        // the room's own figures, so a hall saved back a course wider or narrower still lands centred on the
        // way in instead of overhanging the wall it was measured from.
        BlockPos corner = hallCorner(door, into, floor, wide(rotation, hall.getSize()));
        // The template's own box is not anchored at (0,0,0) once it is turned, so the corner the caller
        // wants is the minimum of the turned box rather than the position handed to the placing.
        BoundingBox box = hall.getBoundingBox(settings, BlockPos.ZERO);
        BlockPos anchor = corner.offset(-box.minX(), -box.minY(), -box.minZ());
        hall.placeInWorld(level, anchor, anchor, settings, level.getRandom(), 2);
        return corner;
    }

    /**
     * Lays the hall again at this world's goblin pin, for a hall that has been furnished since it was cut:
     * save the decorated room over {@code goblin_hall.nbt} and this puts it back in, without cutting the cave
     * again.
     */
    @Nullable
    public static BlockPos dress(ServerLevel level) {
        BlockPos pin = Pins.at(level, PIN);
        if (pin == null || !TolkienDimensions.ARDA.equals(level.dimension())) return null;
        Map<Long, Integer> ground = new HashMap<>();
        Integer surface = ground(level, pin.getX(), pin.getZ(), ground);
        if (surface == null) return null;
        Direction into = steepest(level, pin.getX(), pin.getZ(), surface, ground);
        return dress(level, pin, into, surface - DROP);
    }

    /**
     * Cuts the cave and lays the hall in it as a player comes near, so the cave is ground the world has rather
     * than something somebody has to run a command to make.
     * <p>
     * The camps and Rivendell are raised the same way - a clock check per player, a reach, and a chunk that has
     * to be loaded before a block is written into it - because the map is walked by players who have no commands
     * and no reason to be told a cave is about to exist. The cut is one pass over eleven thousand cells, so it is
     * done once and remembered; the hall's own bench is the record that the room is already there, which is also
     * what catches a world cut by hand before this existed. Nothing is said in chat: a player walks into the hill
     * and the cave and hall are simply there.
     */
    public static void raised(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return;
        BlockPos pin = Pins.at(level, PIN);
        if (pin == null) return;
        if (player.distanceToSqr(pin.getX() + 0.5D, pin.getY(), pin.getZ() + 0.5D) > RAISE_WAY * RAISE_WAY) return;
        if (!level.hasChunkAt(pin)) return;
        if (RAISED.contains(pin.asLong())) return;
        AABB hall = hallAt(level);
        if (hall != null && bench(level, hall) != null) {
            RAISED.add(pin.asLong());
            return;
        }
        if (cut(level) != null) RAISED.add(pin.asLong());
    }

    /**
     * Stands Ulmug and his folk in the hall while page 27 is being carried.
     * <p>
     * The fires are the page's quarry, so they are the campaign's bodies rather than the world's scenery:
     * the hall stands in every world, and the goblins in it are only there for a player who has been sent
     * to put them out. A player who walks in before Gloin has spoken finds the room and nobody home, which
     * is also what keeps a hall of hostiles off a road nobody holds a page for.
     * <p>
     * Ulmug stands beside the hall's own log bench rather than at a fixed offset in the template, so a hall
     * furnished a second time moves him with the furniture. The rest are stood on the hall's floor and left
     * to wander it. Both are found again by tag rather than counted in memory, so nothing multiplies them.
     */
    public static void keep(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return;
        if (!onGoblinPage(player)) return;
        AABB hall = hallAt(level);
        if (hall == null || !hall.inflate(FOLK_WAY).contains(player.position())) return;
        List<Entity> kings = level.getEntities((Entity) null, hall.inflate(4.0D),
                found -> found.entityTags().contains(ULMUG_TAG));
        List<Entity> folk = level.getEntities((Entity) null, hall.inflate(4.0D),
                found -> found.entityTags().contains(FOLK_TAG));
        if (!kings.isEmpty() && folk.size() >= FOLK) return;
        if (kings.isEmpty()) {
            BlockPos bench = bench(level, hall);
            if (bench == null) return;
            stand(level, TolkienEntities.ENTITY_TTM_GOBLINKING.get(), beside(level, bench), ULMUG_TAG, ULMUG);
        }
        // One of the fires carries the key to the hall's own chest, and only one. The carrier is marked as the
        // folk are stood, and a hall that has already given its key up arms nobody else - so the key is one
        // body's, not a purse every goblin in the room is holding.
        boolean keyed = KEYED.contains(keyOf(hall));
        for (int stood = folk.size(); stood < FOLK; stood++) {
            BlockPos spot = floor(level, hall);
            if (spot == null) continue;
            Mob goblin = stand(level, TolkienEntities.ENTITY_TTM_GOBLIN.get(), spot, FOLK_TAG, null);
            if (goblin != null && !keyed) {
                goblin.addTag(KEY_TAG);
                keyed = true;
            }
        }
    }

    /**
     * Drops the hall's key from the one goblin carrying it, cut to the code the hall's own chest is set to.
     * <p>
     * The code is read off the chest rather than written here, which is the whole point: the template carries
     * the chest and the chest carries the code, so a hall furnished and saved again moves both together and
     * the key still opens the chest it was found in. A hall with no lockable chest in it has nothing to cut a
     * key to, and gives none up.
     */
    public static void dropKey(ServerLevel level, Mob goblin) {
        if (!goblin.entityTags().contains(KEY_TAG)) return;
        AABB hall = hallAt(level);
        if (hall == null) return;
        // Asked before the code is looked for: one key a hall, whether or not this one finds a chest to fit.
        if (!KEYED.add(keyOf(hall))) return;
        String code = chestCode(level, hall);
        if (code.isEmpty()) return;
        ItemStack key = new ItemStack(TolkienItems.MASTER_KEY.get());
        KeyItem.setKeyData(key, code, 3, true);
        stock(level, key, code);
        ItemEntity drop = new ItemEntity(level, goblin.getX(), goblin.getY() + 0.5D, goblin.getZ(), key);
        drop.setDefaultPickUpDelay();
        level.addFreshEntity(drop);
    }

    /**
     * Fills the key's own slots from the table its code names, so the key a goblin gives up carries the hoard
     * as well as the lock: a key coded DungeonKey is stocked from tolkienmobs:chests/dungeonkey.
     * <p>
     * The code is the table's name, and that is what lets one key serve more than one chest: several chests
     * set to the same code are one lock, and the table the code names is one hoard behind them. A full id
     * names a table anywhere; a bare name names one under the pack's own chests folder.
     * <p>
     * The roll happens as the key is made rather than as a chest is opened, because the drop is one moment
     * and a key that is already stocked cannot be rolled a second time - which is the whole guard needed,
     * where rolling on open would want a flag to tell an emptied key from a key nobody has opened yet. A code
     * that names no table leaves the key empty rather than failing: the lock still works without a hoard.
     */
    private static void stock(ServerLevel level, ItemStack key, String code) {
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        List<ItemStack> rolled = level.getServer().reloadableRegistries()
                .getLootTable(tableFor(code))
                .getRandomItems(params);
        if (rolled.isEmpty()) return;
        NonNullList<ItemStack> slots = NonNullList.withSize(KeyItemContainer.SLOTS, ItemStack.EMPTY);
        for (int slot = 0; slot < Math.min(slots.size(), rolled.size()); slot++) {
            slots.set(slot, rolled.get(slot));
        }
        key.set(TolkienDataComponents.ITEMSTACK_HANDLER.get(), ItemContainerContents.fromItems(slots));
    }

    /** The table a code names: the id itself where one is written, and the chests folder otherwise. */
    private static ResourceKey<LootTable> tableFor(String code) {
        Identifier id = code.contains(":")
                ? Identifier.tryParse(code)
                : Identifier.fromNamespaceAndPath("tolkienmobs", "chests/" + code.toLowerCase(Locale.ROOT));
        if (id == null) id = Identifier.fromNamespaceAndPath("tolkienmobs", "chests/dungeonkey");
        return ResourceKey.create(Registries.LOOT_TABLE, id);
    }

    /** The code the hall's lockable chest is set to, or an empty string when the hall has none set. */
    private static String chestCode(ServerLevel level, AABB hall) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = (int) hall.minY; y < (int) hall.maxY; y++) {
            for (int x = (int) hall.minX; x < (int) hall.maxX; x++) {
                for (int z = (int) hall.minZ; z < (int) hall.maxZ; z++) {
                    cursor.set(x, y, z);
                    if (!(level.getBlockEntity(cursor) instanceof LockableChestBlockEntity chest)) continue;
                    String code = chest.getKeyCode();
                    // The attachment's own default is a prompt rather than a code, and a chest nobody has set
                    // is a chest a key cannot be cut for.
                    if (code != null && !code.isEmpty() && !code.equals("Enter Code")) return code;
                }
            }
        }
        return "";
    }

    /** The hall as it stands in this world, or null when there is no door to measure it from. */
    @Nullable
    private static AABB hallAt(ServerLevel level) {
        BlockPos door = Pins.at(level, PIN);
        if (door == null || !level.hasChunkAt(door)) return null;
        Map<Long, Integer> ground = new HashMap<>();
        Integer surface = ground(level, door.getX(), door.getZ(), ground);
        if (surface == null) return null;
        Direction into = steepest(level, door.getX(), door.getZ(), surface, ground);
        return hallBox(level, door, into, surface - DROP);
    }

    /** One hall, as a number, for the set of halls that have already given their key up. */
    private static long keyOf(AABB hall) {
        return BlockPos.containing(hall.minX, hall.minY, hall.minZ).asLong();
    }

    /** Whether this player is carrying page 27's hunt, which is the only reason the fires are lit. */
    private static boolean onGoblinPage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.complete()) continue;
            if (GOBLINS_TARGET.equals(quest.target())) return true;
        }
        return false;
    }

    /** The hall's own box in the world, or null when its structure will not load. */
    @Nullable
    private static AABB hallBox(ServerLevel level, BlockPos door, Direction into, int floor) {
        StructureTemplate hall = level.getStructureManager().get(Identifier.parse(HALL)).orElse(null);
        if (hall == null) return null;
        Rotation rotation = rotationFor(into);
        Vec3i size = hall.getSize();
        int wide = wide(rotation, size);
        boolean turned = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
        int deep = turned ? size.getX() : size.getZ();
        // The corner the hall was laid from is the minimum of its turned box, so the box is that corner
        // carried out by the file's own size: the room the folk are looked for in is the room as built.
        BlockPos corner = hallCorner(door, into, floor, wide);
        return new AABB(corner.getX(), corner.getY(), corner.getZ(),
                corner.getX() + wide, corner.getY() + size.getY(), corner.getZ() + deep);
    }

    /** How wide the hall is once it is turned: the file's x unless it is lying on its side. */
    private static int wide(Rotation rotation, Vec3i size) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90
                ? size.getZ() : size.getX();
    }

    /** The near corner of the hall's floor course: one course below the hall's floor, at its near edge. */
    private static BlockPos hallCorner(BlockPos door, Direction into, int floor, int wide) {
        return door.relative(into, HALL_BACK)
                .relative(into.getClockWise(), (wide - 1) / 2)
                .atY(floor - 1);
    }

    /**
     * The hall's log bench, found by walking the room it was saved in rather than by an offset written
     * here: the hall is furnished by hand, so where its bench is is the file's business and not this
     * class's, and a second save that moves it moves Ulmug with it.
     */
    @Nullable
    private static BlockPos bench(ServerLevel level, AABB hall) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = (int) hall.minY; y < (int) hall.maxY; y++) {
            for (int x = (int) hall.minX; x < (int) hall.maxX; x++) {
                for (int z = (int) hall.minZ; z < (int) hall.maxZ; z++) {
                    cursor.set(x, y, z);
                    if (level.getBlockState(cursor).is(TolkienBlocks.LOG_BENCH.get())) return cursor.immutable();
                }
            }
        }
        return null;
    }

    /** A spot beside a seat a body can stand in, or null when the bench is against a wall on every side. */
    @Nullable
    private static BlockPos beside(ServerLevel level, BlockPos seat) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            for (int lift = 0; lift <= 2; lift++) {
                BlockPos spot = seat.relative(side).above(lift);
                if (open(level, spot)) return spot;
            }
        }
        return null;
    }

    /** A spot on the hall's own floor for one of the folk, or null when the room is odd all over. */
    @Nullable
    private static BlockPos floor(ServerLevel level, AABB hall) {
        int y = (int) hall.minY + 1;
        int wide = Math.max(1, (int) hall.maxX - (int) hall.minX - 2);
        int deep = Math.max(1, (int) hall.maxZ - (int) hall.minZ - 2);
        for (int tries = 0; tries < 24; tries++) {
            BlockPos spot = new BlockPos(
                    (int) hall.minX + 1 + level.getRandom().nextInt(wide), y,
                    (int) hall.minZ + 1 + level.getRandom().nextInt(deep));
            if (open(level, spot)) return spot;
        }
        return null;
    }

    /** Whether a body can stand here: air in the spot and over it, and something solid under it. */
    private static boolean open(ServerLevel level, BlockPos spot) {
        if (!level.getBlockState(spot).isAir()) return false;
        if (!level.getBlockState(spot.above()).isAir()) return false;
        BlockState under = level.getBlockState(spot.below());
        return !under.isAir() && !under.getFluidState().is(Fluids.WATER);
    }

    /** Stands one body at a spot, named when it is somebody, tagged so the next look finds it. */
    @Nullable
    private static Mob stand(ServerLevel level, EntityType<? extends Mob> type, @Nullable BlockPos spot,
                             String tag, @Nullable String name) {
        if (spot == null) return null;
        Mob body = type.create(level, EntitySpawnReason.EVENT);
        if (body == null) return null;
        body.snapTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        body.setPersistenceRequired();
        body.addTag(tag);
        if (name != null) {
            body.setCustomName(Component.literal(name).withStyle(ChatFormatting.GOLD));
            body.setCustomNameVisible(true);
        }
        level.addFreshEntity(body);
        return body;
    }

    /** The quarter turn that puts the file's plus-z, which is south, along the way into this hill. */
    private static Rotation rotationFor(Direction into) {
        return switch (into) {
            case SOUTH -> Rotation.NONE;
            case WEST -> Rotation.CLOCKWISE_90;
            case NORTH -> Rotation.CLOCKWISE_180;
            default -> Rotation.COUNTERCLOCKWISE_90;
        };
    }

    /** Whether the plan puts cave air at one door-relative cell, before the hill is consulted. */
    private static boolean carve(int a, int s, int y, int doorFloor, int floor, Spur spur) {
        int sideways = Math.abs(s);
        if (a >= 0 && a <= 2 && sideways <= MOUTH_SIDE
                && y >= doorFloor && y <= doorFloor + MOUTH_HEIGHT - 1) {
            return true;
        }
        if (a >= 1 && a <= RAMP && sideways <= MOUTH_SIDE) {
            int low = floor + DROP - (a - 1) * DROP / (RAMP - 1);
            if (y >= low && y <= low + MOUTH_HEIGHT - 1) return true;
        }
        if (a >= RAMP - 2 && a <= HALL_BACK && sideways <= LANDING_SIDE
                && y >= floor && y <= floor + LANDING_HEIGHT - 1) {
            return true;
        }
        if (a >= HALL_BACK && a <= HALL_FRONT && hall(a, s, y - floor)) return true;
        if (!cavern(a, s, y, floor)) return false;
        // A stalactite or a stalagmite is rock the cut leaves standing rather than a block it places:
        // the hillside's own material comes down from the ceiling and up off the floor.
        return spur == null || !spur.covers(y);
    }

    /** A chamber is several overlapping lobes, so no room is one ellipse with a dome on it. */
    private static boolean cavern(int a, int s, int y, int floor) {
        if (y < floor || y >= floor + CHAMBER_TALL) return false;
        // Rubble on the floor, so a cavern's bottom is not a poured slab.
        if (y == floor && hash(a >> 1, s >> 1, 505) < 40) return false;
        // The hall's own walls win: a chamber may not break into the room, only a tunnel may.
        if (a < HALL_BACK - 2 || a > HALL_FRONT + 2 || Math.abs(s) > HALL_SIDE + HALL_CLEAR) {
            for (Lobe lobe : FLANK) {
                if (swell(a, s, y, floor, lobe.a(), lobe.s(), lobe.ra(), lobe.rs(), lobe.height())) return true;
                if (swell(a, s, y, floor, lobe.a(), -lobe.s(), lobe.ra(), lobe.rs(), lobe.height())) return true;
            }
            for (Lobe lobe : BACK) {
                if (swell(a, s, y, floor, lobe.a(), lobe.s(), lobe.ra(), lobe.rs(), lobe.height())) return true;
            }
        }
        // And the ways out of the hall, wandering out of its wall rather than punched through it.
        if (a >= DOOR_BACK && a <= DOOR_FRONT) {
            for (Lobe ball : DOORWAY) {
                if (swell(a, s, y, floor, ball.a(), ball.s(), ball.ra(), ball.rs(), ball.height())) return true;
                if (swell(a, s, y, floor, ball.a(), -ball.s(), ball.ra(), ball.rs(), ball.height())) return true;
            }
        }
        // The flank tunnels are only in one stretch of the plan, so the rest of it never looks at them.
        if (a >= TUNNEL_BACK && a <= TUNNEL_FRONT && Math.abs(s) >= TUNNEL_INSIDE) {
            for (Lobe ball : TUNNEL) {
                if (swell(a, s, y, floor, ball.a(), ball.s(), ball.ra(), ball.rs(), ball.height())) return true;
                if (swell(a, s, y, floor, ball.a(), -ball.s(), ball.ra(), ball.rs(), ball.height())) return true;
            }
        }
        return false;
    }

    /**
     * The hall: squared in plan, because it is the room somebody made, but hewn rather than machined -
     * the four corners cut at 45 degrees, the ceiling vaulted in from the walls and both ends, and the
     * faces worn a block either way by the cell hash, so they are dressed rock and not a milled wall.
     */
    private static boolean hall(int a, int s, int level) {
        if (a < HALL_BACK || a > HALL_FRONT || level < 0 || level >= HALL_HEIGHT) return false;
        int wear = hash(a >> 1, s >> 1, level >> 1);
        int side = HALL_SIDE + (wear < 40 ? 1 : 0) - (wear > 236 ? 1 : 0);
        int inset = level >= HALL_VAULT ? HALL_INSET : 0;
        if (Math.abs(s) > side - inset) return false;
        int along = Math.min(a - HALL_BACK, HALL_FRONT - a) - inset;
        if (along < 0) return false;
        // Corners cut at 45 degrees, three blocks in, so the room is not a box in plan.
        return along + (side - Math.abs(s)) >= HALL_CUT;
    }

    /**
     * One lobe: radii pinched near its ceiling, swelling round the compass by three sine terms, then
     * worn off true by the cell hash. The terms are {@code tools/cave_section.py}'s too.
     */
    private static boolean swell(int a, int s, int y, int floor, int centreA, int centreS,
                                 int reachA, int reachS, int height) {
        if (y < floor || y >= floor + height) return false;
        // Nothing this far outside a lobe can be inside it however far its wall wanders, so it is turned
        // away before the trigonometry: the plan is wide and asks this of every cell in it.
        if (Math.abs(a - centreA) > reachA * 1.8 + 1 || Math.abs(s - centreS) > reachS * 1.8 + 1) {
            return false;
        }
        double share = (y - floor) / (double) (height - 1);
        double shrink = share <= 0.4 ? 1.0 : 1.0 - 0.5 * ((share - 0.4) / 0.6);
        double da = a - centreA;
        double ds = s - centreS;
        double angle = Math.atan2(ds, da);
        shrink *= 1.0 + 0.26 * Math.sin(2.0 * angle + 2.1)
                + 0.17 * Math.sin(3.0 * angle + 0.6)
                + 0.09 * Math.sin(5.0 * angle - 1.3);
        double ra = reachA * shrink;
        double rs = reachS * shrink;
        if (ra <= 0 || rs <= 0) return false;
        // The wall wanders: one wobble over four blocks and a finer one over two, so it is rock rather
        // than a swept surface. The hash is the same number in the drawing tool, so the shapes agree.
        double wander = (hash(a >> 2, s >> 2, y >> 2) - 128) / 128.0 * COARSE
                + (hash(a >> 1, s >> 1, y >> 1) - 128) / 128.0 * FINE;
        return da * da / (ra * ra) + ds * ds / (rs * rs) <= 1.0 + wander;
    }

    /**
     * A deterministic 0..255 from a cell. Integer arithmetic only and no state, because
     * {@code tools/cave_section.py} has to compute the same number: the drawing is only evidence of the
     * cut while both sides agree, so a mismatch here is a silent lie in the plan.
     */
    private static int hash(int a, int s, int y) {
        int h = a * 0x27D4EB2D ^ s * 0x165667B1 ^ y * 0x9E3779B1;
        h ^= h >>> 15;
        h *= 0x85EBCA6B;
        h ^= h >>> 13;
        h *= 0xC2B2AE35;
        h ^= h >>> 16;
        return h & 0xFF;
    }

    /** What one column of a cavern keeps: how far a stalactite hangs, how far a stalagmite stands. */
    private record Spur(int low, int high, int hang, int stand) {
        boolean covers(int y) {
            return (hang > 0 && y >= high - hang) || (stand > 0 && y < low + stand);
        }
    }

    /**
     * The spur this column keeps, or null for most columns. Only rock is taken out, so a stalactite is
     * ceiling left hanging and a stalagmite is floor left standing - no material to pick, and nothing to
     * place. Both are chosen off the column's own hash, so the same cave grows the same spurs.
     */
    @Nullable
    private static Spur spur(int a, int s, int floor) {
        int hang = hash(a >> 1, s >> 1, 101) < SPUR_TOP
                ? 1 + hash(a >> 1, s >> 1, 202) % SPUR_LONG : 0;
        int stand = hash(a >> 1, s >> 1, 303) < SPUR_LOW
                ? 1 + hash(a >> 1, s >> 1, 404) % SPUR_LONG : 0;
        if (hang == 0 && stand == 0) return null;
        int low = floor;
        while (low < floor + CHAMBER_TALL && !cavern(a, s, low, floor)) low++;
        int high = low;
        while (high < floor + CHAMBER_TALL && cavern(a, s, high, floor)) high++;
        return high > low ? new Spur(low, high, hang, stand) : null;
    }

    /** The way into the hill: the compass direction whose ground rises most over the next stretch. */
    private static Direction steepest(ServerLevel level, int x, int z, int surface, Map<Long, Integer> cache) {
        Direction best = Direction.SOUTH;
        double steepest = Double.NEGATIVE_INFINITY;
        for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            double sum = 0;
            int count = 0;
            for (int a = 6; a <= 26; a += 4) {
                for (int s = -4; s <= 4; s += 4) {
                    int px = x + direction.getStepX() * a - direction.getStepZ() * s;
                    int pz = z + direction.getStepZ() * a + direction.getStepX() * s;
                    Integer top = ground(level, px, pz, cache);
                    if (top == null) continue;
                    sum += top - surface;
                    count++;
                }
            }
            if (count == 0) continue;
            double mean = sum / count;
            if (mean > steepest) {
                steepest = mean;
                best = direction;
            }
        }
        return best;
    }

    /** Asks for every chunk the cut reaches, so the ground under it exists before it is measured. */
    private static void reach(ServerLevel level, int x, int z, int stepX, int stepZ, int acrossX, int acrossZ) {
        int lowX = Integer.MAX_VALUE;
        int lowZ = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;
        int highZ = Integer.MIN_VALUE;
        for (int a : new int[]{-OUTSIDE, FAR}) {
            for (int s : new int[]{-FAR_SIDE, FAR_SIDE}) {
                int px = x + stepX * a + acrossX * s;
                int pz = z + stepZ * a + acrossZ * s;
                lowX = Math.min(lowX, px);
                lowZ = Math.min(lowZ, pz);
                highX = Math.max(highX, px);
                highZ = Math.max(highZ, pz);
            }
        }
        for (int cx = lowX >> 4; cx <= highX >> 4; cx++) {
            for (int cz = lowZ >> 4; cz <= highZ >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    /** The top solid block of a column, cached: the cut asks for the same columns many times. */
    @Nullable
    private static Integer ground(ServerLevel level, int x, int z, Map<Long, Integer> cache) {
        long key = ((long) x << 32) ^ (z & 0xFFFFFFFFL);
        Integer known = cache.get(key);
        if (known != null) return known;
        for (int y = level.getMaxY() - 1; y > level.getMinY(); y--) {
            BlockState state = level.getBlockState(new BlockPos(x, y, z));
            if (state.isAir() || state.getFluidState().is(Fluids.WATER)) continue;
            cache.put(key, y);
            return y;
        }
        return null;
    }
}
