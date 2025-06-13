package net.slimediamond.atom.api.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.Atom
import net.slimediamond.atom.discord.DiscordBot
import okhttp3.internal.toImmutableList
import java.util.*
import java.util.function.Consumer

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

    fun register(config: SimpleBuilder.() -> Unit) {
        register(SimpleBuilder().apply(config).build())
    }

    class SimpleBuilder {
        var aliases: List<String>? = null
        var description: String? = null
        private var executor: (suspend (CommandNodeContext) -> CommandResult)? = null

        fun executor(executor: suspend (CommandNodeContext) -> CommandResult) {
            this.executor = executor
        }

        fun build(): CommandNode {
            if (aliases == null) {
                throw IllegalStateException("Command aliases must not be null")
            }
            if (description == null) {
                throw IllegalStateException("Command description must not be null")
            }
            if (executor == null) {
                throw IllegalStateException("Command executor must not be null")
            }
            return object : CommandNode(description!!, aliases!!) {
                override suspend fun execute(context: CommandNodeContext): CommandResult {
                    return executor!!.invoke(context)
                }
            }
        }
    }

}