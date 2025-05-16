package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.api.command.RootOnlyCommandNode

class ChannelCommand : RootOnlyCommandNode("channel") {

    init {
        addChild(ChannelQueryCommand())
        addChild(ChannelAddCommand())
    }

}