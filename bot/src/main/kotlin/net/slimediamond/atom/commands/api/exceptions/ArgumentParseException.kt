package net.slimediamond.atom.commands.api.exceptions

import net.slimediamond.atom.messaging.RichMessage

class ArgumentParseException(val input: String, val index: Int, message: RichMessage) : CommandException(message)