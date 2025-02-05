package net.slimediamond.atom.command.discord;

import net.slimediamond.atom.command.CommandExecutor;

@FunctionalInterface
public interface DiscordCommandExecutor extends CommandExecutor {
    void execute(DiscordCommandContext context) throws Exception;
}
