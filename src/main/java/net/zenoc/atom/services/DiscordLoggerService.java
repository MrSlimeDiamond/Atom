package net.zenoc.atom.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CachedMessage;
import net.zenoc.atom.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;

public class DiscordLoggerService extends ListenerAdapter implements Service {
    private static Logger log = LoggerFactory.getLogger(DiscordLoggerService.class);

    @Override
    public void startService() throws Exception {
        JDA jda = DiscordBot.jda;

        jda.addEventListener(this);
    }

    @SubscribeEvent
    public void onMessageDelete(MessageDeleteEvent event) {
        Atom.database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            Atom.database.getMessage(event.getMessageIdLong()).ifPresentOrElse(cachedMessage -> {
                EmbedBuilder builder = new EmbedBuilder()
                        .setAuthor(cachedMessage.getUser().getAsTag(), null, cachedMessage.getUser().getAvatarUrl())
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
        Atom.database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            Atom.database.getMessage(event.getMessageIdLong()).ifPresentOrElse(cachedMessage -> {
                EmbedBuilder builder = new EmbedBuilder()
                        .setAuthor(UserUtil.getUserName(event.getAuthor()), null, event.getAuthor().getAvatarUrl())
                        .setDescription("Message Updated - [Jump](" + event.getJumpUrl() + ")")
                        .addField("Old Content", cachedMessage.getMessageContent(), false)
                        .addField("New Content", event.getMessage().getContentDisplay(), false);
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
                        .setDescription("Message Updated - [Jump](" + event.getJumpUrl() + ")\n\n*Unable to get content information*");
                Guild guild = event.getGuild();
                event.getJDA().retrieveUserById(event.getAuthor().getId()).queue(user -> {
                    guild.retrieveMember(user).queue(member -> {
                        builder.setColor(member.getColor());
                    });
                });
                channel.sendMessageEmbeds(builder.build()).queue();
            });
            Atom.database.updateMessage(event.getMessageIdLong(), event.getMessage().getContentDisplay());
        });
    }

    @SubscribeEvent
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Atom.database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                    .setTitle(event.getUser().getName() + " left")
                    .build();

            channel.sendMessageEmbeds(embed).queue();
        });
    }

    @SubscribeEvent
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Atom.database.getGuildLog(event.getGuild()).ifPresent(channel -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                    .setTitle(event.getUser().getName() + " joined")
                    .build();

            channel.sendMessageEmbeds(embed).queue();
        });
    }

}
