package net.slimediamond.atom.data.keys;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.slimediamond.atom.common.data.ResourceKey;
import net.slimediamond.data.Key;
import net.slimediamond.data.value.Value;

public final class GuildKeys {
    public static final Key<Value<TextChannel>> LOG_CHANNEL = Key.from(ResourceKey.atom("log_channel"), TextChannel.class);
    public static final Key<Value<TextChannel>> PINNERINO_CHANNEL = Key.from(ResourceKey.atom("pinnerino_channel"), TextChannel.class);
    public static final Key<Value<TextChannel>> STREAMS_CHANNEL = Key.from(ResourceKey.atom("streams_channel"), TextChannel.class);
    public static final Key<Value<GuildChannel>> MEMES_CHANNEL = Key.from(ResourceKey.atom("memes_channel"), GuildChannel.class);
    public static final Key<Value<String>> PINNERINO_EMOJI = Key.from(ResourceKey.atom("pinnerino_emoji"), String.class);
    public static final Key<Value<Integer>> PINNERINO_THRESHOLD = Key.from(ResourceKey.atom("pinnerino_threshold"), Integer.class);
}
