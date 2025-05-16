package net.slimediamond.atom.api.irc

/**
 * An IRC server
 */
data class Server(
    val name: String?,
    val host: String,
    val port: Int,
    val ssl: Boolean = false
)