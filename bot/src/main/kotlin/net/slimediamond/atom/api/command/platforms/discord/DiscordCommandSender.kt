package net.slimediamond.atom.api.command.platforms.discord

import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.storage.dao.UserDao

class DiscordCommandSender(private val user: User) : CommandSender {

    override val name: String
        get() = user.displayName
    override val userDao: UserDao?
        get() = UserDao.getFromDiscord(user).orElse(null)

    override fun hasPermission(permission: String): Boolean {
        return UserDao.getFromDiscord(user)
            .map { user -> user.hasPermission(permission) }
            .orElse(false)
    }

    override fun sendMessage(message: String) {
        user.sendMessage(message)
    }

    override fun sendMessage(message: RichText) {
        user.sendMessage(message)
    }

}