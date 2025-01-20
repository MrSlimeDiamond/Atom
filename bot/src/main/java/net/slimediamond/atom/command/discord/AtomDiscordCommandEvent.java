package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

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

    @Nullable private SlashCommandInteractionEvent slashCommandInteractionEvent;

    public AtomDiscordCommandEvent(MessageReceivedEvent event) {
        this.user = event.getAuthor();
        this.channel = event.getChannel();
        this.member = event.getMember();
        this.isFromGuild = event.isFromGuild();

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

    public Channel getChannel() {
        return channel;
    }

    public boolean isFromGuild() {
        return isFromGuild;
    }

    public Member getMember() {
        return member;
    }

    public void reply(String message) {
        if (isTextCommand()) {
            channel.sendMessage(message).queue();
        } else {
            if (slashCommandInteractionEvent != null) {
                slashCommandInteractionEvent.reply(message).queue();
            }
        }
    }

    public boolean isTextCommand() {
        return slashCommandInteractionEvent == null;
    }
}
