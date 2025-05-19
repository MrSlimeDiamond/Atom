package net.slimediamond.atom.api.service.events

import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.service.ServiceContainer

class ServiceStopEvent(cause: Cause, container: ServiceContainer, clazz: Class<*>) : AbstractServiceEvent(cause, container, clazz)