package net.slimediamond.atom.api.command.platforms.discord

import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.DiscordAudience
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.SlashCommandAudience
import net.slimediamond.atom.utils.Embeds

class DiscordCommandNodeContext(
    commandNode: CommandNode,
    cause: Cause,
    sender: CommandSender,
    input: String,
    platform: CommandPlatform,
    parameterKeyMap: Map<String, String>,
    private val audience: DiscordAudience
) : CommandNodeContext(commandNode, cause, sender, input, platform, parameterKeyMap) {
    override suspend fun replySuccess(message: String) {
        sendEmbeds(Embeds.success(message))
    }

    override suspend fun replySuccess(message: String, ephemeral: Boolean) {
        if (!ephemeral) {
            replySuccess(message)
        } else {
            if (audience is SlashCommandAudience) {
                audience.sendEmbeds(Embeds.success(message), ephemeral = true)
            }
        }
    }

    override suspend fun sendMessage(message: String) {
        audience.sendMessage(message)
    }

    override suspend fun sendMessage(message: RichText) {
        audience.sendMessage(message)
    }

    suspend fun sendEmbeds(vararg embeds: EmbedBuilder) {
        audience.sendEmbeds(*embeds)
    }

    suspend fun sendEmbed(config: EmbedBuilder.() -> Unit) {
        audience.sendEmbeds(EmbedBuilder().apply(config))
    }

}