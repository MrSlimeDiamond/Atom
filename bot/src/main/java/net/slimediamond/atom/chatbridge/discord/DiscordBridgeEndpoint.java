package net.slimediamond.atom.chatbridge.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeMessage;
import net.slimediamond.atom.chatbridge.EventType;
import net.slimediamond.atom.database.Database;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordBridgeEndpoint implements BridgeEndpoint {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private TextChannel channel;
    private Database database;
    private String identifier;
    private int id;

    private HashMap<BridgeEndpoint, ArrayList<MessageEmbed>> queuedUpdates = new HashMap<>();

    public DiscordBridgeEndpoint(TextChannel channel, String identifier, int id) {
        this.channel = channel;
        this.database = Atom.getServiceManager().getInstance(Database.class);
        this.identifier = identifier;
        this.id = id;

        scheduler.scheduleAtFixedRate(this::forceSendUpdate, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        forceSendUpdate();
        channel.retrieveWebhooks().queue(webhooks -> {
            String avatarUrl = message.avatarUrl();
            if (avatarUrl == null) {
                avatarUrl = channel.getGuild().getIconUrl();
            }
            boolean useEmbed = true;
            for (Webhook webhook : webhooks) {
                if (webhook.getName().toLowerCase().contains("atom") || webhook.getName().toLowerCase().contains("bridge")) {
                    // Use the webhook, else use embeds.
                    useEmbed = false;
                    try {
                        sendWebhook(webhook.getUrl(), message.username(), message.content(), avatarUrl, source.getShortName());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (useEmbed) sendEmbed(channel, message.username(), message.content(), source.getShortName());
        });
    }

    @Override
    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        // Chat bridge connection
        if (eventType == EventType.CONNECT) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor("Chat bridge connected")
                    .build()).queue();
        } else if (eventType == EventType.DISCONNECT) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor("Chat bridge disconnected")
                    .build()).queue();
        } else if (eventType == EventType.JOIN) {
            queueUpdate(source, new EmbedBuilder()
                    .setDescription("**" + username + "** joined")
                    .build());
        } else if (eventType == EventType.LEAVE) {
            queueUpdate(source, new EmbedBuilder()
                    .setDescription("**" + username + "** left")
                    .build());
        } else if (eventType == EventType.QUIT) {
            queueUpdate(source, new EmbedBuilder()
                    .setDescription("**" + username + "** quit (" + comment + ")")
                    .build());
        } else if (eventType == EventType.NAME_CHANGE) {
            queueUpdate(source, new EmbedBuilder()
                    .setDescription("**" + username + "** is now known as **" + comment + "**")
                    .build());
        }
    }

    @Override
    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {
        this.sendMessage(new BridgeMessage(message.username(), message.avatarUrl(),"*" + message.content() + "*"), source);

    }

    private void queueUpdate(BridgeEndpoint source, MessageEmbed embed) {
        // Add the new embed to the queue for this endpoint
        queuedUpdates.computeIfAbsent(source, k -> new ArrayList<>()).add(embed);
        sendUpdateIfApplicable(source);
    }

    private void sendUpdateIfApplicable(BridgeEndpoint source) {
        ArrayList<MessageEmbed> embeds = queuedUpdates.computeIfAbsent(source, k -> new ArrayList<>());
        // discord max is 10
        if (embeds.size() >= 10) {
            forceSendUpdate(source);
        }
    }

    private void forceSendUpdate(BridgeEndpoint source) {
        ArrayList<MessageEmbed> embeds = queuedUpdates.get(source);
        if (embeds == null || embeds.isEmpty()) return;
        channel.retrieveWebhooks().queue(webhooks -> {
            boolean useEmbed = true;
            for (Webhook webhook : webhooks) {
                if (webhook.getName().toLowerCase().contains("atom") || webhook.getName().toLowerCase().contains("bridge")) {
                    // Use the webhook if it matches the criteria
                    useEmbed = false;
                    try {
                        String avatarUrl = database.getBridgedEndpointAvatar(this.id);
                        if (avatarUrl == null) {
                            avatarUrl = channel.getGuild().getIconUrl();
                        }
                        WebhookClient client = WebhookClient.withUrl(webhook.getUrl());
                        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                                .setUsername(source.getChannelName() + " [" + source.getShortName() + "]")
                                .setAvatarUrl(avatarUrl);

                        // Send queued updates as webhook embeds
                        embeds.forEach(embed -> builder.addEmbeds(WebhookEmbedBuilder.fromJDA(embed).build()));
                        client.send(builder.build());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (useEmbed) {
                // If no webhook matched, send as embeds in the channel
                channel.sendMessageEmbeds(embeds).queue();
            }
        });

        // Clear the updates after sending
        queuedUpdates.remove(source);
    }

    private void forceSendUpdate() {
        // look thru sources here
        // Create a copy of the key set to avoid concurrent modification
        new HashMap<>(queuedUpdates).forEach((source, embeds) -> {
            if (source != null) {
                forceSendUpdate(source);
            }
        });
    }

    @Override
    public String getName() {
        return "Discord";
    }

    @Override
    public String getShortName() {
        return "DIS";
    }

    @Override
    public String getType() {
        return "discord";
    }

    @Override
    public String getChannelName() {
        return channel.getName();
    }

    @Override
    public String getUniqueIdentifier() {
        return this.identifier;
    }

    @Override
    public int getId() {
        return this.id;
    }

    private void sendEmbed(TextChannel channel, String nickname, String content, String source) {
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(nickname + "[" + source + "]")
                .setDescription(content)
                .setColor(Color.GREEN)
                .build();
        channel.sendMessageEmbeds(embed).queue();
    }

    private void sendWebhook(String url, String nickname, String content, String avatarUrl, String source) throws SQLException {
        WebhookClient client = WebhookClient.withUrl(url);
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(nickname + " [" + source + "]")
                .setContent(content)
                .setAvatarUrl(avatarUrl);
        client.send(builder.build());
    }
}
