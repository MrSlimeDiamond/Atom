package net.slimediamond.atom.commands

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandResult

class WhoamiCommand : CommandNode("whoami") {

    override fun execute(context: CommandNodeContext): CommandResult {
        context.sendMessage("name: ${context.sender.name} (TODO...)")
        return CommandResult.success
    }

}