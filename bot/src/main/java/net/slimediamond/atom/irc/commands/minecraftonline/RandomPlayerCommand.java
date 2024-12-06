package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.util.MinecraftOnlineAPI;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class RandomPlayerCommand {
    @Command(
            name = "randomplayer",
            usage = "randomplayer",
            description = "Finds a random online player on MCO",
            whitelistedChannels = {"#minecraftonline"}
    )
    public void randomPlayerCommand(CommandEvent event) throws IOException {
        MinecraftOnlineAPI.getOnlinePlayers().ifPresentOrElse(players -> {
            String randomPlayer = players.get(ThreadLocalRandom.current().nextInt(players.size()));
            event.reply("Random online player: " + randomPlayer);
        }, () -> {
            event.reply("No players are online or the server is offline.");
        });
    }
}
