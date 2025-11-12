package net.slimediamond.atom.api.messaging

import dev.kord.rest.builder.message.EmbedBuilder

interface SlashCommandAudience : DiscordAudience {

    /**
     * Defer the response on the Discord-side, showing a "thinking" prompt
     */
    suspend fun defer(ephemeral: Boolean = false)

    /**
     * Send a message to the audience
     */
    suspend fun sendMessage(message: String, ephemeral: Boolean = false)

    /**
     * Send a message to the audience
     */
    suspend fun sendMessage(message: RichText, ephemeral: Boolean = false)

    /**
     * Send the discord audience some embeds
     */
    suspend fun sendEmbeds(vararg embeds: EmbedBuilder, ephemeral: Boolean = false)

}