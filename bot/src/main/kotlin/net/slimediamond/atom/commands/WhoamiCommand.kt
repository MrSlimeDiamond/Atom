package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.command.platforms.discord.DiscordCommandNodeContext
import net.slimediamond.atom.api.discord.embed.description
import net.slimediamond.atom.api.messaging.RichText
import net.slimediamond.atom.api.messaging.richText

class WhoamiCommand : CommandNode("Check who the bot thinks you are", "whoami") {

    override suspend fun execute(context: CommandNodeContext): CommandResult {
        val messages = mutableListOf(
            richText("Sender name: ").bold().append(richText(context.sender.name)),
            richText("Sender type: ").bold().append(richText(context.sender.javaClass.simpleName)),
            richText("Given input: ").bold().append(richText(context.input)),
            richText("Platform: ").bold().append(richText(context.platform.javaClass.simpleName))
        )

        if (context.sender.userDao != null) {
            val userDao = context.sender.userDao!!
            messages.add(richText("User ID: ").bold().append(richText(userDao.id)))
            messages.add(richText("Recognized IRC hostname: ").bold()
                .append(richText {
                    if (userDao.ircHostname != null) {
                        append(richText(userDao.ircHostname!!))
                    } else {
                        append(richText("None").italics())
                    }
                }))
            messages.add(richText("IRC Nickname: ").bold()
                .append(richText {
                    if (userDao.ircNickname != null) {
                        append(richText(userDao.ircNickname!!))
                    } else {
                        append(richText("None").italics())
                    }
                }))
            messages.add(richText("Discord ID: ").bold()
                .append(richText {
                    if (userDao.discordId != null) {
                        append(richText(userDao.discordId!!))
                    } else {
                        append(richText("None").italics())
                    }
                }))
        } else {
            messages.add(richText("You are not a recognized user in the database."))
        }

        if (context is DiscordCommandNodeContext) {
            context.sendEmbed {
                title = "User information"
                description(RichText.join(richText("\n"), messages))
            }
        } else {
            context.sendMessage(RichText.join(richText(" | "), messages))
        }

        return CommandResult.success
    }

}