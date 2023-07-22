package net.zenoc.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.GetService;
import net.zenoc.atom.annotations.Service;
import net.zenoc.atom.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

@Service("meme vote")
public class MemeVoteService extends ListenerAdapter {
    @Inject
    private JDA jda;

    @GetService
    private Database database;

    @Service.Start
    public void startService() throws Exception {
        jda.addEventListener(this);
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        database.getServerMemesChannel(event.getGuild()).ifPresent(channel -> {
//            log.info("Got channel");
            if (event.getChannel().getId().equals(channel.getId()) && event.getMessage().getAttachments().size() > 0) {
                event.getMessage().addReaction(Emoji.fromUnicode("⬆️")).queue();
                event.getMessage().addReaction(Emoji.fromUnicode("⬇️")).queue();
                event.getMessage().addReaction(Emoji.fromUnicode("❤️")).queue();
            }
        });
    }

    @SubscribeEvent
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser() == null) return;
        if (!event.isFromGuild()) return;
        if (event.getUser().isBot()) return;
        database.getServerMemesChannel(event.getGuild()).ifPresent(channel -> {
            if (event.getChannel().getId().equals(channel.getId())) {
                if (event.getReaction().getEmoji().asUnicode().getName().equals("❤️")) {
                    event.getUser().openPrivateChannel().queue(dm -> {
                        event.getChannel().retrieveMessageById(event.getMessageId()).queue(msg -> {
                            EmbedBuilder builder = new EmbedBuilder()
                                    .setColor(Color.YELLOW)
                                    .setAuthor(msg.getAuthor().getAsTag(), null, msg.getAuthor().getEffectiveAvatarUrl())
                                    .setDescription("[Jump](" + msg.getJumpUrl() + ")\n\n" + msg.getContentRaw());
                            msg.getAttachments().stream().findFirst().ifPresent(attachment -> builder.setImage(attachment.getUrl()));
                            dm.sendMessageEmbeds(builder.build()).queue();
                        });
                    });
                }
            }
        });
    }
}
