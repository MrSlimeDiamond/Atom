package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.util.number.NumberUtils;

import java.sql.SQLException;

public class BridgeCommand {
    @GetService
    private Database database;

    @Command(
            name = "bridge",
            description = "Manage chat bridges",
            usage = "bridge <channel|pipe>",
            adminOnly = true
    )
    public void bridgeCommand(CommandEvent event) {
        if (event.getCommandArgs().length == 0) {
            event.reply("Usage: bridge <channel|pipe|blacklist>");
            return;
        } else
            if (event.getCommandArgs()[0].equals("channel")) {
                if (event.getCommandArgs()[1].equals("set")) {
                    // bridge set <discord id>
                    if (event.getCommandArgs().length == 2) {
                        event.reply("Usage: bridge channel set <discord id>");
                        return;
                    } else {
                        try {
                            database.setIRCDiscordBridgeChannelID(event.getChannel().getName(), Long.parseLong(event.getCommandArgs()[2]));
                            event.reply("Set Discord bridge channel!");
                        } catch (SQLException e) {
                            event.reply("SQLException! Is the database down? Tell an admin!");
                        }
                    }
                } else if (event.getCommandArgs()[1].equals("unset")) {
                    try {
                        database.setIRCDiscordBridgeChannelID(event.getChannel().getName(), -1L);
                        event.reply("Unset Discord bridge channel!");
                    } catch (SQLException e) {
                        event.reply("SQLException! Is the database down? Tell an admin!");
                    }
                }
            } else if (event.getCommandArgs()[0].equals("pipe")) {
            if (event.getCommandArgs()[1].equals("on")) {
                try {
                    database.enableIRCPipe(event.getChannel().getName());
                    event.reply("Bridge status: on");
                } catch (SQLException e) {
                    event.reply("SQLException! Is the database down? Tell an admin!");
                    return;
                }
            } else {
                try {
                    database.disableIRCPipe(event.getChannel().getName());
                    event.reply("Bridge status: off");
                } catch (SQLException e) {
                    event.reply("SQLException! Is the database down? Tell an admin!");
                    return;
                }
            }
        } else if (event.getCommandArgs()[0].equals("blacklist")) {
            if (event.getCommandArgs().length == 1) {
                event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
            } else if (event.getCommandArgs()[1].equals("add")) { // bridge blacklist add
                if (event.getCommandArgs().length == 2) {
                    event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                } else if (event.getCommandArgs()[2].equals("irc")) { // bridge blacklist add irc
                    if (event.getCommandArgs().length == 3) {
                        event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else { // bridge blacklist add irc user
                        String user = event.getCommandArgs()[3];
                        try {
                            database.addUserIRCBridgeBlacklist(user);
                            event.reply("Added user to blacklist");
                        } catch(SQLException e) {
                            event.reply("SQLException! Is the database down?");
                            throw new RuntimeException(e);
                        }
                    }
                } else if (event.getCommandArgs()[2].equals("discord")) { // bridge blacklist add discord
                    if (event.getCommandArgs().length == 3) {
                        event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else {
                        if (!NumberUtils.isNumeric(event.getCommandArgs()[3])) {
                            event.reply("Usage: bridge blacklist add discord <user ID>");
                        } else {
                            long id = Long.parseLong(event.getCommandArgs()[3]);
                            try {
                                database.addUserDiscordBridgeBlacklist(id);
                                event.reply("Added user to blacklist");
                            } catch(SQLException e) {
                                event.reply("SQLException! Is the database down?");
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                }
            } else if (event.getCommandArgs()[1].equals("remove")) { // bridge blacklist add
                if (event.getCommandArgs().length == 2) {
                    event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                } else if (event.getCommandArgs()[2].equals("irc")) { // bridge blacklist add irc
                    if (event.getCommandArgs().length == 3) {
                        event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else { // bridge blacklist add irc user
                        String user = event.getCommandArgs()[3];
                        try {
                            database.removeUserIRCBridgeBlacklist(user);
                            event.reply("Removed user from blacklist");
                        } catch(SQLException e) {
                            event.reply("SQLException! Is the database down?");
                            throw new RuntimeException(e);
                        }
                    }
                } else if (event.getCommandArgs()[2].equals("discord")) { // bridge blacklist add discord
                    if (event.getCommandArgs().length == 3) {
                        event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else {
                        if (!NumberUtils.isNumeric(event.getCommandArgs()[3])) {
                            event.reply("Usage: bridge blacklist add discord <user ID>");
                        } else {
                            long id = Long.parseLong(event.getCommandArgs()[3]);
                            try {
                                database.removeUserDiscordBridgeBlacklist(id);
                                event.reply("Removed user from blacklist");
                            } catch(SQLException e) {
                                event.reply("SQLException! Is the database down?");
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    event.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                }
            }
        }
    }
}
