package net.slimediamond.atom.discordbot.commands.streams;

import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.command.discord.args.UserArgument;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.util.Optional;

public class StreamsRemoveCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        Optional<UserArgument> arg = context.getArguments().get("login");
        if (arg.isPresent()) {
            String login = arg.get().getAsString();
            database.deleteServerStreamer(context.getGuild(), login);
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed streamer"));
        } else {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("Please specify a user to delete"));
        }
    }
}
