package net.slimediamond.atom.command.irc;

import net.slimediamond.atom.command.*;
import net.slimediamond.atom.irc.McObotMessageParser;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

import java.util.Arrays;

public class IRCCommandContext implements CommandContext {
    private ChannelMessageEvent event;
    private CommandMetadata metadata;
    private McObotMessageParser mcObotMessageParser;
    private String[] args;
    private boolean hidden;
    private CommandManager commandManager;

    public IRCCommandContext(ChannelMessageEvent event, CommandMetadata metadata, McObotMessageParser mcObotMessageParser, String[] args, boolean hidden, CommandManager commandManager) {
        this.event = event;
        this.metadata = metadata;
        this.mcObotMessageParser = mcObotMessageParser;
        this.args = args;
        this.hidden = hidden;
        this.commandManager = commandManager;
    }
    @Override
    public CommandSender getSender() {
        String name;
        if (mcObotMessageParser.isChatMessage()) {
            name = mcObotMessageParser.getSenderUsername();
        } else {
            name = this.event.getActor().getNick();
        }
        return new IRCCommandSender(name, this.event.getActor());
    }

    @Override
    public String[] getArgs() {
        return this.args;
    }

    @Override
    public CommandMetadata getCommandMetadata() {
        return this.metadata;
    }

    @Override
    public void reply(String message) {
        if (this.hidden) {
            message = "# " + message;
        }
        this.event.sendReply(message);
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }
    
    public String getDesiredCommandUsername() {
        if (this.args.length == 0) {
            return this.getSender().getName();
        } else {
            return this.args[0]; // probably
        }
    }

    @Override
    public void sendUsage() {
        this.reply("Usage: " + metadata.getCommandUsage());
    }

    public String getChannelName() {
        return event.getChannel().getName();
    }
}
