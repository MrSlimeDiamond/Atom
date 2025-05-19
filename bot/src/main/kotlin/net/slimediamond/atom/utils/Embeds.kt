package net.slimediamond.atom.utils

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder

object Embeds {

    val THEME_COLOR = Color(79, 235, 52)

    fun success(message: String): EmbedBuilder {
        return EmbedBuilder().apply {
            color = THEME_COLOR
            description = message
        }
    }

}