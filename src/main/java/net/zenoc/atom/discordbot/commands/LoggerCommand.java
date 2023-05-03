package net.zenoc.atom.discordbot.commands;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CommandEvent;
import net.zenoc.atom.discordbot.annotations.Command;
import net.zenoc.atom.discordbot.annotations.Option;
import net.zenoc.atom.discordbot.annotations.Subcommand;
import net.zenoc.atom.discordbot.exceptions.IncorrectUsageException;
import net.zenoc.atom.discordbot.util.EmbedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class LoggerCommand {
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
    public void loggerCommand(CommandEvent event) throws IncorrectUsageException, SQLException {
        if (!event.isSubCommand()) throw new IncorrectUsageException();
        if (event.getSubcommandName().equals("set")) {
            GuildChannel channel = event.getChannelOption("channel");
            Long channelID = channel.getIdLong();
            log.debug(String.valueOf(channelID));
            Atom.database.updateServerLogChannel(event.getGuild().getIdLong(), channelID);
        } else if (event.getSubcommandName().equals("unset")) {
            Atom.database.updateServerLogChannel(event.getGuild().getIdLong(), -1L);
        }
        event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Updated logger channel"));
    }
}
