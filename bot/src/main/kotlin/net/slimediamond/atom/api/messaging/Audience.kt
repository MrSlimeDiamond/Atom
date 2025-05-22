package net.slimediamond.atom.api.messaging

interface Audience {

    /**
     * Send a message to the audience
     */
    suspend fun sendMessage(message: String)

    /**
     * Send a message to the audience
     */
    suspend fun sendMessage(message: RichText)

}