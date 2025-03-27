package net.slimediamond.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.data.Database;
import net.slimediamond.atom.data.DatabaseV2;
import net.slimediamond.atom.data.keys.GuildKeys;
import net.slimediamond.atom.discord.entities.Guild;

import javax.annotation.Nullable;
import java.awt.*;

@Service(value = "meme vote")
public class MemeVoteService extends ListenerAdapter {
    @Inject
    @Nullable
    private JDA jda;

    @GetService
    private DatabaseV2 database;

    @Service.Start
    public void startService() throws Exception {
        if (jda == null) return;
        jda.addEventListener(this);
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getMessage().getContentRaw().toLowerCase().contains("stfu atom")) return;
        Guild guild = database.getGuild(event.getGuild()).orElseThrow();
        guild.get(GuildKeys.MEMES_CHANNEL).ifPresent(channel -> {
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
        Guild guild = database.getGuild(event.getGuild()).orElseThrow();
        guild.get(GuildKeys.MEMES_CHANNEL).ifPresent(channel -> {
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
