package net.slimediamond.atom.api.irc.entities

import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.messaging.RichMessage
import net.slimediamond.atom.api.messaging.renderer.IrcRichMessageRenderer

class UserImpl(
    val connection: Connection,
    override val nickname: String,
    override val username: String,
    override val hostname: String
) : User {

    override fun sendMessage(message: String) {
        connection.sendMessage(nickname, message)
    }

    override fun sendMessage(message: RichMessage) {
        IrcRichMessageRenderer.sendMessage(connection, username, message)
    }

}