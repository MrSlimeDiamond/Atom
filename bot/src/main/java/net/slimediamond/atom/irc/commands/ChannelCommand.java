package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.common.annotations.GetService;

import java.sql.SQLException;

public class ChannelCommand implements IRCCommandExecutor {
    @GetService
    private Database database;

    public void execute(IRCCommandContext event) {
        // channel add <channel>
        if (event.getArgs()[0].equals("add")) {
            if (event.getArgs().length == 1) {
                event.reply("Usage: channel add <channel>");
            } else {
                String channelName = event.getArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getArgs()[2];
                try {
                    database.addIRCChannel(channelName);
                    event.reply("Added channel!");
                } catch(SQLException e) {
                    event.reply("SQLException! Is the database down?");
                    throw new RuntimeException(e);
                }

            }
        } else if (event.getArgs()[0].equals("modify")) {
            if (event.getArgs().length == 1) {
                event.reply("Usage: channel modify <channel>");
                return;
            } else {
                String channelName = event.getArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getArgs()[2];

                if (event.getArgs().length == 2) {
                    event.reply("Usage: channel modify <channel> <autojoin>");
                    return;
                }

                if (event.getArgs()[2].equals("autojoin")) {
                    if (event.getArgs().length == 3) {
                        event.reply("Usage: channel modify <channel> autojoin on/off");
                    } else if (event.getArgs()[3].equals("on")) {
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
        } else if (event.getArgs()[0].equals("join")) {
            if (event.getArgs().length == 1) {
                event.reply("Usage: channel join <channel>");
            } else {
                String channelName = event.getArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getArgs()[2];
                IRC.client.addChannel(channelName);
                event.reply("Joined channel " + channelName);
            }
        } else if (event.getArgs()[0].equals("part")) {
            if (event.getArgs().length == 1) {
                event.reply("Usage: channel part <channel>");
            } else {
                String channelName = event.getArgs()[1];
                if (!channelName.startsWith("#")) channelName = "#" + event.getArgs()[2];
                IRC.client.removeChannel(channelName);
                event.reply("Left channel " + channelName);
            }
        }
    }
}
