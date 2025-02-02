package net.slimediamond.atom.chatbridge.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeMessage;
import net.slimediamond.atom.chatbridge.EventType;
import net.slimediamond.atom.chatbridge.Netsplit;
import net.slimediamond.atom.util.DiscordUtil;

import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordBridgeEndpoint implements BridgeEndpoint {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private TextChannel channel;
    private String identifier;
    private int id;
    private boolean isEnabled;

    private final HashMap<BridgeEndpoint, ArrayList<MessageEmbed>> queuedUpdates = new HashMap<>();

    public DiscordBridgeEndpoint(TextChannel channel, String identifier, int id, boolean isEnabled) {
        this.channel = channel;
        this.identifier = identifier;
        this.id = id;
        this.isEnabled = isEnabled;

        scheduler.scheduleAtFixedRate(this::forceSendUpdate, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        forceSendUpdate();
        awaitEmptyQueue();
        channel.retrieveWebhooks().queue(webhooks -> {
            String avatarUrl = message.getAvatarUrl();
            if (avatarUrl == null) {
                avatarUrl = channel.getGuild().getIconUrl();
            }

            ArrayList<File> files = message.getFiles();

            boolean useEmbed = true;
            for (Webhook webhook : webhooks) {
                if (webhook.getName().toLowerCase().contains("atom") || webhook.getName().toLowerCase().contains("bridge")) {
                    // Use the webhook, else use embeds.
                    useEmbed = false;
                    try {
                        sendWebhook(webhook.getUrl(), message.getUsername(), DiscordUtil.removeFormatting(message.getContent()), avatarUrl, source.getShortName(), files);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (useEmbed) sendEmbed(channel, DiscordUtil.removeFormatting(message.getUsername()), DiscordUtil.removeFormatting(message.getContent()), source.getShortName(), files);
        });
    }

    // TODO: rework this so it's less garbage
    @Override
    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        if (!this.isEnabled) return;
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
            String msg = "**" + username + "** joined";
            if (comment != null) {
                msg = "**" + username + "** was added by **" + comment + "**";
            }
            queueUpdate(source, new EmbedBuilder()
                    .setDescription(msg)
                    .build());
        } else if (eventType == EventType.LEAVE) {
            String msg = "**" + username + "** left";
            if (comment != null) {
                msg = "**" + username + "** was removed by **" + comment + "**";
            }
            queueUpdate(source, new EmbedBuilder()
                    .setDescription(msg)
                    .build());
        } else if (eventType == EventType.QUIT) { // IRC only
            queueUpdate(source, new EmbedBuilder()
                    .setDescription("**" + username + "** quit (" + comment + ")")
                    .build());
        } else if (eventType == EventType.NAME_CHANGE) {
            queueUpdate(source, new EmbedBuilder()
                    .setDescription("**" + username + "** is now known as **" + comment + "**")
                    .build());
        }
    }

    public void awaitEmptyQueue() {
        try {
            synchronized (queuedUpdates) {
                while (!queuedUpdates.isEmpty()) {
                    queuedUpdates.wait();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        this.sendMessage(new BridgeMessage(message.getUsername(), message.getAvatarUrl(),"*" + message.getContent() + "*"), source);

    }

    @Override
    public void netsplitQuits(Netsplit netsplit, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        String quits = String.join(", ", netsplit.getQuits());
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Netsplit")
                .setTitle(DiscordUtil.removeFormatting(netsplit.getServers()[0] + " <-> " + netsplit.getServers()[1]))
                .setDescription("**Quits:** " + DiscordUtil.removeFormatting(quits))
                .build();
        queueUpdate(source, embed);
    }

    @Override
    public void netsplitJoins(Netsplit netsplit, BridgeEndpoint source) {
        if (!this.isEnabled) return;
        String joins = String.join(", ", netsplit.getJoins());
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Netsplit over")
                .setDescription("**Joins:** " + DiscordUtil.removeFormatting(joins))
                .build();
        queueUpdate(source, embed);
    }

    @Override
    public String getAvatarUrl() {
        return channel.getGuild().getIconUrl();
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
                    String avatarUrl = source.getAvatarUrl();
                    if (avatarUrl == null) {
                        avatarUrl = this.getAvatarUrl();
                    }
                    WebhookClient client = WebhookClient.withUrl(webhook.getUrl());
                    WebhookMessageBuilder builder = new WebhookMessageBuilder()
                            .setUsername(source.getChannelName() + " [" + source.getShortName() + "]")
                            .setAvatarUrl(avatarUrl);

                    // Send queued updates as webhook embeds
                    embeds.forEach(embed -> builder.addEmbeds(WebhookEmbedBuilder.fromJDA(embed).build()));
                    client.send(builder.build()).thenAccept(msg -> {
                        // after we confirm that message has sent, we can remove it
                        // and also possibly notify things which are
                        // waiting on it
                        synchronized (queuedUpdates) {
                            queuedUpdates.remove(source);
                            if (queuedUpdates.isEmpty()) {
                                queuedUpdates.notifyAll();
                            }
                        }
                    });
                }
            }
            if (useEmbed) {
                // If no webhook matched, send as embeds in the channel
                channel.sendMessageEmbeds(embeds).queue(msg -> {
                    // after we confirm that message has sent, we can remove it
                    // and also possibly notify things which are
                    // waiting on it
                    synchronized (queuedUpdates) {
                        queuedUpdates.remove(source);
                        if (queuedUpdates.isEmpty()) {
                            queuedUpdates.notifyAll();
                        }
                    }
                });
            }
        });
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

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    private void sendEmbed(TextChannel channel, String nickname, String content, String source, ArrayList<File> files) {
        MessageCreateBuilder builder = new MessageCreateBuilder();

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(nickname + "[" + source + "]")
                .setDescription(content)
                .setColor(Color.GREEN)
                .build();

        builder.addEmbeds(embed);
        files.forEach(file -> builder.addFiles(FileUpload.fromData(file)));
        channel.sendMessageEmbeds(embed).queue();
    }

    private void sendWebhook(String url, String nickname, String content, String avatarUrl, String source, ArrayList<File> files) throws SQLException {
        WebhookClient client = WebhookClient.withUrl(url);
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(nickname + " [" + source + "]")
                .setContent(content)
                .setAvatarUrl(avatarUrl);

        files.forEach(builder::addFile);
        client.send(builder.build());
    }
}
