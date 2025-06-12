package net.slimediamond.atom.api.discord.event

import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.discord.entities.SlashCommandInteraction
import net.slimediamond.atom.api.discord.entities.User
import net.slimediamond.atom.api.event.Cause
import net.slimediamond.atom.api.messaging.DiscordAudience

open class DiscordSlashCommandEvent(
    cause: Cause,
    client: DiscordClient,
    val audience: DiscordAudience,
    val user: User,
    val interaction: SlashCommandInteraction
) : DiscordEvent(cause, client)