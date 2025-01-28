package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.minecraftonline.MCOPlayer;
import net.slimediamond.atom.util.minecraftonline.exceptions.UnknownPlayerException;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class PlaytimeCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        context.deferReply();

        AtomicReference<String> username = new AtomicReference();
        context.getArguments().get("username").ifPresentOrElse(arg -> username.set(arg.getAsString()), () -> username.set(context.getSender().getName()));

        MCOPlayer player = new MCOPlayer(username.get()); // should get correct name from this
        Optional<Long> playtime = player.getPlaytime();
        if (playtime.isPresent()) {
            BigDecimal hours = new BigDecimal(playtime.get()).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
            context.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(player.getName(), null, "https://mc-heads.net/avatar/" + player.getName())
                    .setDescription(player.getName() + " has played on Freedonia for " + hours + " hours")
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build()
            );
        } else {
            throw new UnknownPlayerException(player);
        }
    }
}
