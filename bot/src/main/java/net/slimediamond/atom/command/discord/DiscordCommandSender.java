package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.entities.User;
import net.slimediamond.atom.command.CommandSender;

public class DiscordCommandSender implements CommandSender {
    private String name;
    private User user;

    public DiscordCommandSender(String name, User user) {
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
