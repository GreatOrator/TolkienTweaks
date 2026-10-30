package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.client.FactionLedgerScreen;
import com.greatorator.tolkienmobs.handler.ProcessHandlerClient;
import com.greatorator.tolkienmobs.handler.interfaces.block.IProcess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientProxy extends CommonProxy {
    @Override
    public MinecraftServer getMCServer() {
        return super.getMCServer();
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public boolean isCTRLKeyDown() {
        return Minecraft.getInstance().hasControlDown();
    }

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public void addProcess(IProcess iProcess) {
        ProcessHandlerClient.addProcess(iProcess);
    }

    @Override
    public void runSidedProcess(IProcess process) {
        ProcessHandlerClient.addProcess(process);
    }

    /**
     * The chat now keeps its own display queue: a message added with a signature is folded,
     * re-wrapped and trimmed by {@code ChatComponent}, and an older message with the same
     * signature is retired through {@code ChatComponent#deleteMessage}. The hand-rolled loop over
     * {@code allMessages} and {@code trimmedMessages} that rebuilt the chat here had no access to
     * the new {@code GuiMessage.Line} parent, so it has been dropped.
     */
    @Override
    public void sendIndexedMessage(Player player, Component message, MessageSignature signature) {
        Minecraft mc = Minecraft.getInstance();
        if (message == null || mc.gui == null) return;
        ChatComponent chat = mc.gui.getChat();
        deleteMessage(signature);
        chat.addPlayerMessage(message, signature, null);
    }

    public void deleteMessage(@Nullable MessageSignature signature) {
        Minecraft mc = Minecraft.getInstance();
        if (signature == null || mc.gui == null) return;
        mc.gui.getChat().deleteMessage(signature);
    }

    @Override
    public void setClipboardString(String text) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
    }

    /** The quest book opens the ledger on its Quests tab; the key opens it on the alliances. */
    @Override
    public void openQuestBook() {
        Minecraft.getInstance().setScreen(new FactionLedgerScreen(FactionLedgerScreen.Tab.QUESTS));
    }
}
