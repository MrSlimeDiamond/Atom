package net.slimediamond.atom.irc.api.entities

import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.messaging.RichMessage

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

    override fun sendMessage(message: RichMessage) {
        TODO("Not yet implemented")
    }

}