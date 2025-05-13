package net.slimediamond.atom.commands.api

abstract class CommandNodeContext(val input: String, val platform: CommandPlatform) {

    /**
     * Send a reply to the sender
     */
    abstract fun reply(message: String)

}