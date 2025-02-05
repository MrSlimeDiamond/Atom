package net.slimediamond.telegram.event;

import net.slimediamond.telegram.entity.Chat;
import net.slimediamond.telegram.TelegramClient;
import net.slimediamond.telegram.entity.User;

public class UserAddedToChatEvent extends ChatEvent {
    private User adder;
    private User newUser;

    public UserAddedToChatEvent(TelegramClient client, Chat chat, User user, User newUser) {
        super(client, chat);
        this.adder = user;
        this.newUser = newUser;
    }

    public User getUser() {
        return this.adder;
    }

    public User getNewUser() {
        return this.newUser;
    }
}
