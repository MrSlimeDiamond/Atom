package net.slimediamond.atom.api.irc.entities

import net.slimediamond.atom.api.irc.WhoisResponse
import net.slimediamond.atom.api.messaging.Audience
import java.util.concurrent.CompletableFuture

/**
 * A user on the IRC server
 */
interface User : Audience {

    /**
     * The name of the user
     */
    val nickname: String

    /**
     * The 'username' of the user
     */
    val username: String

    /**
     * The user's hostname
     */
    val hostname: String

    /**
     * The user's real name
     */
    val realName: String?

    /**
     * Get a whois response for this user
     */
    fun whois(): CompletableFuture<WhoisResponse?>

}