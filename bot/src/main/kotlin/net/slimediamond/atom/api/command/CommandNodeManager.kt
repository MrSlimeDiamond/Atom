package net.slimediamond.atom.api.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.Atom
import net.slimediamond.atom.discord.DiscordBot
import okhttp3.internal.toImmutableList
import java.util.*

/**
 * The [CommandNode] manager
 */
class CommandNodeManager {

    private val _commands = LinkedList<CommandNode>()
    val commands
        get() = _commands.toImmutableList()

    /**
     * Register a command node with the global command manager
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun register(command: CommandNode) {
        _commands.add(command)
        command.aliases.forEach { alias ->
            Atom.bot.commandManager.register(alias, command)
        }
        val discordBot = Atom.bot.serviceManager.provide(DiscordBot::class)?: return
        GlobalScope.launch {
            discordBot.client.slashCommandNodeManager.register(command)
        }
    }

}