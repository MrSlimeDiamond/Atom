package net.slimediamond.atom.api.command.exceptions

import net.slimediamond.atom.api.messaging.RichMessage

open class CommandException(val msg: RichMessage) : Exception(msg.content)