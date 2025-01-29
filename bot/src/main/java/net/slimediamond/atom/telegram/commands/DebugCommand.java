package net.slimediamond.atom.telegram.commands;

import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;

public class DebugCommand implements TelegramCommandExecutor {
    @Override
    public void execute(TelegramCommandContext context) throws Exception {
        context.reply("user id: " + context.getSender().getId());
    }
}
