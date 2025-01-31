package net.slimediamond.telegram.event;

import net.slimediamond.telegram.Chat;
import net.slimediamond.telegram.User;
import net.slimediamond.telegram.TelegramClient;

public class MessageReceivedEvent extends ChatEvent {
    private User sender;
    private Chat chat;
    private String text;
    private TelegramClient client;

    public MessageReceivedEvent(TelegramClient client, User sender, Chat chat, String text) {
        super(client, chat);
        this.client = client;
        this.sender = sender;
        this.chat = chat;
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public Chat getChat() {
        return chat;
    }

    public String getText() {
        return text;
    }

    public TelegramClient getClient() {
        return client;
    }
}
