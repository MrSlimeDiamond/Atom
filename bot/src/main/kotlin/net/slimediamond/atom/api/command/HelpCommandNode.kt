package net.slimediamond.atom.api.command

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText
import java.util.LinkedList

class HelpCommandNode : CommandNode("Help subcommand", "help", "?") {

    override fun execute(context: CommandNodeContext): CommandResult {
        val parents = LinkedList<CommandNode>()
        // construct a map
        if (parent == null) {
            // Instead of doing a specific subcommand, show *all* subcommands in general
            parents.addAll(Atom.instance.commandNodeManager.getCommands())
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

        // Send it to the command sender and not into the actual channel
        context.sender.sendMessage(result)

        return CommandResult.success
    }

    private fun command(command: CommandNode): RichText {
        return RichText.of()
            .append(RichText.of("> ").color(Color.PINK))
            .append(RichText.of(command.aliases.joinToString(", ")).color(Color.BLUE))
            .append(RichText.of(" - ").color(Color.GRAY))
            .append(RichText.of(command.description).color(Color.WHITE))
    }

}