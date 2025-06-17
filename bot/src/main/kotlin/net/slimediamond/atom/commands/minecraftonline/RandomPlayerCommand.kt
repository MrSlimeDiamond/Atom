package net.slimediamond.atom.commands.minecraftonline

import com.minecraftonline.mcodata.web.MCOWebDataProvider
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.messaging.richText

class RandomPlayerCommand : CommandNode("Get a random player on MCO", "randomplayer", "rp") {

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val players = MCOWebDataProvider.web().server.onlinePlayers
        if (players.isEmpty()) {
            context.sendMessage("No players are on MCO right now")
            return CommandResult.success
        }
        val player = players.random()
        context.sendMessage {
            append(richText("A random player on MinecraftOnline right now is "))
            append(richText(player.name).bold())
            append(richText("."))
        }
        return CommandResult.success
    }
}