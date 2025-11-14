package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.Audience

open class DiscordMessageEditEvent(cause: Cause, client: DiscordClient, user: User, original: String, replacement: String, audience: Audience) :
    DiscordMessageEvent(cause, client, user, replacement, audience)