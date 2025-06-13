package net.slimediamond.atom.api.command

import io.ktor.util.reflect.*
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.discord.entities.Guild
import net.slimediamond.atom.api.irc.entities.Channel
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.richText
import java.util.*

class HelpCommandNode : CommandNode("Help subcommand", "help", "?") {

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val parents = LinkedList<CommandNode>()
        // construct a map
        if (parent == null) {
            // Instead of doing a specific subcommand, show *all* subcommands in general
            parents.addAll(Atom.bot.commandNodeManager.commands)
        } else {
            parents.add(parent!!)
        }
        val result = RichText.of().append(RichText.of("Atom Help").color(Color.GREEN))

        if (parents.size == 1) {
            result.append(RichText.of(" [").color(Color.GRAY)
                .append(RichText.join(RichText.of(", ").color(Color.GRAY), parent!!.aliases.map { RichText.of(it).color(Color.WHITE) }))
                .append(RichText.of("]").color(Color.GRAY)))
                .appendNewline()
                .append(RichText.of("Usage: ${parent!!.usage}").color(Color.CYAN))
        }

        parents.stream()
            .distinct()
            .filter { it.permission == null || context.sender.hasPermission(it.permission!!) }
            .filter { it.platforms.isEmpty() || it.platforms.contains(context.platform) }
            .forEach { parent ->
            result.appendNewline()
                .append(command(parent))
                .append(RichText.join(RichText.newline().append(RichText.of("  ")),
                    parent.children
                        .filter { it !is HelpCommandNode }
                        .map { command(it) }, true))
        }

        // below is slightly bad, seems to work
        if (context.cause.any { it.instanceOf(Guild::class) || it.instanceOf(Channel::class) }) {
            context.replySuccess("Help sent in DMs", ephemeral = true)
        }
        // Send it to the command sender and not into the actual channel
        context.sender.sendMessage(result)

        return CommandResult.success
    }

    private fun command(command: CommandNode): RichText {
        return richText {
            append(richText("> ").color(Color.PINK))
            append(richText(command.aliases.first()).color(Color.BLUE))
            append(richText(" - ").color(Color.GRAY))
            append(richText(command.description).color(Color.WHITE))
        }
    }

}