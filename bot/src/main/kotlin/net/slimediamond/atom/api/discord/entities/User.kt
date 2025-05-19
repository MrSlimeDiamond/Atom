package net.slimediamond.atom.api.discord.entities

import net.slimediamond.atom.api.messaging.DiscordAudience
import net.slimediamond.atom.storage.dao.UserDao
import java.util.*

interface User : DiscordAudience {

    /**
     * The user's display name
     */
    val displayName: String

    /**
     * The user's username
     */
    val username: String

    /**
     * The user's ID
     */
    val id: Long

    /**
     * The associated [UserDao] for this user
     */
    val userDao: Optional<UserDao>
        get() = UserDao.getFromDiscord(this)

}