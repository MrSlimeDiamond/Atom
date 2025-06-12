package net.slimediamond.atom.discord.abstraction.messaging

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.SlashCommandAudience
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer

class KordSlashCommandAudience(private val interaction: ChatInputCommandInteraction) : SlashCommandAudience {

    override suspend fun sendEmbeds(vararg embeds: EmbedBuilder) {
        interaction.deferPublicResponse().respond {
            this.embeds = embeds.toMutableList()
        }
    }

    override suspend fun sendMessage(message: String, ephemeral: Boolean) {
        if (!ephemeral) {
            sendMessage(message)
        } else {
            interaction.deferEphemeralResponse().respond {
                this.content = message
            }
        }
    }

    override suspend fun sendMessage(message: RichText, ephemeral: Boolean) {
        if (!ephemeral) {
            sendMessage(message)
        } else {
            interaction.deferEphemeralResponse().respond {
                this.content = DiscordRichMessageRenderer.render(message)
            }
        }
    }

    override suspend fun sendMessage(message: String) {
        interaction.deferPublicResponse().respond {
            content = message
        }
    }

    override suspend fun sendMessage(message: RichText) {
        interaction.deferPublicResponse().respond {
            content = DiscordRichMessageRenderer.render(message)
        }
    }

    override suspend fun sendEmbeds(vararg embeds: EmbedBuilder, ephemeral: Boolean) {
        if (!ephemeral) {
            sendEmbeds(*embeds)
        } else {
            interaction.deferEphemeralResponse().respond {
                this.embeds = embeds.toMutableList()
            }
        }
    }

}