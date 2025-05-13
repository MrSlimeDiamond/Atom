package net.slimediamond.atom.irc.api.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.CauseImpl
import net.slimediamond.atom.irc.api.Connection
import net.slimediamond.atom.irc.api.entities.Channel
import net.slimediamond.atom.irc.api.entities.UserImpl
import net.slimediamond.atom.irc.api.events.IrcChannelMessageEvent
import net.slimediamond.atom.irc.api.events.IrcUserMessageEvent

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
            val cause = CauseImpl()

            if (target.startsWith("#")) {
                // TODO: Users list
                // (or omit it maybe?)
                val channel = Channel(connection, target, listOf())
                Atom.instance.eventManager.post(IrcChannelMessageEvent(cause, connection, line, content, channel, user))
            } else {
                Atom.instance.eventManager.post(IrcUserMessageEvent(cause, connection, line, content, user))
            }
        }
    }

}