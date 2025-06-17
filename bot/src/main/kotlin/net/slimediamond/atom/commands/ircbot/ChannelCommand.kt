package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.api.command.RootOnlyCommandNode

class ChannelCommand : RootOnlyCommandNode("Manage IRC channels", "channels") {

    init {
        addChild(ChannelQueryCommand())
        addChild(ChannelJoinCommand())
        addChild(ChannelAutoJoinCommand())
    }

}