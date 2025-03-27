package net.slimediamond.atom.common.data;

/**
 * Atom resource key wrapper
 */
public final class ResourceKey {
    public static net.slimediamond.data.identification.ResourceKey atom(String id) {
        return net.slimediamond.data.identification.ResourceKey.of("atom", id);
    }
}
