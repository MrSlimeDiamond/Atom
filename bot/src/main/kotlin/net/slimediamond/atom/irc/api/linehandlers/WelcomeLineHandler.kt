package net.slimediamond.atom.irc.api.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.CauseImpl
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.irc.api.events.IrcReceivedWelcomeEvent

class WelcomeLineHandler : LineHandler {

    private var done = false

    override fun handle(line: String, connection: Connection) {
        // Yep...
        // This is very silly
        if (line.contains("welcome", true) && !done) {
            val cause = CauseImpl()
            Atom.instance.eventManager.post(IrcReceivedWelcomeEvent(cause, connection, line))
            done = true
        }
    }

}