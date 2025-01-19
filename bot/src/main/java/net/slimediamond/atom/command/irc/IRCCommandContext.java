package net.slimediamond.atom.command.irc;

import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.command.CommandPlatform;
import net.slimediamond.atom.command.CommandSender;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

import java.util.Arrays;

public class IRCCommandContext implements CommandContext {
    private ChannelMessageEvent event;
    private CommandMetadata metadata;
    private String[] args;

    public IRCCommandContext(ChannelMessageEvent event, CommandMetadata metadata, String[] args) {
        this.event = event;
        this.metadata = metadata;
        this.args = args;
    }
    @Override
    public CommandSender getSender() {
        return new IRCCommandSender(this.event.getActor());
    }

    @Override
    public String[] getArgs() {
        return this.args;
    }

    @Override
    public CommandPlatform getPlatform() {
        return CommandPlatform.IRC;
    }

    @Override
    public CommandMetadata getCommandMetadata() {
        return this.metadata;
    }

    @Override
    public void reply(String message) {
        this.event.sendReply(message);
    }
}
