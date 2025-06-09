package net.slimediamond.atom

import be.bendem.sqlstreams.SqlStream
import net.slimediamond.atom.configuration.Configuration

object Atom {

    @Volatile
    lateinit var configuration: Configuration
    @Volatile
    lateinit var bot: Bot
    @Volatile
    lateinit var sql: SqlStream

}