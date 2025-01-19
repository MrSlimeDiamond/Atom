package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;

import java.util.StringJoiner;

public class HelpCommand implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (CommandMetadata command : ctx.getCommandManager().getCommands()) {
            // ignoring admin-only commands
            // TODO: Show admins admin-only commands
            if(!command.isAdminOnly()) {
                stringJoiner.add(command.getName());
            }
        }

        ctx.reply("My commands are: " + stringJoiner);
    }
}
