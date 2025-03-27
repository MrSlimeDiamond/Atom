package net.slimediamond.atom.discordbot.commands.pinnerino;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.List;

public class PinnerinoBlacklistCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        List<GuildChannel> channels = context.getInteractionEvent().getMessage().getMentions().getChannels();
        if (channels.isEmpty()) {
            context.reply("You did not provide a channel to modify!");
        }
        GuildChannel channel = channels.get(0);
        if (context.getArgs()[0].equals("add")) {
            database.addPinnerinoBlacklist(channel.getIdLong());
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added channel to blacklist"));
        } else if (context.getArgs()[0].equals("remove")) {
            database.removePinnerinoBlacklist(channel.getIdLong());
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed channel from blacklist"));
        }
    }
}
