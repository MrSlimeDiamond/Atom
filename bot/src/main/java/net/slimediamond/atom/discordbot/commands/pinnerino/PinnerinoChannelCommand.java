package net.slimediamond.atom.discordbot.commands.pinnerino;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.data.keys.GuildKeys;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.List;

public class PinnerinoChannelCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        if (context.getArgs()[0].equals("set")) {
            // set pinnerino channel
            List<GuildChannel> channels = context.getInteractionEvent().getMessage().getMentions().getChannels();
            if (channels.isEmpty()) {
                context.reply("You need to specify a channel to set");
                return;
            }
            if (!(channels.get(0) instanceof TextChannel channel)) {
                context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Pinnerino channel must be a text channel"));
                return;
            }
            context.getGuild().offer(GuildKeys.PINNERINO_CHANNEL, channel);
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set pinnerino channel"));
        } else if (context.getArgs()[0].equals("unset")) {
            context.getGuild().offer(GuildKeys.PINNERINO_CHANNEL, null);
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Unset pinnerino channel"));
        } else {
            context.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("pinnerino channel <set|unset> [<channel>]"));
        }
    }
}
