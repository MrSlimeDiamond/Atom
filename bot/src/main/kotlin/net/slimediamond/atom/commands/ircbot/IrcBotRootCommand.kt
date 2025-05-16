package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.commands.api.RootOnlyCommandNode
import net.slimediamond.atom.commands.api.platforms.CommandPlatforms

class IrcBotRootCommand : RootOnlyCommandNode("ircbot") {

    init {
        platforms.add(CommandPlatforms.IRC)

        children.add(ChannelCommand())
    }

}