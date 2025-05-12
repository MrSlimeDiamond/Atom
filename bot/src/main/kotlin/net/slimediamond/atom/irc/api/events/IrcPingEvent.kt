package net.slimediamond.atom.irc.api.events

import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.irc.api.Connection

class IrcPingEvent(override val cause: Cause, connection: Connection, line: String) : IrcEvent(cause, connection, line) {

    val message: String = line.split(":")[1]

}