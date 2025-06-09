package net.slimediamond.atom.storage.sql

import be.bendem.sqlstreams.SqlStream
import be.bendem.sqlstreams.util.SqlFunction
import net.slimediamond.atom.Atom
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

inline fun <T> SqlStream.updateReturning(sql: String, mapping: SqlFunction<ResultSet, T>, vararg params: Any): T {
    val update = Atom.sql.update { conn -> conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) }
    update.with(*params)
    update.execute()

    val rs = update.statement.generatedKeys

    if (!rs.next()) {
        throw SQLException("No generated keys found")
    }

    return mapping.apply(rs)
}