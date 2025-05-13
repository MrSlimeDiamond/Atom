package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Audience

interface Command {

    /**
     * Execute the command
     *
     * @return The result of the command
     */
    fun execute(sender: CommandSender, input: String, platform: CommandPlatform, audience: Audience): CommandResult

}