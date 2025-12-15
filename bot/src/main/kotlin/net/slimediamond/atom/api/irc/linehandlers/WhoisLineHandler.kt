package net.slimediamond.atom.api.irc.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.NetworkedServer
import net.slimediamond.atom.api.irc.WhoisResponseBuilder
import net.slimediamond.atom.api.irc.entities.UserImpl
import net.slimediamond.atom.ircbot.IrcBot

class WhoisLineHandler : LineHandler {

    private val pattern = Regex("^:[^ ]+ ([0-9]{3}) [^ ]+ ([^ ]+) (.*)")
    private val builders = HashMap<String, WhoisResponseBuilder>()
    private val whoisTracker
        get() = Atom.bot.serviceManager.provide(IrcBot::class)!!.connection.whoisTracker
    private val subHandlers = mapOf(
        311 to SubHandler { builder, nickname, line, connection ->
            // ident    host         real name
            // findlayr 127.0.0.1 * :Findlay Richardson
            val parts = line.split(" ")
            val ident = parts[0]
            val host = parts[1]
            val realName = parts[3].substring(1)
            connection.userTracker.put(nickname, realName)
            builder.user = UserImpl(connection, nickname, ident, host, realName)
            builder.realName = realName
            builder.hostname = host
        },
        319 to SubHandler { builder, _, line, _ ->
            // space-separated list of channels
            val channels = line.substring(1).split(" ")
            builder.channels = channels
        },
        312 to SubHandler { builder, _, line, _ ->
            // server
            // anarchy.esper.net :Destroy All White Permanents (Gravelines, France)
            val (name, description) = line.split(":", limit = 2)
            builder.server = NetworkedServer(name, description)
        },
        330 to SubHandler { builder, _, line, _ ->
            val (account, _) = line.split(":", limit = 2)
            builder.account = account
        },
        318 to SubHandler { builder, nickname, _, _ ->
            whoisTracker.complete(nickname, builder.build())
        },
        401 to SubHandler { _, nickname, _, _ ->
            whoisTracker.complete(nickname, null)
            whoisTracker.remove(nickname)
        }
    )

    override fun handle(line: String, connection: Connection) {
        val match = pattern.matchEntire(line)
        if (match != null) {
            val type = match.groupValues[1].toIntOrNull()
            val nickname = match.groupValues[2]
            val content = match.groupValues[3]

            if (!whoisTracker.isTracking(nickname)) {
                return
            }

            if (type == null) {
                // just in case I guess
                return
            }

            if (builders[nickname] == null) {
                builders[nickname] = WhoisResponseBuilder()
            }

            val builder = builders[nickname]!!

            if (subHandlers.containsKey(type)) {
                // handle the line
                subHandlers[type]?.handle(builder, nickname, content, connection)
            }
        }
    }

    fun interface SubHandler {

        fun handle(builder: WhoisResponseBuilder, nickname: String, line: String, connection: Connection)

    }

}