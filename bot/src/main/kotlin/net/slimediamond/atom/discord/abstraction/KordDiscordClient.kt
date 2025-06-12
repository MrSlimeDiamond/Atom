package net.slimediamond.atom.discord.abstraction

import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.SlashCommandNodeManager
import net.slimediamond.atom.api.discord.entities.Guild
import net.slimediamond.atom.api.discord.entities.SlashCommandInteraction
import net.slimediamond.atom.api.discord.event.DiscordGuildMessageEvent
import net.slimediamond.atom.api.discord.event.DiscordSlashCommandEvent
import net.slimediamond.atom.api.discord.event.DiscordUserMessageEvent
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.discord.abstraction.entities.KordGuild
import net.slimediamond.atom.discord.abstraction.entities.KordMessageChannel
import net.slimediamond.atom.discord.abstraction.entities.KordUser
import net.slimediamond.atom.discord.abstraction.messaging.KordSlashCommandAudience

class KordDiscordClient(private val token: String) : DiscordClient {

    lateinit var kord: Kord

    @Volatile
    private lateinit var _slashCommandNodeManager: SlashCommandNodeManager

    override var loggedIn: Boolean
        get() {
            if (!::kord.isInitialized) {
                return false
            }
            return kord.isActive
        }
        set(_) {}

    override var slashCommandNodeManager: SlashCommandNodeManager
        get() = _slashCommandNodeManager
        set(_) {}

    override suspend fun login() {
        kord = Kord(token)

        _slashCommandNodeManager = KordSlashCommandManager(kord)

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
                Atom.bot.eventManager.post(DiscordGuildMessageEvent(cause, this@KordDiscordClient, user, message.content, channel, guild))
            } else {
                // user message
                Atom.bot.eventManager.post(DiscordUserMessageEvent(cause, this@KordDiscordClient, user, message.content))
            }
        }

        kord.on<ChatInputCommandInteractionCreateEvent> {
            val user = KordUser(interaction.user)
            val cause = Cause.of(user)
            val audience = KordSlashCommandAudience(interaction)
            // long name!
            if (this is GuildChatInputCommandInteractionCreateEvent) {
                val guild = KordGuild(interaction.getGuild())
                cause.push(guild)
            }
            val inter = SlashCommandInteraction(interaction.invokedCommandName)
            Atom.bot.eventManager.post(DiscordSlashCommandEvent(cause, this@KordDiscordClient, audience, user, inter))
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