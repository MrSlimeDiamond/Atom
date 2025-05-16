package net.slimediamond.atom.api.factory

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