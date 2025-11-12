package net.slimediamond.atom.discord.abstraction.messaging

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.DeferredMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.SlashCommandAudience
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer

class KordSlashCommandAudience(private val interaction: ChatInputCommandInteraction) : SlashCommandAudience {

    private var deferred: Boolean = false
    private var response: DeferredMessageInteractionResponseBehavior? = null

    override suspend fun defer(ephemeral: Boolean) {
        deferred = true
        response = if (ephemeral) interaction.deferEphemeralResponse()
        else interaction.deferPublicResponse()
    }

    private suspend fun respond(content: String? = null, embeds: List<EmbedBuilder>? = null, ephemeral: Boolean = false) {
        if (deferred) {
            response!!.respond {
                if (content != null) {
                    this.content = content
                }
                if (embeds != null) {
                    this.embeds = embeds.toMutableList()
                }
            }
        } else if (ephemeral) {
            interaction.respondEphemeral {
                if (content != null) {
                    this.content = content
                }
                if (embeds != null) {
                    this.embeds = embeds.toMutableList()
                }
            }
        } else {
            interaction.respondPublic {
                if (content != null) {
                    this.content = content
                }
                if (embeds != null) {
                    this.embeds = embeds.toMutableList()
                }
            }
        }
    }

    override suspend fun sendMessage(message: String) =
        respond(content = message)

    override suspend fun sendMessage(message: String, ephemeral: Boolean) =
        respond(content = message, ephemeral = ephemeral)

    override suspend fun sendMessage(message: RichText) =
        respond(content = DiscordRichMessageRenderer.render(message))

    override suspend fun sendMessage(message: RichText, ephemeral: Boolean) =
        respond(content = DiscordRichMessageRenderer.render(message), ephemeral = ephemeral)

    override suspend fun sendEmbeds(vararg embeds: EmbedBuilder) =
        respond(embeds = embeds.toList())

    override suspend fun sendEmbeds(vararg embeds: EmbedBuilder, ephemeral: Boolean) =
        respond(embeds = embeds.toList(), ephemeral = ephemeral)

}