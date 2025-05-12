package net.slimediamond.atom.service.events

import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.service.ServiceContainer

class ServiceStartEvent<T>(override val cause: Cause, serviceContainer: ServiceContainer) : AbstractServiceEvent<T>(cause, serviceContainer)