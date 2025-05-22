package net.slimediamond.atom.api.discord.embed

import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer

fun EmbedBuilder.description(text: RichText) {
    description = DiscordRichMessageRenderer.render(text)
}