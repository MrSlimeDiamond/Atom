package net.slimediamond.atom.commands

import kotlinx.coroutines.flow.count
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.discord.embed.description
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.discord.DiscordBot
import net.slimediamond.atom.ircbot.IrcBot
import net.slimediamond.atom.utils.Embeds
import java.util.LinkedList

class BotInfoCommand : CommandNode("Get information about the bot", "botinfo") {

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val messages = LinkedList<RichText>()
        Atom.bot.serviceManager.provide(DiscordBot::class)?.also { discordBot ->
            messages.add(RichText.of()
                .append(RichText.of("Discord guild count").bold())
                .append(RichText.of(": ${discordBot.client.guilds.count()}")))
        }
        Atom.bot.serviceManager.provide(IrcBot::class)?.also { ircBot ->
            messages.add(RichText.of()
                .append(RichText.of("IRC Bot Channels").bold())
                .append(RichText.of(": ${ircBot.connection.channels.size}")))
        }
        if (context is DiscordCommandNodeContext) {
            context.sendEmbed {
                color = Embeds.THEME_COLOR
                title = "Bot Info"
                description(RichText.join(RichText.newline(), messages))
            }
        } else {
            context.sendMessage(RichText.join(RichText.of(". "), messages))
        }
        return CommandResult.success
    }

}