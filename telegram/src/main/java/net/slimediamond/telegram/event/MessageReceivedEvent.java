package net.slimediamond.telegram.event;

import net.slimediamond.telegram.entity.Chat;
import net.slimediamond.telegram.entity.Message;
import net.slimediamond.telegram.entity.User;
import net.slimediamond.telegram.TelegramClient;

public class MessageReceivedEvent extends ChatEvent {
    private User sender;
    private Chat chat;
    private Message message;
    private TelegramClient client;

    public MessageReceivedEvent(TelegramClient client, User sender, Chat chat, Message message) {
        super(client, chat);
        this.client = client;
        this.sender = sender;
        this.chat = chat;
        this.message = message;
    }

    public User getSender() {
        return this.sender;
    }

    public Chat getChat() {
        return this.chat;
    }

    public Message getMessage() {
        return this.message;
    }

    public TelegramClient getClient() {
        return this.client;
    }
}
