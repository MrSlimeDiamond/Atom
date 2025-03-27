package net.slimediamond.atom.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.util.UserUtil;

import java.awt.*;

public class WhoamiCommand implements DiscordCommandExecutor {
    @GetService
    private Database database;

    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        boolean admin = database.isDiscordAdminByID(context.getSender().getId());
        String used = UserUtil.getUserName(context.getSender().getRaw());
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setAuthor(used, null, context.getSender().getRaw().getAvatarUrl())
                .addField("Atom Admin", (admin ? "Yes" : "No"), true)
                .addField("Discriminated name", context.getSender().getRaw().getAsTag(), true)
                .addField("Username", context.getSender().getName(), true)
                .addField("Display Name", context.getSender().getRaw().getGlobalName(), true)
                .addField("Server Display Name", context.getSender().getRaw().getEffectiveName(), true)
                .addField("Used Discriminator", used, true);

        context.replyEmbeds(builder.build());
    }
}
