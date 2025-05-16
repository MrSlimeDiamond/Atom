package net.slimediamond.atom.commands.parameters

import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.command.parameter.Parameter
import net.slimediamond.atom.api.messaging.RichMessage
import net.slimediamond.atom.storage.dao.ChannelDao

object Parameters {

    val MESSAGE: Parameter.Value<String> = Parameter.string()
        .key("message")
        .greedy()
        .build()

    val OPTIONAL_MESSAGE: Parameter.Value<String> = Parameter.string()
        .optional()
        .key("message")
        .greedy()
        .build()

    val NUMBER: Parameter.Value<Int> = Parameter.int()
        .key("number")
        .build()

    val BOOLEAN = Parameter.boolean()
        .key("status")
        .build()

    val IRC_CHANNEL: Parameter.Value<String> = Parameter.string()
        .key("channel")
        .parser { input ->
            if (!input.startsWith("#")) {
                throw ArgumentParseException(input, 0, RichMessage.of("IRC channel names must start with '#'"))
            } else if (input.contains(" ")) {
                throw ArgumentParseException(input, input.indexOf(" "),
                    RichMessage.of("IRC channel names must not contain spaces"))
            }
            return@parser input
        }
        .build()

}