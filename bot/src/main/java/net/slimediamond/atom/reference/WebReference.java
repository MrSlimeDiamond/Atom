package net.slimediamond.atom.reference;

import net.slimediamond.atom.Atom;

import java.io.IOException;

public class WebReference {
    public static int PORT;

    static {
        try {
            PORT = Integer.parseInt(Atom.config.web().getProperty("port"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
