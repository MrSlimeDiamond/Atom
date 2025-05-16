package net.slimediamond.atom.api.irc.factory

import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.DefaultConnection
import net.slimediamond.atom.api.irc.Server

class ConnectionFactory {

    fun create(nickname: String, realName: String, username: String, server: Server): Connection {
        return DefaultConnection(nickname, realName, username, server)
    }

}