package net.zenoc.atom.discordbot.commands;

import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CommandEvent;
import net.zenoc.atom.discordbot.annotations.Command;

import java.sql.SQLException;

public class TestCommands {
    @Command(name = "admin_only", description = "Admin only command : Only for admins", slashCommand = false, adminOnly = true, usage = "don't use it")
    public void adminOnlyTest(CommandEvent event) {
        event.reply("Working :)");
    }
    @Command(name = "get_log_chan", description = "Get the log channel", slashCommand = false, usage = "get_log_chan")
    public void getLogChannelCommand(CommandEvent event) throws SQLException {
        event.reply(String.valueOf(Atom.database.getServerLogChannel(event.getGuild().getIdLong())));
    }
}
