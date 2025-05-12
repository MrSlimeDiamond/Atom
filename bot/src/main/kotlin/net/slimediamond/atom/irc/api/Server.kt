package net.slimediamond.atom.irc.api

/**
 * An IRC server
 */
data class Server(
    val name: String?,
    val host: String,
    val port: Int,
    val ssl: Boolean = false
)