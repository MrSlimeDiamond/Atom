package net.zenoc.atom.ircbot.commands.minecraftonline;

import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.util.MCOPlayer;
import net.zenoc.atom.util.MinecraftOnlineAPI;
import net.zenoc.atom.util.UnknownPlayerException;

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

        try {
            MCOPlayer player = new MCOPlayer(username);

            player.getPlaytime().ifPresentOrElse(playtime -> {
                long hours = playtime / 3600;
                event.reply(username + " has logged " + hours + " on Freedonia");
            }, () -> {
                event.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            event.reply("Could not find that player!");
        }
    }
}