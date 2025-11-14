package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.Guild
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience

class DiscordGuildMessageEditEvent(
    cause: Cause,
    client: DiscordClient,
    user: User,
    val original: String,
    val replacement: String,
    audience: Audience,
    val guild: Guild
) : DiscordMessageEditEvent(cause, client, user, original, replacement, audience)