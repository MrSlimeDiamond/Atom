package net.slimediamond.atom.irc.api.entities

import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.messaging.RichMessage
import net.slimediamond.atom.messaging.renderer.IrcRichMessageRenderer

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
        connection.sendMessage(nickname, IrcRichMessageRenderer.render(message))
    }

}