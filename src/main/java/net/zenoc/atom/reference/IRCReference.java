package net.zenoc.atom.reference;

import net.zenoc.atom.Atom;

import java.io.IOException;

public class IRCReference {
    public static String nickname;
    public static String username;
    public static String realname;
    public static String host;
    public static int port;
    public static boolean ssl;
    public static String nickServPassword;
    public static String nickServUsername;
    public static String prefix;
    public static final String defaultIcon = "https://cdn.discordapp.com/attachments/696218632618901507/1098213012059471994/irc1.png";

    static {
        try {
            nickname = Atom.config.irc().getProperty("nickname");
            username = Atom.config.irc().getProperty("username");
            realname = Atom.config.irc().getProperty("realname");
            host = Atom.config.irc().getProperty("host");
            port = Integer.parseInt(Atom.config.irc().getProperty("port"));
            ssl = Boolean.parseBoolean(Atom.config.irc().getProperty("ssl"));
            nickServPassword = Atom.config.irc().getProperty("nickServPassword");
            nickServUsername = Atom.config.irc().getProperty("nickServUsername");
            prefix = Atom.config.irc().getProperty("prefix");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
