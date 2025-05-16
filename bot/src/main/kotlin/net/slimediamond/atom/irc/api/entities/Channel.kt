package net.slimediamond.atom.irc.api.entities

import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.messaging.RichMessage
import net.slimediamond.atom.messaging.renderer.IrcRichMessageRenderer
import net.slimediamond.atom.storage.dao.ChannelDao

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
        IrcRichMessageRenderer.sendMessage(connection, name, message)
    }

    val channelDao: ChannelDao = ChannelDao.getByName(this.name)

}