package net.slimediamond.atom.discord.commands.bridge;

import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

public class BridgePipeCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        context.getArguments().get("status").ifPresentOrElse(pipe -> {
            boolean status = pipe.getAsBoolean();
            database.getBridgedChannel(context.getChannel()).ifPresentOrElse(channel -> {
                try {
                    if (status) {
                        database.enableIRCPipe(channel);
                    } else {
                        database.disableIRCPipe(channel);
                    }
                } catch (SQLException e) {
                    context.replyEmbeds(EmbedUtil.expandedErrorEmbed("SQLException! Is the database down?"));
                    throw new RuntimeException(e);
                }
            }, () -> context.replyEmbeds(EmbedUtil.expandedErrorEmbed("This channel is not bridged.")));
        }, () -> {
            context.reply("Usage: " + context.getCommandMetadata().getCommandUsage());
        });
    }
}
