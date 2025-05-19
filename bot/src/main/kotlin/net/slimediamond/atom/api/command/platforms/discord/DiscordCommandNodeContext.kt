package net.slimediamond.atom.api.command.platforms.discord

import dev.kord.core.entity.Embed
import dev.kord.rest.builder.message.EmbedBuilder
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.messaging.DiscordAudience
import net.slimediamond.atom.api.messaging.RichText

class DiscordCommandNodeContext(
    commandNode: CommandNode,
    sender: CommandSender,
    input: String,
    platform: CommandPlatform,
    parameterKeyMap: Map<String, String>,
    private val audience: DiscordAudience
) : CommandNodeContext(commandNode, sender, input, platform, parameterKeyMap) {

    override fun sendMessage(message: String) {
        audience.sendMessage(message)
    }

    override fun sendMessage(message: RichText) {
        audience.sendMessage(message)
    }

    suspend fun sendEmbeds(vararg embeds: EmbedBuilder) {
        audience.sendEmbeds(*embeds)
    }

}