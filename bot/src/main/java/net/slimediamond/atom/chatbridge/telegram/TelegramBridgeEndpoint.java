package net.slimediamond.atom.chatbridge.telegram;

import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeMessage;
import net.slimediamond.atom.chatbridge.EventType;
import net.slimediamond.telegram.Chat;

public class TelegramBridgeEndpoint implements BridgeEndpoint {
    private Chat chat;
    private int id;

    public TelegramBridgeEndpoint(Chat chat, int id) {
        this.chat = chat;
        this.id = id;
    }

    @Override
    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        chat.sendMessage("[" + source.getShortName() + "] " + message.username() + ": " + message.content());

    }

    @Override
    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        if (eventType == EventType.JOIN) {
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
        chat.sendMessage("[" + source.getShortName() + "] * " + message.username() + " " + message.content());

    }

    @Override
    public void sendReconnectMessage() {
        chat.sendMessage("Chat bridge reconnected");
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
}
