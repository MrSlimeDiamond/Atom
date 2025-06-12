package net.slimediamond.atom.api.command.platforms.irc

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.messaging.RichText

class IrcCommandNodeContext(
    commandNode: CommandNode,
    cause: Cause,
    sender: CommandSender,
    input: String,
    platform: CommandPlatform,
    parameterKeyMap: Map<String, String>,
    private val audience: Audience
) : CommandNodeContext(commandNode, cause, sender, input, platform, parameterKeyMap) {

    override suspend fun sendMessage(message: String) {
        audience.sendMessage(message)
    }

    override suspend fun sendMessage(message: RichText) {
        audience.sendMessage(message)
    }

}