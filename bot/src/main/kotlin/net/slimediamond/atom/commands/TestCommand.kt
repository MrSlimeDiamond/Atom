package net.slimediamond.atom.commands

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters

class TestCommand : CommandNode("test") {

    init {
        children.add(ChildCommand())

        parameters.add(Parameters.OPTIONAL_MESSAGE)
    }

    override fun execute(context: CommandNodeContext): CommandResult {
        context.sendMessage("Parent")

        val message = context.one(Parameters.OPTIONAL_MESSAGE)
        if (message != null) {
            context.sendMessage("Message: $message")
        }

        return CommandResult.success
    }

    class ChildCommand : CommandNode("child") {

        init {
            parameters.add(Parameters.MESSAGE)
        }

        override fun execute(context: CommandNodeContext): CommandResult {
            val message = context.requireOne(Parameters.MESSAGE)
            context.sendMessage("Child")

            // required message
            context.sendMessage("Message: $message")

            return CommandResult.success
        }

    }

}