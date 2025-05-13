package net.slimediamond.atom.irc.api.factory

import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.irc.api.DefaultConnection
import net.slimediamond.atom.irc.api.Server

class ConnectionFactory {

    fun create(nickname: String, realName: String, username: String, server: Server): Connection {
        return DefaultConnection(nickname, realName, username, server)
    }

}