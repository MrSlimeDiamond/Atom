package net.slimediamond.atom.irc.api.events

import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.irc.api.entities.Channel
import net.slimediamond.atom.irc.api.entities.User

class IrcChannelMessageEvent(
    cause: Cause,
    connection: Connection,
    line: String,
    message: String,
    user: User,
    val channel: Channel,
) : IrcMessageEvent(cause, connection, line, message, channel, user)