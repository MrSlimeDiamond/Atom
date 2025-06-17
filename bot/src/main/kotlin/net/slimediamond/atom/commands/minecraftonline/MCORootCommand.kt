package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.RootOnlyCommandNode

/**
 * Root command node for MCO slash commands
 */
class MCORootCommand : RootOnlyCommandNode("MinecraftOnline commands", "mco", "minecraftonline") {

    init {
        addChild(MCOCommandInstances.timeplayedCommand)
        addChild(MCOCommandInstances.lastseenCommand)
        addChild(MCOCommandInstances.firstseenCommand)
        addChild(MCOCommandInstances.banWhyCommand)
        addChild(MCOCommandInstances.banCountCommand)
    }

}