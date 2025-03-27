package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.entities.User;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.command.CommandSender;
import net.slimediamond.atom.data.Database;

import java.sql.SQLException;

// TODO: Extend JDA command sender
public class DiscordCommandSender implements CommandSender {
    private String name;
    private User user;
    private Database database;

    public DiscordCommandSender(String name, User user) {
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
        return database.isDiscordAdminByID(this.getId());
    }

    public Long getId() {
        return this.user.getIdLong();
    }

    public User getRaw() {
        return this.user;
    }
}
