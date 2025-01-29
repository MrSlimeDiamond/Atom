package net.slimediamond.atom.telegram.commands.minecraftonline;

import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;
import net.slimediamond.atom.util.minecraftonline.MCOPlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class MCOPlaytime implements TelegramCommandExecutor {
    public void execute(TelegramCommandContext ctx) throws Exception {
        String username = ctx.getDesiredCommandUsername();

        MCOPlayer player = new MCOPlayer(username); // should get correct name from this
        Optional<Long> playtime = player.getPlaytime();
        if (playtime.isPresent()) {
            BigDecimal hours = new BigDecimal(playtime.get()).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
            ctx.reply(player.getName() + " has played on Freedonia for " + hours + " hours");
        }
    }
}