package net.slimediamond.atom.services;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.data.Database;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.ExecutionException;

@Service(value = "pinnerino")
public class Pinnerino extends ListenerAdapter {
    @Inject
    @Nullable
    private JDA jda;

    @GetService
    private Database database;
    
    @Service.Start
    public void startService() throws Exception {
        if (jda == null) return;
        jda.addEventListener(this);
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
            if (database.isChannelBlacklistedPinnerino(event.getChannel().getIdLong())) return;
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            message.getReactions().forEach(emoji -> database.getServerPinnerinoThreshold(event.getGuild()).ifPresent(threshold -> {
                if (emoji.getCount() >= threshold) {
                    database.getServerPinnerinoEmoji(event.getGuild()).ifPresent(pinEmoji -> {
                        if (emoji.getEmoji().equals(pinEmoji)) {
                            if (database.isMessagePinnerinoed(message.getIdLong())) {
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
        if (database.isMessagePinnerinoed(event.getMessageIdLong())) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

            // Update it to 0 because there might not be any reactions left after one is removed
            updatePin(event.getChannel().asTextChannel(), message, 0, event.getReaction());

            message.getReactions().forEach(emoji -> {
                database.getServerPinnerinoEmoji(event.getGuild()).ifPresent(pinEmoji -> {
                    if (emoji.getEmoji().equals(pinEmoji)) {
                        updatePin(event.getChannel().asTextChannel(), message, emoji.getCount(), event.getReaction());
                    }
                });
            });
        }
    }

    public void onMessageUpdate(MessageUpdateEvent event) {
        if (database.isMessagePinnerinoed(event.getMessageIdLong())) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
            message.getReactions().forEach(emoji -> {
                database.getServerPinnerinoEmoji(event.getGuild()).ifPresent(pinEmoji -> {
                    if (emoji.getEmoji().equals(pinEmoji)) {
                        updatePin(event.getChannel().asTextChannel(), message, emoji.getCount(), emoji);
                    }
                });
            });
        }
    }

    public void onMessageDelete(MessageDeleteEvent event) {
        if (database.isMessagePinnerinoed(event.getMessageIdLong())) {
            database.getPinnerino(event.getGuild(), event.getMessageIdLong()).ifPresent(pin -> pin.queue(message -> message.delete().queue()));
        }
    }

    public void pinMessage(Message message, int count, MessageReaction emoji) {
        database.getServerPinnerinoChannel(message.getGuild()).ifPresent(channel -> {
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

            client.build().send(builder.build()).thenAccept(msg -> database.addPinnerino(message.getGuild().getIdLong(), message.getIdLong(), msg.getId()));
        });
    }

    public void updatePin(TextChannel channel, Message message, int count, MessageReaction emoji) {
        database.getServerPinnerinoChannel(message.getGuild()).ifPresent(pinChannel -> {
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
            database.getPinnerino(channel.getGuild(), message.getIdLong()).ifPresent(notif -> notif.queue(pinNotification -> client.build().edit(pinNotification.getId(), builder.build())));
        });
    }

    private Webhook createPinWebhook(TextChannel channel) {
        return channel.createWebhook("Atom Pinnerino").complete();
    }

    public static WebhookMessageBuilder fromMessage(Message message, Boolean attachments) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setUsername(message.getAuthor().getEffectiveName());
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
