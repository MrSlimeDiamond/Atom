package net.slimediamond.atom.commands.api.platforms.irc

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.messaging.Color
import net.slimediamond.atom.messaging.RichMessage

class IrcCommandPlatform : CommandPlatform {

    override fun renderTooManyArguments(command: CommandNode, index: Int, input: String): RichMessage {
        return RichMessage.of("Too many arguments! Usage: ${command.usage}").color(Color.RED)
    }

    override fun renderNotEnoughArguments(command: CommandNode, index: Int, input: String): RichMessage {
        return RichMessage.of("Not enough arguments! Usage: ${command.usage}").color(Color.RED)
    }

    override fun createContext(command: CommandNode, sender: CommandSender, input: String, audience: Audience): CommandNodeContext {
        return IrcCommandNodeContext(command, sender, input, this, audience)
    }

}