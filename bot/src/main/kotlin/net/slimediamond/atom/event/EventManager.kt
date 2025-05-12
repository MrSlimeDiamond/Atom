package net.slimediamond.atom.event

import java.util.LinkedList

class EventManager {

    private val listeners: MutableList<Any> = LinkedList()

    fun registerListener(instance: Any) {
        listeners.add(instance)
    }

    fun post(event: Event) {
        for (listener in listeners) {
            listener.javaClass.declaredMethods
                .filter { method ->
                    method.isAnnotationPresent(Listener::class.java) &&
                            method.parameterTypes.size == 1 &&
                            method.parameterTypes[0].isAssignableFrom(event.javaClass)
                }
                .forEach { method ->
                    method.isAccessible = true
                    method.invoke(listener, event)
                }
        }
    }

}