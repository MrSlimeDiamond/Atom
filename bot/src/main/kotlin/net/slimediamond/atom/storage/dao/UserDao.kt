package net.slimediamond.atom.storage.dao

import net.slimediamond.atom.Atom
import net.slimediamond.atom.irc.api.entities.User
import net.slimediamond.atom.services.PermissionService
import net.slimediamond.atom.storage.StorageService
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

}