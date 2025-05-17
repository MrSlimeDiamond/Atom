package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.event.Cause

class DiscordUserMessageEvent(cause: Cause, client: DiscordClient, user: User, message: String) : DiscordMessageEvent(
    cause, client,
    user,
    message,
    user
)