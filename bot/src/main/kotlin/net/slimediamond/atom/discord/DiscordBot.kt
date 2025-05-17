package net.slimediamond.atom.discord

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.event.Listener
import net.slimediamond.atom.api.service.Service
import net.slimediamond.atom.api.service.events.ServiceStartEvent
import net.slimediamond.atom.discord.abstraction.KordDiscordClient
import net.slimediamond.atom.discord.listeners.DiscordMessageListener

@Service("discord")
class DiscordBot {

    lateinit var discordClient: DiscordClient

    @OptIn(DelicateCoroutinesApi::class)
    @Listener
    fun onServiceStart(event: ServiceStartEvent) {
        event.container.logger.info("Starting Discord bot")
        val token = Atom.instance.configuration.discordConfiguration.token
        if (token.isEmpty()) {
            error("No token provided - Discord bot will not start")
        }
        discordClient = KordDiscordClient(token)
        GlobalScope.launch {
            event.container.logger.info("Logging in!")
            discordClient.login()
        }

        Atom.instance.eventManager.registerListener(DiscordMessageListener())
    }

}