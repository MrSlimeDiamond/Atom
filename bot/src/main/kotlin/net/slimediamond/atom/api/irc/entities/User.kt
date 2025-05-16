package net.slimediamond.atom.api.irc.entities

import net.slimediamond.atom.api.messaging.Audience

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

}