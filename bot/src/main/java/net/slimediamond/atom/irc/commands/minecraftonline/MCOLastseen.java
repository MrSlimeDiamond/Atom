package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.util.minecraftonline.MCOPlayer;
import net.slimediamond.atom.util.minecraftonline.exceptions.UnknownPlayerException;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class MCOLastseen implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws Exception {
        String username = ctx.getDesiredCommandUsername();

        try {
            MCOPlayer player = new MCOPlayer(username);

            player.getLastseen().ifPresentOrElse(lastseen -> {
                this.sendResponse(player.getName(), lastseen, ctx);
            }, () -> {
                ctx.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            ctx.reply("Could not find that player!");
        }
    }

    private void sendResponse(String username, Date firstseenDate, CommandContext ctx) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, d MMMMM yyyy");
        String lastseen = formatter.format(firstseenDate);

        Duration firstseenDuration = Duration.between(firstseenDate.toInstant(), Instant.now());

        // TODO: Add years to duration
        String fromNow = DurationFormatUtils.formatDurationWords(firstseenDuration.toMillis(), true, true) + " ago";

        ctx.reply(username + " last visited Freedonia at " + lastseen + " [" + fromNow + "]");
    }
}
