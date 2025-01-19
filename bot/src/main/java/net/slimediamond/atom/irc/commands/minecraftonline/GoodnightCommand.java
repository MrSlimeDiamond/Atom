package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.irc.annotations.Command;

public class GoodnightCommand implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) {
        // Special characters are funny
        ctx.reply("\u0001ACTION says goodnight\u0001");
    }
}
