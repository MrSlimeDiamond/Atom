package net.slimediamond.atom.discordbot.commands.pinnerino;

import com.vdurmont.emoji.EmojiParser;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.EmbedUtil;

public class PinnerinoEmojiCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        if (context.getArgs()[0].equals("set")) {
            if (context.getArgs()[1] == null) {
                context.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("pinnerino emoji set <emoji>"));
                return;
            }
            if (EmojiParser.extractEmojis(context.getArgs()[1]).isEmpty()) {
                // Custom emoji
                String id = context.getArgs()[1].split(":")[2].replace(">", "");
                database.setServerPinnerinoEmoji(context.getGuild().getIdLong(), id);
            } else {
                // Unicode emoji
                String unicode = EmojiParser.parseToHtmlHexadecimal(context.getArgs()[1])
                        .replace("&", "U")
                        .replace("#", "+")
                        .replace(";", "")
                        .replaceFirst("x", "");
                database.setServerPinnerinoEmoji(context.getGuild().getIdLong(), unicode);
            }
            context.replyEmbeds(EmbedUtil.genericSuccessEmbed("Set server pinnerino emoji"));
        } else {
            context.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("pinnerino emoji set <emoji>"));
        }
    }
}
