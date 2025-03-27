package net.slimediamond.atom.discord;

import net.dv8tion.jda.api.JDA;
import net.slimediamond.atom.discord.entities.Guild;

import java.util.List;

public interface DiscordAPI {
    /**
     * Get a guild by its ID
     *
     * @param id The ID to get the guild from
     * @return Guild object
     */
    Guild getGuildById(long id);

    /**
     * Get the latency to Discord
     *
     * @return Discord ping
     */
    long getGatewayPing();

    /**
     * Get all guilds in the API
     * @return Guilds
     */
    List<Guild> getGuilds();

    /**
     * Get the JDA instance abstracting the Discord API
     *
     * @return JDA instance
     */
    JDA getJDA();
}
