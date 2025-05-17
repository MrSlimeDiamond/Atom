package net.slimediamond.atom.api.command.platforms

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.messaging.RichText

interface CommandPlatform {

    /**
     * Render a "too many arguments" message
     */
    fun renderTooManyArguments(command: CommandNode, index: Int, input: String): RichText

    /**
     * Render a "not enough arguments" message
     */
    fun renderNotEnoughArguments(command: CommandNode, index: Int): RichText

    /**
     * Render an argument parse exception
     */
    fun renderArgumentParseException(exception: ArgumentParseException): RichText

    /**
     * Create a command context instance
     */
    fun createContext(command: CommandNode, sender: CommandSender, input: String, audience: Audience, parameterKeyMap: Map<String, String>): CommandNodeContext

}