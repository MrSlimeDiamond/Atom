package net.slimediamond.atom.commands.minecraftonline

/**
 * Static holder of MinecraftOnline command instances
 *
 * This is because they need to registered as root commands themselves,
 * as well as commands under [MCORootCommand]
 */
object MCOCommandInstances {

    val banCountCommand = BanCountCommand()

    val banWhyCommand = BanWhyCommand()

    val timeplayedCommand = TimeplayedCommand()

    val firstseenCommand = SeenCommand(true, "See the first seen date of a player", "firstseen", "fs")

    val lastseenCommand = SeenCommand(false, "See the last seen date of a player", "lastseen", "ls")

    val randomPlayerCommand = RandomPlayerCommand()

}