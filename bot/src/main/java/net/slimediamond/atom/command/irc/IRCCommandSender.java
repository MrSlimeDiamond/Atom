package net.slimediamond.atom.command.irc;

import net.slimediamond.atom.command.CommandSender;
import org.kitteh.irc.client.library.element.User;

public class IRCCommandSender implements CommandSender {
    private String name;
    private User user;

    public IRCCommandSender(String name, User user) {
        this.name = name;
        this.user = user;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public User getRaw() {
        return this.user;
    }
}
