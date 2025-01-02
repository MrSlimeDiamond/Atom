package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.UnknownPlayerException;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
                BigDecimal hours = new BigDecimal(playtime).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
                event.reply(username + " has played on Freedonia for " + hours.toString() + " hours");
            }, () -> {
                event.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            event.reply("Could not find that player!");
        }
    }
}