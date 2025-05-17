package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.Guild
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience

class DiscordGuildMessageEvent(cause: Cause, client: DiscordClient, user: User, message: String, audience: Audience, val guild: Guild) :
    DiscordMessageEvent(
        cause, client,
        user,
        message,
        audience
    )