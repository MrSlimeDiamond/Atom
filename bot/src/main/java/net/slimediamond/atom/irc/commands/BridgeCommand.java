package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.util.number.NumberUtils;

import java.sql.SQLException;

public class BridgeCommand implements IRCCommandExecutor {
    @GetService
    private Database database;

    public void execute(IRCCommandContext ctx) {
        if (ctx.getArgs().length == 0) {
            ctx.reply("Usage: bridge <channel|pipe|blacklist>");
            return;
        } else
            if (ctx.getArgs()[0].equals("channel")) {
                if (ctx.getArgs()[1].equals("set")) {
                    // bridge set <discord id>
                    if (ctx.getArgs().length == 2) {
                        ctx.reply("Usage: bridge channel set <discord id>");
                        return;
                    } else {
                        try {
                            database.setIRCDiscordBridgeChannelID(ctx.getChannelName(), Long.parseLong(ctx.getArgs()[2]));
                            ctx.reply("Set Discord bridge channel!");
                        } catch (SQLException e) {
                            ctx.reply("SQLException! Is the database down? Tell an admin!");
                        }
                    }
                } else if (ctx.getArgs()[1].equals("unset")) {
                    try {
                        database.setIRCDiscordBridgeChannelID(ctx.getChannelName(), -1L);
                        ctx.reply("Unset Discord bridge channel!");
                    } catch (SQLException e) {
                        ctx.reply("SQLException! Is the database down? Tell an admin!");
                    }
                }
            } else if (ctx.getArgs()[0].equals("pipe")) {
            if (ctx.getArgs()[1].equals("on")) {
                try {
                    database.enableIRCPipe(ctx.getChannelName());
                    ctx.reply("Bridge status: on");
                } catch (SQLException e) {
                    ctx.reply("SQLException! Is the database down? Tell an admin!");
                    return;
                }
            } else {
                try {
                    database.disableIRCPipe(ctx.getChannelName());
                    ctx.reply("Bridge status: off");
                } catch (SQLException e) {
                    ctx.reply("SQLException! Is the database down? Tell an admin!");
                    return;
                }
            }
        } else if (ctx.getArgs()[0].equals("blacklist")) {
            if (ctx.getArgs().length == 1) {
                ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
            } else if (ctx.getArgs()[1].equals("add")) { // bridge blacklist add
                if (ctx.getArgs().length == 2) {
                    ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                } else if (ctx.getArgs()[2].equals("irc")) { // bridge blacklist add irc
                    if (ctx.getArgs().length == 3) {
                        ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else { // bridge blacklist add irc user
                        String user = ctx.getArgs()[3];
                        try {
                            database.addUserIRCBridgeBlacklist(user);
                            ctx.reply("Added user to blacklist");
                        } catch(SQLException e) {
                            ctx.reply("SQLException! Is the database down?");
                            throw new RuntimeException(e);
                        }
                    }
                } else if (ctx.getArgs()[2].equals("discord")) { // bridge blacklist add discord
                    if (ctx.getArgs().length == 3) {
                        ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else {
                        if (!NumberUtils.isNumeric(ctx.getArgs()[3])) {
                            ctx.reply("Usage: bridge blacklist add discord <user ID>");
                        } else {
                            long id = Long.parseLong(ctx.getArgs()[3]);
                            try {
                                database.addUserDiscordBridgeBlacklist(id);
                                ctx.reply("Added user to blacklist");
                            } catch(SQLException e) {
                                ctx.reply("SQLException! Is the database down?");
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                }
            } else if (ctx.getArgs()[1].equals("remove")) { // bridge blacklist add
                if (ctx.getArgs().length == 2) {
                    ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                } else if (ctx.getArgs()[2].equals("irc")) { // bridge blacklist add irc
                    if (ctx.getArgs().length == 3) {
                        ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else { // bridge blacklist add irc user
                        String user = ctx.getArgs()[3];
                        try {
                            database.removeUserIRCBridgeBlacklist(user);
                            ctx.reply("Removed user from blacklist");
                        } catch(SQLException e) {
                            ctx.reply("SQLException! Is the database down?");
                            throw new RuntimeException(e);
                        }
                    }
                } else if (ctx.getArgs()[2].equals("discord")) { // bridge blacklist add discord
                    if (ctx.getArgs().length == 3) {
                        ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                    } else {
                        if (!NumberUtils.isNumeric(ctx.getArgs()[3])) {
                            ctx.reply("Usage: bridge blacklist add discord <user ID>");
                        } else {
                            long id = Long.parseLong(ctx.getArgs()[3]);
                            try {
                                database.removeUserDiscordBridgeBlacklist(id);
                                ctx.reply("Removed user from blacklist");
                            } catch(SQLException e) {
                                ctx.reply("SQLException! Is the database down?");
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    ctx.reply("Usage: bridge blacklist <add|remove> <discord|irc> <user>");
                }
            }
        }
    }
}
