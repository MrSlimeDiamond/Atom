package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;

/**
 * Triggered either by a slash command or a
 * regular text command.
 *
 * Just a nice helper class so that both of those are on the same page.
 */
public class AtomDiscordCommandEvent {
    private User user;
    private Guild guild;
    private MessageChannel channel;
    private Member member;
    private boolean isFromGuild;
    private boolean deferred;
    private Message message;

    @Nullable private SlashCommandInteractionEvent slashCommandInteractionEvent;

    public AtomDiscordCommandEvent(MessageReceivedEvent event) {
        this.user = event.getAuthor();
        this.channel = event.getChannel();
        this.member = event.getMember();
        this.isFromGuild = event.isFromGuild();
        this.message = event.getMessage();

        if (event.isFromGuild()) {
            this.guild = event.getGuild();
        }
    }

    public AtomDiscordCommandEvent(SlashCommandInteractionEvent event) {
        this.slashCommandInteractionEvent = event;
        this.user = event.getUser();
        this.channel = event.getChannel();
        this.member = event.getMember();
        this.isFromGuild = event.isFromGuild();

        if (event.isFromGuild()) {
            this.guild = event.getGuild();
        }
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public boolean isFromGuild() {
        return isFromGuild;
    }

    public Member getMember() {
        return member;
    }

    public void setDeferred(boolean deferred) {
        this.deferred = deferred;
    }

    @Nullable
    public Message getMessage() {
        return this.message;
    }

    public void reply(String message) {
        if (isTextCommand()) {
            channel.sendMessage(message).queue();
        } else {
            if (slashCommandInteractionEvent != null) {
                if (deferred) {
                    slashCommandInteractionEvent.getHook().sendMessage(message).queue();
                } else {
                    slashCommandInteractionEvent.reply(message).queue();
                }
            }
        }
    }

    public void replyEmbeds(MessageEmbed... embeds) {
        if (isTextCommand()) {
            this.channel.sendMessageEmbeds(Arrays.asList(embeds)).queue();
        } else {
            if (slashCommandInteractionEvent != null) {
                if (deferred) {
                    slashCommandInteractionEvent.getHook().sendMessageEmbeds(Arrays.asList(embeds)).queue();
                } else {
                    slashCommandInteractionEvent.replyEmbeds(Arrays.asList(embeds)).queue();
                }
            }
        }
    }

    public boolean isTextCommand() {
        return slashCommandInteractionEvent == null;
    }

    public @Nullable SlashCommandInteractionEvent getSlashCommandInteractionEvent() {
        return slashCommandInteractionEvent;
    }
}
