package net.slimediamond.atom.services

import net.slimediamond.atom.commands.ColorsCommand
import net.slimediamond.atom.commands.TestCommand
import net.slimediamond.atom.commands.PingCommand
import net.slimediamond.atom.commands.WhoamiCommand
import net.slimediamond.atom.commands.api.CommandManager
import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeManager
import net.slimediamond.atom.event.Listener
import net.slimediamond.atom.service.Service
import net.slimediamond.atom.service.events.ServiceStartEvent

@Service("command")
class CommandService {

    /**
     * The manager for [CommandNode]
     */
    val commandNodeManager: CommandManager<CommandNode> = CommandNodeManager()

    @Listener
    fun onStartService(event: ServiceStartEvent) {
        event.container.logger.info("Starting command service")
        PingCommand().register()
        WhoamiCommand().register()
        ColorsCommand().register()
        TestCommand().register()
    }

}