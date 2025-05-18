package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.handler.ProcessHandler;
import com.greatorator.tolkienmobs.handler.interfaces.block.IProcess;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class CommonProxy {

    public MinecraftServer getMCServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public Level getClientWorld() {
        return null;
    }

    public boolean isCTRLKeyDown() {
        return false;
    }

    public Player getClientPlayer() {
        return null;
    }

    public void addProcess(IProcess iProcess) {
        ProcessHandler.addProcess(iProcess);
    }

    public void runSidedProcess(IProcess process) {
        ProcessHandler.addProcess(process);
    }

    @Deprecated //Local Multiplayer issue
    public void sendIndexedMessage(Player player, Component message, MessageSignature signature) {
        PacketHandler.sendIndexedMessage((ServerPlayer) player, message, signature);
    }

    @Deprecated //Local Multiplayer issue
    public void sendIndexedMessage(Player player, Component message, UUID sig) {
        PacketHandler.sendIndexedMessage((ServerPlayer) player, message, GeneralUtility.uuidToSig(sig));
    }

    public void setClipboardString(String text) {}
}