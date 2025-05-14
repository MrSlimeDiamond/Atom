package net.slimediamond.atom.commands.api

import net.slimediamond.atom.messaging.Audience

interface CommandSender : Audience {

    /**
     * The name of the command sender
     */
    val name: String

    /**
     * Get whether this command sender has a permission
     */
    fun hasPermission(node: String)

}