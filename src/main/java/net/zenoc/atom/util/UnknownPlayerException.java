package net.zenoc.atom.util;

public class UnknownPlayerException extends Exception {
    public UnknownPlayerException(String msg) {
        super(msg);
    }

    public UnknownPlayerException(MCOPlayer player) {
        super(player.username + " is not a MinecraftOnline player");
    }
}
