package net.slimediamond.atom.commands.minecraftonline

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.renderer.DiscordRichMessageRenderer
import net.slimediamond.atom.api.messaging.richText
import net.slimediamond.atom.commands.parameters.Parameters
import net.slimediamond.atom.utils.Embeds
import net.slimediamond.atom.utils.getTargetMCOPlayer

class BanWhyCommand : CommandNode("Get the ban reason of a player", "banwhy", "why") {

    init {
        parameters.add(Parameters.MCO_PLAYER)
    }

    companion object {
        private const val LEGACY = "You have been permanently banned! (legacy)"
    }

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val target = context.getTargetMCOPlayer()
        val ban = target.banReason.orElse(null)
        if (ban != null) {
            if (context is DiscordCommandNodeContext) {
                context.sendEmbed {
                    author {
                        name = target.name
                        icon = target.avatarUrl
                    }
                    title = "Ban reason for ${target.name}"
                    field {
                        name = "Date"
                        value = "${DiscordRichMessageRenderer.render(RichText.timestamp(ban.date))} " +
                                "(${DiscordRichMessageRenderer.render(RichText.timestamp(ban.date, relative = true))})"
                        inline = true
                    }
                    footer = Embeds.MCO_FOOTER
                    if (ban.message != LEGACY) {
                        description = ban.message
                        field {
                            name = "Banned by"
                            value = ban.author.name
                            inline = true
                        }
                    } else {
                        description = "*Legacy ban*"
                    }
                }
            } else {
                context.sendMessage {
                    append(richText(target.name))
                    if (ban.message == LEGACY) {
                        append(richText(" has a legacy ban from "))
                        append(richText(ban.date))
                    } else {
                        append(richText(" is banned: ${ban.message} (left by ${ban.author.name} on "))
                        append(richText(ban.date))
                        append(richText(")"))
                    }
                }
            }
        } else {
            context.replySuccess("${target.name} is not banned")
        }
        return CommandResult.success
    }

}