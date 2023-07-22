package net.slimediamond.atom.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class CachedMessage {
    User user;
    String messageContent;
    Guild guild;
    public CachedMessage(User user, String messageContent, Guild guild) {
        this.user = user;
        this.messageContent = messageContent;
        this.guild = guild;
    }

    public User getUser() {
        return this.user;
    }

    public String getMessageContent() {
        return this.messageContent;
    }

    public Guild getGuild() {
        return this.guild;
    }
}
