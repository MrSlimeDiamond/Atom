package net.slimediamond.atom.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.internal.JDAImpl;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.data.DatabaseV2;
import net.slimediamond.atom.discord.entities.AtomGuild;
import net.slimediamond.atom.discord.entities.Guild;

import java.util.ArrayList;
import java.util.List;

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
            Guild guild = database.getDAOManager(AtomGuild.class).orElseThrow().getAllManaged().stream()
                    .filter(g -> g.getIdLong() == id)
                    .findAny()
                    .orElseThrow();
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
