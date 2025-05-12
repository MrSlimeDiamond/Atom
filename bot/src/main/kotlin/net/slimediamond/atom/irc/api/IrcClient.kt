package net.slimediamond.atom.irc.api

import net.slimediamond.atom.Atom
import net.slimediamond.atom.irc.api.factory.ConnectionFactory
import net.slimediamond.atom.utils.factory.provide
import java.util.LinkedList

/**
 * An IRC client
 */
class IrcClient {

    /**
     * The [Connection]s the bot is using
     */
    private val connections: MutableList<Connection> = LinkedList()

    /**
     * Add a server to the IRC client
     */
    fun connect(metadata: ConnectionInfo) {
        val connection: Connection = Atom.instance.factoryProvider.provide<ConnectionFactory>()
            .create(metadata.nickname, metadata.realName, metadata.server)
        connection.connect()
        connections.add(connection)
    }

}