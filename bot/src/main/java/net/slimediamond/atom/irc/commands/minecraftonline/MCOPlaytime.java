package net.slimediamond.atom.irc.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.UnknownPlayerException;

import java.awt.*;
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