package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.RootOnlyCommandNode

/**
 * Root command node for MCO slash commands
 */
class MCORootCommand : RootOnlyCommandNode("MinecraftOnline commands", "mco", "minecraftonline") {

    init {
        addChild(TimeplayedCommand())
        addChild(SeenCommands.lastSeenCommand())
        addChild(SeenCommands.firstSeenCommand())
        addChild(BanWhyCommand())
        addChild(BanCountCommand())
        addChild(RandomPlayerCommand())
    }

}