package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.discord.embed.description
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.richText
import net.slimediamond.atom.utils.Embeds

class VoteCommand : CommandNode("Show voting sites for MinecraftOnline", "vote") {

    // TODO: Configurate this (:
    companion object {
        private val VOTING_SITES = listOf(
            Site("Minecraft Server List", "https://minecraft-server-list.com/server/267113/vote/"),
            Site("Planet Minecraft", "https://www.planetminecraft.com/server/minecraft-online-3375846/vote/")
        )
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        if (context is DiscordCommandNodeContext) {
            context.sendEmbed {
                title = "MinecraftOnline voting sites"
                description {
                    VOTING_SITES.forEach { site ->
                        append(richText("* [${site.name}](${site.url})\n"))
                    }
                }
                footer = Embeds.MCO_FOOTER
            }
        } else {
            context.sendMessage {
                append(richText("Voting sites for MinecraftOnline: "))
                append(RichText.join(richText(", "), VOTING_SITES.stream().map { richText(it.url) }.toList()))
            }
        }
        return CommandResult.success
    }

    data class Site(val name: String, val url: String)

}