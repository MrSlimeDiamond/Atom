package net.slimediamond.atom.reference;

import net.slimediamond.atom.Atom;

import java.io.IOException;

public class TelegramReference {
    public static String token;
    public static String prefix;

    static {
        try {
            token = Atom.config.telegram().getProperty("token");
            prefix = Atom.config.telegram().getProperty("prefix");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
