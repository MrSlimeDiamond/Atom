package net.slimediamond.telegram.events;

import net.slimediamond.telegram.Chat;
import net.slimediamond.telegram.MessageSender;

public class MessageReceivedEvent {
    private MessageSender sender;
    private Chat chat;
    private String text;

    public MessageReceivedEvent(MessageSender sender, Chat chat, String text) {
        this.sender = sender;
        this.chat = chat;
        this.text = text;
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
}
