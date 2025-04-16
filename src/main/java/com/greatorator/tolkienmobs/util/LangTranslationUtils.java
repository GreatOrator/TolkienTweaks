package com.greatorator.tolkienmobs.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.text.NumberFormat;

import static net.minecraft.ChatFormatting.*;

public class LangTranslationUtils {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    private static final String
            BaseDamage = "damage.base",
            BowFasterPull = "bow_speed.faster",
            BowSlowerPull = "bow_speed.slower",
            InfiniteAmmo = "ammo.infinite",
            RemainFuel = "fuel.remaining",
            ShootingPower = "ranged.power";

    public static Component infiniteAmmo() {return i18n(BLUE, InfiniteAmmo);}

    public static Component bowFasterPull(float speed) {return i18n(DARK_GREEN, BowFasterPull, speed);}

    public static Component bowSlowerPull(float speed) {return i18n(RED, BowSlowerPull, speed);}

    public static Component shootingPower(float power) {return i18n(DARK_GREEN, ShootingPower, power);}

    public static Component baseDamage(int damage) {return i18n(DARK_GREEN, BaseDamage, damage);}

    public static Component remainingFuel(int fuel) {return i18n(DARK_RED, RemainFuel, nf.format(fuel));}

    public static Component i18n(ChatFormatting color, String text, Object... args) {
        if(args == null) {args = new Object[0];}
        MutableComponent result = MutableComponent.create(new TranslatableContents(String.format("tooltip.tolkienmobs.%s", text), null, args));
        return result.withStyle(color);
    }

    public static Component i18n(String text, Object... args) {
        if(args == null) {args = new Object[0];}
        MutableComponent result = MutableComponent.create(new TranslatableContents(String.format("tooltip.tolkienmobs.%s", text), null, args));
        return result.withStyle(GRAY);
    }

    public static Component clientMessage(ChatFormatting color, String text, Object... args) {
        if(args == null) {args = new Object[0];}
        MutableComponent result = MutableComponent.create(new TranslatableContents(String.format("message.tolkienmobs.%s", text), null, args));
        return result.withStyle(color);
    }

    public static Component clientMessage(String text, Object... args) {
        if(args == null) {args = new Object[0];}
        MutableComponent result = MutableComponent.create(new TranslatableContents(String.format("message.tolkienmobs.%s", text), null, args));
        return result.withStyle(WHITE);
    }
}