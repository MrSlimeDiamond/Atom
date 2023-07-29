package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.discord.annotations.Option;
import net.slimediamond.atom.discord.annotations.Subcommand;
import net.slimediamond.atom.util.EmbedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class LoggerCommand {
    @GetService
    private Database database;
    
    private static final Logger log = LoggerFactory.getLogger(LoggerCommand.class);
    @Command(
            name = "logger_channel",
            description = "Options for the logger service",
            usage = "logger channel <set|unset>",
            aliases = {"log_channel"},
            slashCommand = false,
            adminOnly = true,
            subcommands = {
                    @Subcommand(
                            name = "set",
                            description = "Set the log channel",
                            usage = "logger channel set <channel>",
                            options = {
                                    @Option(
                                            type = OptionType.CHANNEL,
                                            name = "channel",
                                            id = 0,
                                            required = true,
                                            description = "The channel to set"
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "unset",
                            description = "Unset the log channel",
                            usage = "logger channel unset"
                    )
            }
    )
    public void loggerCommand(CommandEvent event) throws SQLException {
        if (!event.isSubCommand()) event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("logger_chan set <channel>"));
        if (event.getSubcommandName().equals("set")) {
            GuildChannel channel = event.getChannelOption("channel");
            Long channelID = channel.getIdLong();
            log.debug(String.valueOf(channelID));
            database.updateServerLogChannel(event.getGuild().getIdLong(), channelID);
        } else if (event.getSubcommandName().equals("unset")) {
            database.updateServerLogChannel(event.getGuild().getIdLong(), -1L);
        }
        event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Updated logger channel"));
    }
}
