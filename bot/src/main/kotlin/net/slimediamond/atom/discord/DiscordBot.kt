package net.slimediamond.atom.discord

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.discord.DiscordClient
import net.slimediamond.atom.api.event.Listener
import net.slimediamond.atom.api.service.Service
import net.slimediamond.atom.api.service.events.ServiceStartEvent
import net.slimediamond.atom.api.service.events.ServiceStopEvent
import net.slimediamond.atom.discord.abstraction.KordDiscordClient
import net.slimediamond.atom.discord.listeners.DiscordMessageListener

@Service("discord")
class DiscordBot {

    lateinit var client: DiscordClient

    @OptIn(DelicateCoroutinesApi::class)
    @Listener
    fun onServiceStart(event: ServiceStartEvent) {
        event.container.logger.info("Starting Discord bot")
        val token = Atom.instance.configuration.discordConfiguration.token
        if (token.isEmpty()) {
            event.container.logger.error("No token provided - Discord bot will not start")
            return
        }
        client = KordDiscordClient(token)
        GlobalScope.launch {
            event.container.logger.info("Logging in!")
            client.login()
        }

        Atom.instance.eventManager.registerListener(DiscordMessageListener())
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Listener
    fun onServiceStop(event: ServiceStopEvent) {
        GlobalScope.launch {
            client.logout()
        }
    }

}