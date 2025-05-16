package net.slimediamond.atom.storage

import be.bendem.sqlstreams.SqlStream
import net.slimediamond.atom.Atom
import net.slimediamond.atom.event.Listener
import net.slimediamond.atom.service.Service
import net.slimediamond.atom.service.events.ServiceStartEvent
import org.apache.commons.dbcp2.BasicDataSource
import org.apache.logging.log4j.Logger
import java.sql.DriverManager

@Service("storage")
class StorageService {

    @Volatile
    lateinit var logger: Logger
    @Volatile
    lateinit var sql: SqlStream

    @Listener
    fun startService(event: ServiceStartEvent) {
        logger = event.container.logger

        logger.info("Starting storage service (database)")

        val jdbc = Atom.instance.configuration.storageConfiguration.jdbcString
        if (jdbc.isEmpty()) {
            logger.error("Storage jdbc url is not configured")
            return
        }

//        val dataSource = BasicDataSource()
//            .apply {
//                url = jdbc
//                validationQuery = "SELECT 1"
//                maxIdle = 1
//                initialSize = 2
//            }

        sql = SqlStream.connect(DriverManager.getConnection(jdbc))
        Atom.instance.sql = sql
    }

}