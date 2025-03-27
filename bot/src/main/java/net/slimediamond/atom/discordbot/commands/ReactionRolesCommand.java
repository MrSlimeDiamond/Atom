package net.slimediamond.atom.discordbot.commands;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

import java.sql.SQLException;

public class ReactionRolesCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    public void execute(DiscordCommandContext context) throws SQLException {
        if (context.getArgs()[0].equals("add")) {
            // reactionroles add <content id> <emoji> <role>
            // try to parse an emoji
            long messageID;
            long roleID;
            try {
                messageID = Long.parseLong(context.getArgs()[1]);
                roleID = Long.parseLong(context.getArgs()[3]);
            } catch (NumberFormatException e) {
                context.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("reactionroles add <content id> <emoji> <role id>"));
                return;
            }

            String emoji;
            Emoji reaction;
            if (EmojiParser.extractEmojis(context.getArgs()[2]).isEmpty()) {
                // Custom emoji
                emoji = context.getArgs()[2].split(":")[2].replace(">", "");
                String name = context.getArgs()[2].split(":")[1];
                reaction = Emoji.fromCustom(name, Long.parseLong(emoji), false);
            } else {
                // Unicode emoji
                emoji = EmojiParser.parseToHtmlHexadecimal(context.getArgs()[2])
                                .replace("&", "U")
                                .replace("#", "+")
                                .replace(";", "")
                                .replaceFirst("x", "");
                reaction = Emoji.fromUnicode(emoji);
            }
            database.insertReactionRole(messageID, emoji, roleID);
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added reaction role\n> " + context.getArgs()[2] + " = <@&" + context.getArgs()[3] + ">"));

            // this throws a million errors but don't worry about it
            context.getGuild().getTextChannels().forEach(channel -> {
                channel.retrieveMessageById(messageID).queue(message -> {
                    if (message != null) {
                        message.addReaction(reaction).queue();
                    }
                });
            });
        } else if (context.getArgs()[0].equals("remove") || context.getArgs()[0].equals("rem")) {
            // remove <content id> <role id>
            long messageID;
            long roleID = 0;
            try {
                messageID = Long.parseLong(context.getArgs()[1]);
                if (context.getArgs().length == 1) {
                    roleID = Long.parseLong(context.getArgs()[2]);
                }
            } catch (NumberFormatException e) {
                context.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("reactionroles remove <content id> [role id]"));
                return;
            }

            boolean removeAll = roleID == 0;

            if (removeAll) {
                context.getGuild().getTextChannels().forEach(channel -> {
                    channel.retrieveMessageById(messageID).queue(message -> {
                        if (message != null) {
                            message.getReactions().forEach(messageReaction -> messageReaction.removeReaction().queue());
                        }
                    });
                });
            } else {

                database.getReactionRoleEmoji(context.getGuild(), messageID, roleID).ifPresent(reaction -> {
                    context.getGuild().getTextChannels().forEach(channel -> {
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
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Removed reaction role"));
        }
    }
}
