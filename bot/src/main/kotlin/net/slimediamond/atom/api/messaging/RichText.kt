package net.slimediamond.atom.api.messaging

import java.util.LinkedList

/**
 * A "rich message", supporting colours, appending elements, etc
 */
class RichText private constructor(var content: String, var style: Style?) {

    val parts: MutableList<RichText> = LinkedList()

    init {
        parts.add(this)
        this.style = Style(null, bold = false, italics = false)
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

        fun join(separator: RichText, parts: List<RichText>, separatorFirst: Boolean = false): RichText {
            val builder = of()
            parts.forEach { part ->
                // append the separator for all parts besides the first
                if (parts.indexOf(part) > 0 || separatorFirst) {
                    builder.append(separator)
                }
                builder.append(part)
            }
            return builder
        }
    }

    fun color(color: Color): RichText {
        if (this.style == null) {
            val style = Style(color, bold = false, italics = false)
            this.style = style
        } else {
            this.style!!.color = color
        }
        return this
    }

    fun bold(): RichText {
        if (this.style == null) {
            val style = Style(null, bold = true, italics = false)
            this.style = style
        } else {
            this.style!!.bold = true
        }
        return this
    }

    fun italics(): RichText {
        if (this.style == null) {
            val style = Style(null, bold = false, italics = true)
            this.style = style
        } else {
            this.style!!.italics = true
        }
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

    data class Style(var color: Color?, var bold: Boolean, var italics: Boolean)

}