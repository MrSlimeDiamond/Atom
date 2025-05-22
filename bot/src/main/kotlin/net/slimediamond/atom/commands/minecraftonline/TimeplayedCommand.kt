package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.Embeds

class TimeplayedCommand : CommandNode("Check an MCO player's time online", "timeplayed", "playtime", "tp", "pt") {

    init {
        parameters.add(Parameters.MCO_PLAYER)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        // at the very start, take a timestamp
        val player = context.requireOne(Parameters.MCO_PLAYER)
        val hoursFormatted = String.format("%.2f", player.timeOnline / 3600.0)
        if (context is DiscordCommandNodeContext) {
            // embeds!
            context.sendEmbed {
                color = Embeds.THEME_COLOR
                author {
                    name = player.name
                    icon = player.avatarUrl
                }
                description = "**${player.name}** has played on Freedonia for **$hoursFormatted** hours"
            }
        } else {
            context.sendMessage("${player.name} has played on Freedonia for $hoursFormatted hours")
        }
        return CommandResult.success
    }

}