package net.slimediamond.atom.api.command.parameter

import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.messaging.richText

class IntValueParser : ValueParser<Int> {

    override fun parse(input: String): Int {
        return input.toIntOrNull()
            ?: throw ArgumentParseException(input, 0, richText("Provided input is not a number"))
    }

    override val clientParameterType: ClientParameterType
        get() = ClientParameterType.INTEGER

}