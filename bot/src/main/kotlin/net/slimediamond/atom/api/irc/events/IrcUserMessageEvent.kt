package net.slimediamond.atom.api.irc.events

import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.entities.User

class IrcUserMessageEvent(cause: Cause, connection: Connection, line: String, message: String, user: User) : IrcMessageEvent(cause, connection, line, message, user, user)
