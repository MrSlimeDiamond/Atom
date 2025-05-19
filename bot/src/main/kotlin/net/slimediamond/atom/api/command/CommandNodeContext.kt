package net.slimediamond.atom.api.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.exceptions.CommandException
import net.slimediamond.atom.api.command.parameter.Parameter
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.command.platforms.CommandPlatforms
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.utils.Embeds

abstract class CommandNodeContext(
    private val commandNode: CommandNode,
    val sender: CommandSender,
    val input: String,
    val platform: CommandPlatform,
    val parameterKeyMap: Map<String, String>
) : Audience {

    /**
     * Require a parameter
     */
    @Throws(ArgumentParseException::class, CommandException::class)
    fun <T> requireOne(parameter: Parameter.Value<T>): T {
        val notEnoughArgs = CommandException(platform.renderNotEnoughArguments(commandNode, commandNode.parameters.indexOf(parameter)))
        val parameterInput = parameterKeyMap[parameter.key] ?: throw notEnoughArgs
        val value = parameter.parse(parameterInput)

        if (value == null || (value is String && value.isEmpty())) {
            throw notEnoughArgs
        }

        return value
    }

    fun <T> one(parameter: Parameter.Value<T>): T? {
        val parameterInput = parameterKeyMap[parameter.key] ?: return null
        return parameter.parse(parameterInput)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun replySuccess(message: String) {
        if (this is DiscordCommandNodeContext) {
            GlobalScope.launch {
                sendEmbeds(Embeds.success(message))
            }
        } else {
            sendMessage(message)
        }
    }

}