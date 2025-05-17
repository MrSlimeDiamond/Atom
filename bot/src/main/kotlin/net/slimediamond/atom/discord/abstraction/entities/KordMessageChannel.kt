package net.slimediamond.atom.discord.abstraction.entities

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.api.discord.entities.MessageChannel
import net.slimediamond.atom.api.messaging.RichMessage
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer

class KordMessageChannel(private val channel: dev.kord.core.entity.channel.MessageChannel) : MessageChannel {

    override val id: Long
        get() = channel.id.value.toLong()

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(message: String) {
        GlobalScope.launch {
            channel.createMessage(message)
        }
    }

    override fun sendMessage(message: RichMessage) {
        sendMessage(DiscordRichMessageRenderer.render(message));
    }

}