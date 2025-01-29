package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.atom.util.minecraftonline.MCOPlayer;
import net.slimediamond.atom.util.minecraftonline.MinecraftOnlineAPI;
import net.slimediamond.atom.util.minecraftonline.exceptions.UnknownPlayerException;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class BanwhyCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        context.deferReply();

        AtomicReference<String> username = new AtomicReference();
        context.getArguments().get("username").ifPresentOrElse(arg -> username.set(arg.getAsString()), () -> username.set(context.getSender().getName()));

        MinecraftOnlineAPI.getBan(username.get()).ifPresentOrElse(ban -> {
            context.replyEmbeds(new EmbedBuilder()
                    .setColor(0x00BEBE)
                    .setAuthor(ban.getPlayer().getName(), null, "https://minecraftonline.com/cgi-bin/getplayerhead.sh?" + ban.getPlayer().getName())
                    .setTitle(ban.getPlayer().getName() + " is banned!")
                    .setDescription(ban.getReason())
                    .addField(new MessageEmbed.Field("Date", "<t:" + ban.getDate().toInstant().getEpochSecond() + ":f> [<t:" + ban.getDate().toInstant().getEpochSecond() + ":R>]", true))
                    .addField(new MessageEmbed.Field("Banned by", ban.getBanner().getName(), true))
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build()
            );
        }, () -> {
            MCOPlayer player;
            try {
                player = new MCOPlayer(username.get());
            } catch (UnknownPlayerException e) {
                throw new RuntimeException(e);
            }
            context.replyEmbeds(new EmbedBuilder()
                    .setColor(0x00BEBE)
                    .setAuthor(player.getName(), null, "https://minecraftonline.com/cgi-bin/getplayerhead.sh?" + player.getName())
                    .setDescription(player.getName() + " is not banned.")
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build()
            );
        });
    }
}
