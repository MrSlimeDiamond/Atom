package net.slimediamond.atom.command.discord.args;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.command.discord.AtomDiscordCommandEvent;

public class UserArgument {
    private Object value;
    private DiscordArgumentMetadata metadata;

    public UserArgument(Object value, DiscordArgumentMetadata metadata) {
        this.value = value;
        this.metadata = metadata;
    }

    public DiscordArgumentMetadata getMetadata() {
        return this.metadata;
    }

    public String getAsString() {
        return String.valueOf(value);
    }

    public int getAsInt() {
        return (int)value;
    }

    public double getAsDouble() {
        return (double)value;
    }

    public boolean getAsBoolean() {
        return (boolean)value;
    }

    public GuildChannel getAsChannel() {
        return (GuildChannel)value;
    }
}
