package net.zenoc.atom.services;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.engio.mbassy.listener.Handler;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.util.EmbedUtil;
import net.zenoc.atom.reference.DiscordReference;
import net.zenoc.atom.reference.IRCReference;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.helper.ConnectionEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatBridgeService extends ListenerAdapter implements Service {
    @Override
    public void startService() throws Exception {
        DiscordBot.jda.addEventListener(this);
        IRC.client.getEventManager().registerEventListener(this);
    }

    @Override
    public void shutdownService() {
        // TODO: Disconnect message
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getContentRaw().startsWith(DiscordReference.prefix)) return;
        try {
            if (Atom.database.isUserBlacklisted(event.getMessage().getAuthor().getIdLong())) return;
            if (Atom.database.isChannelBridged(event.getChannel().getIdLong())) {
                // Is a bridged channel
                String channel = Atom.database.getIRCBridgeChannel(event.getChannel().getIdLong());
                if (!Atom.database.isPipeEnabled(channel)) return;
                if (channel == null) return;
                IRC.client.sendMessage(channel, "[DIS] " + event.getMember().getEffectiveName() + ": " + event.getMessage().getContentDisplay());
            }
        } catch (SQLException e) {
            //event.getChannel().sendMessageEmbeds(EmbedUtil.expandedErrorEmbed("Unable to relay message: SQLException")).queue();
            throw new RuntimeException(e);
        }
    }
    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        if (event.getActor().getNick().equals(IRCReference.nickname)) return;
        try {
            if (Atom.database.isUserBlacklisted(event.getActor().getNick())) return;
            if (!Atom.database.isPipeEnabled(event.getChannel().getName())) return;
            if (event.getMessage().startsWith(IRCReference.prefix)) return;
            long channelID = Atom.database.getDiscordBridgeChannelID(event.getChannel().getName());
            if (channelID == -1L) return;
            TextChannel channel = DiscordBot.jda.getTextChannelById(channelID);
            if (channel == null) return;

            toDiscord(channel, event.getActor().getNick(), event.getMessage(), event.getChannel().getName());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Handler
    public void onChannelJoin(ChannelJoinEvent event) {
        if (event.getActor().getNick().equals(IRCReference.nickname)) return;
        try {
            if (!Atom.database.isPipeEnabled(event.getChannel().getName())) return;
            long channelID = Atom.database.getDiscordBridgeChannelID(event.getChannel().getName());
            if (channelID == -1L) return;
            TextChannel channel = DiscordBot.jda.getTextChannelById(channelID);
            if (channel == null) return;

            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setDescription(event.getActor().getNick() + " joined " + event.getChannel().getName())
                    .build();

            channel.sendMessageEmbeds(embed).queue();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Handler
    public void onChannelLeave(ChannelPartEvent event) {
        if (event.getActor().getNick().equals(IRCReference.nickname)) return;
        try {
            if (!Atom.database.isPipeEnabled(event.getChannel().getName())) return;
            long channelID = Atom.database.getDiscordBridgeChannelID(event.getChannel().getName());
            if (channelID == -1L) return;
            TextChannel channel = DiscordBot.jda.getTextChannelById(channelID);
            if (channel == null) return;

            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(event.getActor().getNick() + " left " + event.getChannel().getName())
                    .build();

            channel.sendMessageEmbeds(embed).queue();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Handler
    public void onUserQuit(UserQuitEvent event) {
        if (event.getActor().getNick().equals(IRCReference.nickname)) return;
        try {
            if (!event.getAffectedChannel().isPresent()) return;
            if (!Atom.database.isPipeEnabled(event.getAffectedChannel().get().getName())) return;
            long channelID = Atom.database.getDiscordBridgeChannelID(event.getAffectedChannel().get().getName());
            if (channelID == -1L) return;
            TextChannel channel = DiscordBot.jda.getTextChannelById(channelID);
            if (channel == null) return;

            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(event.getActor().getNick() + " quit (" + event.getMessage() + ")")
                    .build();

            channel.sendMessageEmbeds(embed).queue();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Handler
    public void onReady(ConnectionEvent event) {
        Atom.database.getBridgedChannelsDiscord().forEach(channel -> {
            if (channel == null) return;
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setDescription("Chat bridge connected")
                    .build();
            channel.sendMessageEmbeds(embed).queue();
        });
    }

    private void toDiscord(TextChannel channel, String nickname, String content, String ircChannel) {
        channel.retrieveWebhooks().queue(webhooks -> {
            boolean useEmbed = true;
            for (Webhook webhook : webhooks) {
                if (webhook.getName().toLowerCase().contains("atom") || webhook.getName().toLowerCase().contains("bridge")) {
                    // Use the webhook, else use embeds.
                    useEmbed = false;
                    try {
                        sendWebhook(webhook.getUrl(), nickname, content, ircChannel);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (useEmbed) sendEmbed(channel, nickname, content);
        });
    }

    private void sendEmbed(TextChannel channel, String nickname, String content) {
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(nickname + " [IRC]")
                .setDescription(content)
                .setColor(Color.GREEN)
                .build();
        channel.sendMessageEmbeds(embed).queue();
    }

    private void sendWebhook(String url, String nickname, String content, String channel) throws SQLException {
        WebhookClient client = WebhookClient.withUrl(url);
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setUsername(nickname + " [IRC]")
                .setContent(content)
                .setAvatarUrl(Atom.database.getChannelBridgeIcon(channel));
        client.send(builder.build());
    }
}
