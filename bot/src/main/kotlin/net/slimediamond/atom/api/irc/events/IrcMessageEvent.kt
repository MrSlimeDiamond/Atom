package net.slimediamond.atom.api.irc.events

import net.slimediamond.atom.api.messaging.Audience
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.entities.User

open class IrcMessageEvent(override val cause: Cause, val connection: Connection, val line: String, val message: String, val audience: Audience, val user: User) :
    IrcEvent(cause, connection, line)