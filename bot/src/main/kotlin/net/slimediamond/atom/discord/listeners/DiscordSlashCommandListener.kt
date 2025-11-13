package net.slimediamond.atom.discord.listeners

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.platforms.CommandPlatforms
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandSender
import net.slimediamond.atom.api.discord.event.DiscordSlashCommandEvent
import net.slimediamond.atom.api.event.Listener
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.richText
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
            val parameterKeyMap = event.interaction.parameterKeyMap.toMutableMap()

            val cmd = Atom.bot.commandManager.commands[event.interaction.name]

            if (cmd !is CommandNode) {
                event.audience.sendEmbeds(Embeds.fail("This command does not support slash commands because " +
                        "it is not an instance of `CommandNode`"))
                return@launch
            }

            try {
                val result = cmd.execute(
                    sender,
                    event.interaction.subcommand.orEmpty(),
                    CommandPlatforms.DISCORD,
                    event.audience,
                    event.cause,
                    parameterKeyMap
                )

                if (!result.success) {
                    // send a response
                    event.audience.sendMessage(result.message!!.color(Color.RED))
                }
            } catch (e: Error) {
                event.audience.sendMessage {
                    richText(e.message?: "An error occurred").color(Color.RED)
                }
            }
        }
    }

}