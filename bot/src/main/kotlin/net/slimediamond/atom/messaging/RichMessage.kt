package net.slimediamond.atom.messaging

import java.util.LinkedList

/**
 * A "rich message", supporting colours, appending elements, etc
 */
class RichMessage private constructor(var content: String, var style: Style?) {

    val parts: MutableList<RichMessage> = LinkedList()

    init {
        parts.add(this)
    }

    companion object {
        fun of(content: String): RichMessage {
            return RichMessage(content, null)
        }
    }

    fun color(color: Color): RichMessage {
        val style = Style(color)
        this.style = style
        return this
    }

    fun append(other: RichMessage): RichMessage {
        parts.addAll(other.parts)
        return this
    }

    data class Style(var color: Color)

}