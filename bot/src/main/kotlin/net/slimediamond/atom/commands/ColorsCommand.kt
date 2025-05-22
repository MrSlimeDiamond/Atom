package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText

// I can't speak American English much longer.
class ColorsCommand : CommandNode("Display ANSI colours", "colors", "colours") {

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val builder: RichText = RichText.of("Colours:")

        for (color in Color.entries) {
            builder.append(RichText.of(" "))
            builder.append(RichText.of(color.name).color(color))
        }

        context.sendMessage(builder)

        return CommandResult.success
    }

}