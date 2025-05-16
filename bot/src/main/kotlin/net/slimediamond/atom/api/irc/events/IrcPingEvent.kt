package net.slimediamond.atom.api.irc.events

import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection

class IrcPingEvent(override val cause: Cause, connection: Connection, line: String) : IrcEvent(cause, connection, line) {

    val message: String = line.split(":")[1]

}