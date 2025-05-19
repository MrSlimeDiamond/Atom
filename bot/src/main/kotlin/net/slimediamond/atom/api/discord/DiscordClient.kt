package net.slimediamond.atom.api.discord

import net.slimediamond.atom.api.discord.entities.Guild

interface DiscordClient {

    /**
     * Login to the Discord API
     */
    suspend fun login()

    /**
     * Log out from the Discord API
     */
    suspend fun logout()

    /**
     * Get a guild by its ID
     */
    suspend fun getGuildById(id: Long): Guild?

}