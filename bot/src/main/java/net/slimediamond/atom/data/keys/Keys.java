package net.slimediamond.atom.data.keys;

import net.slimediamond.atom.common.data.ResourceKey;
import net.slimediamond.data.Key;

public final class Keys {
    public static final Key<String> TEST = Key.of(ResourceKey.atom("test"), String.class);
}
