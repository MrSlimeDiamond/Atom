package net.slimediamond.atom.commands

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.CommandPlatforms
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.command.platforms.discord.slashCommand
import net.slimediamond.atom.discord.DiscordBot
import net.slimediamond.atom.utils.Embeds

class ReloadSlashCommands : CommandNode("Reload Discord slash commands", "reloadslashcommands") {

    init {
        slashCommand = false
        platforms.add(CommandPlatforms.DISCORD)
        permission = "atom.command.reloadslashcommands"
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val manager = Atom.bot.serviceManager.provide(DiscordBot::class)!!.client.slashCommandNodeManager
        manager.reload()
        (context as DiscordCommandNodeContext).sendEmbeds(Embeds.success("Reloaded slash commands"))
        return CommandResult.success
    }

}