package net.slimediamond.atom.api.irc.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.entities.Channel
import net.slimediamond.atom.api.irc.entities.UserImpl
import net.slimediamond.atom.api.irc.events.IrcChannelMessageEvent
import net.slimediamond.atom.api.irc.events.IrcUserMessageEvent

class MessageLineHandler : LineHandler {

    override fun handle(line: String, connection: Connection) {
        val privmsg = "^:(\\S+)!(\\S+)@(\\S+) PRIVMSG (\\S+) :(.+)\$".toRegex()
        val privmsgMatch = privmsg.matchEntire(line)
        if (privmsgMatch != null) {
            val nickname = privmsgMatch.groupValues[1]
            val ident = privmsgMatch.groupValues[2]
            val hostname = privmsgMatch.groupValues[3]
            val target = privmsgMatch.groupValues[4]
            val content = privmsgMatch.groupValues[5]

            val user = UserImpl(connection, nickname, ident, hostname)
            val cause = Cause.of(user, connection)

            if (target.startsWith("#")) {
                // TODO: Users list
                // (or omit it maybe?)
                val channel = Channel(connection, target, listOf())
                cause.push(channel)
                Atom.bot.eventManager.post(IrcChannelMessageEvent(cause, connection, line, content, user, channel))
            } else {
                Atom.bot.eventManager.post(IrcUserMessageEvent(cause, connection, line, content, user))
            }
        }
    }

}