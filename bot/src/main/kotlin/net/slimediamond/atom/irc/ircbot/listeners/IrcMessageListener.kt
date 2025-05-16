package net.slimediamond.atom.irc.ircbot.listeners

import net.slimediamond.atom.Atom
import net.slimediamond.atom.commands.api.platforms.CommandPlatforms
import net.slimediamond.atom.commands.api.platforms.irc.IrcCommandSender
import net.slimediamond.atom.event.Listener
import net.slimediamond.atom.irc.api.events.IrcMessageEvent

class IrcMessageListener {

    @Listener
    fun onMessage(event: IrcMessageEvent) {
        val prefix = Atom.instance.configuration.commandConfiguration.prefix
        if (event.message.lowercase().startsWith(prefix)) {
            // a command!
            val command = event.message.split(prefix)[1].split(" ")[0]
            val sender = IrcCommandSender(event.user)
            val input = event.message.split(" ").toList().drop(2).joinToString(" ")
            Atom.instance.commandNodeManager.handle(sender, command, input, CommandPlatforms.IRC, event.audience)
        }
    }

}