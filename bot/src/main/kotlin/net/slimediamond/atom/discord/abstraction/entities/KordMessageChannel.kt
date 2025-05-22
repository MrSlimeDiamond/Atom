package net.slimediamond.atom.discord.abstraction.entities

import dev.kord.core.behavior.channel.createMessage
import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.discord.entities.MessageChannel
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer

class KordMessageChannel(private val channel: dev.kord.core.entity.channel.MessageChannel) : MessageChannel {

    override val id: Long
        get() = channel.id.value.toLong()

    override suspend fun sendEmbeds(vararg embeds: EmbedBuilder) {
        channel.createMessage {
            this.embeds = embeds.toMutableList()
        }
    }

    override suspend fun sendMessage(message: String) {
        channel.createMessage(message)
    }

    override suspend fun sendMessage(message: RichText) {
        sendMessage(DiscordRichMessageRenderer.render(message));
    }

}