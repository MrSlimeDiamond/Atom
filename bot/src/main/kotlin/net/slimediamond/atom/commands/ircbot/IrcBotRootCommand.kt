package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.api.command.RootOnlyCommandNode
import net.slimediamond.atom.api.command.platforms.CommandPlatforms

class IrcBotRootCommand : RootOnlyCommandNode("ircbot", "irc") {

    init {
        platforms.add(CommandPlatforms.IRC)

        addChild(ChannelCommand())
    }

}