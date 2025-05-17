package net.slimediamond.atom.discord.abstraction.entities

import dev.kord.core.entity.effectiveName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.messaging.RichMessage
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer

class KordUser(private val user: dev.kord.core.entity.User) : User {

    override val displayName: String
        get() = user.effectiveName
    override val username: String
        get() = user.username
    override val id: Long
        get() = user.id.value.toLong()

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(message: String) {
        GlobalScope.launch {
            user.getDmChannel().createMessage(message)
        }
    }

    override fun sendMessage(message: RichMessage) {
        sendMessage(DiscordRichMessageRenderer.render(message))
    }

}