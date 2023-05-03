package net.zenoc.atom.discordbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public interface CachedMessage {
    User getUser();
    String getMessageContent();
    Guild getGuild();
}
