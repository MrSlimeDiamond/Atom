package net.slimediamond.atom.api.command.parameter

import net.slimediamond.atom.api.command.exceptions.ArgumentParseException
import net.slimediamond.atom.api.messaging.richText

class BooleanValueParser : ValueParser<Boolean> {

    override fun parse(input: String): Boolean {
        return input.toBooleanStrictOrNull()
            ?: throw ArgumentParseException(input, 0,
                richText("Provided input is not a boolean ('true' or 'false')"))
    }

    override val clientParameterType: ClientParameterType
        get() = ClientParameterType.BOOLEAN

}