package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters

class TestCommand : CommandNode("Debug/test commands", "test") {

    init {
        addChild(ChildCommand())
        addChild(ErrorCommand())
        addChild(NumberCommand())
        addChild(PermissionTestCommand())

        parameters.add(Parameters.OPTIONAL_MESSAGE)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        context.sendMessage("Parent")

        val message = context.one(Parameters.OPTIONAL_MESSAGE)
        if (message != null) {
            context.sendMessage("Message: $message")
        }

        return CommandResult.success
    }

    class ChildCommand : CommandNode("Test subcommands", "child") {

        init {
            parameters.add(Parameters.MESSAGE)
        }

        override suspend fun execute(context: CommandNodeContext): CommandResult {
            val message = context.requireOne(Parameters.MESSAGE)
            context.sendMessage("Child")

            // required message
            context.sendMessage("Message: $message")

            return CommandResult.success
        }

    }

    class ErrorCommand : CommandNode("Test throwing an error", "error") {

        override suspend fun execute(context: CommandNodeContext): CommandResult {
            return CommandResult.error("Enjoy this error!")
        }

    }

    class NumberCommand : CommandNode("Test parsing a number", "number") {

        init {
            parameters.add(Parameters.NUMBER)
        }

        override suspend fun execute(context: CommandNodeContext): CommandResult {
            val number = context.requireOne(Parameters.NUMBER)

            context.sendMessage("Your number input: $number")

            return CommandResult.success
        }

    }

    class PermissionTestCommand : CommandNode("Test permission checks", "permission") {

        init {
            permission = "atom.command.test.permission"
        }

        override suspend fun execute(context: CommandNodeContext): CommandResult {
            context.sendMessage("you have permission")
            return CommandResult.success
        }

    }

}