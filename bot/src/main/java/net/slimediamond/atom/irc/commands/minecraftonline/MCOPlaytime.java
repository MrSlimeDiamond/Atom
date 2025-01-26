package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.UnknownPlayerException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MCOPlaytime implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws Exception {
        String username = ctx.getDesiredCommandUsername();

        try {
            MCOPlayer player = new MCOPlayer(username);

            player.getPlaytime().ifPresentOrElse(playtime -> {
                BigDecimal hours = new BigDecimal(playtime).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
                ctx.reply(username + " has played on Freedonia for " + hours.toString() + " hours");
            }, () -> {
                ctx.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            ctx.reply("Could not find that player!");
        }
    }
}