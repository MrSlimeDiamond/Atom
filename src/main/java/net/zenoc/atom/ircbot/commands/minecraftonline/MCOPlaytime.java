package net.zenoc.atom.ircbot.commands.minecraftonline;

import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.util.MinecraftOnlineAPI;

import java.util.concurrent.atomic.AtomicReference;

public class MCOPlaytime {
    @Command(
            name = "playtime",
            aliases = {"pt", "tp", "tl", "timeplayed"},
            usage = "lastseen [player]",
            description = "Get a MinecraftOnline player's playtime data",
            whitelistedChannels = {"#minecraftonline", "#narwhalbot", "#slimediamond"}
    )
    public void playtimeCommand(CommandEvent event) throws Exception {
        String username = event.getDesiredCommandUsername();
        AtomicReference<String> correctname = new AtomicReference<>();
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> event.reply("Could not find that player!"));

        if (correctname.get() == null) {
            return;
        }

        MinecraftOnlineAPI.getPlayerPlaytime(correctname.get()).ifPresent(playtime -> {
            long hours = playtime / 3600;

            event.reply(correctname.get() + " has played on Freedonia for " + hours + " hours");
        });
    }
}