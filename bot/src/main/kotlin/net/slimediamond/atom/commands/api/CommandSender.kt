package net.slimediamond.atom.commands.api

import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.storage.dao.UserDao

interface CommandSender : Audience {

    /**
     * The name of the command sender
     */
    val name: String

    /**
     * The associated UserDao for this command sender (may not be present)
     */
    val userDao: UserDao?

    /**
     * Get whether this command sender has a permission
     */
    fun hasPermission(permission: String): Boolean

}