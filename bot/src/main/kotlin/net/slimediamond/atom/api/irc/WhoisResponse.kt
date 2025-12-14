package net.slimediamond.atom.api.irc

import net.slimediamond.atom.api.irc.entities.User

/**
 * The response of a whois query
 */
data class WhoisResponse(val user: User, val realName: String, val hostname: String, val channels: List<String>, val server: NetworkedServer, val account: String?)

/**
 * A builder for a whois response
 */
class WhoisResponseBuilder {
    lateinit var user: User
    lateinit var realName: String
    lateinit var hostname: String
    var channels: List<String> = emptyList()
    lateinit var server: NetworkedServer
    var account: String? = null

    fun build() = WhoisResponse(
        user, realName, hostname, channels, server, account
    )
}

fun whoisResponse(block: WhoisResponseBuilder.() -> Unit): WhoisResponse =
    WhoisResponseBuilder().apply(block).build()

/**
 * A server which has a name and description, as per the whois response
 */
data class NetworkedServer(val name: String, val description: String)