package net.slimediamond.atom.api.discord.entities

import net.slimediamond.atom.api.messaging.DiscordAudience

interface MessageChannel : DiscordAudience {

    /**
     * The channel's ID
     */
    val id: Long

}