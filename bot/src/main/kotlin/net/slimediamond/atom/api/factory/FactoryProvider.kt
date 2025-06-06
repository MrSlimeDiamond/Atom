package net.slimediamond.atom.api.factory

import kotlin.reflect.KClass

interface FactoryProvider {

    /**
     * Register a factory to be accessible via this provider
     */
    fun offer(instance: Any)

    /**
     * Provide a factory
     */
    fun <T : Any> provide(clazz: KClass<T>): T

}