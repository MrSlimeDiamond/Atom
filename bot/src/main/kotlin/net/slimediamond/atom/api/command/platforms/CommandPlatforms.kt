package net.slimediamond.atom.api.command.platforms

import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandPlatform
import net.slimediamond.atom.api.command.platforms.irc.IrcCommandPlatform

object CommandPlatforms {

    val IRC: CommandPlatform = IrcCommandPlatform()
    val DISCORD = DiscordCommandPlatform()

}