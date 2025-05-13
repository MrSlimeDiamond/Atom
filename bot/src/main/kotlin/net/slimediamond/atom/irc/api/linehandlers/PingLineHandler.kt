package net.slimediamond.atom.irc.api.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.event.CauseImpl
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.irc.api.events.IrcPingEvent

class PingLineHandler : LineHandler {

    override fun handle(line: String, connection: Connection) {
        val cause: Cause = CauseImpl()
        if (line.startsWith("PING")) {
            val event = IrcPingEvent(cause, connection, line)
            // respond to the ping
            connection.sendRaw("PONG :${event.message}")
            Atom.instance.eventManager.post(event)
        }
    }

}