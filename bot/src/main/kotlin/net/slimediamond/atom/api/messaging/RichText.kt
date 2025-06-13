package net.slimediamond.atom.api.messaging

import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Duration
import java.time.Instant
import java.util.*

/**
 * A "rich message", supporting colours, appending elements, etc
 */
open class RichText private constructor(var content: String, var style: Style?) {

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

        fun timestamp(date: Date, relative: Boolean = false): Timestamp {
            return Timestamp(date, relative)
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

    fun appendSpace(): RichText {
        parts.add(of(" "))
        return this
    }

    fun appendNewline(): RichText {
        parts.add(newline())
        return this
    }

    data class Style(var color: Color?, var bold: Boolean, var italics: Boolean)

    data class Timestamp(val date: Date, val relative: Boolean) : RichText(
        if (relative) relativeDate(date) else date.toString(), null)

}

fun relativeDate(date: Date): String {
    val duration = Duration.between(date.toInstant(), Instant.now())
    return DurationFormatUtils.formatDurationWords(duration.toMillis(), true, true) + " ago"
}

inline fun richText(build: RichText.() -> Unit): RichText = RichText.of().apply(build)

fun richText(date: Date): RichText = RichText.timestamp(date)

inline fun richText(date: Date, build: RichText.() -> Unit): RichText = RichText.timestamp(date)

inline fun richText(text: String, build: RichText.() -> Unit): RichText = RichText.of(text).apply(build)

fun richText(text: String): RichText = RichText.of(text)