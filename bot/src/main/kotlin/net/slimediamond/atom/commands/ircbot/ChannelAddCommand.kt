package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.storage.dao.ChannelDao

class ChannelAddCommand : CommandNode("add") {

    init {
        parameters.add(Parameters.IRC_CHANNEL)
    }

    override fun execute(context: CommandNodeContext): CommandResult {
        val channel = context.requireOne(Parameters.IRC_CHANNEL)
        // This creates a DAO (adds it to the database) if one doesn't exist
        val dao = ChannelDao.getByName(channel)
        context.sendMessage("Channel $channel added (or nothing was done if it's already added)")
        return CommandResult.success
    }

}