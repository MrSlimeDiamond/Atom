package net.slimediamond.atom.irc.ircbot.listeners

import net.slimediamond.atom.Atom
import net.slimediamond.atom.commands.api.CommandPlatform
import net.slimediamond.atom.commands.api.platforms.irc.IrcCommandSender
import net.slimediamond.atom.event.Listener
import net.slimediamond.atom.irc.api.events.IrcMessageEvent

class IrcMessageListener {

    // TODO config
    companion object {
        private const val PREFIX = "!a "
    }

    @Listener
    fun onMessage(event: IrcMessageEvent) {
        if (event.message.lowercase().startsWith(PREFIX)) {
            // a command!
            val command = event.message.split(PREFIX)[1].split(" ")[0]
            val sender = IrcCommandSender(event.user)
            val input = event.message.split(" ").toList().drop(2).joinToString(" ")
            Atom.instance.commandService.commandNodeManager.handle(sender, command, input, CommandPlatform.IRC, event.audience)
        }
    }

}