package net.slimediamond.atom.service.events

import net.slimediamond.atom.event.AbstractEvent
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.service.ServiceContainer

abstract class AbstractServiceEvent(
    override val cause: Cause,
    val container: ServiceContainer,
    open val clazz: Class<*>
) : AbstractEvent(cause)