package net.slimediamond.atom.commands.minecraftonline

object SeenCommands {

    fun firstSeenCommand() = SeenCommand(true, "See the first seen date of a player", "firstseen", "fs")

    fun lastSeenCommand() = SeenCommand(false, "See the last seen date of a player", "lastseen", "ls");

}