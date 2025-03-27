package net.slimediamond.atom.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.slimediamond.atom.discord.DiscordAPI;
import net.slimediamond.atom.discord.entities.Guild;

import java.util.ArrayList;
import java.util.List;

public class AtomDiscordAPI implements DiscordAPI {
    private final JDAImpl jda;
    private final List<Guild> guilds = new ArrayList<>();

    public AtomDiscordAPI(JDAImpl jda) {
        this.jda = jda;

        // Add guilds to this api instance
        jda.getGuilds().forEach(guild ->
                guilds.add(getGuildById(guild.getIdLong())));
    }

    @Override
    public Guild getGuildById(long id) {
        if (guilds.stream().map(GuildImpl::getIdLong).anyMatch(gid -> gid.equals(id))) {
            // return an existing guild object
            return guilds.stream().filter(guild -> guild.getIdLong() == id).findAny().orElseThrow();
        } else {
            Guild guild = new Guild(jda, id);
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
