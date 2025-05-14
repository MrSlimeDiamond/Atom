package net.slimediamond.atom.commands

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandResult

class ParentCommand : CommandNode("parent") {

    init {
        children.add(ChildCommand())
    }

    override fun execute(context: CommandNodeContext): CommandResult {
        context.sendMessage("Parent")
        return CommandResult.success
    }

    class ChildCommand : CommandNode("child") {

        override fun execute(context: CommandNodeContext): CommandResult {
            context.sendMessage("Child")
            return CommandResult.success
        }

    }

}