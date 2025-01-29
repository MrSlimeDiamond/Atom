package net.slimediamond.atom.telegram.commands.minecraftonline;

import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;
import net.slimediamond.atom.util.minecraftonline.MinecraftOnlineAPI;

import java.io.IOException;

public class BansCommand implements TelegramCommandExecutor {
    public void execute(TelegramCommandContext ctx) throws IOException {
        MinecraftOnlineAPI.getBanCount().ifPresentOrElse(bans -> {
            ctx.reply(bans + " players have been banished from Freedonia!");
        }, () -> {
            ctx.reply("MinecraftOnlineAPI::getBanCount Optional was not present! What the fuck happened? Tell an admin!");
        });
    }
}
