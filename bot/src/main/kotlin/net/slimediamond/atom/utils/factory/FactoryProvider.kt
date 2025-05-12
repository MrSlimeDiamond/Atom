package net.slimediamond.atom.utils.factory

interface FactoryProvider {

    /**
     * Register a factory to be accessible via this provider
     */
    fun offer(instance: Any)

    /**
     * Provide a factory
     */
    fun <T : Any> provide(clazz: Class<T>): T

}