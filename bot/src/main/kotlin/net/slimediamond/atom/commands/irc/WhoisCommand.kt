package net.slimediamond.atom.commands.irc

import kotlinx.coroutines.future.await
import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.CommandPlatforms
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.discord.embed.description
import net.slimediamond.atom.api.messaging.richText
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.ircbot.IrcBot

class WhoisCommand : CommandNode("View the WHOIS of a user on IRC", "whois") {

    init {
        platforms.add(CommandPlatforms.DISCORD)

        parameters.add(Parameters.IRC_USER_NICKNAME_NOT_VALIDATED)
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val nickname = context.requireOne(Parameters.IRC_USER_NICKNAME_NOT_VALIDATED)
        // we are on a new thread anyway - safe to await
        val response = Atom.bot.serviceManager.provide(IrcBot::class)!!.connection.whois(nickname).await()
            ?: return CommandResult.error("User $nickname not found.")
        if (context is DiscordCommandNodeContext) {
            context.sendEmbed {
                title = "${response.user.nickname} (${response.user.username}@${response.user.hostname})"
                description {
                    response.user.realName?.let { realName ->
                        append(richText("Real name: ").bold())
                        append(richText(realName))
                        appendNewline()
                    }
                    append(richText("Server: ").bold())
                    append(richText("${response.server.name} | ${response.server.description}"))
                    if (response.channels.isNotEmpty()) {
                        appendNewline()
                        append(richText("Channels: ").bold())
                        append(richText(response.channels.joinToString(", ")))
                    }
                    response.account?.let { account ->
                        appendNewline()
                        append(richText("Account: ").bold())
                        append(richText(account))
                    }
                }
            }
        } else {
            context.sendMessage("$response") // lazy :P it shouldn't be called from anywhere other than Discord anyway
        }
        return CommandResult.success
    }

}