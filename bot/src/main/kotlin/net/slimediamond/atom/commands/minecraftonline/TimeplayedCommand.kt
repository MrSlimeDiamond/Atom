package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.discord.embed.description
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.Embeds

class TimeplayedCommand : CommandNode("Check an MCO player's time online", "timeplayed", "playtime", "tp", "pt") {

    init {
        parameters.add(Parameters.MCO_PLAYER)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        // at the very start, take a timestamp
        val player = context.requireOne(Parameters.MCO_PLAYER)
        val message = RichText.of()
            .append(RichText.of(player.name).bold())
            .append(RichText.of(" has played on Freedonia for "))
            .append(RichText.of(String.format("%.2f", player.timeOnline / 3600.0)).bold())
            .append(RichText.of(" hours."))
        if (context is DiscordCommandNodeContext) {
            // embeds!
            context.sendEmbed {
                color = Embeds.THEME_COLOR
                author {
                    name = player.name
                    icon = player.avatarUrl
                }
                description(message)
            }
        } else {
            context.sendMessage(message)
        }
        return CommandResult.success
    }

}