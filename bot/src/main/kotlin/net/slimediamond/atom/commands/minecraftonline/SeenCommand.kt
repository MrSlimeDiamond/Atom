package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.infoEmbed

class SeenCommand(private val firstseen: Boolean, description: String, vararg aliases: String) : CommandNode(description, *aliases) {

    init {
        parameters.add(Parameters.MCO_PLAYER)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val player = context.requireOne(Parameters.MCO_PLAYER)
        val date = if (firstseen) {
            player.firstseen
        } else {
            player.lastseen
        }
        val message = RichText.of()
            .append(RichText.of(player.name).bold())
            .appendSpace()
            .append(RichText.of(if (firstseen) "first" else "last"))
            .append(RichText.of(" visited Freedonia on "))
            .append(RichText.timestamp(date).bold())
            .append(RichText.of(" ("))
            .append(RichText.timestamp(date, relative = true).bold())
            .append(RichText.of(")"))
        if (context is DiscordCommandNodeContext) {
            context.sendEmbeds(player.infoEmbed(message))
        } else {
            context.sendMessage(message)
        }
        return CommandResult.success
    }

}