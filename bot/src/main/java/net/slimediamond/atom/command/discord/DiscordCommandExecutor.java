package net.slimediamond.atom.command.discord;

import net.slimediamond.atom.command.CommandExecutor;

public interface DiscordCommandExecutor extends CommandExecutor {
    void execute(DiscordCommandContext context);
}
