package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.discord.annotations.Option;
import net.slimediamond.atom.discord.annotations.Subcommand;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;
import java.util.Objects;

public class StreamsCommand {
    @GetService
    private Database database;
    @Command(
            name = "streams",
            description = "Manage the stream listener service",
            adminOnly = true,
            usage = "streams <channel/add/remove>",
            subcommands = {
                    @Subcommand(
                            name = "channel",
                            description = "Set/unset streams channel",
                            usage = "channel <set/unset>",
                            options = {
                                    @Option(
                                            name = "channel",
                                            id = 0,
                                            description = "The channel to use",
                                            type = OptionType.CHANNEL
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "add",
                            description = "Add a user to the streams notifier",
                            usage = "streams add <login>",
                            options = {
                                    @Option(
                                            name = "login",
                                            id = 0,
                                            description = "The login of the user (.tv/<this>)",
                                            type = OptionType.STRING
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "remove",
                            description = "Remove a user to the streams notifier",
                            usage = "streams remove <login>",
                            options = {
                                    @Option(
                                            name = "login",
                                            id = 0,
                                            description = "The login of the user (.tv/<this>)",
                                            type = OptionType.STRING
                                    )
                            }
                    )
            }
    )
    public void streamsCommand(CommandEvent event) throws SQLException {
        if (event.getSubcommandName().equals("channel")) {
            if (event.getCommandArgs()[1].equals("set")) {
                database.setServerStreamsChannel(event.getGuild(), Objects.requireNonNull(event.getJDA().getTextChannelById(event.getChannelOption("channel").getIdLong())));
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set streams channel"));
            } else {
                // unset
                database.setServerStreamsChannel(event.getGuild(), null);
            }
        } else if (event.getSubcommandName().equals("add")) {
            database.insertServerStreamer(event.getGuild(), event.getStringOption("login"));
            event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added streamer"));
        } else if (event.getSubcommandName().equals("remove")) {
            database.deleteServerStreamer(event.getGuild(), event.getStringOption("login"));
            event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed streamer"));
        }
    }
}
