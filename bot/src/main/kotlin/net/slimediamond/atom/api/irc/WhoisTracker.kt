package net.slimediamond.atom.api.irc

import dev.kord.cache.api.ConcurrentHashMap
import java.util.concurrent.CompletableFuture

class WhoisTracker {

    /**
     * The user WHOIS responses
     */
    val pending = ConcurrentHashMap<String, CompletableFuture<WhoisResponse>>()

    /**
     * The current name that a whois response is being created for
     */
    var currentName: String? = null

}