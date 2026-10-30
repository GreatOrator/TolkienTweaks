package com.greatorator.tolkienmobs.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * One piece of mercenary work: an alliance pays for soldiers of another one to be killed, and the work
 * is done by carrying the contract until the count is met.
 * <p>
 * The whole contract is a value, so it rides on the item as a data component. That is what makes a
 * contract the paper it is written on: losing the item abandons the work, and two contracts are two
 * stacks because their terms differ.
 */
public record Contract(Alliance employer, Alliance target, int goal, int progress) {
    /** The range of kills one contract asks for. */
    public static final int MIN_GOAL = 4;
    public static final int MAX_GOAL = 8;

    /**
     * An alliance is saved by its id rather than its ordinal: reordering {@link Alliance} would
     * otherwise rewrite every contract already carried in a world.
     */
    private static final Codec<Alliance> ALLIANCE = Codec.STRING.xmap(Alliance::byId, Alliance::id);

    public static final Codec<Contract> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ALLIANCE.fieldOf("employer").forGetter(Contract::employer),
            ALLIANCE.fieldOf("target").forGetter(Contract::target),
            Codec.INT.optionalFieldOf("goal", MIN_GOAL).forGetter(Contract::goal),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(Contract::progress)
    ).apply(instance, Contract::new));

    public static final StreamCodec<FriendlyByteBuf, Contract> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, contract -> contract.employer().id(),
            ByteBufCodecs.STRING_UTF8, contract -> contract.target().id(),
            ByteBufCodecs.VAR_INT, Contract::goal,
            ByteBufCodecs.VAR_INT, Contract::progress,
            (employer, target, goal, progress) ->
                    new Contract(Alliance.byId(employer), Alliance.byId(target), goal, progress));

    public boolean complete() {
        return progress >= goal;
    }

    /** The same contract one kill further on, which is what a kill writes back onto the item. */
    public Contract advanced() {
        return new Contract(employer, target, goal, Math.min(goal, progress + 1));
    }

    /**
     * What the employer pays for the work. Kill value is reused so a contract pays what the same kills
     * would have paid twice over - the work is worth the standing, not a separate currency.
     */
    public int reward() {
        return goal * FactionRank.KILL_VALUE;
    }

    /** What each of the employer's rivals loses: half a reward, the same shape as handing tokens in. */
    public int betrayal() {
        return -reward() / 2;
    }

    /**
     * The contract a board posts for this player: work for the alliance they stand best with, against
     * one of its rivals, drawn fresh once a day.
     * <p>
     * The day and the player's own id seed the draw, so a board keeps offering the same work to the same
     * player until the sun comes up again, and two players reading the same board are offered different
     * work. Ties go to the alliance the day lands on, so a new player is not always handed the West.
     */
    public static Contract offer(ServerPlayer player) {
        long day = player.level().getGameTime() / 24000L;
        Alliance employer = Alliance.values()[Math.floorMod(day, Alliance.values().length)];
        int best = Integer.MIN_VALUE;
        for (Alliance alliance : Alliance.values()) {
            int standing = FactionStandings.standing(player, alliance);
            if (standing > best) {
                best = standing;
                employer = alliance;
            }
        }

        RandomSource random = RandomSource.create(day * 31L + player.getUUID().getLeastSignificantBits());
        List<Alliance> rivals = employer.rivals();
        Alliance target = rivals.get(random.nextInt(rivals.size()));
        int goal = MIN_GOAL + random.nextInt(MAX_GOAL - MIN_GOAL + 1);
        return new Contract(employer, target, goal, 0);
    }
}
