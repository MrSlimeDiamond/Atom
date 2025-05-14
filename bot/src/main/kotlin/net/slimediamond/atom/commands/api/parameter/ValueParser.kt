package net.slimediamond.atom.commands.api.parameter

interface ValueParser<T> {

    /**
     * Parse input to return a value
     */
    fun parse(input: String): T

}