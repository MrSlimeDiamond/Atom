package net.slimediamond.atom.api.messaging

import java.util.LinkedList

/**
 * A "rich message", supporting colours, appending elements, etc
 */
class RichText private constructor(var content: String, var style: Style?) {

    val parts: MutableList<RichText> = LinkedList()

    init {
        parts.add(this)
    }

    companion object {
        fun of(content: String): RichText {
            return RichText(content, null)
        }

        fun of(): RichText {
            return RichText("", null)
        }

        fun newline(): RichText {
            return of("\n")
        }

        fun join(separator: RichText, parts: Collection<RichText>): RichText {
            val builder = of()
            parts.forEach { part ->
                builder.append(separator).append(part)
            }
            return builder
        }
    }

    fun color(color: Color): RichText {
        val style = Style(color)
        this.style = style
        return this
    }

    fun append(other: RichText): RichText {
        if (other.style == null) {
            other.style = this.style
        }
        parts.addAll(other.parts)
        return this
    }

    fun appendNewline(): RichText {
        parts.add(newline())
        return this
    }

    data class Style(var color: Color)

}