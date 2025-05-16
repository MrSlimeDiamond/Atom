package net.slimediamond.atom.api.service.events

import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.service.ServiceContainer

class ServiceStartEvent(override val cause: Cause, serviceContainer: ServiceContainer, override val clazz: Class<*>) : AbstractServiceEvent(cause, serviceContainer, clazz)