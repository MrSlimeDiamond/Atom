package net.slimediamond.atom

import net.slimediamond.atom.configuration.Configuration
import net.slimediamond.atom.event.EventManager
import net.slimediamond.atom.irc.ircbot.IrcBot
import net.slimediamond.atom.service.ServiceManager
import net.slimediamond.atom.services.CommandService
import net.slimediamond.atom.utils.factory.DefaultFactoryProvider
import net.slimediamond.atom.utils.factory.FactoryProvider
import net.slimediamond.data.identification.NamespaceHolder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path

class Atom : NamespaceHolder {

    @Volatile
    lateinit var configuration: Configuration
    @Volatile
    lateinit var serviceManager: ServiceManager
    @Volatile
    lateinit var eventManager: EventManager
    @Volatile
    lateinit var factoryProvider: FactoryProvider
    @Volatile
    lateinit var commandService: CommandService

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

        val configPath = Path.of("atom.conf")
        if (!configPath.toFile().exists()) {
            configPath.toFile().createNewFile()
        }
        val options = ConfigurationOptions.defaults()
        val configLoader = HoconConfigurationLoader.builder()
            .path(configPath)
            .defaultOptions(options)
            .build()

        val reference = configLoader.loadToReference().referenceTo(Configuration::class.java)

        configLoader.save(reference.node())

        configuration = reference.get()?: error("Configuration failed to load")

        serviceManager = ServiceManager()
        eventManager = EventManager()
        factoryProvider = DefaultFactoryProvider()

        serviceManager.addService(IrcBot())

        commandService = CommandService()
        serviceManager.addService(commandService)

        logger.info("Starting all services")
        serviceManager.startAll()
    }

    override fun getNamespace(): String {
        return "atom"
    }

}