package net.slimediamond.atom.api.event

open class Cause(val root: Any, open val causes: List<Any>) : Iterable<Any> {

    companion object {
        fun of(root: Any, vararg causes: Any): Cause.Mutable {
            return Cause.Mutable(root, causes.toMutableList())
        }
    }

    /**
     * Get the first occurrence of a particular cause
     */
    fun <T> first(clazz: Class<T>): T {
        if (clazz.isInstance(root)) return clazz.cast(root)
        for (cause in causes) {
            if (clazz.isInstance(cause)) return clazz.cast(cause)
        }
        throw NoSuchElementException("No element of type ${clazz.name} found")
    }

    override fun iterator(): Iterator<Any> {
        return Itr(this)
    }

    private class Itr(private val cause: Cause) : Iterator<Any> {

        private var index = -1

        override fun hasNext(): Boolean {
            return index < cause.causes.size
        }

        override fun next(): Any {
            index++
            return when (index) {
                0 -> cause.root
                in 1..cause.causes.size -> cause.causes[index - 1]
                else -> throw NoSuchElementException()
            }
        }

    }

    fun toMutable(): Mutable {
        return Mutable(root, causes.toMutableList())
    }

    /**
     * A mutable cause, which things can be added to
     */
    class Mutable(val cause: Any, override val causes: MutableList<Any>) : Cause(cause, causes) {

        /**
         * Add a cause to the cause stack
         */
        fun push(cause: Any) {
            causes.add(cause)
        }

    }

}
