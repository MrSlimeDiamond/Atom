package net.slimediamond.atom.commands.api.irc

import net.slimediamond.atom.Audience
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandPlatform

class IrcCommandNodeContext(input: String, platform: CommandPlatform, private val audience: Audience) : CommandNodeContext(input, platform) {

    override fun reply(message: String) {
        audience.sendMessage(message)
    }

}