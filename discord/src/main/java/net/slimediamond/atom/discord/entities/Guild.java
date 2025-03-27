package net.slimediamond.atom.discord.entities;

import net.slimediamond.data.DataHolder;

/**
 * A Discord guild
 */
public interface Guild extends DataHolder, net.dv8tion.jda.api.entities.Guild {
    /**
     * Get the ID for this guild
     *
     * @return Discord ID
     */
    long getDiscordId();
}
