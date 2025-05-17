package net.slimediamond.atom.api.irc

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.irc.factory.ConnectionFactory
import net.slimediamond.atom.api.irc.linehandlers.LineHandler
import net.slimediamond.atom.api.irc.linehandlers.MessageLineHandler
import net.slimediamond.atom.api.irc.linehandlers.PingLineHandler
import net.slimediamond.atom.api.irc.linehandlers.WelcomeLineHandler
import java.util.LinkedList
import java.util.function.Consumer

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
    fun connect(metadata: ConnectionInfo): Connection {
        val connection: Connection = Atom.instance.factoryProvider.provide(ConnectionFactory::class.java)
            .create(metadata.nickname, metadata.realName, metadata.username, metadata.server)
        connection.connect(this)
        connections.add(connection)
        return connection
    }

    fun addLineHandler(handler: LineHandler) {
        lineHandlers.add(handler)
    }

    fun addLineHandler(handler: Consumer<String>) {
        lineHandlers.add(object : LineHandler {
            override fun handle(line: String, connection: Connection) {
                handler.accept(line)
            }

        })
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