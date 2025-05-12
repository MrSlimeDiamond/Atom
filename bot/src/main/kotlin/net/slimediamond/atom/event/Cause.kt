package net.slimediamond.atom.event

interface Cause {

    fun <T> first(clazz: Class<T>): T?

}