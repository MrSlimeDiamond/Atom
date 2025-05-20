package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters

class TimeplayedCommand : CommandNode("Check an MCO player's time online", "timeplayed", "playtime", "tp", "pt") {

    init {
        parameters.add(Parameters.MCO_PLAYER)
    }

    override fun execute(context: CommandNodeContext): CommandResult {
        val player = context.requireOne(Parameters.MCO_PLAYER)
        context.sendMessage("${player.name} has played on Freedonia for ${String.format("%.2f", player.timeOnline / 3600.0)} hours")
        return CommandResult.success
    }

}