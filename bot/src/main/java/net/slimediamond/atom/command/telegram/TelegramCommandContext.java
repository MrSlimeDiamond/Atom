package net.slimediamond.atom.command.telegram;

import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.telegram.Chat;

public class TelegramCommandContext implements CommandContext {
    private TelegramCommandSender sender;
    private String[] args;
    private CommandMetadata commandMetadata;
    private CommandManager commandManager;
    private Chat chat;

    public TelegramCommandContext(TelegramCommandSender sender, String[] args, CommandMetadata commandMetadata, CommandManager commandManager, Chat chat) {
        this.sender = sender;
        this.args = args;
        this.commandMetadata = commandMetadata;
        this.commandManager = commandManager;
        this.chat = chat;
    }

    @Override
    public TelegramCommandSender getSender() {
        return this.sender;
    }

    @Override
    public String[] getArgs() {
        return this.args;
    }

    @Override
    public CommandMetadata getCommandMetadata() {
        return this.commandMetadata;
    }

    @Override
    public void reply(String message) {
        this.chat.sendMessage(message);
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public void sendUsage() {
        this.reply("Usage: " + commandMetadata.getCommandUsage());
    }
}
