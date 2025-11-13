package net.slimediamond.atom.discord.abstraction

import dev.kord.core.Kord
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.HelpCommandNode
import net.slimediamond.atom.api.command.parameter.ClientParameterType
import net.slimediamond.atom.api.command.parameter.Parameter
import net.slimediamond.atom.api.command.platforms.discord.slashCommand
import net.slimediamond.atom.api.discord.SlashCommandNodeManager
import org.apache.logging.log4j.LogManager

class KordSlashCommandManager(val kord: Kord) : SlashCommandNodeManager {

    private val logger = LogManager.getLogger();

    override suspend fun register(command: CommandNode) {
        if (!command.slashCommand) {
            return
        }
        kord.createGlobalChatInputCommand(command.aliases.first(), command.description) {
            applyParameters(command)
            command.children.forEach { child ->
                if (child !is HelpCommandNode) {
                    subCommand(child.aliases.first(), child.description) {
                        applyParameters(child)
                    }
                }
            }
        }
    }

    override suspend fun removeAll(): Int {
        var deleted = 0
        kord.getGlobalApplicationCommands(false).collect { command ->
            deleted++
            command.delete()
        }
        return deleted
    }

    override suspend fun reload() {
        removeAll()
        Atom.bot.commandNodeManager.commands.forEach { this.register(it) }
    }

}

fun BaseInputChatBuilder.applyParameters(command: CommandNode) {
    command.parameters.forEach { parameter ->
        if (parameter is Parameter.Value<*>) {
            when (parameter.parser.clientParameterType) {
                ClientParameterType.STRING -> string(parameter.key, parameter.description) {
                    required = !parameter.optional
                }
                ClientParameterType.INTEGER -> integer(parameter.key, parameter.description) {
                    required = !parameter.optional
                }
                ClientParameterType.BOOLEAN -> boolean(parameter.key, parameter.description) {
                    required = !parameter.optional
                }
            }
        }
    }
}