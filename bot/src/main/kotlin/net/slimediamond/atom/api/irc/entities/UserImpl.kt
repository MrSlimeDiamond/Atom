package net.slimediamond.atom.api.irc.entities

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.WhoisResponse
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.renderer.IrcRichMessageRenderer
import net.slimediamond.atom.ircbot.IrcBot
import java.util.concurrent.CompletableFuture

class UserImpl(
    val connection: Connection,
    override val nickname: String,
    override val username: String,
    override val hostname: String,
    override val realName: String?
) : User {

    override suspend fun sendMessage(message: String) {
        connection.sendMessage(nickname, message)
    }

    override suspend fun sendMessage(message: RichText) {
        IrcRichMessageRenderer.sendMessage(connection, username, message)
    }

    override fun whois(): CompletableFuture<WhoisResponse?> {
        return Atom.bot.serviceManager.provide(IrcBot::class)!!.connection.whois(this.nickname)
    }

}