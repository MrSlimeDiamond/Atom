package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

public class MemesCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    // this is always !a memes channel
    public void execute(DiscordCommandContext context) throws SQLException {
        String[] args = context.getArgs();
            if (args[0].equalsIgnoreCase("set")) {
                // try to parse a channel
                GuildChannel channel = context.getInteractionEvent().getMessage().getMentions().getChannels().get(0);
                database.setServerMemesChannel(channel.getIdLong(), channel.getGuild());
                context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set server memes channel"));
            } else if (args[0].equalsIgnoreCase("unset")) {
                database.unsetServerMemesChannel(context.getGuild());
                context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Unset server memes channel"));
            } else {
                context.reply("Usage: " + context.getCommandMetadata().getCommandUsage());
            }
        }
}
