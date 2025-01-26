package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.atom.util.MinecraftOnlineAPI;

public class BansCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        context.deferReply();
        MinecraftOnlineAPI.getBanCount().ifPresentOrElse(bans -> {
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(0x00BEBE)
                    .setTitle("MinecraftOnline ban count")
                    .setThumbnail(EmbedReference.mcoBanhammer)
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon);
            builder.setDescription(bans + " players have been banished from Freedonia!");

            context.replyEmbeds(builder.build());
        }, () -> {
            context.replyEmbeds(EmbedUtil.expandedErrorEmbed("MinecraftOnlineAPI::getBanCount Optional was not present! Tell an admin!"));
        });
    }
}
