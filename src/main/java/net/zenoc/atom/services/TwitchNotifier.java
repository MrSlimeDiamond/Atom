package net.zenoc.atom.services;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.zenoc.atom.Atom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TwitchNotifier implements Service {

    private static final Logger log = LoggerFactory.getLogger(TwitchNotifier.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    String API_TOKEN = "2ba2nezninfo0twz2coertacwzcgfn";

    TwitchClient client;

    @Override
    public void startService() throws Exception {
        client = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .build();

        scheduler.scheduleAtFixedRate(this::refreshStreams, 0, 1, TimeUnit.MINUTES);
    }

    public void refreshStreams() {
        DiscordBot.jda.getGuilds().forEach(this::refreshStreams);
    }

    public void refreshStreams(Guild guild) {
        Thread.currentThread().setName("Twitch Refresh Thread");
        log.info("Streams are being refreshed for " + guild.getName());
        Atom.database.getServerStreamers(guild).ifPresent(streamers -> {
            StreamList streams = client.getHelix().getStreams(API_TOKEN, null, null, null, null, null, null, streamers).execute();
            MessageCreateBuilder builder = new MessageCreateBuilder();
            StringJoiner joiner = new StringJoiner(", ");
            streams.getStreams().forEach(stream -> {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setDescription(stream.getTitle())
                        .setTitle("Watch - twitch.tv/" + stream.getUserLogin(), "https://twitch.tv/" + stream.getUserLogin())
                        .setFooter(stream.getGameName());

                User user = client.getHelix().getUsers(API_TOKEN, Arrays.asList(stream.getUserId()), null).execute().getUsers().get(0);
                embedBuilder.setAuthor(user.getDisplayName(), "https://twitch.tv/" + user.getLogin(), user.getProfileImageUrl());
                builder.addEmbeds(embedBuilder.build());
                joiner.add("**" + user.getDisplayName() + "**");
            });

            Atom.database.getServerStreamsChannel(guild).ifPresent(channel -> {
                MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
                List<Message> messages = history.getRetrievedHistory();

                Message message = messages.stream()
                        .filter(msg -> msg.getAuthor() == DiscordBot.jda.getSelfUser())
                        .findFirst().orElseGet(() -> this.createStreamsMessage(channel));

                if (joiner.length() == 0) {
                    message.editMessage("**Nobody is live!**").setEmbeds().queue();
                } else {
                    builder.addContent(joiner + (builder.getEmbeds().size() == 1 ? " is" : " are") + " currently streaming!");
                    message.editMessage(MessageEditBuilder.fromCreateData(builder.build()).build()).queue();
                }
            });
        });
    }

    private Message createStreamsMessage(TextChannel channel) {
        return channel.sendMessage("**Nobody is live!**").complete();
    }
}
