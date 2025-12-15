package net.slimediamond.atom.api.irc

import dev.kord.cache.api.ConcurrentHashMap
import java.util.concurrent.CompletableFuture

class WhoisTracker {

    /**
     * The user WHOIS responses
     */
    private val pending = ConcurrentHashMap<String, CompletableFuture<WhoisResponse?>>()

    fun submit(nickname: String, future: CompletableFuture<WhoisResponse?>) {
        this.pending[nickname.lowercase()] = future
    }

    fun isTracking(nickname: String): Boolean {
        return this.pending.containsKey(nickname.lowercase())
    }

    fun complete(nickname: String, response: WhoisResponse?) {
        this.pending[nickname]?.complete(response)
    }

    fun remove(nickname: String) {
        this.pending.remove(nickname)
    }

}