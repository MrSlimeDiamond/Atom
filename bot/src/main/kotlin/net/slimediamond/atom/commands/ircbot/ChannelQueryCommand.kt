package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult

class ChannelQueryCommand : CommandNode("Get a list of IRC channels", "query") {

    init {
        permission = "atom.command.ircbot.channel.query"
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        TODO("Not yet implemented")
    }

}