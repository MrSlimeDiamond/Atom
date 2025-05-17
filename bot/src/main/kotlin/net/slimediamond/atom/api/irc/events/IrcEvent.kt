package net.slimediamond.atom.api.irc.events

import net.slimediamond.atom.api.event.AbstractEvent
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection

open class IrcEvent(override val cause: Cause, val connection: Connection, val line: String) : AbstractEvent(cause)