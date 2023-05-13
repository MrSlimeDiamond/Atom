package net.zenoc.atom.reference;

import net.zenoc.atom.Atom;

import java.io.IOException;

public class DiscordReference {
    public static String prefix;
    public static String token;

    static {
        try {
            prefix = Atom.config.discord().getProperty("prefix");
            token = Atom.config.discord().getProperty("token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
