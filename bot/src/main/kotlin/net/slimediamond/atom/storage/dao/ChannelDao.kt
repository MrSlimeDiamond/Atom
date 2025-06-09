package net.slimediamond.atom.storage.dao

import net.slimediamond.atom.Atom
import net.slimediamond.atom.storage.sql.updateReturning

class ChannelDao(private val id: Int, val name: String) {

    companion object {
        /**
         * Get an IRC channel DAO by its name, otherwise make one
         */
        fun getByName(name: String): ChannelDao {
            val existing = Atom.sql.first("SELECT id, channel FROM irc_channels WHERE channel = ?", {
                val id = it.getInt("id")
                return@first ChannelDao(id, name)
            }, name)

            if (existing.isPresent) {
                return existing.get()
            } else {
                // make a new one, since we can easily
                val id = Atom.sql.updateReturning("INSERT INTO irc_channels (channel, auto_join) VALUES (?, ?)", { it.getInt(1) }, name, false)
                return ChannelDao(id, name)
            }
        }
    }

    var autoJoin: Boolean
        get() {
            return Atom.sql.first("SELECT auto_join FROM irc_channels WHERE id = ?", { it.getBoolean("auto_join") }, this.id)
                .orElse(false)
        }
        set(value) {
            Atom.sql.execute("UPDATE irc_channels SET auto_join = ? WHERE id = ?")
                .with(value, this.id)
                .execute()
        }

}