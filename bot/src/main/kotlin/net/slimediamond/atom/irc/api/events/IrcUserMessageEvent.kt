package net.slimediamond.atom.irc.api.events

import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.irc.api.entities.User

class IrcUserMessageEvent(cause: Cause, connection: Connection, line: String, message: String, user: User) : IrcMessageEvent(cause, connection, line, message, user, user)
