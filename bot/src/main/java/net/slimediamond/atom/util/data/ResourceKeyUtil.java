package net.slimediamond.atom.util.data;

import net.slimediamond.data.identification.ResourceKey;

public class ResourceKeyUtil {
    public static ResourceKey from(String resourceKey) {
        if (!resourceKey.contains(":")) {
            throw new IllegalArgumentException("Resource key must follow the format: 'namespace:id'");
        }
        String[] parts = resourceKey.split(":");
        return ResourceKey.of(parts[0], parts[1]);
    }
}
