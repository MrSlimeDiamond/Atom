package net.slimediamond.atom.service.events

import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.service.ServiceContainer

class ServiceStartEvent(override val cause: Cause, serviceContainer: ServiceContainer, override val clazz: Class<*>) : AbstractServiceEvent(cause, serviceContainer, clazz)