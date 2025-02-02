package net.slimediamond.atom.chatbridge.telegram;

import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeMessage;
import net.slimediamond.atom.chatbridge.EventType;
import net.slimediamond.atom.chatbridge.Netsplit;
import net.slimediamond.telegram.entity.Chat;

public class TelegramBridgeEndpoint implements BridgeEndpoint {
    private Chat chat;
    private int id;
    private boolean isEnabled;

    public TelegramBridgeEndpoint(Chat chat, int id, boolean isEnabled) {
        this.chat = chat;
        this.id = id;
        this.isEnabled = isEnabled;
    }

    @Override
    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        chat.sendMessage("[" + source.getShortName() + "] " + message.getUsername() + ": " + message.getContent());

    }

    @Override
    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        if (!this.isEnabled) return;
        if (eventType == EventType.CONNECT) {
            chat.sendMessage("Chat bridge reconnected.");
        } else if (eventType == EventType.DISCONNECT) {
            chat.sendMessage("Chat bridge disconnected");
        } else if (eventType == EventType.JOIN) {
            chat.sendMessage("[" + source.getShortName() + "] " + username + " joined " + source.getChannelName());
        } else if (eventType == EventType.LEAVE) {
            chat.sendMessage("[" + source.getShortName() + "] " + username + " left " + source.getChannelName());
        } else if (eventType == EventType.QUIT) {
            chat.sendMessage("[" + source.getShortName() + "] " + username + " quit (" + comment + ")");
        } else if (eventType == EventType.NAME_CHANGE) {
            chat.sendMessage("[" + source.getShortName() + "] " + username + " is now known as " + comment);
        }
    }

    @Override
    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        chat.sendMessage("[" + source.getShortName() + "] * " + message.getUsername() + " " + message.getContent());

    }

    @Override
    public void netsplitQuits(Netsplit netsplit, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        String quits = String.join(", ", netsplit.getQuits());
        chat.sendMessage("[" + source.getShortName() + "] Netsplit quits: " + quits);
    }

    @Override
    public void netsplitJoins(Netsplit netsplit, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        String joins = String.join(", ", netsplit.getJoins());
        chat.sendMessage("[" + source.getShortName() + "] Netsplit over, joins: " + joins);
    }

    @Override
    public String getAvatarUrl() {
        return chat.getPhoto().download();
    }

    @Override
    public String getName() {
        return "Telegram";
    }

    @Override
    public String getShortName() {
        return "TG";
    }

    @Override
    public String getType() {
        return "telegram";
    }

    @Override
    public String getChannelName() {
        return chat.getName();
    }

    @Override
    public String getUniqueIdentifier() {
        return String.valueOf(chat.getId());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
