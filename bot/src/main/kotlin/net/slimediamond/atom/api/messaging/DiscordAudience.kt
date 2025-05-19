package net.slimediamond.atom.api.messaging

import dev.kord.rest.builder.message.EmbedBuilder

interface DiscordAudience : Audience {

    /**
     * Send the discord audience some embeds
     */
    suspend fun sendEmbeds(vararg embeds: EmbedBuilder)

}