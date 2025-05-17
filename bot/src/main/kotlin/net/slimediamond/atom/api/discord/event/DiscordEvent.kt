package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.event.AbstractEvent
import net.slimediamond.atom.api.event.Cause

open class DiscordEvent(override val cause: Cause, val client: DiscordClient) : AbstractEvent(cause)