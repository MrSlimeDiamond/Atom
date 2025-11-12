package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.messaging.richText
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.getTargetMCOPlayer
import net.slimediamond.atom.utils.infoEmbed

class SeenCommand(private val firstseen: Boolean, description: String, vararg aliases: String) : CommandNode(description, *aliases) {

    init {
        parameters.add(Parameters.MCO_PLAYER)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val player = context.getTargetMCOPlayer()

        context.defer()

        val date = if (firstseen) {
            player.firstseen
        } else {
            player.lastseen
        }
        val message = richText {
            append(richText(player.name).bold())
            appendSpace()
            append(richText(if (firstseen) "first" else "last"))
            append(richText(" visited Freedonia on "))
            append(richText(date).bold())
            append(richText("("))
            append(richText(date, relative = true).bold())
            append(richText(")"))
        }

        if (context is DiscordCommandNodeContext) {
            context.sendEmbeds(player.infoEmbed(message))
        } else {
            context.sendMessage(message)
        }

        return CommandResult.success
    }

}