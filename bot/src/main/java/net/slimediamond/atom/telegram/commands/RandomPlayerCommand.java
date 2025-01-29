package net.slimediamond.atom.telegram.commands;

import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;
import net.slimediamond.atom.util.minecraftonline.MinecraftOnlineAPI;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class RandomPlayerCommand implements TelegramCommandExecutor {
    public void execute(TelegramCommandContext ctx) throws IOException {
        MinecraftOnlineAPI.getOnlinePlayers().ifPresentOrElse(players -> {
            String randomPlayer = players.get(ThreadLocalRandom.current().nextInt(players.size()));
            ctx.reply("Random online player: " + randomPlayer);
        }, () -> {
            ctx.reply("No players are online or the server is offline.");
        });
    }
}
