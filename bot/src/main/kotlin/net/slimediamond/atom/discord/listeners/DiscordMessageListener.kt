package net.slimediamond.atom.discord.listeners

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.platforms.CommandPlatforms
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandSender
import net.slimediamond.atom.api.discord.event.DiscordMessageEvent
import net.slimediamond.atom.api.event.Listener

class DiscordMessageListener {

    private val prefix = Atom.configuration.commandConfiguration.prefix

    @Listener
    fun onDiscordMessage(event: DiscordMessageEvent) {
        if (event.message.lowercase().startsWith(prefix)) {
            val command = event.message.split(prefix)[1].split(" ")[0]
            val sender = DiscordCommandSender(event.user)
            val input = event.message.split(" ").toList().drop(2).joinToString(" ")
            Atom.bot.commandManager.handle(sender, command, input, CommandPlatforms.DISCORD, event.audience, event.cause)
        }
    }

}