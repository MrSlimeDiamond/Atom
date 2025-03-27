package net.slimediamond.atom.discordbot.commands.logger;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.command.discord.args.UserArgument;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.Optional;

public class LoggerChannelSetCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        Optional<UserArgument> arg = context.getArguments().get("channel");
        GuildChannel channel;
        if (arg.isPresent()) {
            channel = arg.get().getAsChannel();
        } else {
            context.reply("Usage: " + context.getCommandMetadata().getCommandUsage());
            return;
        }
        Long channelID = channel.getIdLong();
        database.updateServerLogChannel(context.getGuild().getIdLong(), channelID);
        context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set log channel for this server, it is now <#" + channelID + ">"));
    }
}
