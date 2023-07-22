package net.slimediamond.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.util.UserUtil;

import java.awt.*;

@Service("discord logger")
public class DiscordLoggerService extends ListenerAdapter {
    @Inject
    private JDA jda;

    @GetService
    private Database database;

    @Service.Start
    public void startService() throws Exception {
        jda.addEventListener(this);
    }

    @SubscribeEvent
    public void onMessageDelete(MessageDeleteEvent event) {
        database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            database.getMessage(event.getMessageIdLong()).ifPresentOrElse(cachedMessage -> {
                EmbedBuilder builder = new EmbedBuilder()
                        .setAuthor(UserUtil.getUserName(cachedMessage.getUser()), null, cachedMessage.getUser().getAvatarUrl())
                        .setDescription("Message Deleted in <#" + event.getChannel().getId() + ">")
                        .addField("Content", cachedMessage.getMessageContent(), false);
                Guild guild = cachedMessage.getGuild();
                guild.retrieveMember(cachedMessage.getUser()).queue(member -> {
                    builder.setColor(member.getColor());
                });
                channel.sendMessageEmbeds(builder.build()).queue();
            }, () -> {
                MessageEmbed embed = new EmbedBuilder()
                        .setDescription("Message Deleted in <#" + event.getChannel().getId() + ">")
                        .addField("Content", "*Unable to get content information*", false)
                        .build();
                channel.sendMessageEmbeds(embed).queue();
            });
        });
    }

    @SubscribeEvent
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getAuthor().isBot()) return;
        database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            database.getMessage(event.getMessageIdLong()).ifPresentOrElse(cachedMessage -> {
                String oldContent = cachedMessage.getMessageContent();
                String newContent = event.getMessage().getContentRaw();
                if (oldContent.equals(newContent)) return;

                EmbedBuilder builder = new EmbedBuilder()
                        .setAuthor(UserUtil.getUserName(event.getAuthor()), null, event.getAuthor().getAvatarUrl())
                        .setDescription("Message Updated - [Jump](" + event.getJumpUrl() + ")")
                        .addField("Old Content", oldContent, false)
                        .addField("New Content", newContent, false)
                        .setFooter("Message ID: " + event.getMessageId());
                Guild guild = event.getGuild();
                event.getJDA().retrieveUserById(event.getAuthor().getId()).queue(user -> {
                    guild.retrieveMember(user).queue(member -> {
                        builder.setColor(member.getColor());
                    });
                });
                channel.sendMessageEmbeds(builder.build()).queue();
            }, () -> {
                EmbedBuilder builder = new EmbedBuilder()
                        .setAuthor(UserUtil.getUserName(event.getAuthor()), null, event.getAuthor().getAvatarUrl())
                        .setDescription("Message Updated - [Jump](" + event.getJumpUrl() + ")\n\n*Unable to get content information*")
                        .setFooter("Message ID: " + event.getMessageId());
                Guild guild = event.getGuild();
                event.getJDA().retrieveUserById(event.getAuthor().getId()).queue(user -> {
                    guild.retrieveMember(user).queue(member -> {
                        builder.setColor(member.getColor());
                    });
                });
                channel.sendMessageEmbeds(builder.build()).queue();
            });
            database.updateMessage(event.getMessageIdLong(), event.getMessage().getContentRaw());
        });
    }

    @SubscribeEvent
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor(UserUtil.getUserName(event.getUser()), null, event.getUser().getAvatarUrl())
                    .setTitle(event.getUser().getName() + " left")
                    .build();

            channel.sendMessageEmbeds(embed).queue();
        });
    }

    @SubscribeEvent
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(UserUtil.getUserName(event.getUser()), null, event.getUser().getAvatarUrl())
                    .setTitle(event.getUser().getName() + " joined")
                    .build();

            channel.sendMessageEmbeds(embed).queue();
        });
    }

}
