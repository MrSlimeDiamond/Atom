package net.zenoc.atom.services;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.vdurmont.emoji.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.zenoc.atom.Atom;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Pinnerino extends ListenerAdapter implements Service {
    private static Logger log = LoggerFactory.getLogger(Pinnerino.class);
    @Override
    public void startService() throws Exception {
        DiscordBot.jda.addEventListener(this);
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
            if (Atom.database.isChannelBlacklistedPinnerino(event.getChannel().getIdLong())) return;
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            message.getReactions().forEach(emoji -> Atom.database.getServerPinnerinoThreshold(event.getGuild()).ifPresent(threshold -> {
                if (emoji.getCount() >= threshold) {
                    Atom.database.getServerPinnerinoEmoji(event.getGuild()).ifPresent(pinEmoji -> {
                        if (emoji.getEmoji().equals(pinEmoji)) {
                            if (Atom.database.isMessagePinnerinoed(message.getIdLong())) {
                                updatePin(message.getChannel().asTextChannel(), message, emoji.getCount(), emoji);
                            } else {
                                pinMessage(message, emoji.getCount(), emoji);
                            }
                        }
                    });
                }
            }));
    }

    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (Atom.database.isMessagePinnerinoed(event.getMessageIdLong())) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            // Update it to 0 because there might not be any reactions left after one is removed
            updatePin(event.getChannel().asTextChannel(), message, 0, event.getReaction());

            message.getReactions().forEach(emoji -> {
                Atom.database.getServerPinnerinoEmoji(event.getGuild()).ifPresent(pinEmoji -> {
                    if (emoji.getEmoji().equals(pinEmoji)) {
                        updatePin(event.getChannel().asTextChannel(), message, emoji.getCount(), event.getReaction());
                    }
                });
            });
        }
    }

    public void pinMessage(Message message, int count, MessageReaction emoji) {
        Atom.database.getServerPinnerinoChannel(message.getGuild()).ifPresent(channel -> {
            Webhook webhook = channel.retrieveWebhooks().complete().stream()
                    .filter(hook -> hook.getName().toLowerCase().contains("atom") || hook.getName().toLowerCase().contains("pinnerino"))
                    .findAny().orElseGet(() -> this.createPinWebhook(channel));

            WebhookClientBuilder client = new WebhookClientBuilder(webhook.getUrl()).setAllowedMentions(AllowedMentions.none());
            WebhookMessageBuilder builder = fromMessage(message, true);
            WebhookEmbed jumpEmbed = new WebhookEmbedBuilder()
                    .setColor(0xff0000)
                    .setDescription(emoji.getEmoji().getFormatted() + count + " - [Jump](" + message.getJumpUrl() + ")")
                    .build();
            builder.addEmbeds(jumpEmbed);

            client.build().send(builder.build()).thenAccept(msg -> Atom.database.addPinnerino(message.getGuild().getIdLong(), message.getIdLong(), msg.getId()));
        });
    }

    public void updatePin(TextChannel channel, Message message, int count, MessageReaction emoji) {
        Atom.database.getServerPinnerinoChannel(message.getGuild()).ifPresent(pinChannel -> {
            Webhook webhook = pinChannel.retrieveWebhooks().complete().stream()
                    .filter(hook -> hook.getName().toLowerCase().contains("atom") || hook.getName().toLowerCase().contains("pinnerino"))
                    .findAny().orElseGet(() -> this.createPinWebhook(pinChannel));

            WebhookClientBuilder client = new WebhookClientBuilder(webhook.getUrl()).setAllowedMentions(AllowedMentions.none());
            WebhookMessageBuilder builder = fromMessage(message, false);
            WebhookEmbed jumpEmbed = new WebhookEmbedBuilder()
                    .setColor(0xff0000)
                    .setDescription(emoji.getEmoji().getFormatted() + count + " - [Jump](" + message.getJumpUrl() + ")")
                    .build();
            builder.addEmbeds(jumpEmbed);
            Atom.database.getPinnerino(channel.getGuild(), message.getIdLong()).ifPresent(notif -> notif.queue(pinNotification -> client.build().edit(pinNotification.getId(), builder.build())));
        });
    }

    private Webhook createPinWebhook(TextChannel channel) {
        return channel.createWebhook("Atom Pinnerino").complete();
    }

    public static WebhookMessageBuilder fromMessage(Message message, Boolean attachments) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(message.getAuthor().getName());
        builder.setAvatarUrl(message.getAuthor().getEffectiveAvatarUrl());
        builder.setContent(message.getContentRaw());
        builder.setAllowedMentions(AllowedMentions.none());
        message.getEmbeds().forEach(embed -> builder.addEmbeds(WebhookEmbedBuilder.fromJDA(embed).build()));
        if (attachments) {
            message.getAttachments().forEach(attachment -> {
                try {
                    builder.addFile(attachment.getFileName(), attachment.downloadToFile().get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return builder;
    }

}
