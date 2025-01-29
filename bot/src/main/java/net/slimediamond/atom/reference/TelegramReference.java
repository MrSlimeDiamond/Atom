package net.slimediamond.atom.reference;

import net.slimediamond.atom.Atom;

import java.io.IOException;

public class TelegramReference {
    public static String token;

    static {
        try {
            token = Atom.config.telegram().getProperty("token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
