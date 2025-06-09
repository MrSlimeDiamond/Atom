package net.slimediamond.atom.api.command

import net.slimediamond.atom.Atom
import okhttp3.internal.toImmutableList
import java.util.LinkedList

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
    fun register(command: CommandNode) {
        _commands.add(command)
        command.aliases.forEach { alias ->
            Atom.bot.commandManager.register(alias, command)
        }
    }

}