package net.slimediamond.atom.api.messaging.renderer

import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText

object IrcRichMessageRenderer {

    private const val COLOR_CODE = '\u0003'
    private val COLORS: Map<Color, String> = mapOf(
        Color.WHITE to "00",
//        Color.BLACK to "01",
//        Color.NAVY to "02",
        Color.GREEN to "03",
        Color.RED to "04",
//        Color.DARK_RED to "05",
//        Color.PURPLE to "06",
//        Color.OLIVE to "07",
        Color.YELLOW to "08",
//        Color.LIGHT_GREEN to "09",
//        Color.TEAL to "10",
        Color.CYAN to "11",
        Color.BLUE to "12",
        Color.PINK to "13",
        Color.GRAY to "14",
//        Color.LIGHT_GRAY to "15"
    )

    fun render(message: RichText): String {
        val builder = StringBuilder()
        message.parts.forEach { part ->
            val color = COLORS[part.style?.color]
            if (color != null) {
                builder.append(COLOR_CODE).append(color)
            }
            builder.append(part.content)
        }
        return builder.toString()
    }

    fun sendMessage(connection: Connection, target: String, message: RichText) {
        val rendered = render(message)
        if (!rendered.contains("\n")) {
            connection.sendMessage(target, rendered)
            return
        }
        rendered.split("\n").forEach { part ->
            connection.sendMessage(target, part)
        }
    }

}