package net.slimediamond.atom.ircbot.listeners

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.platforms.CommandPlatforms
import net.slimediamond.atom.api.command.platforms.irc.IrcCommandSender
import net.slimediamond.atom.api.event.Listener
import net.slimediamond.atom.api.irc.events.IrcMessageEvent

class IrcMessageListener {

    @Listener
    fun onMessage(event: IrcMessageEvent) {
        val prefix = Atom.configuration.commandConfiguration.prefix
        if (event.message.lowercase().startsWith(prefix)) {
            // a command!
            val command = event.message.split(prefix)[1].split(" ")[0]
            val sender = IrcCommandSender(event.user)
            val input = event.message.split(" ").toList().drop(2).joinToString(" ")
            Atom.bot.commandManager.handle(sender, command, input, CommandPlatforms.IRC, event.audience)
        }
    }

}