package net.slimediamond.atom.discord.abstraction.entities

import net.slimediamond.atom.api.discord.entities.Guild

class KordGuild(private val kordGuild: dev.kord.core.entity.Guild) : Guild {

    override val id: Long
        get() = kordGuild.id.value.toLong()

}