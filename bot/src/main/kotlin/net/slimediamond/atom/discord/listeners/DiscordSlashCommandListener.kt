package net.slimediamond.atom.discord.listeners

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.platforms.CommandPlatforms
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandSender
import net.slimediamond.atom.api.discord.event.DiscordSlashCommandEvent
import net.slimediamond.atom.api.event.Listener
import net.slimediamond.atom.utils.Embeds

class DiscordSlashCommandListener {

    @OptIn(DelicateCoroutinesApi::class)
    @Listener
    fun onSlashCommand(event: DiscordSlashCommandEvent) {
        GlobalScope.launch {
            val command = event.interaction.name
            if (!Atom.bot.commandManager.commands.containsKey(command)) {
                event.audience.sendEmbeds(Embeds.fail("This command does not exist. Please refresh slash commands"))
                return@launch
            }
            val sender = DiscordCommandSender(event.user)
            Atom.bot.commandManager.handle(sender, command, "", CommandPlatforms.DISCORD, event.audience)
        }
    }

}