package net.slimediamond.atom.chatbridge.irc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeMessage;
import net.slimediamond.atom.chatbridge.EventType;
import org.kitteh.irc.client.library.element.Channel;

public class IRCBridgeEndpoint implements BridgeEndpoint {
    private Channel channel;
    private String identifier;
    private int id;

    public IRCBridgeEndpoint(Channel channel, String identifier, int id) {
        this.channel = channel;
        this.identifier = identifier;
        this.id = id;
    }

    @Override
    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        channel.sendMessage("[" + source.getShortName() + "] " + message.username() + ": " + message.content());
    }

    @Override
    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        if (eventType == EventType.JOIN) {
            channel.sendMessage("[" + source.getShortName() + "] " + username + " joined " + source.getChannelName());
        } else if (eventType == EventType.LEAVE) {
            channel.sendMessage("[" + source.getShortName() + "] " + username + " left " + source.getChannelName());
        } else if (eventType == EventType.QUIT) {
            channel.sendMessage("[" + source.getShortName() + "] " + username + " quit (" + comment + ")");
        } else if (eventType == EventType.NAME_CHANGE) {
            channel.sendMessage("[" + source.getShortName() + "] " + username + " is now known as " + comment);
        }
    }

    @Override
    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {
        channel.sendMessage("[" + source.getShortName() + "] * " + message.username() + " " + message.content());

    }

    @Override
    public String getName() {
        return "IRC";
    }

    @Override
    public String getShortName() {
        return "IRC";
    }

    @Override
    public String getType() {
        return "irc";
    }

    @Override
    public String getChannelName() {
        return channel.getName();
    }

    @Override
    public String getUniqueIdentifier() {
        return this.identifier;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
