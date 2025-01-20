package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.UnknownPlayerException;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class PlaytimeCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        context.deferReply();

        String username = context.getArguments().get("username").getAsString();
        if (username == null) {
            username = context.getSender().getName();
        }

        MCOPlayer player = new MCOPlayer(username); // should get correct name from this
        Optional<Long> playtime = player.getPlaytime();
        if (playtime.isPresent()) {
            BigDecimal hours = new BigDecimal(playtime.get()).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
            context.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(username, null, "https://mc-heads.net/avatar/" + username)
                    .setDescription(username + " has played on Freedonia for " + hours.toString() + " hours")
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build()
            );
        } else {
            throw new UnknownPlayerException(player);
        }
    }
}
