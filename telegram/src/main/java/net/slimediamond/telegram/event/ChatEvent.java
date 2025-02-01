package net.slimediamond.telegram.event;

import net.slimediamond.telegram.entity.Chat;
import net.slimediamond.telegram.TelegramClient;

public class ChatEvent extends GenericEvent {
    protected Chat chat;
    public ChatEvent(TelegramClient client, Chat chat) {
        super(client);
        this.chat = chat;
    }

    public Chat getChat() {
        return this.chat;
    }
}
