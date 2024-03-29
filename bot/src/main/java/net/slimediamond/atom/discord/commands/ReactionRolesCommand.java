package net.slimediamond.atom.discord.commands;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

public class ReactionRolesCommand {
    @GetService
    private Database database;
    
    @Command(
            name = "reactionroles",
            aliases = {"rr"},
            description = "Manage reaction roles",
            usage = "reactionroles <add|remove|modify>",
            adminOnly = true,
            slashCommand = false
    )
    public void reactionRolesCommand(CommandEvent event) throws SQLException {
        if (event.getCommandArgs() == null) {
            return;
        } else {
            if (event.getCommandArgs()[0].equals("add")) {
                // reactionroles add <message id> <emoji> <role>
                // try to parse an emoji
                long messageID;
                long roleID;
                try {
                    messageID = Long.parseLong(event.getCommandArgs()[1]);
                    roleID = Long.parseLong(event.getCommandArgs()[3]);
                } catch (NumberFormatException e) {
                    event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("reactionroles add <message id> <emoji> <role id>"));
                    return;
                }

                String emoji;
                Emoji reaction;
                if (EmojiParser.extractEmojis(event.getCommandArgs()[2]).size() == 0) {
                    // Custom emoji
                    emoji = event.getCommandArgs()[2].split(":")[2].replace(">", "");
                    String name = event.getCommandArgs()[2].split(":")[1];
                    reaction = Emoji.fromCustom(name, Long.parseLong(emoji), false);
                } else {
                    // Unicode emoji
                    emoji = EmojiParser.parseToHtmlHexadecimal(event.getCommandArgs()[2])
                                    .replace("&", "U")
                                    .replace("#", "+")
                                    .replace(";", "")
                                    .replaceFirst("x", "");
                    reaction = Emoji.fromUnicode(emoji);
                }
                database.insertReactionRole(messageID, emoji, roleID);
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added reaction role\n> " + event.getCommandArgs()[2] + " = <@&" + event.getCommandArgs()[3] + ">"));

                // this throws a million errors but don't worry about it
                event.getGuild().getTextChannels().forEach(channel -> {
                    channel.retrieveMessageById(messageID).queue(message -> {
                        if (message != null) {
                            message.addReaction(reaction).queue();
                        }
                    });
                });
            } else if (event.getCommandArgs()[0].equals("remove") || event.getCommandArgs()[0].equals("rem")) {
                // remove <message id> <role id>
                long messageID;
                long roleID = 0;
                try {
                    messageID = Long.parseLong(event.getCommandArgs()[1]);
                    if (event.getCommandArgs().length == 1) {
                        roleID = Long.parseLong(event.getCommandArgs()[2]);
                    }
                } catch (NumberFormatException e) {
                    event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("reactionroles remove <message id> [role id]"));
                    return;
                }

                boolean removeAll = roleID == 0;

                if (removeAll) {
                    event.getGuild().getTextChannels().forEach(channel -> {
                        channel.retrieveMessageById(messageID).queue(message -> {
                            if (message != null) {
                                message.getReactions().forEach(messageReaction -> messageReaction.removeReaction().queue());
                            }
                        });
                    });
                } else {

                    database.getReactionRoleEmoji(event.getGuild(), messageID, roleID).ifPresent(reaction -> {
                        event.getGuild().getTextChannels().forEach(channel -> {
                            channel.retrieveMessageById(messageID).queue(message -> {
                                if (message != null) {
                                    message.getReaction(reaction).removeReaction().queue();
                                }
                            });
                        });
                    });
                }
                if (removeAll) {
                    database.removeAllReactionRoles(messageID);
                } else {
                    database.removeReactionRole(messageID, roleID);
                }
                event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed reaction role"));
            }
        }
    }
}
