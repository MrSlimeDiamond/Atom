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
        commands[command]?.execute(input, platform, audience)
    }

}