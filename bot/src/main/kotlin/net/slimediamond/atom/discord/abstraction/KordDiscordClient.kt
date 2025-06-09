package net.slimediamond.atom.discord.abstraction

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.Guild
import net.slimediamond.atom.api.discord.event.DiscordGuildMessageEvent
import net.slimediamond.atom.api.discord.event.DiscordUserMessageEvent
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.discord.abstraction.entities.KordGuild
import net.slimediamond.atom.discord.abstraction.entities.KordMessageChannel
import net.slimediamond.atom.discord.abstraction.entities.KordUser

class KordDiscordClient(private val token: String) : DiscordClient {

    lateinit var kord: Kord

    override suspend fun login() {
        kord = Kord(token)

        kord.on<MessageCreateEvent> {
            val kordUser = message.author ?: return@on
            val user = KordUser(kordUser)
            val kordGuild = message.getGuildOrNull()
            val kordChannel = message.getChannel()
            val cause = Cause.of(user, kordChannel)
            if (kordGuild != null) {
                val guild = KordGuild(kordGuild)
                val channel = KordMessageChannel(kordChannel)
                cause.push(guild)
                cause.push(channel)
                Atom.instance.eventManager.post(DiscordGuildMessageEvent(cause, this@KordDiscordClient, user, message.content, channel, guild))
            } else {
                // user message
                Atom.instance.eventManager.post(DiscordUserMessageEvent(cause, this@KordDiscordClient, user, message.content))
            }
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }

    override suspend fun logout() {
        kord.logout()
    }

    override suspend fun getGuildById(id: Long): Guild? {
        val kordGuild = kord.guilds.filter { it.id.value.toLong() == id }.firstOrNull()
        if (kordGuild != null) {
            return KordGuild(kordGuild)
        }
        return null
    }

}