package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.UnknownPlayerException;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class MCOFirstseen implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws Exception {
        String username = ctx.getDesiredCommandUsername();

        try {
            MCOPlayer player = new MCOPlayer(username);

            player.getFirstseen().ifPresentOrElse(firstseen -> {
                this.sendFirstseenResponse(player.getName(), firstseen, ctx);
            }, () -> {
                ctx.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            ctx.reply("Could not find that player!");
        }
    }

    private void sendFirstseenResponse(String username, Date firstseenDate, CommandContext ctx) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, d MMMMM yyyy");
        String firstseen = formatter.format(firstseenDate);

        Duration firstseenDuration = Duration.between(firstseenDate.toInstant(), Instant.now());

        // TODO: Add years to duration
        String fromNow = DurationFormatUtils.formatDurationWords(firstseenDuration.toMillis(), true, true) + " ago";

        ctx.reply(username + " first visited Freedonia at " + firstseen + " [" + fromNow + "]");
    }
}
