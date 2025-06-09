package net.slimediamond.atom.api.command.platforms.discord

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.DiscordAudience
import net.slimediamond.atom.api.messaging.RichText

class DiscordCommandPlatform : CommandPlatform {

    override fun renderTooManyArguments(command: CommandNode, index: Int, input: String): RichText {
        val args = input.trim().split("\\s+".toRegex())
        val charOffset = input.indexOf(args.getOrNull(index) ?: "")

        val maxLen = 50
        val truncated = input.length > maxLen
        val limited = if (truncated) input.take(maxLen) + "..." else input

        val pointerPos = charOffset.coerceAtMost(limited.length)
        val pointer = " ".repeat(pointerPos) + "^"

        return RichText.of().color(Color.RED)
            .append(RichText.of("Too many arguments!"))
            .appendNewline()
            .append(RichText.of("> $limited"))
            .appendNewline()
            .append(RichText.of("  $pointer here"))
            .appendNewline()
            .append(RichText.of("Usage: "))
            .append(RichText.of(command.usage))
    }

    override fun renderNotEnoughArguments(command: CommandNode, index: Int): RichText {
        val safeIndex = index.coerceAtLeast(0)
        val pointer = caretUnder("Usage", command.usage, safeIndex + 1)

        return RichText.of().color(Color.RED)
            .append(RichText.of("Not enough arguments!"))
            .appendNewline()
            .append(RichText.of("Usage: ${command.usage}"))
            .appendNewline()
            .append(RichText.of("$pointer here"))
    }

    override fun renderArgumentParseException(e: ArgumentParseException): RichText {
        val index = e.index
        val input = e.input

        val pointer = " ".repeat(index.coerceAtMost(input.length)) + "^"
        var limited = input

        if (input.length > 50) {
            limited = input.substring(0, 50) + "..."
        }

        return RichText.of().color(Color.RED)
            .append(RichText.of("${e.javaClass.name}: "))
            .append(e.msg)
            .appendNewline()
            .append(RichText.of("> $limited"))
            .appendNewline()
            .append(RichText.of("  $pointer here"))
    }

    override fun createContext(command: CommandNode, cause: Cause, sender: CommandSender, input: String, audience: Audience, parameterKeyMap: Map<String, String>): CommandNodeContext {
        if (audience !is DiscordAudience) {
            throw IllegalArgumentException("Provided audience is not Discord compatible")
        }
        return DiscordCommandNodeContext(command, cause, sender, input, this, parameterKeyMap, audience)
    }

    private fun caretUnder(prefix: String, content: String, paramIndex: Int): String {
        val parts = content.split(' ')
        val clampedIndex = paramIndex.coerceIn(0, parts.lastIndex)
        val offset = parts.take(clampedIndex).sumOf { it.length + 3 }
        return " ".repeat(prefix.length + offset) + "^"
    }


}