package net.slimediamond.atom.chatbridge.irc;

import net.slimediamond.atom.Atom;
import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeMessage;
import net.slimediamond.atom.chatbridge.EventType;
import net.slimediamond.atom.chatbridge.Netsplit;
import net.slimediamond.atom.database.Database;
import org.kitteh.irc.client.library.element.Channel;

import java.sql.SQLException;
import java.util.Arrays;

public class IRCBridgeEndpoint implements BridgeEndpoint {
    private Channel channel;
    private String identifier;
    private int id;
    private Database database;

    public IRCBridgeEndpoint(Channel channel, String identifier, int id) {
        this.channel = channel;
        this.identifier = identifier;
        this.id = id;
        this.database = Atom.getServiceManager().getInstance(Database.class);
    }

    @Override
    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        String text = message.getContent();
        if (!message.getFiles().isEmpty()) {
            String[] imageExtensions = {".png", ".jpg", ".jpeg", ".bmp"};

            if (message.getFiles().stream().anyMatch(file ->
                    Arrays.stream(imageExtensions).anyMatch(ext -> file.getName().endsWith(ext)))) {
                text = "[image] " + message.getContent();
            } else {
                text = "[file] " + message.getContent();
            }
        }
        channel.sendMessage("[" + source.getShortName() + "] " + message.getUsername() + ": " + text);
    }

    @Override
    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        if (eventType == EventType.JOIN) {
            String msg = username + " joined" + source.getChannelName();
            if (comment != null) {
                msg = username + " was added to " + source.getChannelName() + " by " + comment;
            }
            channel.sendMessage("[" + source.getShortName() + "] " + msg);
        } else if (eventType == EventType.LEAVE) {
            String msg = username + " left" + source.getChannelName();
            if (comment != null) {
                msg = username + " was removed from " + source.getChannelName() + " by " + comment;
            }
            channel.sendMessage("[" + source.getShortName() + "] " + msg);
        } else if (eventType == EventType.QUIT) {
            channel.sendMessage("[" + source.getShortName() + "] " + username + " quit (" + comment + ")");
        } else if (eventType == EventType.NAME_CHANGE) {
            channel.sendMessage("[" + source.getShortName() + "] " + username + " is now known as " + comment);
        }
    }

    @Override
    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {
        channel.sendMessage("[" + source.getShortName() + "] * " + message.getUsername() + " " + message.getContent());

    }

    @Override
    public void netsplitQuits(Netsplit netsplit, BridgeEndpoint source) {
        String quits = String.join(", ", netsplit.getQuits());
        channel.sendMessage("[" + source.getShortName() + "] Netsplit quits: " + quits);
    }

    @Override
    public void netsplitJoins(Netsplit netsplit, BridgeEndpoint source) {
        String joins = String.join(", ", netsplit.getJoins());
        channel.sendMessage("[" + source.getShortName() + "] Netsplit over, joins: " + joins);
    }

    @Override
    public String getAvatarUrl() {
        try {
            return database.getBridgedEndpointAvatar(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
