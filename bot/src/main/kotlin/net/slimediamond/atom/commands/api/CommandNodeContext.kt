package net.slimediamond.atom.commands.api

import net.slimediamond.atom.commands.api.exceptions.ArgumentParseException
import net.slimediamond.atom.commands.api.exceptions.CommandException
import net.slimediamond.atom.commands.api.parameter.Parameter
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience

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

}