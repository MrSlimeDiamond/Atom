package net.slimediamond.atom.commands.api.irc

import net.slimediamond.atom.commands.api.CommandSender
import net.slimediamond.atom.irc.api.entities.User

class IrcCommandSender(private val user: User) : CommandSender {

    override val name: String
        get() = user.nickname

    override fun hasPermission(node: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessage(message: String) {
        user.sendMessage(message)
    }

}