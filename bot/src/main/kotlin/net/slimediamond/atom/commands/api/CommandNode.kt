package net.slimediamond.atom.commands.api

import net.slimediamond.atom.Atom
import net.slimediamond.atom.messaging.Audience
import net.slimediamond.atom.commands.api.platforms.irc.IrcCommandNodeContext
import java.util.LinkedList

abstract class CommandNode(vararg aliases: String) : Command {

    val aliases: MutableList<String> = LinkedList()
    val platforms: MutableList<CommandPlatform> = LinkedList()
    val children: MutableList<CommandNode> = LinkedList()
    
    abstract fun execute(context: CommandNodeContext): CommandResult

    override fun execute(sender: CommandSender, input: String, platform: CommandPlatform, audience: Audience): CommandResult {
        var command = this
        val maybe = input.split(" ")[0]
        val cmd = children.stream().filter { cmd -> cmd.aliases.contains(maybe) }.findFirst()
        if (cmd.isPresent) {
            input.drop(0)
            command = cmd.get()
        }

        // TODO: proper parameters
        // context depends on the platform
        val context = when (platform) {
            CommandPlatform.IRC -> IrcCommandNodeContext(sender, input, platform, audience)
        }

        return command.execute(context)
    }

    fun register() {
        Atom.instance.commandService.commandNodeManager.register(this, aliases)
    }

    init {
        aliases.forEach { alias -> this.aliases.add(alias) }
    }

}