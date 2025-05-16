package net.slimediamond.atom.commands.ircbot

import net.slimediamond.atom.commands.api.RootOnlyCommandNode

class ChannelCommand : RootOnlyCommandNode("channel") {

    init {
        children.add(ChannelQueryCommand())
    }

}