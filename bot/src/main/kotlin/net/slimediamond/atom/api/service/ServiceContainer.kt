package net.slimediamond.atom.api.service

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.service.events.ServiceStartEvent
import net.slimediamond.atom.api.service.events.ServiceStopEvent
import org.apache.logging.log4j.Logger

data class ServiceContainer(val name: String, val instance: Any, val logger: Logger) {

    fun start(cause: Cause) {
        Atom.bot.eventManager.post(ServiceStartEvent(cause, this, instance.javaClass), instance)
    }

    fun stop(cause: Cause) {
        Atom.bot.eventManager.post(ServiceStopEvent(cause, this, instance.javaClass), instance)
    }

    fun restart(cause: Cause) {
        stop(cause)
        start(cause)
    }

}