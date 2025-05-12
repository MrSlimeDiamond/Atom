package net.slimediamond.atom.service

import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.event.CauseImpl
import net.slimediamond.atom.service.events.ServiceStartEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.LinkedList

class ServiceManager {

    private val services: MutableList<ServiceContainer> = LinkedList()

    fun addService(instance: Any) {
        if (!instance.javaClass.isAnnotationPresent(Service::class.java)) {
            throw IllegalArgumentException("Service class is not annotated with @Service");
        }
        val name: String = instance.javaClass.getAnnotation(Service::class.java).value
        val logger: Logger = LogManager.getLogger(name)
        services.add(ServiceContainer(name, instance, logger))
        Atom.instance.eventManager.registerListener(instance)
    }

    fun startAll() {
        val cause: Cause = CauseImpl()
        services.forEach {
            // new event instance for each class fired
            val event: ServiceStartEvent<Any> = ServiceStartEvent(cause, it)
            Atom.instance.eventManager.post(event)
        }
    }

}