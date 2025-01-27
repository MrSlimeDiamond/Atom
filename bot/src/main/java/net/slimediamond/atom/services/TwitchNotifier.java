package net.slimediamond.atom.services;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.User;
import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.slimediamond.atom.reference.TwitchReference;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service(value = "twitch notifier")
public class TwitchNotifier {
    @Inject
    private JDA jda;

    @Inject
    private Logger logger;

    @GetService
    private Database database;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    TwitchClient client;

    @Service.Start
    public void startService() throws Exception {
        client = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .build();

        scheduler.scheduleAtFixedRate(this::refreshStreams, 0, 1, TimeUnit.MINUTES);
    }

    public void refreshStreams() {
        jda.getGuilds().forEach(this::refreshStreams);
    }

    public void refreshStreams(Guild guild) {
        Thread.currentThread().setName("Twitch Refresh Thread");
        logger.info("Streams are being refreshed for " + guild.getName());
        database.getServerStreamers(guild).ifPresent(streamers -> {
            StreamList streams = client.getHelix().getStreams(TwitchReference.API_TOKEN, null, null, null, null, null, null, streamers).execute();
            ArrayList<String> live = new ArrayList<>();
            streams.getStreams().forEach(stream -> {
                live.add(stream.getUserLogin());
                try {
                    ArrayList<Long> messageIDs = database.getStreamerMessageIDs(stream.getUserLogin());

                    // if they were not live last time we checked
                    if (messageIDs.isEmpty()) {
                        MessageCreateBuilder builder = new MessageCreateBuilder();
                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setDescription(stream.getTitle())
                                .setTitle("Watch - twitch.tv/" + stream.getUserLogin(), "https://twitch.tv/" + stream.getUserLogin())
                                .setFooter(stream.getGameName());

                        User user = client.getHelix().getUsers(TwitchReference.API_TOKEN, Arrays.asList(stream.getUserId()), null).execute().getUsers().get(0);
                        embedBuilder.setAuthor(user.getDisplayName(), "https://twitch.tv/" + user.getLogin(), user.getProfileImageUrl());
                        builder.addEmbeds(embedBuilder.build());

                        database.getServerStreamsChannel(guild).ifPresent(channel -> {
                            channel.sendMessage(builder.build()).queue(result -> {
                                try { // FUUUUUCK
                                    database.addStreamerMessage(stream.getUserLogin(), result.getIdLong());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        });
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            database.getServerStreamsChannel(guild).ifPresent(channel -> {
                streamers.forEach(streamer -> {
                    // they are NOT live
                    if (!live.contains(streamer)) {
                        try {
                            // delete any messages which are there
                            database.getStreamerMessageIDs(streamer).forEach(id -> {
//                                System.out.println(id);
                                channel.deleteMessageById(id).queue();
                            });
                        } catch (Exception ignored) {} // don't care about it
                    }
                });
            });
        });
    }
}
