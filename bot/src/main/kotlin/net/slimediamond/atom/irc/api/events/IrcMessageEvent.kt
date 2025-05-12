package net.slimediamond.atom.irc.api.events

import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.irc.api.Connection

class IrcMessageEvent(override val cause: Cause, connection: Connection, line: String, val message: String) :
    IrcEvent(cause, connection, line)