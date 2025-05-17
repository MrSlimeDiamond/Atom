package net.slimediamond.atom.api.discord.entities

import net.slimediamond.atom.api.messaging.Audience

interface MessageChannel : Audience {

    /**
     * The channel's ID
     */
    val id: Long

}