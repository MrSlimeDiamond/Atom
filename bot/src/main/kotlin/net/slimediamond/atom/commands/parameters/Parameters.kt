package net.slimediamond.atom.commands.parameters

import com.minecraftonline.mcodata.api.MCODataServiceProvider
import com.minecraftonline.mcodata.api.exceptions.PlayerNotFoundException
import com.minecraftonline.mcodata.api.model.MCOPlayer
import com.minecraftonline.mcodata.api.web.WebAPI
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.parameter.Parameter
import net.slimediamond.atom.api.messaging.RichText

object Parameters {

    val MESSAGE = Parameter.string()
        .key("message")
        .greedy()
        .build()

    val OPTIONAL_MESSAGE = Parameter.string()
        .optional()
        .key("message")
        .greedy()
        .build()

    val NUMBER = Parameter.int()
        .key("number")
        .build()

    val BOOLEAN = Parameter.boolean()
        .key("status")
        .build()

    val IRC_CHANNEL = Parameter.string()
        .key("channel")
        .parser { input ->
            if (!input.startsWith("#")) {
                throw ArgumentParseException(input, 0, RichText.of("IRC channel names must start with '#'"))
            } else if (input.contains(" ")) {
                throw ArgumentParseException(input, input.indexOf(" "),
                    RichText.of("IRC channel names must not contain spaces"))
            }
            return@parser input
        }
        .build()

    val SERVICE = Parameter.service()
        .key("service")
        .build()

    val MCO_PLAYER = Parameter.builder(MCOPlayer::class.java)
        .key("player")
        .parser { input ->
            return@parser MCODataServiceProvider.web().getPlayerByName(input)
                .orElseThrow { PlayerNotFoundException(input) }
        }
        .build()

}