package net.slimediamond.atom.data.keys;

import net.slimediamond.atom.common.data.ResourceKey;
import net.slimediamond.data.Key;
import net.slimediamond.data.value.Value;

public final class Keys {
    public static final Key<Value<String>> TEST = Key.from(ResourceKey.atom("test"), String.class);
}
