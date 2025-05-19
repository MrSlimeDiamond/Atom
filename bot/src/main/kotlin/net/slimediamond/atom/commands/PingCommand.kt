package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult

class PingCommand : CommandNode("Replies with pong", "ping") {

    override fun execute(context: CommandNodeContext): CommandResult {
        context.sendMessage("Pong")
        return CommandResult.success
    }

}