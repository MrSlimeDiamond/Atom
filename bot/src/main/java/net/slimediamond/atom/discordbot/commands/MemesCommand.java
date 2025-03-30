package net.slimediamond.atom.discordbot.commands;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.data.keys.GuildKeys;
import net.slimediamond.atom.discord.entities.Guild;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

public class MemesCommand implements DiscordCommandExecutor {
    // this is always !a memes channel
    public void execute(DiscordCommandContext context) throws SQLException {
        String[] args = context.getArgs();
        Guild guild = context.getGuild();
        if (args[0].equalsIgnoreCase("set")) {
            // try to parse a channel
            GuildChannel channel = context.getInteractionEvent().getMessage().getMentions().getChannels().get(0);
            if (channel instanceof TextChannel textChannel) {
                guild.offer(GuildKeys.MEMES_CHANNEL, textChannel);
                context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set server memes channel"));
            } else {
                context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Memes channel must be a text channel"));
            }
        } else if (args[0].equalsIgnoreCase("unset")) {
            guild.offer(GuildKeys.MEMES_CHANNEL, null);
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Unset server memes channel"));
        } else {
            context.reply("Usage: " + context.getCommandMetadata().getCommandUsage());
        }
    }
}
