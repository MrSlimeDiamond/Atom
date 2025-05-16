package net.slimediamond.atom.api.irc

/**
 * A client's connection to a [Server]
 *
 * @see Server
 * @see IrcClient
 */
interface Connection {

    /**
     * The name the client has on the server
     */
    var nickname: String

    /**
     * The "real name" of the client
     */
    val realName: String

    /**
     * The username of the client
     */
    val username: String

    /**
     * Whether the client is connected to the server
     */
    var isConnected: Boolean

    /**
     * The server that this connection is using
     */
    val server: Server

    /**
     * Connect to the server
     */
    fun connect(client: IrcClient)

    /**
     * Disconnect the socket from the server
     */
    fun disconnect(message: String)

    /**
     * Send a message to the specified channel
     *
     * @param target The target to send the message to, like
     * a channel or a user
     * @param message The message to send
     */
    fun sendMessage(target: String, message: String)

    /**
     * Sends a raw message to the server
     */
    fun sendRaw(line: String)

}