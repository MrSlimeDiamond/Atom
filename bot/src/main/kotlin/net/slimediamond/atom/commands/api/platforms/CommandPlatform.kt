package net.slimediamond.atom.commands.api.platforms

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.commands.api.exceptions.ArgumentParseException
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.messaging.RichMessage

interface CommandPlatform {

    /**
     * Render a "too many arguments" message
     */
    fun renderTooManyArguments(command: CommandNode, index: Int, input: String): RichMessage

    /**
     * Render a "not enough arguments" message
     */
    fun renderNotEnoughArguments(command: CommandNode, index: Int, input: String): RichMessage

    /**
     * Render an argument parse exception
     */
    fun renderArgumentParseException(exception: ArgumentParseException): RichMessage

    /**
     * Create a command context instance
     */
    fun createContext(command: CommandNode, sender: CommandSender, input: String, audience: Audience): CommandNodeContext

}