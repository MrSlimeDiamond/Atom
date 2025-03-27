package net.slimediamond.atom.data.keys;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.slimediamond.atom.common.data.ResourceKey;
import net.slimediamond.data.Key;

public final class GuildKeys {
    public static final Key<TextChannel> LOG_CHANNEL = Key.of(ResourceKey.atom("log_channel"), TextChannel.class);
    public static final Key<TextChannel> PINNERINO_CHANNEL = Key.of(ResourceKey.atom("pinnerino_channel"), TextChannel.class);
    public static final Key<TextChannel> STREAMS_CHANNEL = Key.of(ResourceKey.atom("streams_channel"), TextChannel.class);
    public static final Key<TextChannel> MEMES_CHANNEL = Key.of(ResourceKey.atom("memes_channel"), TextChannel.class);
    public static final Key<String> PINNERINO_EMOJI = Key.of(ResourceKey.atom("pinnerino_emoji"), String.class);
    public static final Key<Integer> PINNERINO_THRESHOLD = Key.of(ResourceKey.atom("pinnerino_threshold"), Integer.class);
}
