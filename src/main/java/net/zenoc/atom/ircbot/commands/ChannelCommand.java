package net.zenoc.atom.ircbot.commands;

import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.GetService;
import net.zenoc.atom.database.Database;
import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.services.IRC;

import java.sql.SQLException;

public class ChannelCommand {
    @GetService
    private Database database;

    @Command(
            name = "channel",
            description = "Manage bot channels",
            usage = "channel <add|modify|join|part> <channel>",
            adminOnly = true
    )
    public void channelCommand(CommandEvent event) {
        // channel add <channel>
        if (event.getCommandArgs()[0].equals("add")) {
            if (event.getCommandArgs().length == 1) {
                event.reply("Usage: channel add <channel>");
            } else {
                String channelName = event.getCommandArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getCommandArgs()[2];
                try {
                    database.addIRCChannel(channelName);
                    event.reply("Added channel!");
                } catch(SQLException e) {
                    event.reply("SQLException! Is the database down?");
                    throw new RuntimeException(e);
                }

            }
        } else if (event.getCommandArgs()[0].equals("modify")) {
            if (event.getCommandArgs().length == 1) {
                event.reply("Usage: channel modify <channel>");
                return;
            } else {
                String channelName = event.getCommandArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getCommandArgs()[2];

                if (event.getCommandArgs().length == 2) {
                    event.reply("Usage: channel modify <channel> <autojoin>");
                    return;
                }

                if (event.getCommandArgs()[2].equals("autojoin")) {
                    if (event.getCommandArgs().length == 3) {
                        event.reply("Usage: channel modify <channel> autojoin on/off");
                    } else if (event.getCommandArgs()[3].equals("on")) {
                        try {
                            database.enableIRCAutojoin(channelName);
                            event.reply("Autojoin for " + channelName + " on");
                        } catch(SQLException e) {
                            event.reply("SQLException! Is the database down?");
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            database.disableIRCAutojoin(channelName);
                            event.reply("Autojoin for " + channelName + " off");
                        } catch(SQLException e) {
                            event.reply("SQLException! Is the database down?");
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        } else if (event.getCommandArgs()[0].equals("join")) {
            if (event.getCommandArgs().length == 1) {
                event.reply("Usage: channel join <channel>");
            } else {
                String channelName = event.getCommandArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getCommandArgs()[2];
                IRC.client.addChannel(channelName);
                event.reply("Joined channel " + channelName);
            }
        } else if (event.getCommandArgs()[0].equals("part")) {
            if (event.getCommandArgs().length == 1) {
                event.reply("Usage: channel part <channel>");
            } else {
                String channelName = event.getCommandArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getCommandArgs()[2];
                IRC.client.removeChannel(channelName);
                event.reply("Left channel " + channelName);
            }
        }
    }
}
