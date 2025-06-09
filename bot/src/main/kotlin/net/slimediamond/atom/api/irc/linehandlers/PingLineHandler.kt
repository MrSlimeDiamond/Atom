package net.slimediamond.atom.api.irc.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.events.IrcPingEvent

class PingLineHandler : LineHandler {

    override fun handle(line: String, connection: Connection) {
        val cause: Cause = Cause.of(connection)
        if (line.startsWith("PING")) {
            val event = IrcPingEvent(cause, connection, line)
            // respond to the ping
            connection.sendRaw("PONG :${event.message}")
            Atom.bot.eventManager.post(event)
        }
    }

}