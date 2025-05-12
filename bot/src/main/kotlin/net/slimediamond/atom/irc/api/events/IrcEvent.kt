package net.slimediamond.atom.irc.api.events

import net.slimediamond.atom.event.AbstractEvent
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.irc.api.Connection

open class IrcEvent(override val cause: Cause, connection: Connection, line: String) : AbstractEvent(cause)