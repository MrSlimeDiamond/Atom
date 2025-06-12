package net.slimediamond.atom.api.command

import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience

interface Command {

    /**
     * Execute the command
     *
     * @return The result of the command
     */
    suspend fun execute(sender: CommandSender, input: String, platform: CommandPlatform, audience: Audience, cause: Cause): CommandResult

}