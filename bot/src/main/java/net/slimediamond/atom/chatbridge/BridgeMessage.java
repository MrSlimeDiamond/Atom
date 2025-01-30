package net.slimediamond.atom.chatbridge;

import org.checkerframework.checker.nullness.qual.Nullable;

public record BridgeMessage(String username, @Nullable String avatarUrl, String content) { }
