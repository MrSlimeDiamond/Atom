package net.slimediamond.atom.utils

import com.minecraftonline.mcodata.api.exceptions.PlayerNotFoundException
import com.minecraftonline.mcodata.api.model.MCOPlayer
import com.minecraftonline.mcodata.web.WebMCODataService
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

fun CommandNodeContext.getTargetMCOPlayer(): MCOPlayer {
    return this.one(Parameters.MCO_PLAYER)?: WebMCODataService().getPlayerByName(this.sender.name)
        .orElseThrow { PlayerNotFoundException(this.sender.name) }
}