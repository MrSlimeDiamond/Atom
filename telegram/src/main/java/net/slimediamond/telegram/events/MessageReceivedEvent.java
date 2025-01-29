package net.slimediamond.telegram.events;

import net.slimediamond.telegram.Chat;
import net.slimediamond.telegram.MessageSender;
import net.slimediamond.telegram.TelegramClient;

public class MessageReceivedEvent {
    private MessageSender sender;
    private Chat chat;
    private String text;
    private TelegramClient client;

    public MessageReceivedEvent(MessageSender sender, Chat chat, String text, TelegramClient client) {
        this.sender = sender;
        this.chat = chat;
        this.text = text;
        this.client = client;
    }

    public MessageSender getSender() {
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
