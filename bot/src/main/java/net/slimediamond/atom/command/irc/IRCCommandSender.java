package net.slimediamond.atom.command.irc;

import net.slimediamond.atom.command.CommandSender;
import org.kitteh.irc.client.library.element.User;

public class IRCCommandSender implements CommandSender {
    private User user;

    public IRCCommandSender(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return this.user.getName();
    }

    public User getRaw() {
        return this.user;
    }
}
