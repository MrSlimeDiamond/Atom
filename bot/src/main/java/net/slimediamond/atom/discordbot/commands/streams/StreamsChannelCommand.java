package net.slimediamond.atom.discordbot.commands.streams;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.List;

public class StreamsChannelCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        if (context.getArgs()[0].equals("set")) {
            // set streams channel
            List<GuildChannel> channels = context.getInteractionEvent().getMessage().getMentions().getChannels();
            if (channels.isEmpty()) {
                context.reply("You need to specify a channel to set");
                return;
            }
            GuildChannel channel = channels.get(0);
            database.setServerStreamsChannel(context.getGuild(), (TextChannel) channel); // pray
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set streams channel"));
        } else {
            context.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("streams channel <set|unset> [<channel>]"));
        }
    }
}
