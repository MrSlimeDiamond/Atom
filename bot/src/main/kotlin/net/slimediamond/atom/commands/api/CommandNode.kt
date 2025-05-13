package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Audience
import net.slimediamond.atom.commands.api.irc.IrcCommandNodeContext

abstract class CommandNode : Command {
    
    abstract fun execute(context: CommandNodeContext): CommandResult

    override fun execute(input: String, platform: CommandPlatform, audience: Audience): CommandResult {
        // TODO: proper parameters

        // context depends on the platform
        val context = when (platform) {
            CommandPlatform.IRC -> IrcCommandNodeContext(input, platform, audience)
        }

        return execute(context)
    }

}