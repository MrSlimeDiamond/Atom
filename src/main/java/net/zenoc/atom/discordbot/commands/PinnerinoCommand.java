package net.zenoc.atom.discordbot.commands;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CommandEvent;
import net.zenoc.atom.discordbot.annotations.Command;
import net.zenoc.atom.discordbot.annotations.Subcommand;
import net.zenoc.atom.discordbot.exceptions.IncorrectUsageException;
import net.zenoc.atom.discordbot.util.EmbedUtil;
import net.zenoc.atom.discordbot.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class PinnerinoCommand {
    private static final Logger log = LoggerFactory.getLogger(PinnerinoCommand.class);
    @Command(
            name = "pinnerino",
            description = "Pinnerino service management",
            usage = "pinnerino <blacklist|channel|emoji>",
            aliases = {"pins"},
            slashCommand = false,
            adminOnly = true,
            subcommands = {
                    @Subcommand(
                            name = "blacklist",
                            description = "Manage the pinnerino blacklist",
                            usage = "pinnerino blacklist <add|remove>"
                    ),
                    @Subcommand(
                            name = "channel",
                            description = "Set (or unset) the channel for pins",
                            usage = "pinnerino channel <set|unset>"
                    ),
                    @Subcommand(
                            name = "emoji",
                            description = "Set the pinnerino emoji",
                            usage = "pinnerino emoji set <emoji>"
                    ),
                    @Subcommand(
                            name = "threshold",
                            description = "Set the pinnerino threshold",
                            usage = "pinnerino threshold set <threshold>"
                    )
            }
    )
    public void pinnerinoCommand(CommandEvent event) throws IncorrectUsageException, SQLException {
        if (!event.isSubCommand()) {
            throw new IncorrectUsageException();
        }
        if (event.getSubcommandName().equals("channel")) {
            if (event.getCommandArgs()[1].equals("set")) {
                // set pinnerino channel
                List<GuildChannel> channels = event.getMessage().getMentions().getChannels();
                if (channels.size() == 0) {
                    event.reply("Channel size is zero");
                }
                GuildChannel channel = channels.get(0);
                Atom.database.setServerPinnerinoChannel(event.getGuild().getIdLong(), channel.getIdLong());
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set pinnerino channel"));
            } else if (event.getCommandArgs()[1].equals("unset")) {
                Atom.database.setServerPinnerinoChannel(event.getGuild().getIdLong(), -1L);
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Unset pinnerino channel"));
            } else {
                event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("pinnerino channel set <channel>"));
            }
        } else if (event.getSubcommandName().equals("emoji")) {
            if (event.getCommandArgs()[1].equals("set")) {
                if (event.getCommandArgs()[2] == null) {
                    event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("pinnerino emoji set <emoji>"));
                    return;
                }
                if (EmojiParser.extractEmojis(event.getCommandArgs()[2]).size() == 0) {
                    // Custom emoji
                    String id = event.getCommandArgs()[2].split(":")[2].replace(">", "");
                    Atom.database.setServerPinnerinoEmoji(event.getGuild().getIdLong(), id);
                } else {
                    // Unicode emoji
                    String unicode = "U+" + Integer.toHexString(event.getCommandArgs()[2].toCharArray()[0] | 0x10000).substring(1);
                    Atom.database.setServerPinnerinoEmoji(event.getGuild().getIdLong(), unicode);
                }
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set server pinnerino emoji"));
            } else {
                event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("pinnerino emoji set <emoji>"));
            }
        } else if (event.getSubcommandName().equals("threshold")) {
            if (event.getCommandArgs()[1].equals("set")) {
                if (NumberUtils.isNumeric(event.getCommandArgs()[2])) {
                    int threshold = Integer.parseInt(event.getCommandArgs()[2]);
                    Atom.database.setServerPinnerinoThreshold(event.getGuild().getIdLong(), threshold);
                    event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set pinnerino threshold"));
                }
            }
        } else if (event.getSubcommandName().equals("blacklist")) {
            List<GuildChannel> channels = event.getMessage().getMentions().getChannels();
            if (channels.size() == 0) {
                event.reply("Channel size is zero");
            }
            GuildChannel channel = channels.get(0);
            if (event.getCommandArgs()[1].equals("add")) {
                Atom.database.addPinnerinoBlacklist(channel.getIdLong());
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added channel to blacklist"));
            } else if (event.getCommandArgs()[1].equals("remove")) {
                Atom.database.removePinnerinoBlacklist(channel.getIdLong());
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed channel from blacklist"));
            }
        }
    }
}
