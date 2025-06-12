package net.slimediamond.atom.api.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.SlashCommandAudience
import org.apache.logging.log4j.LogManager

/**
 * The global command registrar, which acts as a place for
 * storing command aliases and dispatching command execution.
 *
 * @see register
 */
class CommandManager {

    private val logger = LogManager.getLogger("command manager")
    val commands = HashMap<String, Command>()

    /**
     * Register a command with the command handler
     *
     * @param alias The alias to associate with the command
     * @param command The command which is executed
     */
    fun register(alias: String, command: Command) {
        commands[alias] = command
    }

    /**
     * Handle a command execution
     *
     * @param sender The command sender
     * @param command The alias for the command
     * @param platform The platform which the command execution
     * originates from
     * @param audience The audience to callback to
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun handle(sender: CommandSender, command: String, args: String, platform: CommandPlatform, audience: Audience, cause: Cause) {
        GlobalScope.launch {
            try {
                val cmd = commands[command]
                if (cmd != null) {
                    val result = cmd.execute(sender, args, platform, audience, cause)
                    if (!result.success && result.message != null) {
                        if (audience is SlashCommandAudience) {
                            audience.sendMessage(result.message!!.color(Color.RED), ephemeral = true)
                        } else {
                            audience.sendMessage(result.message!!.color(Color.RED))
                        }
                        logger.error("Unable to execute command '$command' for user '${sender.name}'\n" +
                                result.message!!.content)
                    }
                }
            } catch (e: Throwable) {
                logger.error(e)
                audience.sendMessage(RichText.of("${e.javaClass.name}: ${e.message}").color(Color.RED))
            }
        }
    }

}