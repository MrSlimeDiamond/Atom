package net.zenoc.atom.services;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.zenoc.atom.Atom;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class Pinnerino extends ListenerAdapter implements Service {
    private static Logger log = LoggerFactory.getLogger(Pinnerino.class);
    @Override
    public void startService() throws Exception {
        DiscordBot.jda.addEventListener(this);
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
            if (Atom.database.isChannelBlacklistedPinnerino(event.getChannel().getIdLong())) return;
            if (Atom.database.isMessagePinnerinoed(event.getMessageIdLong())) return;
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            message.getReactions().forEach(emoji -> {
                Atom.database.getServerPinnerinoThreshold(event.getGuild()).ifPresent(threshold -> {
                    if (emoji.getCount() == threshold) {
                        Atom.database.getServerPinnerinoEmoji(event.getGuild()).ifPresent(pinEmoji -> {
                            if (emoji.getEmoji().equals(pinEmoji)) {
                                pinMessage(message, emoji.getCount());
                            }
                        });
                    }
                });
            });
    }

    public void pinMessage(Message message, int count) {
        Atom.database.getServerPinnerinoChannel(message.getGuild()).ifPresent(channel -> {
            Webhook webhook = channel.retrieveWebhooks().complete().stream()
                    .filter(hook -> hook.getName().toLowerCase().contains("atom") || hook.getName().toLowerCase().contains("pinnerino"))
                    .findAny().orElseGet(() -> this.createPinWebhook(channel));

            WebhookClientBuilder client = new WebhookClientBuilder(webhook.getUrl()).setAllowedMentions(AllowedMentions.none());
            WebhookMessageBuilder builder = new WebhookMessageBuilder()
                    .setUsername(message.getMember().getEffectiveName())
                    .setAvatarUrl(message.getMember().getEffectiveAvatarUrl())
                    .setContent(message.getContentRaw())
                    .setAllowedMentions(AllowedMentions.none());

            message.getAttachments().forEach(attachment -> {
                try {
                    builder.addFile(attachment.getFileName(), attachment.downloadToFile().get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });

            message.getEmbeds().forEach(embed -> {
                WebhookEmbedBuilder webhookEmbedBuilder = WebhookEmbedBuilder.fromJDA(embed);
                builder.addEmbeds(webhookEmbedBuilder.build());
            });
            WebhookEmbed jumpEmbed = new WebhookEmbedBuilder()
                    .setColor(0xff0000)
                    .setDescription(count + " " + Atom.database.getServerPinnerinoEmoji(message.getGuild()).get().getAsReactionCode() + " - [Jump](" + message.getJumpUrl() + ")")
                    .build();
            builder.addEmbeds(jumpEmbed);

            client.build().send(builder.build()).thenAccept(msg -> {
                Atom.database.addPinnerino(message.getGuild().getIdLong(), message.getIdLong(), msg.getId());
            });
        });
    }

    private Webhook createPinWebhook(TextChannel channel) {
        return channel.createWebhook("Atom Pinnerino").complete();
    }

}
