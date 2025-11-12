package net.slimediamond.atom.commands.minecraftonline

import com.minecraftonline.mcodata.web.MCOWebDataProvider
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult

class BanCountCommand : CommandNode("Check the ban count on MinecraftOnline", "bancount", "bans", "bc") {

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        context.defer()
        context.replySuccess("MinecraftOnline has ${MCOWebDataProvider.web().server.banCount} bans")
        return CommandResult.success
    }

}