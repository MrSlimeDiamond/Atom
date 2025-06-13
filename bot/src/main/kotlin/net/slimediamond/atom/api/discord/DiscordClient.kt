package net.slimediamond.atom.api.discord

import kotlinx.coroutines.flow.Flow
import net.slimediamond.atom.api.discord.entities.Guild

interface DiscordClient {

    /**
     * The [SlashCommandNodeManager] for registering
     * [net.slimediamond.atom.api.command.CommandNode]s with slashes
     */
    var slashCommandNodeManager: SlashCommandNodeManager

    /**
     * Whether the client is currently logged in
     */
    var loggedIn: Boolean

    /**
     * The guilds that the bot is in
     */
    val guilds: Flow<Guild>

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