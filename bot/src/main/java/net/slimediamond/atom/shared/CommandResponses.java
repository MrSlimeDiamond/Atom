package net.slimediamond.atom.shared;

import net.slimediamond.atom.command.CommandContext;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class CommandResponses {
    public static void sendFirstseenResponse(String username, Date firstseenDate, CommandContext ctx) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, d MMMMM yyyy");
        String firstseen = formatter.format(firstseenDate);

        Duration firstseenDuration = Duration.between(firstseenDate.toInstant(), Instant.now());

        // TODO: Add years to duration
        String fromNow = DurationFormatUtils.formatDurationWords(firstseenDuration.toMillis(), true, true) + " ago";

        ctx.reply(username + " first visited Freedonia at " + firstseen + " [" + fromNow + "]");
    }

    public static void sendLastseenResponse(String username, Date firstseenDate, CommandContext ctx) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, d MMMMM yyyy");
        String lastseen = formatter.format(firstseenDate);

        Duration firstseenDuration = Duration.between(firstseenDate.toInstant(), Instant.now());

        // TODO: Add years to duration
        String fromNow = DurationFormatUtils.formatDurationWords(firstseenDuration.toMillis(), true, true) + " ago";

        ctx.reply(username + " last visited Freedonia at " + lastseen + " [" + fromNow + "]");
    }
}
