package net.slimediamond.atom.utils

import com.minecraftonline.mcodata.api.model.MCOPlayer
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.discord.embed.description
import net.slimediamond.atom.api.messaging.RichText

object Embeds {

    const val MCO_FOOTER_TEXT: String = "MinecraftOnline.com"
    const val MCO_ICON: String = "https://minecraftonline.com/w/images/favicon_diamondcross1_130_transparent.png"
    const val MCO_ICON_LARGE: String = "https://minecraftonline.com/w/images/d/d6/Mco_logo_256x256.png"
    val MCO_FOOTER = EmbedBuilder.Footer().apply {
        text = MCO_FOOTER_TEXT
        icon = MCO_ICON
    }
    val THEME_COLOR = Color(79, 235, 52)

    fun success(message: String): EmbedBuilder {
        return EmbedBuilder().apply {
            color = THEME_COLOR
            description = message
        }
    }

}

fun MCOPlayer.infoEmbed(description: RichText): EmbedBuilder {
    val player = this
    return EmbedBuilder().apply {
        color = Embeds.THEME_COLOR
        author {
            name = player.name
            icon = player.avatarUrl
        }
        footer = Embeds.MCO_FOOTER
        description(description)
    }
}