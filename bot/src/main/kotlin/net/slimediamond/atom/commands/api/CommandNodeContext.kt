package net.slimediamond.atom.commands.api

import net.slimediamond.atom.commands.api.exceptions.CommandException
import net.slimediamond.atom.commands.api.parameter.Parameter
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience

abstract class CommandNodeContext(
    private val commandNode: CommandNode,
    val sender: CommandSender,
    val input: String,
    val platform: CommandPlatform
) : Audience {

    /**
     * Require a parameter
     */
    @Throws(CommandException::class)
    fun <T> requireOne(parameter: Parameter.Value<T>): T {
        val value = parameter.parse(input)

        if (value == null || (value is String && value.isEmpty())) {
            throw CommandException(platform.renderNotEnoughArguments(commandNode, commandNode.parameters.indexOf(parameter), input))
        }

        return value
    }

    fun <T> one(parameter: Parameter.Value<T>): T? {
        return parameter.parse(input)
    }

}