package net.slimediamond.atom.api.command.parameter

interface ValueParser<T> {

    /**
     * Parse input to return a value
     */
    fun parse(input: String): T

}