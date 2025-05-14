package net.slimediamond.atom.commands.api.exceptions

import net.slimediamond.atom.messaging.RichMessage

class CommandException(val msg: RichMessage) : Exception(msg.content)