package net.slimediamond.atom.api.messaging

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer

class SlashCommandAudience(private val interaction: ChatInputCommandInteraction) : DiscordAudience {

    override suspend fun sendEmbeds(vararg embeds: EmbedBuilder) {
        interaction.deferPublicResponse().respond {
            this.embeds = embeds.toMutableList()
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

}