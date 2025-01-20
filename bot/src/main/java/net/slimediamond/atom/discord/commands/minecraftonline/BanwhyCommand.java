package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.atom.util.MCOPlayer;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class BanwhyCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        context.deferReply();

        AtomicReference<String> username = new AtomicReference();
        context.getArguments().get("username").ifPresentOrElse(arg -> username.set(arg.getAsString()), () -> username.set(context.getSender().getName()));

        MCOPlayer player = new MCOPlayer(username.get()); // should get correct name from this

        if (!player.isBanned()) {
            context.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(player.getName(), null, "https://mc-heads.net/avatar/" + player.getName())
                    .setDescription(player.getName() + " is not banned!")
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build());

        } else {
            player.getBanDate().ifPresentOrElse(date -> context.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(player.getName(), null, "https://mc-heads.net/avatar/" + player.getName())
                    .setTitle(player.getName() + " is naughty!")
                    .addField("Ban reason", player.getBanReason().orElseThrow(), false)
                    .addField("Ban time", "<t:" + date.toInstant().getEpochSecond() + ":f> [<t:" + date.toInstant().getEpochSecond() + ":R>]", false)
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build()
            ), () -> context.replyEmbeds(EmbedUtil.genericErrorEmbed()));
        }
    }
}
