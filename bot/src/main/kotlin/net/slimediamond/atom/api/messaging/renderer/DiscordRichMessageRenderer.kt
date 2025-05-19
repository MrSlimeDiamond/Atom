package net.slimediamond.atom.api.messaging.renderer

import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText

// wrapper for the irc renderer but in a discord 'ansi' embed
object DiscordRichMessageRenderer {

    private const val COLOR_CODE = '\u001b'

    // FIXME: Not all colours are populated.
    private val COLORS = mapOf(
        Color.GRAY to "90m",
        Color.RED to "31m",
        Color.DARK_RED to "31m",
        Color.GREEN to "32m",
        Color.LIGHT_GREEN to "32m",
        Color.YELLOW to "33m",
        Color.BLUE to "34m",
        Color.PINK to "35m",
        Color.PURPLE to "35m",
        Color.CYAN to "36m",
        Color.WHITE to "37m",
        Color.RED to "91m",
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
            builder.append(part.content)
        }
        return builder.toString()
    }

    fun render(message: RichText): String {
        return buildString {
            append("```ansi\n")
            append(renderAnsi(message))
            append("```")
        }
    }

}