package net.slimediamond.atom.api.irc.events

import net.slimediamond.atom.api.event.AbstractEvent
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection

open class IrcEvent(override val cause: Cause, connection: Connection, line: String) : AbstractEvent(cause)