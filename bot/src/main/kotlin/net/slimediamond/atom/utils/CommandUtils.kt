package net.slimediamond.atom.utils

import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.exceptions.CommandException
import net.slimediamond.atom.api.command.platforms.irc.IrcCommandNodeContext
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.storage.dao.ChannelDao

fun CommandNodeContext.getChannelDao(): ChannelDao {
    if (this !is IrcCommandNodeContext) {
        throw CommandException(RichText.of("This command is only available in IRC"))
    }
    val channel = this.requireOne(Parameters.IRC_CHANNEL)
    return ChannelDao.getByName(channel)
}