package net.zenoc.atom.ircbot;

import net.zenoc.atom.reference.IRCReference;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

import java.util.Arrays;

public class CommandEvent {
    ChannelMessageEvent event;
    boolean hidden;
    public CommandEvent(ChannelMessageEvent event, boolean hidden) {
        this.event = event;
        this.hidden = hidden;
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
}
