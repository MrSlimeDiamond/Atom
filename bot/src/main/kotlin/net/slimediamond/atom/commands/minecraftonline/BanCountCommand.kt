package net.slimediamond.atom.commands.minecraftonline

import com.minecraftonline.mcodata.web.MCOWebDataProvider
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.getTargetMCOPlayer

class BanCountCommand : CommandNode("Check the ban count on MinecraftOnline", "bancount", "bans", "bc") {

    init {
        parameters.add(Parameters.MCO_PLAYER)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val target = context.getTargetMCOPlayer()
        context.replySuccess("MinecraftOnline has ${MCOWebDataProvider.web().server.banCount} bans")
        return CommandResult.success
    }

}