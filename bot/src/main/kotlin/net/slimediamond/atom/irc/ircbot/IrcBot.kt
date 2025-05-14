package net.slimediamond.atom.irc.ircbot

import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.Listener
import net.slimediamond.atom.irc.api.ConnectionInfo
import net.slimediamond.atom.irc.api.IrcClient
import net.slimediamond.atom.irc.api.Server
import net.slimediamond.atom.irc.api.factory.ConnectionFactory
import net.slimediamond.atom.irc.ircbot.listeners.IrcMessageListener
import net.slimediamond.atom.service.Service
import net.slimediamond.atom.service.events.ServiceStartEvent
import org.apache.logging.log4j.Logger

@Service("ircbot")
class IrcBot {

    private lateinit var logger: Logger
    private lateinit var client: IrcClient

    @Listener
    fun onServiceStart(event: ServiceStartEvent) {
        // TODO: injection for this logger, or similar
        this.logger = event.container.logger
        logger.info("Starting IRC bot")
        Atom.instance.factoryProvider.offer(ConnectionFactory())
        client = IrcClient()

        Atom.instance.eventManager.registerListener(IrcMessageListener())

        val ircConfiguration = Atom.instance.configuration.ircConfiguration
        val serverConfig = ircConfiguration.serverConfiguration
        val userConfig = ircConfiguration.userConfiguration

        val name = serverConfig.name?: error("IRC server has no name configured")
        val hostname = serverConfig.hostname?: error("IRC server has no host configured")
        val port = serverConfig.port?: error("IRC server has no port configured")
        val nickname = userConfig.nickname?: error("IRC user has no nickname configured")
        val username = userConfig.username?: error("IRC user has no username configured")
        val realname = userConfig.realname?: error("IRC user has no real name configured")

        val server = Server(name, hostname, port, serverConfig.ssl)
        logger.info("Connecting to {} ({}, {})", server.name, server.host, server.port)
        client.connect(ConnectionInfo(nickname, realname, username, server))
        // No code executes past here. Fuck threading
    }

}