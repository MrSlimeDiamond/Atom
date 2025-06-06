package net.slimediamond.atom.api.factory

import kotlin.reflect.KClass

class DefaultFactoryProvider : FactoryProvider {

    private val factories: MutableMap<KClass<*>, Any> = HashMap()

    override fun offer(instance: Any) {
        factories[instance::class] = instance
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> provide(clazz: KClass<T>): T {
        return factories[clazz] as T
    }

}