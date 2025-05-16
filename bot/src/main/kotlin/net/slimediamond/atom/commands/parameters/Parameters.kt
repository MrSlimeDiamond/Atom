package net.slimediamond.atom.commands.parameters

import net.slimediamond.atom.api.command.parameter.Parameter

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

}