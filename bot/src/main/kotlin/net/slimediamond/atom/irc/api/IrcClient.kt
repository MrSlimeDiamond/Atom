package net.slimediamond.atom.irc.api

import net.slimediamond.atom.Atom
import net.slimediamond.atom.irc.api.factory.ConnectionFactory
import net.slimediamond.atom.irc.api.linehandlers.LineHandler
import net.slimediamond.atom.irc.api.linehandlers.MessageLineHandler
import net.slimediamond.atom.irc.api.linehandlers.PingLineHandler
import net.slimediamond.atom.irc.api.linehandlers.WelcomeLineHandler
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
     * The line handlers for this IRC client
     */
    private val lineHandlers: MutableList<LineHandler> = LinkedList()

    /**
     * Add a server to the IRC client
     */
    fun connect(metadata: ConnectionInfo) {
        val connection: Connection = Atom.instance.factoryProvider.provide(ConnectionFactory::class.java)
            .create(metadata.nickname, metadata.realName, metadata.username, metadata.server)
        connection.connect(this)
        connections.add(connection)
    }

    fun addLineHandler(handler: LineHandler) {
        lineHandlers.add(handler)
    }

    fun handleLine(line: String, connection: Connection) {
        lineHandlers.forEach { handler -> handler.handle(line, connection) }
    }

    init {
        addLineHandler(PingLineHandler())
        addLineHandler(MessageLineHandler())
        addLineHandler(WelcomeLineHandler())
    }

}