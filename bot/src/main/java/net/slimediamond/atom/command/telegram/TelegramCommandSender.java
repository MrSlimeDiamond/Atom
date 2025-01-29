package net.slimediamond.atom.command.telegram;

import net.slimediamond.atom.command.CommandSender;
import net.slimediamond.telegram.MessageSender;

import java.sql.SQLException;

public class TelegramCommandSender implements CommandSender {
    private MessageSender sender;

    public TelegramCommandSender(MessageSender sender) {
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

    public MessageSender getRaw() {
        return this.sender;
    }
}
