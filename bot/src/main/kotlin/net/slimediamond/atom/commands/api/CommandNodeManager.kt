package net.slimediamond.atom.commands.api

import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.messaging.Color
import net.slimediamond.atom.messaging.RichMessage
import org.apache.logging.log4j.LogManager

class CommandNodeManager : CommandManager<CommandNode> {

    private val commands: MutableMap<String, CommandNode> = HashMap()
    private val logger = LogManager.getLogger("command node manager")

    override fun register(command: CommandNode, aliases: List<String>) {
        aliases.forEach {
            commands[it] = command
        }
    }

    override fun handle(sender: CommandSender, command: String, input: String, platform: CommandPlatform, audience: Audience) {
        val cmd = commands[command]
        if (cmd != null) {
            if (cmd.platforms.isEmpty() || cmd.platforms.contains(platform)) {
                val result = cmd.execute(sender, input, platform, audience)
                if (!result.success && result.message != null) {
                    audience.sendMessage(RichMessage.of("Error: ${result.message!!}").color(Color.RED))
                    logger.error("Command error for {}: {}", sender.name, result.message!!)
                }
            }
        }
    }

}