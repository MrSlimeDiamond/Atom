package net.slimediamond.atom.commands.api

import net.slimediamond.atom.commands.api.platforms.CommandPlatform
import net.slimediamond.atom.messaging.Audience

interface CommandManager<T> {

    /**
     * Register a command
     */
    fun register(command: T, aliases: List<String>)

    /**
     * Handle a command execution
     */
    fun handle(sender: CommandSender, command: String, input: String, platform: CommandPlatform, audience: Audience)

}