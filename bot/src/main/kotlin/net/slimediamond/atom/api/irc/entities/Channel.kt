package net.slimediamond.atom.api.irc.entities

import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.renderer.IrcRichMessageRenderer
import net.slimediamond.atom.storage.dao.ChannelDao

/**
 * An IRC channel
 */
data class Channel(
    val connection: Connection,
    val name: String,
    val users: List<User>
) : Audience {

    override suspend fun sendMessage(message: String) {
        connection.sendMessage(name, message)
    }

    override suspend fun sendMessage(message: RichText) {
        IrcRichMessageRenderer.sendMessage(connection, name, message)
    }

    val channelDao: ChannelDao = ChannelDao.getByName(this.name)

}