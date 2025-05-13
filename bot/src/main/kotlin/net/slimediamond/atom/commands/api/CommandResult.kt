package net.slimediamond.atom.commands.api

interface CommandResult {

    /**
     * Whether this is a successful command result
     */
    val success: Boolean

    /**
     * A message associated with this command result
     */
    val message: String?

    companion object {

        /**
         * Return a successful command result
         */
        val success: CommandResult
            get() = object: CommandResult {
                override val success = true
                override val message: String? = null
            }

        /**
         * Return an error command result with a message
         */
        fun error(message: String): CommandResult = object : CommandResult {
            override val success = false
            override val message = message
        }

    }

}