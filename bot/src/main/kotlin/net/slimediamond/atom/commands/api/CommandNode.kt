package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Atom
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.commands.api.irc.IrcCommandNodeContext
import java.util.LinkedList

abstract class CommandNode(vararg aliases: String) : Command {

    val aliases: MutableList<String> = LinkedList()
    val platforms: MutableList<CommandPlatform> = LinkedList()
    
    abstract fun execute(context: CommandNodeContext): CommandResult

    override fun execute(sender: CommandSender, input: String, platform: CommandPlatform, audience: Audience): CommandResult {
        // TODO: proper parameters
        // context depends on the platform
        val context = when (platform) {
            CommandPlatform.IRC -> IrcCommandNodeContext(sender, input, platform, audience)
        }

        return execute(context)
    }

    fun register() {
        Atom.instance.commandService.commandNodeManager.register(this, aliases)
    }

    init {
        aliases.forEach { alias -> this.aliases.add(alias) }
    }

}