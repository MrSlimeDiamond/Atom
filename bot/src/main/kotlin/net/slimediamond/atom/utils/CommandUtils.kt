package net.slimediamond.atom.utils

import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.storage.dao.ChannelDao

object CommandUtils {

    fun getChannelDao(context: CommandNodeContext): ChannelDao {
        val channel = context.requireOne(Parameters.IRC_CHANNEL)
        return ChannelDao.getByName(channel)
    }

}