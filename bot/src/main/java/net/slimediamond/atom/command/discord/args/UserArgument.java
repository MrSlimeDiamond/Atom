package net.slimediamond.atom.command.discord.args;

import java.util.Optional;

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
        return (String)value;
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
}
