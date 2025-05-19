package net.slimediamond.atom.api.command

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.messaging.Color
import net.slimediamond.atom.api.messaging.RichText
import java.util.LinkedList

class HelpCommandNode : CommandNode("help", "?") {

    override fun execute(context: CommandNodeContext): CommandResult {
        val parents = LinkedList<CommandNode>()
        // construct a map
        if (parent == null) {
            // Instead of doing a specific subcommand, show *all* subcommands in general
            parents.addAll(Atom.instance.commandNodeManager.getCommands())
        } else {
            parents.add(parent!!)
        }
        val result = RichText.of()
            .append(RichText.of("Atom Help: ").color(Color.GREEN))

        parents.forEach { parent ->
            result
                .append(RichText.of(parent.aliases.first()))
                .appendNewline()
                .append(command(parent))
                .append(RichText.join(RichText.newline().append(RichText.of("  ")), parent.children.filter { it !is HelpCommandNode }.map { command(it) }))
        }
        context.sender.sendMessage(result)

        // TODO: Announce that they've been DMed

        return CommandResult.success
    }

    private fun command(command: CommandNode): RichText {
        return RichText.of()
            .append(RichText.of(command.aliases.joinToString(", ")).color(Color.BLUE))
            .append(RichText.of(" - ").color(Color.GRAY))
            .append(RichText.of("Description: TODO..."))
    }

}