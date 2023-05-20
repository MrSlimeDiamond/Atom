package net.zenoc.atom.reference;

import net.zenoc.atom.Atom;

import java.io.IOException;

public class TwitchReference {
    public static String API_TOKEN;

    static {
        try {
            API_TOKEN = Atom.config.twitch().getProperty("token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
