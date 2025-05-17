package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience

open class DiscordMessageEvent(cause: Cause, client: DiscordClient, val user: User, val message: String, val audience: Audience) : DiscordEvent(cause, client)