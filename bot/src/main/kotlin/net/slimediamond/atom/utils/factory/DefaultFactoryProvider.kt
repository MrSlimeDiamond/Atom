package net.slimediamond.atom.utils.factory

class DefaultFactoryProvider : FactoryProvider {

    private val factories: MutableMap<Class<*>, Any> = HashMap()

    override fun offer(instance: Any) {
        factories[instance.javaClass] = instance
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> provide(clazz: Class<T>): T {
        return factories[clazz] as T
    }

}

inline fun <reified T : Any> FactoryProvider.provide(): T {
    return provide(T::class.java)
}
