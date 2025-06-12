package net.slimediamond.atom.api.command.platforms.discord

import net.slimediamond.atom.api.command.Command
import net.slimediamond.atom.api.command.CommandNode
import java.util.WeakHashMap

private val slashCommands = WeakHashMap<Command, Boolean>()

var CommandNode.slashCommand: Boolean
    get() = slashCommands[this] ?: false
    set(value) {
        slashCommands[this] = value
    }