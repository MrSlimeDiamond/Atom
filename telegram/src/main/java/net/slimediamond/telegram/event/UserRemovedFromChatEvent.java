package net.slimediamond.telegram.event;

import net.slimediamond.telegram.Chat;
import net.slimediamond.telegram.TelegramClient;
import net.slimediamond.telegram.User;

public class UserRemovedFromChatEvent extends ChatEvent {
    private User user;
    private User newUser;

    public UserRemovedFromChatEvent(TelegramClient client, Chat chat, User user, User newUser) {
        super(client, chat);
        this.user = user;
        this.newUser = newUser;
    }

    public User getUser() {
        return this.user;
    }

    public User getNewUser() {
        return this.newUser;
    }
}
