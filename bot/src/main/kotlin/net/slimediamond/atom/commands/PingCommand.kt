package net.slimediamond.atom.commands

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandResult

class PingCommand : CommandNode("ping") {

    override fun execute(context: CommandNodeContext): CommandResult {
        context.sendMessage("Pong")
        return CommandResult.success
    }

}