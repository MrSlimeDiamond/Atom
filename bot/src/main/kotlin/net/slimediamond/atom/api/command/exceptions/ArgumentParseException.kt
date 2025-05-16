package net.slimediamond.atom.api.command.exceptions

import net.slimediamond.atom.api.messaging.RichMessage

class ArgumentParseException(val input: String, val index: Int, message: RichMessage) : CommandException(message)