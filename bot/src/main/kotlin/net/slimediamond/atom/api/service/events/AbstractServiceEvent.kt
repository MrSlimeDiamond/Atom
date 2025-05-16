package net.slimediamond.atom.api.service.events

import net.slimediamond.atom.api.event.AbstractEvent
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.service.ServiceContainer

abstract class AbstractServiceEvent(
    override val cause: Cause,
    val container: ServiceContainer,
    open val clazz: Class<*>
) : AbstractEvent(cause)