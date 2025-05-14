package net.slimediamond.atom.commands.api.platforms.discord

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.commands.api.exceptions.ArgumentParseException
import net.slimediamond.atom.commands.api.parameter.Parameter
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.messaging.Color
import net.slimediamond.atom.messaging.RichMessage

class DiscordCommandPlatform : CommandPlatform {

    override fun renderTooManyArguments(command: CommandNode, index: Int, input: String): RichMessage {
        val pointer = " ".repeat(index.coerceAtMost(input.length)) + "^"
        var limited = input

        if (input.length > 50) {
            limited = input.substring(0, 50) + "..."
        }

        return RichMessage.of().color(Color.RED)
            .append(RichMessage.of("Too many arguments!"))
            .appendNewline()
            .append(RichMessage.of("> $limited"))
            .appendNewline()
            .append(RichMessage.of("  $pointer here"))
            .appendNewline()
            .append(RichMessage.of("Usage: "))
            .append(RichMessage.of(command.usage))
    }

    override fun renderNotEnoughArguments(command: CommandNode, index: Int, input: String,): RichMessage {
        val safeIndex = index.coerceAtLeast(0).coerceAtMost(input.length)
        val pointer = caretUnder("Usage", command.usage, safeIndex + 1)

        return RichMessage.of().color(Color.RED)
            .append(RichMessage.of("Not enough arguments!"))
            .appendNewline()
            .append(RichMessage.of("Usage: ${command.usage}"))
            .appendNewline()
            .append(RichMessage.of("$pointer here"))
    }

    override fun renderArgumentParseException(e: ArgumentParseException): RichMessage {
        val index = e.index
        val input = e.input

        val pointer = " ".repeat(index.coerceAtMost(input.length)) + "^"
        var limited = input

        if (input.length > 50) {
            limited = input.substring(0, 50) + "..."
        }

        return RichMessage.of().color(Color.RED)
            .append(RichMessage.of("${e.javaClass.name}: "))
            .append(e.msg)
            .appendNewline()
            .append(RichMessage.of("> $limited"))
            .appendNewline()
            .append(RichMessage.of("  $pointer here"))
    }

    override fun createContext(command: CommandNode, sender: CommandSender, input: String, audience: Audience): CommandNodeContext {
        TODO("Not yet implemented")
    }

    private fun caretUnder(prefix: String, content: String, paramIndex: Int): String {
        val parts = content.split(' ')
        val clampedIndex = paramIndex.coerceIn(0, parts.lastIndex)
        val offset = parts.take(clampedIndex).sumOf { it.length + 3 }
        return " ".repeat(prefix.length + offset) + "^"
    }


}