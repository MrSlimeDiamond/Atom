package net.zenoc.atom.ircbot;

import net.zenoc.atom.reference.IRCReference;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

import java.util.Arrays;

public class CommandEvent {
    ChannelMessageEvent event;
    McObotMessageParser messageParser;
    boolean hidden;
    public CommandEvent(ChannelMessageEvent event, boolean hidden) {
        this.event = event;
        this.hidden = hidden;
        this.messageParser = new McObotMessageParser(this.getUser(), this.event.getMessage());
    }
    public void reply(String text) {
        if (hidden) {
            event.getChannel().sendMessage("# " + text);
        } else {
            event.getChannel().sendMessage(text);
        }
    }

    public User getUser() {
        return event.getActor();
    }

    public Channel getChannel() {
        return event.getChannel();
    }

    public Client getClient() {
        return event.getClient();
    }

    public String[] getCommandArgs() {
        String[] args = event.getMessage().split(IRCReference.prefix)[1].split(" ");
        return Arrays.copyOfRange(args, 1, args.length);
    }

    public String getCommandSender() {

        if (this.messageParser.isChatMessage()) {
            // Try to parse the command sender from the chat message
            return this.event.getMessage().split("<")[1].split(">")[0].replaceAll("\\*", "");
            // TODO: Discord
        } else {
            return this.getUser().getNick();
        }
    }

    public String getDesiredCommandUsername() {
        if (this.getCommandArgs().length == 0) {
            return this.getCommandSender();
        } else {
            return this.getCommandArgs()[0];
        }
    }
}
