package net.slimediamond.atom.service

import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.Cause
import net.slimediamond.atom.event.CauseImpl
import net.slimediamond.atom.service.events.ServiceStartEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ServiceManager {

    private val services: MutableMap<Class<*>, ServiceContainer> = HashMap()
    private val logger: Logger = LogManager.getLogger("service manager")

    fun addService(instance: Any) {
        if (!instance.javaClass.isAnnotationPresent(Service::class.java)) {
            throw IllegalArgumentException("Service class is not annotated with @Service");
        }
        val name: String = instance.javaClass.getAnnotation(Service::class.java).value
        val logger: Logger = LogManager.getLogger(name)
        val container = ServiceContainer(name, instance, logger)
        services[instance.javaClass] = container
        Atom.instance.eventManager.registerListener(instance)
        this.logger.info("Service '{}' registered", container.name)
    }

    fun startAll() {
        val cause: Cause = CauseImpl()
        services.values.forEach { container ->
            val event = ServiceStartEvent(cause, container, container.instance.javaClass)
            Atom.instance.eventManager.post(event, container.instance)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> provide(clazz: Class<T>): T {
        return services[clazz]?.instance as T
    }

}