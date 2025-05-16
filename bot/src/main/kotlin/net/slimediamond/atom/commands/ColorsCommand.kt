package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichMessage

// I can't speak American English much longer.
class ColorsCommand : CommandNode("colors", "colours") {

    override fun execute(context: CommandNodeContext): CommandResult {
        val builder: RichMessage = RichMessage.of("Colours:")

        for (color in Color.entries) {
            builder.append(RichMessage.of(" "))
            builder.append(RichMessage.of(color.name).color(color))
        }

        context.sendMessage(builder)

        return CommandResult.success
    }

}