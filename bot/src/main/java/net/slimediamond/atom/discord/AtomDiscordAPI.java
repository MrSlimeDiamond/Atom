package net.slimediamond.atom.discord;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.internal.JDAImpl;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.data.DatabaseV2;
import net.slimediamond.atom.data.JsonKeys;
import net.slimediamond.atom.discord.entities.AtomGuild;
import net.slimediamond.atom.discord.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AtomDiscordAPI implements DiscordAPI {
    private final JDAImpl jda;
    private final List<Guild> guilds = new ArrayList<>();
    private final DatabaseV2 database = Atom.getServiceManager().getInstance(DatabaseV2.class);

    public AtomDiscordAPI(JDAImpl jda) {
        this.jda = jda;

        // Add guilds to this api instance
        jda.getGuilds().forEach(guild ->
                guilds.add(getGuildById(guild.getIdLong())));
    }

    @Override
    public Guild getGuildById(long id) {
        System.out.println(guilds);
        if (guilds.stream().map(Guild::getIdLong).anyMatch(gid -> gid.equals(id))) {
            // return an existing guild object
            return guilds.stream().filter(guild -> guild.getIdLong() == id).findAny().orElseThrow();
        } else {
            Guild guild;
            Optional<AtomGuild> existingGuild = database.getDAOManager(AtomGuild.class).orElseThrow().getAllManaged().stream()
                    .filter(g -> g.getIdLong() == id)
                    .findAny();

            if (existingGuild.isPresent()) {
                guild = existingGuild.get();
            } else {
                // load a guild from the db, if we can!
                try (PreparedStatement ps = database.getConn().prepareStatement("SELECT * FROM disc_guilds WHERE discord_id = ?")) {
                    ps.setLong(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        // Yay, we have a guild. Let's construct it
                        int idKey = rs.getInt("id");
                        guild = new AtomGuild(idKey, id, database.getConn(), jda.getGuildById(id));
                        String json = rs.getString("keys_storage");
                        JsonKeys.read(json).forEach(guild::offerRaw);
                    } else {
                        // As a last resort, make one and commit it to the database
                        AtomGuild atomGuild = new AtomGuild(id, database.getConn(), jda.getGuildById(id));
                        atomGuild.save();
                        guild = atomGuild;
                    }
                } catch (SQLException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }
            guilds.add(guild);
            return guild;
        }
    }

    @Override
    public long getGatewayPing() {
        return jda.getGatewayPing();
    }

    @Override
    public List<Guild> getGuilds() {
        return guilds;
    }

    @Override
    public JDA getJDA() {
        return jda;
    }
}
