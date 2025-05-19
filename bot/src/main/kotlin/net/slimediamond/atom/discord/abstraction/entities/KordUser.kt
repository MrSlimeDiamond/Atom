package net.slimediamond.atom.discord.abstraction.entities

import dev.kord.core.entity.effectiveName
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.messaging.RichText

class KordUser(private val user: dev.kord.core.entity.User) : User {

    override val displayName: String
        get() = user.effectiveName
    override val username: String
        get() = user.username
    override val id: Long
        get() = user.id.value.toLong()

    override suspend fun sendEmbeds(vararg embeds: EmbedBuilder) {
        getDmChannel().sendEmbeds(*embeds)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(message: String) {
        GlobalScope.launch {
            getDmChannel().sendMessage(message)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(message: RichText) {
        GlobalScope.launch {
            getDmChannel().sendMessage(message)
        }
    }

    suspend fun getDmChannel(): KordMessageChannel {
        return KordMessageChannel(user.getDmChannel())
    }

}