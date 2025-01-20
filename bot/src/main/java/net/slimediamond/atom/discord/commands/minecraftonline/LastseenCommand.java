package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.UnknownPlayerException;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class LastseenCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws UnknownPlayerException, SQLException, IOException {
        context.deferReply();

        AtomicReference<String> username = new AtomicReference();
        context.getArguments().get("username").ifPresentOrElse(arg -> username.set(arg.getAsString()), () -> username.set(context.getSender().getName()));

        MCOPlayer player = new MCOPlayer(username.get()); // should get correct name from this
        Optional<Date> lastseen = player.getLastseen();

        if (lastseen.isPresent()) {
            Date date = lastseen.get();
            context.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(username.get(), null, "https://mc-heads.net/avatar/" + username.get())
                    .setDescription(username.get() + " last visited Freedonia on <t:" + date.toInstant().getEpochSecond() + ":f> [<t:" + date.toInstant().getEpochSecond() + ":R>]")
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build()
            );
        } else {
            throw new UnknownPlayerException(player);
        }
    }
}
