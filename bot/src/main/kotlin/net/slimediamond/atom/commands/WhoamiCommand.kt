package net.slimediamond.atom.commands

import net.slimediamond.atom.commands.api.CommandNode
import net.slimediamond.atom.commands.api.CommandNodeContext
import net.slimediamond.atom.commands.api.CommandResult
import net.slimediamond.atom.messaging.RichMessage

class WhoamiCommand : CommandNode("whoami") {

    override fun execute(context: CommandNodeContext): CommandResult {
        val builder = RichMessage.of("User information for ${context.sender.name}: ")
        if (context.sender.userDao != null) {
            val userDao = context.sender.userDao!!
            builder.append(RichMessage.of("User ID: ${userDao.id}; recognized IRC nickname: ${userDao.ircNickname}; " +
                    "recognized IRC hostname: ${userDao.ircHostname}; Discord ID: ${userDao.discordId}"))
        } else {
            builder.append(RichMessage.of("not in the database."))
        }

        context.sendMessage(builder)

        return CommandResult.success
    }

}