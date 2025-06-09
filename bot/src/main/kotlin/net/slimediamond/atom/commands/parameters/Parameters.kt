package net.slimediamond.atom.commands.parameters

import com.minecraftonline.mcodata.api.MCODataServiceProvider
import com.minecraftonline.mcodata.api.exceptions.PlayerNotFoundException
import com.minecraftonline.mcodata.api.model.MCOPlayer
import com.minecraftonline.mcodata.api.web.WebAPI
import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.parameter.Parameter
import net.slimediamond.atom.api.command.parameter.parameter
import net.slimediamond.atom.api.messaging.RichText

object Parameters {

    val MESSAGE = Parameter.string {
        key("message")
        greedy()
    }

    val OPTIONAL_MESSAGE = Parameter.string {
        key("message")
        greedy()
        optional()
    }

    val NUMBER = Parameter.int {
        key("number")
    }

    val BOOLEAN = Parameter.boolean {
        key("status")
    }

    val IRC_CHANNEL = Parameter.string {
        key("channel")
        parser { input ->
            if (!input.startsWith("#")) {
                throw ArgumentParseException(input, 0, RichText.of("IRC channel names must start with '#'"))
            } else if (input.contains(" ")) {
                throw ArgumentParseException(input, input.indexOf(" "),
                    RichText.of("IRC channel names must not contain spaces"))
            }
            return@parser input
        }
    }

    val SERVICE = Parameter.service {
        key("service")
    }

    val MCO_PLAYER = parameter<MCOPlayer> {
        key("player")
        parser { input ->
            return@parser MCODataServiceProvider.web().getPlayerByName(input)
                .orElseThrow { PlayerNotFoundException(input) }
        }
        optional()
    }

}