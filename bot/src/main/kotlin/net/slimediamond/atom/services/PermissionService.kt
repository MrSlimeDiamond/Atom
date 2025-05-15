package net.slimediamond.atom.services

import be.bendem.sqlstreams.SqlStream
import net.slimediamond.atom.Atom
import net.slimediamond.atom.service.Service
import net.slimediamond.atom.storage.StorageService
import net.slimediamond.atom.storage.dao.UserDao
import java.sql.ResultSet

@Service("permission")
class PermissionService {

    fun hasPermission(user: UserDao, permission: String): Boolean {
        // We'll start with a specific permission, like 'atom.command.whatever',
        // if the user has 'atom.command.*', then they should have permission
        // for this reason, we should remove the last element as split by the dot
        // to obtain a string like 'atom.command', then query for strings like 'atom.command'
        // from there, we're able to check relevant permissions, such as the permission
        // itself, or the wildcard permission.
        val baseNode = permission.split(".").dropLast(1).joinToString(".")
        return getSql().first("SELECT node FROM permissions WHERE user_id = ? AND node LIKE ?",
            PermissionService::getPermissionNode,
            user.id, baseNode
        ).stream().anyMatch { it.equals("$baseNode.*") || it.equals(permission) }
    }

    fun hasDirectPermission(user: UserDao, permission: String): Boolean {
        return getSql().first("SELECT node FROM permissions WHERE user_id = ? AND node = ?",
            PermissionService::getPermissionNode,
            user.id, permission).isPresent
    }

    companion object {
        @JvmStatic
        fun getPermissionNode(rs: ResultSet): String = rs.getString("node")

        private fun getSql(): SqlStream {
            return Atom.instance.serviceManager.provide(StorageService::class.java).sql
        }
    }

}