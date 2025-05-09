package net.slimediamond.atom.data.keys;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.slimediamond.atom.common.data.ResourceKey;
import net.slimediamond.data.Key;
import net.slimediamond.data.value.Value;

public final class GuildKeys {
    public static final Key<Value<TextChannel>> LOG_CHANNEL = Key.from(ResourceKey.atom("log_channel"), TextChannel.class);
    public static final Key<Value<TextChannel>> PINNERINO_CHANNEL = Key.from(ResourceKey.atom("pinnerino_channel"), TextChannel.class);
    public static final Key<Value<TextChannel>> STREAMS_CHANNEL = Key.from(ResourceKey.atom("streams_channel"), TextChannel.class);
    public static final Key<Value<TextChannel>> MEMES_CHANNEL = Key.from(ResourceKey.atom("memes_channel"), TextChannel.class);
    public static final Key<Value<EmojiUnion>> PINNERINO_EMOJI = Key.from(ResourceKey.atom("pinnerino_emoji"), EmojiUnion.class);
    public static final Key<Value<Integer>> PINNERINO_THRESHOLD = Key.from(ResourceKey.atom("pinnerino_threshold"), Integer.class);
}
