package net.slimediamond.atom.api.event

interface Cause {

    fun <T> first(clazz: Class<T>): T?

}