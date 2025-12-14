package net.slimediamond.atom.commands.irc

import net.slimediamond.atom.api.command.RootOnlyCommandNode
import net.slimediamond.atom.api.command.platforms.CommandPlatforms

class IrcCommand : RootOnlyCommandNode("User-facing IRC commands", "irc") {

    init {
        platforms.add(CommandPlatforms.DISCORD)

        addChild(WhoisCommand())
    }

}