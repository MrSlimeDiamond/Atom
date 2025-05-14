package net.slimediamond.atom.commands.api

import net.slimediamond.atom.messaging.RichMessage

interface CommandResult {

    /**
     * Whether this is a successful command result
     */
    val success: Boolean

    /**
     * A message associated with this command result
     */
    val message: RichMessage?

    companion object {

        /**
         * A successful command result
         */
        val success: CommandResult
            get() = object: CommandResult {
                override val success = true
                override val message: RichMessage? = null
            }

        /**
         * An empty command result
         */
        val empty: CommandResult
            get() = object: CommandResult {
                override val success = false
                override val message: RichMessage? = null
            }

        /**
         * Return an error command result with a message
         */
        fun error(message: String): CommandResult = object : CommandResult {
            override val success = false
            override val message = RichMessage.of(message)
        }

        /**
         * Return an error command result with a message
         */
        fun error(message: RichMessage): CommandResult = object : CommandResult {
            override val success = false
            override val message = message
        }

    }

}