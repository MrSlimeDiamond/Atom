package net.slimediamond.atom.irc.api.entities

import net.slimediamond.atom.irc.api.Connection

class UserImpl(
    val connection: Connection,
    override val nickname: String,
    override val username: String,
    override val hostname: String
) : User {

    override fun sendMessage(message: String) {
        connection.sendMessage(nickname, message)
    }

}