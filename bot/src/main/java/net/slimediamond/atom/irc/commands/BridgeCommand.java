package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.common.annotations.GetService;

public class BridgeCommand implements IRCCommandExecutor {
    @GetService
    private Database database;

    public void execute(IRCCommandContext ctx) {
        ctx.reply("TODO");
    }
}
