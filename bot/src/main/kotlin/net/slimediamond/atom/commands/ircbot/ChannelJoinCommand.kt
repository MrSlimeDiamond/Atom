package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.ircbot.IrcBot

class ChannelJoinCommand : CommandNode("Join an IRC channel", "join") {

    init {
        parameters.add(Parameters.IRC_CHANNEL)
        permission = "atom.command.ircbot.channel.join"
    }

    override fun execute(context: CommandNodeContext): CommandResult {
        val channel = context.requireOne(Parameters.IRC_CHANNEL)

        Atom.instance.serviceManager.provide(IrcBot::class.java).connection.joinChannel(channel)
        context.sendMessage("Joined channel $channel")

        return CommandResult.success
    }

}