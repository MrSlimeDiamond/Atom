package net.slimediamond.atom.ircbot

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.event.Listener
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.ConnectionInfo
import net.slimediamond.atom.api.irc.IrcClient
import net.slimediamond.atom.api.irc.Server
import net.slimediamond.atom.api.irc.events.IrcReceivedWelcomeEvent
import net.slimediamond.atom.api.irc.factory.ConnectionFactory
import net.slimediamond.atom.ircbot.listeners.IrcMessageListener
import net.slimediamond.atom.api.service.Service
import net.slimediamond.atom.api.service.events.ServiceStartEvent
import net.slimediamond.atom.api.service.events.ServiceStopEvent
import net.slimediamond.atom.storage.StorageService
import org.apache.logging.log4j.Logger

@Service("ircbot")
class IrcBot {

    private lateinit var logger: Logger
    private lateinit var client: IrcClient
    lateinit var connection: Connection

    @Listener
    fun onServiceStart(event: ServiceStartEvent) {
        // TODO: injection for this logger, or similar
        this.logger = event.container.logger
        logger.info("Starting IRC bot")
        Atom.bot.factoryProvider.offer(ConnectionFactory())
        client = IrcClient()

        Atom.bot.eventManager.registerListener(IrcMessageListener())

        val ircConfiguration = Atom.configuration.ircConfiguration
        val serverConfig = ircConfiguration.serverConfiguration
        val userConfig = ircConfiguration.userConfiguration

        val name = serverConfig.name
        val hostname = serverConfig.hostname
        val port = serverConfig.port
        val nickname = userConfig.nickname
        val username = userConfig.username
        val realname = userConfig.realname

        if (name.isEmpty() || hostname.isEmpty()) {
            error("Please configure IRC host configuration")
        }

        // client.addLineHandler { println(it) }

        val server = Server(name, hostname, port, serverConfig.ssl)
        logger.info("Connecting to {} ({}, {})", server.name, server.host, server.port)
        connection = client.connect(ConnectionInfo(nickname, realname, username, server))
    }

    @Listener
    fun onServiceStop(event: ServiceStopEvent) {
        connection.disconnect("Service '${event.container.name}' is stopping")
    }

    @Listener
    fun onIrcWelcome(event: IrcReceivedWelcomeEvent) {
        logger.info("IRC bot connected")

        Atom.bot.serviceManager.provide(StorageService::class)!!.getAutoJoinChannels().forEach { channel ->
            event.connection.joinChannel(channel)
        }
    }

}