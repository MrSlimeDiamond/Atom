package net.slimediamond.atom.api.command

/**
 * A command node which is only for a root command, specifically for holding
 * subcommands on that command.
 */
open class RootOnlyCommandNode(description: String, vararg aliases: String) : CommandNode(description, *aliases) {

    override val usage: String
        get() {
            return buildString {
                append("subcommands:<")
                append(children.joinToString("|") { it.aliases.first() })
                append(">")
            }
        }

    override fun execute(context: CommandNodeContext): CommandResult {
        // If we managed to reach this point without finding a subcommand, they've not provided a subcommand
        return CommandResult.error(context.platform.renderNotEnoughArguments(this, 0))
    }

}