package net.slimediamond.atom.services

import net.slimediamond.atom.Atom
import net.slimediamond.atom.api.service.Service
import net.slimediamond.atom.storage.dao.UserDao
import java.sql.ResultSet

@Service("permission")
class PermissionService {

    fun hasPermission(user: UserDao, permission: String): Boolean {
        val parts = permission.split(".")
        val candidates = mutableListOf(permission)

        for (i in 1 until parts.size) {
            val base = parts.take(i).joinToString(".")
            candidates.add("$base.*")
        }

        val placeholders = candidates.joinToString(", ") { "?" }
        val sql = "SELECT node FROM permissions WHERE user_id = ? AND node IN ($placeholders)"
        val results = Atom.instance.sql.first(
            sql,
            PermissionService::getPermissionNode,
            user.id, *candidates.toTypedArray()
        )
        return !results.isEmpty
    }

    fun hasDirectPermission(user: UserDao, permission: String): Boolean {
        return Atom.instance.sql.first("SELECT node FROM permissions WHERE user_id = ? AND node = ?",
            PermissionService::getPermissionNode,
            user.id, permission).isPresent
    }

    companion object {
        @JvmStatic
        fun getPermissionNode(rs: ResultSet): String = rs.getString("node")
    }

}