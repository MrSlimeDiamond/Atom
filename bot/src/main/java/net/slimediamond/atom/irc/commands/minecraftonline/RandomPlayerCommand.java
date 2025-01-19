package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.util.MinecraftOnlineAPI;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class RandomPlayerCommand implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws IOException {
        MinecraftOnlineAPI.getOnlinePlayers().ifPresentOrElse(players -> {
            String randomPlayer = players.get(ThreadLocalRandom.current().nextInt(players.size()));
            ctx.reply("Random online player: " + randomPlayer);
        }, () -> {
            ctx.reply("No players are online or the server is offline.");
        });
    }
}
