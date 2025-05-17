package net.slimediamond.atom.api.command

import net.slimediamond.atom.api.messaging.RichText

interface CommandResult {

    /**
     * Whether this is a successful command result
     */
    val success: Boolean

    /**
     * A message associated with this command result
     */
    val message: RichText?

    companion object {

        /**
         * A successful command result
         */
        val success: CommandResult
            get() = object: CommandResult {
                override val success = true
                override val message: RichText? = null
            }

        /**
         * An empty command result
         */
        val empty: CommandResult
            get() = object: CommandResult {
                override val success = false
                override val message: RichText? = null
            }

        /**
         * Return an error command result with a message
         */
        fun error(message: String): CommandResult = object : CommandResult {
            override val success = false
            override val message = RichText.of(message)
        }

        /**
         * Return an error command result with a message
         */
        fun error(message: RichText): CommandResult = object : CommandResult {
            override val success = false
            override val message = message
        }

    }

}