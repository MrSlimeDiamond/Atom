package net.slimediamond.atom.commands.api.platforms.irc

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.messaging.RichMessage

class IrcCommandNodeContext(
    commandNode: CommandNode,
    sender: CommandSender,
    input: String,
    platform: CommandPlatform,
    private val audience: Audience
) : CommandNodeContext(commandNode, sender, input, platform) {

    override fun sendMessage(message: String) {
        audience.sendMessage(message)
    }

    override fun sendMessage(message: RichMessage) {
        audience.sendMessage(message)
    }

}