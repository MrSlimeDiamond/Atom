package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience

open class DiscordUserMessageEditEvent(cause: Cause, client: DiscordClient, user: User, original: String, replacement: String, audience: Audience) :
    DiscordMessageEditEvent(
        cause,
        client,
        user,
        original,
        replacement,
        audience
    )