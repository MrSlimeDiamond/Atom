package net.slimediamond.atom

import net.slimediamond.atom.commands.PingCommand
import net.slimediamond.atom.event.EventManager
import net.slimediamond.atom.irc.ircbot.IrcBot
import net.slimediamond.atom.service.ServiceManager
import net.slimediamond.atom.services.CommandService
import net.slimediamond.atom.utils.factory.DefaultFactoryProvider
import net.slimediamond.atom.utils.factory.FactoryProvider
import net.slimediamond.data.identification.NamespaceHolder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class Atom : NamespaceHolder {

    @Volatile
    lateinit var serviceManager: ServiceManager
    @Volatile
    lateinit var eventManager: EventManager
    @Volatile
    lateinit var factoryProvider: FactoryProvider

    companion object {
        private val logger: Logger = LogManager.getLogger("atom")
        @Volatile
        lateinit var instance: Atom

        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Initializing...")
            instance = Atom()
            instance.start()
        }
    }

    fun start() {
        logger.info("Starting!")
        serviceManager = ServiceManager()
        eventManager = EventManager()
        factoryProvider = DefaultFactoryProvider()

        serviceManager.addService(IrcBot())
        serviceManager.addService(CommandService())

        logger.info("Starting all services")
        serviceManager.startAll()

        val commandService = serviceManager.provide(CommandService::class.java)
        commandService.commandNodeManager.register(PingCommand(), listOf("ping"))
    }

    override fun getNamespace(): String {
        return "atom"
    }

}