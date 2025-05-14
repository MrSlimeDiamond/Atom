package net.slimediamond.atom.commands.api.irc

import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandPlatform
import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.messaging.RichMessage

class IrcCommandNodeContext(sender: CommandSender, input: String, platform: CommandPlatform, private val audience: Audience) : CommandNodeContext(sender, input, platform) {

    override fun sendMessage(message: String) {
        audience.sendMessage(message)
    }

    override fun sendMessage(message: RichMessage) {
        audience.sendMessage(message)
    }

}