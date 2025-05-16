package net.slimediamond.atom.api.irc.linehandlers

import net.slimediamond.atom.api.irc.Connection

/**
 * A line handler - handles incoming input (or "lines") from
 * a [Connection], and does specific things with them
 */
interface LineHandler {

    /**
     * Handle an incoming line from the IRC client
     */
    fun handle(line: String, connection: Connection)

}