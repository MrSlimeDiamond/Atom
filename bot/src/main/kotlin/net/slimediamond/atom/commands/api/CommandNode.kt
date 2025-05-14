package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Atom
import net.slimediamond.atom.commands.api.parameter.Parameter
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.commands.api.platforms.irc.IrcCommandNodeContext
import net.slimediamond.atom.messaging.Color
import net.slimediamond.atom.messaging.RichMessage
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

        // context depends on the platform
        val context = when (platform) {
            CommandPlatform.IRC -> IrcCommandNodeContext(command, sender, finalInput, platform, audience)
        }

        if (command.parameters.isNotEmpty()) {
            command.parameters.forEach { parameter ->
                if (!parameter.optional && input.length < parameters.indexOf(parameter)) {
                    // show an error message showing they don't have enough arguments
                    context.sendMessage(notEnoughParameters(input, parameters.indexOf(parameter)))
                    return CommandResult.empty
                }
            }
        } else if (input.isNotEmpty()){
            // see if they have too many arguments
            context.sendMessage(tooManyParameters(input, 0))
            return CommandResult.empty
        }

        return try {
            command.execute(context)
        } catch (e: Exception) {
            CommandResult.error("Error: " + (e.message?: "An error occurred when executing this command"))
        }
    }

    fun notEnoughParameters(input: String, index: Int): RichMessage {
        val safeIndex = index.coerceAtLeast(0).coerceAtMost(input.length)
        val pointer = caretUnder("Usage", usage, safeIndex + 1)

        return RichMessage.of().color(Color.RED)
            .append(RichMessage.of("Not enough arguments!"))
            .appendNewline()
            .append(RichMessage.of("Usage: $usage"))
            .appendNewline()
            .append(RichMessage.of(pointer))
    }

    fun tooManyParameters(input: String, index: Int): RichMessage {
        val pointer = " ".repeat(index.coerceAtMost(input.length)) + "^"

        return RichMessage.of().color(Color.RED)
            .append(RichMessage.of("Too many arguments!"))
            .appendNewline()
            .append(RichMessage.of(input))
            .appendNewline()
            .append(RichMessage.of(pointer))
            .appendNewline()
            .append(RichMessage.of("Usage: "))
            .append(RichMessage.of(usage))
    }

    private fun caretUnder(prefix: String, content: String, paramIndex: Int): String {
        val parts = content.split(' ')
        val clampedIndex = paramIndex.coerceIn(0, parts.lastIndex)
        val offset = parts.take(clampedIndex).sumOf { it.length + 3 }
        return " ".repeat(prefix.length + offset) + "^"
    }

    fun register() {
        Atom.instance.commandService.commandNodeManager.register(this, aliases)
    }

    init {
        aliases.forEach { alias -> this.aliases.add(alias) }
    }

}