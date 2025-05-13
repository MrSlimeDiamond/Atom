package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Audience

class CommandNodeManager : CommandManager<CommandNode> {

    private val commands: MutableMap<String, CommandNode> = HashMap()

    override fun register(command: CommandNode, aliases: List<String>) {
        aliases.forEach {
            commands[it] = command
        }
    }

    override fun handle(command: String, input: String, platform: CommandPlatform, audience: Audience) {
        val cmd = commands[command]
        if (cmd != null) {
            if (cmd.platforms.isEmpty() || cmd.platforms.contains(platform)) {
                cmd.execute(input, platform, audience)
            }
        }
    }

}