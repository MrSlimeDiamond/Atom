package net.slimediamond.atom.storage.dao

import net.slimediamond.atom.Atom
import net.slimediamond.atom.irc.api.entities.User
import net.slimediamond.atom.services.PermissionService
import net.slimediamond.atom.storage.StorageService
import java.sql.ResultSet
import java.util.Optional

class UserDao(val id: Int) {

    companion object {
        private val sql = Atom.instance.serviceManager.provide(StorageService::class.java).sql
        private val permissionService = Atom.instance.serviceManager.provide(PermissionService::class.java)

        fun getFromIrc(user: User): Optional<UserDao> {
            return sql.first("SELECT * from users WHERE irc_nickname = ? AND irc_hostname = ?", {
                UserDao(it.getInt("id"))
            }, user.nickname, user.hostname)
        }
    }

    fun hasPermission(permission: String): Boolean {
        return permissionService.hasPermission(this, permission)
    }

    var discordId: Long?
        get() {
            return selectOne("discord_id") { it.getLong("discord_id") }.orElse(null)
        }
        set(value) {
            updateOne("discord_id" to value)
        }

    var ircNickname: String?
        get() {
            return selectOne("irc_nickname") { it.getString("irc_nickname") }.orElse(null)
        }
        set(value) {
            updateOne("irc_nickname" to value)
        }

    var ircHostname: String?
        get() {
            return selectOne("irc_hostname") { it.getString("irc_hostname") }.orElse(null)
        }
        set(value) {
            updateOne("irc_hostname" to value)
        }

    private fun <T> selectOne(field: String, mapper: (ResultSet) -> T): Optional<T> {
        return sql.first("SELECT $field FROM users WHERE id = ?", mapper, this.id)
    }

    private fun <T> updateOne(vararg pairs: Pair<String, T>) {
        val fields = pairs.joinToString(", ") { "${it.first} = ?" }
        val values = pairs.map { it.second } + this.id
        sql.execute("UPDATE users SET $fields WHERE id = ?")
            .apply { values.forEach { with(it) } }
            .execute()
    }

}