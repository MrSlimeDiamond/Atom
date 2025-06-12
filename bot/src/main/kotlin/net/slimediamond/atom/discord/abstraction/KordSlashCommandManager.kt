package net.slimediamond.atom.discord.abstraction

import dev.kord.core.Kord
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.discord.SlashCommandNodeManager

class KordSlashCommandManager(val kord: Kord) : SlashCommandNodeManager {

    override suspend fun register(command: CommandNode) {
        kord.createGlobalChatInputCommand(command.aliases.first(), command.description)
    }

}