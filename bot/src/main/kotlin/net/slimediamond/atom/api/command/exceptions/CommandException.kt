package net.slimediamond.atom.api.command.exceptions

import net.slimediamond.atom.api.messaging.RichText

open class CommandException(val msg: RichText) : Exception(msg.content)