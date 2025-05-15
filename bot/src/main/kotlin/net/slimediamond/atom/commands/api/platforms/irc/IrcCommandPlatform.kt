package net.slimediamond.atom.commands.api.platforms.irc

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.commands.api.exceptions.ArgumentParseException
import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.messaging.Color
import net.slimediamond.atom.messaging.RichMessage

class IrcCommandPlatform : CommandPlatform {

    override fun renderTooManyArguments(command: CommandNode, index: Int, input: String): RichMessage {
        return RichMessage.of("Too many arguments! Usage: ${command.usage}").color(Color.RED)
    }

    override fun renderNotEnoughArguments(command: CommandNode, index: Int): RichMessage {
        return RichMessage.of("Not enough arguments! Usage: ${command.usage}").color(Color.RED)
    }

    override fun renderArgumentParseException(e: ArgumentParseException): RichMessage {
        return RichMessage.of("Could not parse args: ${e.javaClass.simpleName}: ")
            .append(e.msg)
            .color(Color.RED)
    }

    override fun createContext(command: CommandNode, sender: CommandSender, input: String, audience: Audience, parameterKeyMap: Map<String, String>): CommandNodeContext {
        return IrcCommandNodeContext(command, sender, input, this, parameterKeyMap, audience)
    }

}