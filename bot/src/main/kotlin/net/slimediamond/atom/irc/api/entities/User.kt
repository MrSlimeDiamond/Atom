package net.slimediamond.atom.irc.api.entities

import net.slimediamond.atom.Audience

/**
 * A user on the IRC server
 */
interface User : Audience {

    /**
     * The name of the user
     */
    var name: String

}