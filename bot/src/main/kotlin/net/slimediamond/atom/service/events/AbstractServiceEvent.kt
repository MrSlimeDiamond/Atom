package net.slimediamond.atom.service.events

import net.slimediamond.atom.event.AbstractEvent
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.service.ServiceContainer

abstract class AbstractServiceEvent<T>(override val cause: Cause, val serviceContainer: ServiceContainer) : AbstractEvent(cause)