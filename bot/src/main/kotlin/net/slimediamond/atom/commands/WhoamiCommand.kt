package net.slimediamond.atom.commands

import net.slimediamond.atom.api.command.CommandNode
import net.slimediamond.atom.api.command.CommandNodeContext
import net.slimediamond.atom.api.command.CommandResult
import net.slimediamond.atom.api.messaging.RichText

class WhoamiCommand : CommandNode("Check who the bot thinks you are", "whoami") {

    override fun execute(context: CommandNodeContext): CommandResult {
        val builder = RichText.of("User information for ${context.sender.name}: ")
        if (context.sender.userDao != null) {
            val userDao = context.sender.userDao!!
            builder.append(RichText.of("User ID: ${userDao.id}; recognized IRC nickname: ${userDao.ircNickname}; " +
                    "recognized IRC hostname: ${userDao.ircHostname}; Discord ID: ${userDao.discordId}"))
        } else {
            builder.append(RichText.of("not in the database."))
        }

        context.sendMessage(builder)

        return CommandResult.success
    }

}