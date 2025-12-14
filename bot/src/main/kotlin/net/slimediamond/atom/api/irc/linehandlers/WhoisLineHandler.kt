package net.slimediamond.atom.api.irc.linehandlers

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.irc.Connection
import net.slimediamond.atom.api.irc.NetworkedServer
import net.slimediamond.atom.api.irc.WhoisResponseBuilder
import net.slimediamond.atom.api.irc.entities.UserImpl
import net.slimediamond.atom.ircbot.IrcBot
import org.apache.logging.log4j.LogManager

class WhoisLineHandler : LineHandler {

    private val pattern = Regex("^:[^ ]+ ([0-9]{3}) [^ ]+ [^ ]+ (.*)")
    private val logger = LogManager.getLogger()
    private val whoisTracker
        get() = Atom.bot.serviceManager.provide(IrcBot::class)!!.connection.whoisTracker
    private val currentName: String?
        get() = whoisTracker.currentName
    private var currentBuilder = WhoisResponseBuilder()
    private val subHandlers = mapOf(
        311 to SubHandler { line, connection ->
            // ident    host         real name
            // findlayr 127.0.0.1 * :Findlay Richardson
            val parts = line.split(" ")
            val ident = parts[0]
            val host = parts[1]
            val realName = parts[3].substring(1)
            connection.userTracker.put(currentName!!, realName)
            currentBuilder.user = UserImpl(connection, currentName!!, ident, host, realName)
            currentBuilder.realName = realName
            currentBuilder.hostname = host
        },
        319 to SubHandler { line, _ ->
            // space-separated list of channels
            val channels = line.substring(1).split(" ")
            currentBuilder.channels = channels
        },
        312 to SubHandler { line, _ ->
            // server
            // anarchy.esper.net :Destroy All White Permanents (Gravelines, France)
            val (name, description) = line.split(":", limit = 2)
            currentBuilder.server = NetworkedServer(name, description)
        },
        330 to SubHandler { line, _ ->
            val (account, _) = line.split(":", limit = 2)
            currentBuilder.account = account
        },
        318 to SubHandler { _, _ ->
            whoisTracker.pending[currentName!!]?.complete(currentBuilder.build())

            whoisTracker.currentName = null
            currentBuilder = WhoisResponseBuilder()
        }
    )

    override fun handle(line: String, connection: Connection) {
        val match = pattern.matchEntire(line)
        if (match != null) {
            val type = match.groupValues[1].toInt()
            val content = match.groupValues[2]

            if (subHandlers.containsKey(type)) {
                if (currentName == null) {
                    logger.warn("Whois line received, but currentName is null")
                    return
                }
                // handle the line
                subHandlers[type]?.handle(content, connection)
            }
        }
    }

    fun interface SubHandler {

        fun handle(line: String, connection: Connection)

    }

}