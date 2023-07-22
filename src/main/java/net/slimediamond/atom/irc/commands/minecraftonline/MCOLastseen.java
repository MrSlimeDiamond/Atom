package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.UnknownPlayerException;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class MCOLastseen {
    @Command(
            name = "lastseen",
            aliases = {"ls", "lastjoin", "lj"},
            usage = "lastseen [player]",
            description = "Get a MinecraftOnline player's lastseen data",
            whitelistedChannels = {"#minecraftonline", "#narwhalbot", "#slimediamond"}
    )
    public void lastseenCommand(CommandEvent event) throws Exception {
        String username = event.getDesiredCommandUsername();

        try {
            MCOPlayer player = new MCOPlayer(username);

            player.getLastseen().ifPresentOrElse(lastseen -> {
                this.sendResponse(player.getName(), lastseen, event);
            }, () -> {
                event.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            event.reply("Could not find that player!");
        }
    }

    private void sendResponse(String username, Date firstseenDate, CommandEvent event) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, d MMMMM yyyy");
        String lastseen = formatter.format(firstseenDate);

        Duration firstseenDuration = Duration.between(firstseenDate.toInstant(), Instant.now());

        // TODO: Add years to duration
        String fromNow = DurationFormatUtils.formatDurationWords(firstseenDuration.toMillis(), true, true) + " ago";

        event.reply(username + " last visited Freedonia at " + lastseen + " [" + fromNow + "]");
    }
}
