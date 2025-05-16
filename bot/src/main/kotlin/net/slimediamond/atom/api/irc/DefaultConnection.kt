package net.slimediamond.atom.api.irc

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class DefaultConnection(
    override var nickname: String,
    override var realName: String,
    override val username: String,
    override val server: Server
) : Connection {

    private lateinit var socket: Socket
    private lateinit var writer: BufferedWriter

    override var isConnected: Boolean
        get() = ::socket.isInitialized && !socket.isConnected
        set(_) {}

    override fun connect(client: IrcClient) {
        // FIXME
//        check(this.isConnected) {
//            "Client is already connected"
//        }
        // initializing this actually makes the connection
        socket = Socket(server.host, server.port)
        Thread {
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
        }.start()
    }

    override fun disconnect(message: String) {
        sendRaw("QUIT :$message")
        writer.close()
        socket.close()
    }

    override fun sendMessage(target: String, message: String) {
        sendRaw("PRIVMSG $target :$message")
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