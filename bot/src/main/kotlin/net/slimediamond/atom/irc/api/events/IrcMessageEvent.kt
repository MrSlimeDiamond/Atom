package net.slimediamond.atom.irc.api.events

import net.slimediamond.atom.Audience
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.irc.api.Connection

open class IrcMessageEvent(override val cause: Cause, connection: Connection, line: String, val message: String, val audience: Audience) :
    IrcEvent(cause, connection, line)