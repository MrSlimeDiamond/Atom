package net.slimediamond.atom.command.irc;

import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.CommandExecutor;

@FunctionalInterface
public interface IRCCommandExecutor extends CommandExecutor {
    void execute(IRCCommandContext context);
}
