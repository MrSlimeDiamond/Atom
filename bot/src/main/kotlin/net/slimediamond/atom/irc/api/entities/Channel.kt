package net.slimediamond.atom.irc.api.entities

import net.slimediamond.atom.Audience
import net.slimediamond.atom.irc.api.Connection

/**
 * An IRC channel
 */
data class Channel(
    val connection: Connection,
    val name: String,
    val users: List<User>
) : Audience {

    override fun sendMessage(message: String) {
        connection.sendMessage(name, message)
    }

}