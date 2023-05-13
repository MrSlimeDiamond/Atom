package net.zenoc.atom.ircbot.commands.minecraftonline;

import net.zenoc.atom.Atom;
import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.util.MinecraftOnlineAPI;
import net.zenoc.atom.util.MinecraftUtils;
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
    private static final Logger log = LoggerFactory.getLogger(MCOFirstseen.class);
    @Command(
            name = "lastseen",
            aliases = {"ls", "lastjoin", "lj"},
            usage = "lastseen [player]",
            description = "Get a MinecraftOnline player's lastseen data",
            whitelistedChannels = {"#minecraftonline", "#narwhalbot", "#slimediamond"}
    )
    public void lastseenCommand(CommandEvent event) throws Exception {
        String username = event.getDesiredCommandUsername();
        AtomicReference<String> correctname = new AtomicReference<>();
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> event.reply("Could not find that player!"));

        Optional<Date> mcoLastseen = Atom.database.getMCOLastseenByName(correctname.get());
        if (mcoLastseen.isPresent()) {
            // in database
            this.sendResponse(correctname.get(), mcoLastseen.get(), event);
        } else {
            // not in database (or just doesn't exist)

            // send a response immediately, we can add them to the database right after
            AtomicReference<Date> lastseenDate = new AtomicReference<>();
            MinecraftOnlineAPI.getPlayerLastseenByName(username).ifPresentOrElse(lastseenDate::set, () -> event.reply("Could not find that player!"));
            this.sendResponse(correctname.get(), lastseenDate.get(), event);
            log.info(correctname.get() + " hasn't got a lastseen record in the database, adding");
            Optional<String> uuid = MinecraftUtils.getPlayerUUID(correctname.get());
            if (uuid.isPresent()) {
                if (!Atom.database.isMCOUserInDatabaseByUsername(correctname.get())) {
                    log.info("Inserting user");
                    Atom.database.insertMCOUser(correctname.get(), uuid.get());
                }
                Atom.database.setMCOLastseenByUUID(uuid.get(), lastseenDate.get());
                log.info("Set lastseen date!");
            }
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
