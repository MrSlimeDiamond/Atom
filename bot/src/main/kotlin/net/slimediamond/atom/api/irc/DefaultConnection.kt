package net.slimediamond.atom.api.irc

import net.slimediamond.atom.api.irc.entities.Channel
import okhttp3.internal.toImmutableList
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.*

class DefaultConnection(
    override var nickname: String,
    override var realName: String,
    override val username: String,
    override val server: Server
) : Connection {

    private lateinit var socket: Socket
    private lateinit var writer: BufferedWriter
    private lateinit var thread: Thread

    private val _channels = LinkedList<Channel>()

    override var isConnected: Boolean
        get() = ::socket.isInitialized && !socket.isConnected
        set(_) {}

    override val channels: List<Channel>
        get() = _channels.toImmutableList()

    override fun connect(client: IrcClient) {
        // FIXME
//        check(this.isConnected) {
//            "Client is already connected"
//        }
        // initializing this actually makes the connection
        socket = Socket(server.host, server.port)
        thread = Thread {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            sendRaw("NICK $nickname")
            sendRaw("USER $username 0 * :$realName")
            while (socket.isConnected) {
                val line = reader.readLine() ?: break
                handleLine(line, client)
            }
        }.apply {
            name = "irc bot"
        }

        thread.start()
    }

    override fun disconnect(message: String) {
        sendRaw("QUIT :$message")
        writer.close()
        socket.close()
        isConnected = false
        thread.interrupt()
    }

    override fun sendMessage(target: String, message: String) {
        sendRaw("PRIVMSG $target :$message")
    }

    override fun joinChannel(target: String) {
        _channels.add(Channel(this, target, Collections.emptyList())) // TODO Users (probs query???)
        sendRaw("JOIN $target")
    }

    override fun sendRaw(line: String) {
        writer.write(line + "\r\n")
        writer.flush()
    }

    private fun handleLine(line: String, client: IrcClient) {
        // this is because `this` can't be called in a Thread
        client.handleLine(line, this)
    }

}