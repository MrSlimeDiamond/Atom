package net.slimediamond.atom.api.command.exceptions

import net.slimediamond.atom.api.messaging.RichText

class ArgumentParseException(val input: String, val index: Int, message: RichText) : CommandException(message)