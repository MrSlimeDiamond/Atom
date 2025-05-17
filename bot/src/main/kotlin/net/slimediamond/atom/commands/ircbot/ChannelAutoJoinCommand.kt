package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.CommandUtils

class ChannelAutoJoinCommand : CommandNode("autojoin") {

    init {
        parameters.add(Parameters.IRC_CHANNEL)
        parameters.add(Parameters.BOOLEAN)

        permission = "atom.command.ircbot.channel.autojoin"
    }

    override fun execute(context: CommandNodeContext): CommandResult {
        val channelDao = CommandUtils.getChannelDao(context)
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