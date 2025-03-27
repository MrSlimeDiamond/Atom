package net.slimediamond.atom.discordbot.commands.logger;

import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

public class LoggerChannelUnsetCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        database.updateServerLogChannel(context.getGuild().getIdLong(), -1L);
        context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed this guild's logger channel"));
    }
}
