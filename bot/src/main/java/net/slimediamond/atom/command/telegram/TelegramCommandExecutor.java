package net.slimediamond.atom.command.telegram;

import net.slimediamond.atom.command.CommandExecutor;

public interface TelegramCommandExecutor extends CommandExecutor {
    void execute(TelegramCommandContext context) throws Exception;
}
