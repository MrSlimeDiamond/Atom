package net.slimediamond.atom.irc.ircbot

import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.Listener
import net.slimediamond.atom.irc.api.ConnectionInfo
import net.slimediamond.atom.irc.api.IrcClient
import net.slimediamond.atom.irc.api.Server
import net.slimediamond.atom.irc.api.factory.ConnectionFactory
import net.slimediamond.atom.service.Service
import net.slimediamond.atom.service.events.ServiceStartEvent
import org.apache.logging.log4j.Logger

@Service("ircbot")
class IrcBot {

    private lateinit var logger: Logger
    private lateinit var client: IrcClient

    @Listener
    fun onServiceStart(event: ServiceStartEvent<IrcBot>) {
        // TODO: injection for this logger, or similar
        this.logger = event.serviceContainer.logger
        logger.info("Starting IRC bot")
        Atom.instance.factoryProvider.offer(ConnectionFactory())
        client = IrcClient()

        val server: Server = Server("Local", "localhost", 6667, false)
        logger.info("Connecting to {} ({}, {})", server.name, server.host, server.port)
        client.connect(ConnectionInfo("Atom-Test", "Atom testing bot", server))
    }

}