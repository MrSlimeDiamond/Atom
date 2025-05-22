package net.slimediamond.atom.api.messaging.renderer

import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText

// wrapper for the irc renderer but in a discord 'ansi' embed
object DiscordRichMessageRenderer {

    private const val COLOR_CODE = '\u001b'

    // FIXME: Not all colours are populated.
    private val COLORS = mapOf(
        Color.GRAY to "30m",
        Color.RED to "31m",
        Color.GREEN to "32m",
        Color.YELLOW to "33m",
        Color.BLUE to "34m",
        Color.PINK to "35m",
        Color.CYAN to "36m",
        Color.WHITE to "37m",
    )

    private fun renderAnsi(message: RichText): String {
        val builder = StringBuilder()
        message.parts.forEach { part ->
            val color = COLORS[part.style?.color]
            if (color != null) {
                builder.append(COLOR_CODE)
                    .append("[0;")
                    .append(color)
            }
            // why does this need ==true
            val bold = part.style?.bold == true
            val italics = part.style?.italics == true
            if (bold) builder.append("**")
            if (italics) builder.append("*")
            builder.append(part.content)
            if (italics) builder.append("*")
            if (bold) builder.append("**")
        }
        return builder.toString()
    }

    fun render(message: RichText): String {
        return buildString {
            // only show code blocks if there is ansi colour coding
            var ansi = false
            if (message.parts.any { it.style?.color != null }) {
                ansi = true
                append("```ansi\n")
            }
            append(renderAnsi(message))
            if (ansi) {
                append("```")
            }
        }
    }

}