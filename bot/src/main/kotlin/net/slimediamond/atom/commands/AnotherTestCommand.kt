package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters

class AnotherTestCommand : CommandNode("Secondary test command with parameter", "anothertest") {

    init {
        parameters.add(Parameters.NUMBER)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val number = context.requireOne(Parameters.NUMBER)
        context.sendMessage(number.toString())
        return CommandResult.success
    }

}