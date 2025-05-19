package net.slimediamond.atom.discord.abstraction.entities

import dev.kord.core.behavior.channel.createMessage
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(message: String) {
        GlobalScope.launch {
            channel.createMessage(message)
        }
    }

    override fun sendMessage(message: RichText) {
        sendMessage(DiscordRichMessageRenderer.render(message));
    }

}