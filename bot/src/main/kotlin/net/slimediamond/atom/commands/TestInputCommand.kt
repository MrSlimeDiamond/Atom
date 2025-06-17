package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.Command
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.command.platforms.CommandPlatform
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience

class TestInputCommand : Command {

    override suspend fun execute(
        sender: CommandSender,
        input: String,
        platform: CommandPlatform,
        audience: Audience,
        cause: Cause
    ): CommandResult {
        val split = CommandNode.tokenizeInput(input)
        audience.sendMessage(split.toString())
        return CommandResult.success
    }

}