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

public class BridgeCommand {
    @GetService
    private Database database;
    
    @Command(
            name = "bridge",
            description = "Manage chat bridges",
            usage = "bridge <set|unset|pipe>",
            adminOnly = true,
            slashCommand = false,
            subcommands = {
                    @Subcommand(
                            name = "set",
                            description = "Set the bridge channel",
                            usage = "bridge set <channelname>",
                            options = {
                                    @Option(
                                            type = OptionType.STRING,
                                            description = "IRC Channel",
                                            name = "channel",
                                            id = 0
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "pipe",
                            description = "Set the bridge channel",
                            usage = "bridge pipe <on|off>",
                            options = {
                                    @Option(
                                            type = OptionType.BOOLEAN,
                                            description = "Pipe Status",
                                            name = "pipe",
                                            id = 0
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "unset",
                            description = "Removes the bridge",
                            usage = "bridge unset"
                    )
            }
    )
    public void bridgeCommand(CommandEvent event) {
        if (!event.isSubCommand()) {
            event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("Usage: bridge <set|unset|pipe>"));
        }
        if (event.getSubcommandName().equals("pipe")) {
                event.getBooleanOption("pipe").ifPresent(pipe -> {
                    database.getBridgedChannel(event.getChannel()).ifPresentOrElse(channel -> {
                        try {
                            if (pipe) {
                                database.enableIRCPipe(channel);
                            } else {
                                database.disableIRCPipe(channel);
                            }
                        } catch (SQLException e) {
                            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("SQLException! Is the database down?"));
                            throw new RuntimeException(e);
                        }
                    }, () -> {
                        event.replyEmbeds(EmbedUtil.expandedErrorEmbed("This channel is not bridged."));
                    });
                });
        } else if (event.getSubcommandName().equals("set")) {
            try {
                String channel = event.getStringOption("channel");
                if (!channel.startsWith("#")) channel = "#" + channel;
                database.setIRCDiscordBridgeChannelID(channel, event.getChannel().getIdLong());
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set IRC bridge channel"));
            } catch(SQLException e) {
                event.replyEmbeds(EmbedUtil.expandedErrorEmbed("SQLException! Is the database down?"));
            }


        } else if (event.getSubcommandName().equals("unset")) {
            // TODO, apparently
        }
    }
}
