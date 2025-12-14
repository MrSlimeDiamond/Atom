package net.slimediamond.atom.commands.parameters

import com.minecraftonline.mcodata.api.exceptions.PlayerNotFoundException
import com.minecraftonline.mcodata.api.model.MCOPlayer
import com.minecraftonline.mcodata.web.WebMCODataService
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.parameter.Parameter
import net.slimediamond.atom.api.command.parameter.parameter
import net.slimediamond.atom.api.messaging.RichText

object Parameters {

    val MESSAGE = Parameter.string {
        key = "message"
        greedy = true
    }

    val OPTIONAL_MESSAGE = Parameter.string {
        key = "message"
        greedy = true
        optional = true
    }

    val NUMBER = Parameter.int {
        key = "number"
    }

    val BOOLEAN = Parameter.boolean {
        key = "status"
    }

    val IRC_CHANNEL = Parameter.string {
        key = "channel"
        description = "The name of the IRC channel (prefixed with #)"
        parser { input ->
            if (!input.startsWith("#")) {
                throw ArgumentParseException(input, 0, RichText.of("IRC channel names must start with '#'"))
            } else if (input.contains(" ")) {
                throw ArgumentParseException(input, input.indexOf(" "),
                    RichText.of("IRC channel names must not contain spaces"))
            }
            input
        }
    }

    val SERVICE = Parameter.service {
        key = "service"
        description = "The name of the service"
    }

    val MCO_PLAYER = parameter<MCOPlayer> {
        key = "player"
        description = "The username of the player"
        parser { input ->
            WebMCODataService().getPlayerByName(input)
                .orElseThrow { PlayerNotFoundException(input) }
        }
        optional = true
    }

    val IRC_USER_NICKNAME_NOT_VALIDATED = Parameter.string {
        key = "user"
        description = "The nickname of the user on IRC"
    }

}