package net.zenoc.atom.ircbot.commands.minecraftonline;

import net.zenoc.atom.Atom;
import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.util.MCOPlayer;
import net.zenoc.atom.util.MinecraftOnlineAPI;
import net.zenoc.atom.util.MinecraftUtils;
import net.zenoc.atom.util.UnknownPlayerException;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
