package net.slimediamond.atom.api.irc.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.events.IrcReceivedWelcomeEvent

class WelcomeLineHandler : LineHandler {

    private var done = false

    override fun handle(line: String, connection: Connection) {
        // Yep...
        // This is very silly
        if (line.contains("welcome", true) && !done) {
            val cause = Cause.of(connection)
            Atom.instance.eventManager.post(IrcReceivedWelcomeEvent(cause, connection, line))
            done = true
        }
    }

}