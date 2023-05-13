package net.zenoc.atom.reference;

import net.zenoc.atom.Atom;

import java.io.IOException;

public class DBReference {
    public static String host;
    public static int port;
    public static String database;
    public static String user;
    public static String password;


    static {
        try {
            host = Atom.config.database().getProperty("host");
            port = Integer.parseInt(Atom.config.database().getProperty("port"));
            database = Atom.config.database().getProperty("database");
            user = Atom.config.database().getProperty("username");
            password = Atom.config.database().getProperty("password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
