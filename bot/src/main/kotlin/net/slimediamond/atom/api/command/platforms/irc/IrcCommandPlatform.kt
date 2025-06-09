package net.slimediamond.atom.api.command.platforms.irc

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText

class IrcCommandPlatform : CommandPlatform {

    override fun renderTooManyArguments(command: CommandNode, index: Int, input: String): RichText {
        return RichText.of("Too many arguments! Usage: ${command.usage}").color(Color.RED)
    }

    override fun renderNotEnoughArguments(command: CommandNode, index: Int): RichText {
        return RichText.of("Not enough arguments! Usage: ${command.usage}").color(Color.RED)
    }

    override fun renderArgumentParseException(e: ArgumentParseException): RichText {
        return RichText.of("Could not parse args: ${e.javaClass.simpleName}: ")
            .append(e.msg)
            .color(Color.RED)
    }

    override fun createContext(command: CommandNode, cause: Cause, sender: CommandSender, input: String, audience: Audience, parameterKeyMap: Map<String, String>): CommandNodeContext {
        return IrcCommandNodeContext(command, cause, sender, input, this, parameterKeyMap, audience)
    }

}