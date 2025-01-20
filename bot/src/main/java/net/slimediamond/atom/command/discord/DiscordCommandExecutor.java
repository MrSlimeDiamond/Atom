package net.slimediamond.atom.command.discord;

import net.slimediamond.atom.command.CommandExecutor;

import java.io.IOException;

@FunctionalInterface
public interface DiscordCommandExecutor extends CommandExecutor {
    void execute(DiscordCommandContext context) throws Exception;
}
