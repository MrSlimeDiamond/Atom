package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.CommandUtils
import net.slimediamond.atom.utils.getChannelDao

class ChannelAutoJoinCommand : CommandNode("Change auto join status on an IRC channel", "autojoin") {

    init {
        parameters.add(Parameters.IRC_CHANNEL)
        parameters.add(Parameters.BOOLEAN)

        permission = "atom.command.ircbot.channel.autojoin"
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val channelDao = context.getChannelDao()
        val status = context.requireOne(Parameters.BOOLEAN)

        if (channelDao.autoJoin == status) {
            context.sendMessage("Auto join is already $status for ${channelDao.name}")
            return CommandResult.success
        }

        channelDao.autoJoin = status

        context.sendMessage("Auto join for ${channelDao.name} is now: $status")
        return CommandResult.success
    }

}