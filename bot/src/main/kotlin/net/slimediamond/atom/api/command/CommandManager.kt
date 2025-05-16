package net.slimediamond.atom.api.command

import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.messaging.Audience

interface CommandManager {

    /**
     * Handle a command execution
     */
    fun handle(sender: CommandSender, command: String, input: String, platform: CommandPlatform, audience: Audience)

}