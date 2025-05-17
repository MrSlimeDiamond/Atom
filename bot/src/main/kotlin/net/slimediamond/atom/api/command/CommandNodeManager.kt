package net.slimediamond.atom.api.command

import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText
import org.apache.logging.log4j.LogManager
import java.util.concurrent.Executors

class CommandNodeManager : CommandManager {

    private val commands: MutableMap<String, CommandNode> = HashMap()
    private val logger = LogManager.getLogger("command node manager")
    private val executor = Executors.newFixedThreadPool(2)

    override fun handle(sender: CommandSender, command: String, input: String, platform: CommandPlatform, audience: Audience) {
        executor.submit {
            try {
                val cmd = commands[command]
                if (cmd != null) {
                    if (cmd.platforms.isEmpty() || cmd.platforms.contains(platform)) {
                        val result = cmd.execute(sender, input, platform, audience)
                        if (!result.success && result.message != null) {
                            audience.sendMessage(result.message!!.color(Color.RED))
                            logger.error("Command error for {}: {}", sender.name, result.message!!.content)
                        }
                    }
                }
            } catch (e: Throwable) {
                logger.error(e)
                audience.sendMessage(RichText.of("${e.javaClass.name}: ${e.message}").color(Color.RED))
            }
        }
    }

    fun register(command: CommandNode) {
        command.aliases.forEach {
            commands[it] = command
        }
    }

}