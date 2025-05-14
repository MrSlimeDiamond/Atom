package net.slimediamond.atom.commands.api

import net.slimediamond.atom.messaging.Audience

abstract class CommandNodeContext(val sender: CommandSender, val input: String, val platform: CommandPlatform) : Audience