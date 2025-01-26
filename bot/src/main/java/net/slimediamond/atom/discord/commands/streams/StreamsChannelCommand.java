package net.slimediamond.atom.discord.commands.streams;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.List;
import java.util.Objects;

public class StreamsChannelCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        if (context.getArgs()[0].equals("set")) {
            List<GuildChannel> channels = context.getInteractionEvent().getMessage().getMentions().getChannels();
            if (!channels.isEmpty()) {
                Channel channel = channels.get(0);
                if (!(channel instanceof TextChannel textChannel)) {
                    context.replyEmbeds(EmbedUtil.expandedErrorEmbed("That is not a text channel!"));
                    return;
                }

                database.setServerStreamsChannel(context.getGuild(), textChannel);
                context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set streams channel"));
                return;
            }
        }
        // unset
        database.setServerStreamsChannel(context.getGuild(), null);
        context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Unset streams channel"));
    }
}
