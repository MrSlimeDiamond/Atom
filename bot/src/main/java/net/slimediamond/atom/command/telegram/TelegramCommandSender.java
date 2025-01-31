package net.slimediamond.atom.command.telegram;

import net.slimediamond.atom.command.CommandSender;
import net.slimediamond.telegram.User;

import java.sql.SQLException;

public class TelegramCommandSender implements CommandSender {
    private User sender;

    public TelegramCommandSender(User sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        String username = this.sender.getUsername();
        if (username == null) {
            return this.sender.getFirstName();
        } else {
            return username;
        }
    }

    // TODO
    @Override
    public boolean isAdmin() throws SQLException {
        return false;
    }

    public long getId() {
        return sender.getId();
    }

    public User getRaw() {
        return this.sender;
    }
}
