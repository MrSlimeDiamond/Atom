package net.slimediamond.atom.telegram.commands.minecraftonline;

import net.slimediamond.atom.command.telegram.TelegramCommandContext;
import net.slimediamond.atom.command.telegram.TelegramCommandExecutor;
import net.slimediamond.atom.shared.CommandResponses;
import net.slimediamond.atom.util.minecraftonline.MCOPlayer;
import net.slimediamond.atom.util.minecraftonline.exceptions.UnknownPlayerException;

public class MCOFirstseen implements TelegramCommandExecutor {
    public void execute(TelegramCommandContext ctx) throws Exception {
        String username = ctx.getDesiredCommandUsername();

        try {
            MCOPlayer player = new MCOPlayer(username);

            player.getFirstseen().ifPresentOrElse(firstseen -> {
                CommandResponses.sendFirstseenResponse(player.getName(), firstseen, ctx);
            }, () -> {
                ctx.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            ctx.reply("Could not find that player!");
        }
    }
}
