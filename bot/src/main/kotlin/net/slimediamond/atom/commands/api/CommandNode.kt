package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Atom
import net.slimediamond.atom.commands.api.exceptions.ArgumentParseException
import net.slimediamond.atom.commands.api.exceptions.CommandException
import net.slimediamond.atom.commands.api.parameter.Parameter
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience
import java.util.HashMap
import java.util.LinkedList

abstract class CommandNode(vararg aliases: String) : Command {

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

        val parsed = tokenizeInput(finalInput)
        var index = 0

        val parameterKeyMap = HashMap<String, String>()

        if (command.parameters.isNotEmpty()) {
            command.parameters.forEachIndexed { i, parameter ->
                if (parameter.greedy) {
                    val remaining = finalInput.split("\\s+".toRegex(), limit = i + 1).getOrNull(i) ?: ""
                    parameterKeyMap[parameter.key] = remaining
                    return@forEachIndexed
                }

                if (index >= parsed.size) {
                    if (!parameter.optional) {
                        return CommandResult.error(platform.renderNotEnoughArguments(command, i))
                    }
                } else {
                    parameterKeyMap[parameter.key] = parsed[index]
                    index++
                }
            }

            val totalInputArgs = finalInput.trim().split("\\s+".toRegex())
            if (command.parameters.none { it.greedy } && index < totalInputArgs.size) {
                // see if they have too many arguments
                return CommandResult.error(platform.renderTooManyArguments(command, index, finalInput))
            }
        } else if (finalInput.isNotEmpty()) {
            return CommandResult.error(platform.renderTooManyArguments(command, index, finalInput))
        }

        val context = platform.createContext(command, sender, finalInput, audience, parameterKeyMap)

        return try {
            command.execute(context)
        } catch (e: Exception) {
            if (e is ArgumentParseException) {
                return CommandResult.error(platform.renderArgumentParseException(e))
            } else if (e is CommandException) {
                return CommandResult.error(e.msg)
            }
            CommandResult.error(e.message?: "An error occurred when executing this command")
        }
    }

    private fun tokenizeInput(input: String): List<String> {
        val regex = Regex("""("([^"]*)"|\S+)""")
        return regex.findAll(input).map {
            val match = it.groupValues[2]
            match.ifEmpty { it.value.trim('"') }
        }.toList()
    }

    fun register() {
        Atom.instance.commandService.commandNodeManager.register(this, aliases)
    }

    init {
        aliases.forEach { alias -> this.aliases.add(alias) }
    }

}