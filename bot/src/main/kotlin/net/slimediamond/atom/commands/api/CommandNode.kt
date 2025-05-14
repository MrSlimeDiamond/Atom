package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Atom
import net.slimediamond.atom.commands.api.exceptions.CommandException
import net.slimediamond.atom.commands.api.parameter.Parameter
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience
import java.util.LinkedList

abstract class CommandNode(vararg aliases: String) : Command {

    companion object {
        private const val HERE = "here"
    }

    val aliases: MutableList<String> = LinkedList()
    val platforms: MutableList<CommandPlatform> = LinkedList()
    val children: MutableList<CommandNode> = LinkedList()
    val parameters: MutableList<Parameter> = LinkedList()

    val usage: String
        get() = buildString {
            append(aliases.first())
            parameters.forEach { parameter ->
                append(if (parameter.optional) " [" else " <")
                append(parameter.key)
                if (parameter.greedy) {
                    append("...")
                }
                append(if (parameter.optional) "]" else ">")
            }
        }

    @Throws(CommandException::class)
    abstract fun execute(context: CommandNodeContext): CommandResult

    override fun execute(sender: CommandSender, input: String, platform: CommandPlatform, audience: Audience): CommandResult {
        var finalInput = input
        var command = this
        val maybe = input.split(" ")[0]
        val cmd = children.stream().filter { cmd -> cmd.aliases.contains(maybe) }.findFirst()
        if (cmd.isPresent) {
            // this is silly
            finalInput = input.split(" ").drop(1).joinToString(" ")
            command = cmd.get()
        }

        val context = platform.createContext(command, sender, finalInput, audience)

        if (command.parameters.isNotEmpty()) {
            command.parameters.forEach { parameter ->
                if (!parameter.optional && finalInput.length < parameters.indexOf(parameter)) {
                    // show an error message showing they don't have enough arguments
                    context.sendMessage(platform.renderNotEnoughArguments(command, parameters.indexOf(parameter), finalInput))
                    return CommandResult.empty
                }
            }
        } else if (finalInput.isNotEmpty()){
            // see if they have too many arguments
            context.sendMessage(platform.renderTooManyArguments(command, 0, finalInput))
            return CommandResult.empty
        }

        return try {
            command.execute(context)
        } catch (e: Exception) {
            if (e is CommandException) {
                // FIXME: This is a little bit scuffed
                audience.sendMessage(e.msg)
                return CommandResult.empty
            }
            CommandResult.error("Error: " + (e.message?: "An error occurred when executing this command"))
        }
    }

    fun register() {
        Atom.instance.commandService.commandNodeManager.register(this, aliases)
    }

    init {
        aliases.forEach { alias -> this.aliases.add(alias) }
    }

}