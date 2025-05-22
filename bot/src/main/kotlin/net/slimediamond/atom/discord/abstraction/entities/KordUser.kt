package net.slimediamond.atom.discord.abstraction.entities

import dev.kord.core.entity.effectiveName
import dev.kord.rest.builder.message.EmbedBuilder
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

    override suspend fun sendMessage(message: String) {
        getDmChannel().sendMessage(message)
    }

    override suspend fun sendMessage(message: RichText) {
        getDmChannel().sendMessage(message)
    }

    suspend fun getDmChannel(): KordMessageChannel {
        return KordMessageChannel(user.getDmChannel())
    }

}