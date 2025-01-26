package net.slimediamond.atom.discord.commands.bridge;

import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.command.discord.args.UserArgument;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.Optional;

public class BridgeSetCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        String channel;
        Optional<UserArgument> arg = context.getArguments().get("channel");
        if (arg.isPresent()) {
            channel = arg.get().getAsString();
        } else {
            context.reply("Usage: " + context.getCommandMetadata().getCommandUsage());
            return;
        }
        if (!channel.startsWith("#")) channel = "#" + channel;
        database.setIRCDiscordBridgeChannelID(channel, context.getChannel().getIdLong());
        context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set IRC bridge channel"));
    }
}
