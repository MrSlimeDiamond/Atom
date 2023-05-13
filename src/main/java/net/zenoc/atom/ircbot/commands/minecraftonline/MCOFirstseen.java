package net.zenoc.atom.ircbot.commands.minecraftonline;

import net.zenoc.atom.Atom;
import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.util.DateUtil;
import net.zenoc.atom.util.MinecraftOnlineAPI;
import net.zenoc.atom.util.MinecraftUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MCOFirstseen {
    private static final Logger log = LoggerFactory.getLogger(MCOFirstseen.class);
    @Command(
            name = "firstseen",
            aliases = {"fs", "firstjoin", "fj"},
            usage = "firstseen [player]",
            description = "Get a MinecraftOnline player's firstseen data",
            whitelistedChannels = {"#minecraftonline", "#narwhalbot", "#slimediamond"}
    )
    public void firstseenCommand(CommandEvent event) throws Exception {
        String username = event.getDesiredCommandUsername();
        AtomicReference<String> correctname = new AtomicReference<>();
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> event.reply("Could not find that player!"));

        Optional<Date> mcoFirstseen = Atom.database.getMCOFirstseenByName(correctname.get());
        if (mcoFirstseen.isPresent()) {
            // in database
            this.sendFirstseenResponse(correctname.get(), mcoFirstseen.get(), event);
        } else {
            // not in database (or just doesn't exist)

            // send a response immediately, we can add them to the database right after
            AtomicReference<Date> firstseenDate = new AtomicReference<>();
            MinecraftOnlineAPI.getPlayerFirstseenByName(username).ifPresentOrElse(firstseenDate::set, () -> event.reply("Could not find that player!"));
            this.sendFirstseenResponse(correctname.get(), firstseenDate.get(), event);
            log.info(correctname.get() + " hasn't got a firstseen record in the database, adding");
            Optional<String> uuid = MinecraftUtils.getPlayerUUID(correctname.get());
            if (uuid.isPresent()) {
                if (!Atom.database.isMCOUserInDatabaseByUsername(correctname.get())) {
                    log.info("Inserting user");
                    Atom.database.insertMCOUser(correctname.get(), uuid.get());
                }
                Atom.database.setMCOFirstseenByUUID(uuid.get(), firstseenDate.get());
                log.info("Set firstseen date!");
            }
        }
    }

    private void sendFirstseenResponse(String username, Date firstseenDate, CommandEvent event) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, d MMMMM yyyy");
        String firstseen = formatter.format(firstseenDate);

        Duration firstseenDuration = Duration.between(firstseenDate.toInstant(), Instant.now());

        // TODO: Add years to duration
        String fromNow = DurationFormatUtils.formatDurationWords(firstseenDuration.toMillis(), true, true) + " ago";

        event.reply(username + " first visited Freedonia at " + firstseen + " [" + fromNow + "]");
    }
}
