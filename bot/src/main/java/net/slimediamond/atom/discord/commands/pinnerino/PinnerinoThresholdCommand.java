package net.slimediamond.atom.discord.commands.pinnerino;

import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.util.number.NumberUtils;

public class PinnerinoThresholdCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        if (context.getArgs()[0].equals("set")) {
            if (NumberUtils.isNumeric(context.getArgs()[1])) {
                int threshold = Integer.parseInt(context.getArgs()[1]);
                database.setServerPinnerinoThreshold(context.getGuild().getIdLong(), threshold);
                context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set pinnerino threshold"));
            }
        }
    }
}
