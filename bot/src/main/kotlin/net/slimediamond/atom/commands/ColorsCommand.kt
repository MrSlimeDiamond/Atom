package net.slimediamond.atom.commands

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandResult
import net.slimediamond.atom.messaging.Color
import net.slimediamond.atom.messaging.RichMessage

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