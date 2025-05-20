package net.slimediamond.atom

import be.bendem.sqlstreams.SqlStream
import net.slimediamond.atom.api.command.CommandNodeManager
import net.slimediamond.atom.api.command.HelpCommandNode
import net.slimediamond.atom.commands.ircbot.IrcBotRootCommand
import net.slimediamond.atom.configuration.Configuration
import net.slimediamond.atom.api.event.EventManager
import net.slimediamond.atom.ircbot.IrcBot
import net.slimediamond.atom.api.service.ServiceManager
import net.slimediamond.atom.services.PermissionService
import net.slimediamond.atom.storage.StorageService
import net.slimediamond.atom.api.factory.DefaultFactoryProvider
import net.slimediamond.atom.api.factory.FactoryProvider
import net.slimediamond.atom.commands.*
import net.slimediamond.atom.commands.minecraftonline.TimeplayedCommand
import net.slimediamond.atom.discord.DiscordBot
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path

class Atom {

    @Volatile
    lateinit var configuration: Configuration
    @Volatile
    lateinit var serviceManager: ServiceManager
    @Volatile
    lateinit var eventManager: EventManager
    @Volatile
    lateinit var factoryProvider: FactoryProvider
    @Volatile
    lateinit var commandNodeManager: CommandNodeManager
    @Volatile
    lateinit var sql: SqlStream

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


        serviceManager.addService(StorageService())
        serviceManager.addService(PermissionService())
        serviceManager.addService(IrcBot())
        serviceManager.addService(DiscordBot())

        logger.info("Registering commands")
        commandNodeManager = CommandNodeManager()
        commandNodeManager.register(HelpCommandNode())
        commandNodeManager.register(PingCommand())
        commandNodeManager.register(WhoamiCommand())
        commandNodeManager.register(ColorsCommand())
        commandNodeManager.register(TestCommand())
        commandNodeManager.register(IrcBotRootCommand())
        commandNodeManager.register(ServicesCommand())
        commandNodeManager.register(TimeplayedCommand())

        logger.info("Starting all services")
        serviceManager.startAll()
    }

}