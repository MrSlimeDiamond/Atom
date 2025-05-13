package net.slimediamond.atom.irc.api.linehandlers

import net.slimediamond.atom.irc.api.Connection

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