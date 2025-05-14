package net.slimediamond.atom.commands.api.platforms.irc

import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.irc.api.entities.User
import net.slimediamond.atom.messaging.RichMessage

class IrcCommandSender(private val user: User) : CommandSender {

    override val name: String
        get() = user.nickname

    override fun hasPermission(node: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: String) {
        user.sendMessage(message)
    }

    override fun sendMessage(message: RichMessage) {
        user.sendMessage(message)
    }

}