package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.util.minecraftonline.MCOPlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class MCOPlaytime implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws Exception {
        String username = ctx.getDesiredCommandUsername();

        MCOPlayer player = new MCOPlayer(username); // should get correct name from this
        Optional<Long> playtime = player.getPlaytime();
        if (playtime.isPresent()) {
            BigDecimal hours = new BigDecimal(playtime.get()).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
            ctx.reply(player.getName() + " has played on Freedonia for " + hours + " hours");
        }
    }
}