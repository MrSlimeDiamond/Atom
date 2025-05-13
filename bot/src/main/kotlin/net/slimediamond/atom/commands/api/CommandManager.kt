package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Audience

interface CommandManager<T> {

    /**
     * Register a command
     */
    fun register(command: T, aliases: List<String>)

    /**
     * Handle a command execution
     */
    fun handle(command: String, input: String, platform: CommandPlatform, audience: Audience)

}