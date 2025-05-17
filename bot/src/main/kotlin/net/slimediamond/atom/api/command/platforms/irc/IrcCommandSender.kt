package net.slimediamond.atom.api.command.platforms.irc

import net.slimediamond.atom.api.command.CommandSender
import net.slimediamond.atom.api.irc.entities.User
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.storage.dao.UserDao

class IrcCommandSender(private val user: User) : CommandSender {

    override val name: String
        get() = user.nickname

    override val userDao: UserDao?
        get() = UserDao.getFromIrc(user).orElse(null)

    override fun hasPermission(permission: String): Boolean {
        return UserDao.getFromIrc(user)
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