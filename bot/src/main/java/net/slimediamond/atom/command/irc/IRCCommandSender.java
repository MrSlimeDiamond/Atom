package net.slimediamond.atom.command.irc;

import net.slimediamond.atom.Atom;
import net.slimediamond.atom.command.CommandSender;
import net.slimediamond.atom.database.Database;
import org.kitteh.irc.client.library.element.User;

import java.sql.SQLException;

public class IRCCommandSender implements CommandSender {
    private String name;
    private User user;
    private Database database;

    public IRCCommandSender(String name, User user) {
        this.name = name;
        this.user = user;

        this.database = Atom.getServiceManager().getInstance(Database.class);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isAdmin() throws SQLException {
        return database.isIRCAdmin(user);
    }

    public User getRaw() {
        return this.user;
    }
}
