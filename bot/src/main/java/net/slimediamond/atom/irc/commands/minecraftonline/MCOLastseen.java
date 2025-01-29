package net.slimediamond.atom.irc.commands.minecraftonline;

import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;
import net.slimediamond.atom.shared.CommandResponses;
import net.slimediamond.atom.util.minecraftonline.MCOPlayer;
import net.slimediamond.atom.util.minecraftonline.exceptions.UnknownPlayerException;

public class MCOLastseen implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws Exception {
        String username = ctx.getDesiredCommandUsername();

        try {
            MCOPlayer player = new MCOPlayer(username);

            player.getLastseen().ifPresentOrElse(lastseen -> {
                CommandResponses.sendLastseenResponse(player.getName(), lastseen, ctx);
            }, () -> {
                ctx.reply("Could not find that player!");
            });
        } catch (UnknownPlayerException e) {
            ctx.reply("Could not find that player!");
        }
    }

}
