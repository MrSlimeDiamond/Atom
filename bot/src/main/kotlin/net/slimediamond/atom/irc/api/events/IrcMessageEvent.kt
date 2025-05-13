package net.slimediamond.atom.irc.api.events

import net.slimediamond.atom.Audience
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.irc.api.entities.User

open class IrcMessageEvent(override val cause: Cause, val connection: Connection, val line: String, val message: String, val audience: Audience, val user: User) :
    IrcEvent(cause, connection, line)