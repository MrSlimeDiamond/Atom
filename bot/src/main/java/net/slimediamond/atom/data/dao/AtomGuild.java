package net.slimediamond.atom.data.dao;

import net.slimediamond.atom.data.JsonKeys;
import net.slimediamond.atom.discord.entities.Guild;
import net.slimediamond.data.DataHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An Atom guild.
 *
 * <p>Extending DataHolder, for storing
 * {@link net.slimediamond.data.Key}</p>
 */
public class AtomGuild implements Guild, DataHolder, DAO {
    private int id = -1;
    private final long discordId;
    private final Connection conn;
    private net.dv8tion.jda.api.entities.Guild jdaGuild;

    public AtomGuild(long discordId, Connection conn, net.dv8tion.jda.api.entities.Guild jdaGuild) {
        this.discordId = discordId;
        this.conn = conn;
        this.jdaGuild = jdaGuild;
    }

    public AtomGuild(int primaryKey, long id, Connection conn, net.dv8tion.jda.api.entities.Guild jdaGuild) {
        this(id, conn, jdaGuild);
        this.id = primaryKey;
    }

    public long getDiscordId() {
        return discordId;
    }

    @Override
    public void save() throws SQLException {
        // Table structure:
        // CREATE TABLE IF NOT EXISTS guilds (id int NOT NULL AUTO_INCREMENT, discordId BIGINT NOT NULL, keys JSON NOT NULL, PRIMARY KEY(id))
        // Doesn't yet exist, insert it
        if (id == -1) {
            // Do we even have a table?!
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS guilds (id int NOT NULL AUTO_INCREMENT," +
                    "discord_id BIGINT NOT NULL," +
                    "keys JSON NOT NULL," +
                    "PRIMARY KEY(id))").execute();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO guilds (discord_id, keys) VALUES (?, ?)");
            ps.setLong(1, discordId);
            ps.setString(2, JsonKeys.json(this));
            ResultSet rs = ps.executeQuery();
            this.id = rs.getInt("id");
        } else { // Update it
            PreparedStatement ps = conn.prepareStatement("UPDATE GUILDS SET keys = ? WHERE id = ?");
            ps.setString(1, JsonKeys.json(this));
            ps.setInt(2, this.id);
            ps.execute();
        }
    }


}
