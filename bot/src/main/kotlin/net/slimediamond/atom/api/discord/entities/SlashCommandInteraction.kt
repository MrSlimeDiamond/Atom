package net.slimediamond.atom.api.discord.entities

data class SlashCommandInteraction(
    val name: String,
    val subcommand: String?,
    val parameterKeyMap: Map<String, String>
)