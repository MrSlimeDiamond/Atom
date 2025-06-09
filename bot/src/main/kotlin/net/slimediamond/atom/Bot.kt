package net.slimediamond.atom

import net.slimediamond.atom.api.command.CommandManager
import net.slimediamond.atom.api.command.CommandNodeManager
import net.slimediamond.atom.api.command.HelpCommandNode
import net.slimediamond.atom.api.event.EventManager
import net.slimediamond.atom.api.factory.DefaultFactoryProvider
import net.slimediamond.atom.api.factory.FactoryProvider
import net.slimediamond.atom.api.service.ServiceManager
import net.slimediamond.atom.commands.*
import net.slimediamond.atom.commands.ircbot.IrcBotRootCommand
import net.slimediamond.atom.commands.minecraftonline.SeenCommand
import net.slimediamond.atom.commands.minecraftonline.TimeplayedCommand
import net.slimediamond.atom.configuration.Configuration
import net.slimediamond.atom.discord.DiscordBot
import net.slimediamond.atom.ircbot.IrcBot
import net.slimediamond.atom.services.PermissionService
import net.slimediamond.atom.storage.StorageService
import org.apache.logging.log4j.LogManager
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path

class Bot {

    @Volatile
    lateinit var serviceManager: ServiceManager
    @Volatile
    lateinit var eventManager: EventManager
    @Volatile
    lateinit var factoryProvider: FactoryProvider
    @Volatile
    lateinit var commandManager: CommandManager
    @Volatile
    lateinit var commandNodeManager: CommandNodeManager

    companion object {
        val logger = LogManager.getLogger("bot")
        @JvmStatic
        fun main(vararg args: String) {
            Atom.bot = Bot()
            logger.info("Initializing...")
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

            Atom.configuration = reference.get()?: error("Configuration failed to load")
            Atom.bot.start()
        }
    }

    fun start() {
        val logger = LogManager.getLogger("bot")
        logger.info("Starting bot")

        serviceManager = ServiceManager()
        eventManager = EventManager()
        factoryProvider = DefaultFactoryProvider()


        serviceManager.addService(StorageService())
        serviceManager.addService(PermissionService())
        serviceManager.addService(IrcBot())
        serviceManager.addService(DiscordBot())

        logger.info("Registering commands")
        commandManager = CommandManager()
        commandNodeManager = CommandNodeManager()
        commandNodeManager.register(HelpCommandNode())
        commandNodeManager.register(PingCommand())
        commandNodeManager.register(WhoamiCommand())
        commandNodeManager.register(ColorsCommand())
        commandNodeManager.register(TestCommand())
        commandNodeManager.register(IrcBotRootCommand())
        commandNodeManager.register(ServicesCommand())
        commandNodeManager.register(TimeplayedCommand())
        commandNodeManager.register(SeenCommand(true, "Get the first seen date of a player", "firstseen", "fs"))
        commandNodeManager.register(SeenCommand(false, "Get the last seen date of a player", "lastseen", "ls"))

        logger.info("Starting all services")
        serviceManager.startAll()
    }

}