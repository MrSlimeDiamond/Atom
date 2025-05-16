package net.slimediamond.atom.api.irc.events

import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.entities.Channel
import net.slimediamond.atom.api.irc.entities.User

class IrcChannelMessageEvent(
    cause: Cause,
    connection: Connection,
    line: String,
    message: String,
    user: User,
    val channel: Channel,
) : IrcMessageEvent(cause, connection, line, message, channel, user)