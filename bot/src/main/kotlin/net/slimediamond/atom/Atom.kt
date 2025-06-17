package net.slimediamond.atom

import be.bendem.sqlstreams.SqlStream
import net.slimediamond.atom.configuration.Configuration

object Atom {

    /**
     * The configuration for the bot
     */
    @Volatile
    lateinit var configuration: Configuration

    /**
     * The bot instance
     */
    @Volatile
    lateinit var bot: Bot

    /**
     * The [SqlStream] backend powering the [net.slimediamond.atom.storage.StorageService]
     */
    @Volatile
    lateinit var sql: SqlStream

}