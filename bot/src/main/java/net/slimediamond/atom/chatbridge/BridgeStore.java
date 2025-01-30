package net.slimediamond.atom.chatbridge;

import java.util.HashMap;

public class BridgeStore {
    private static HashMap<Integer, BridgedChat> chats = new HashMap<>();

    public static HashMap<Integer, BridgedChat> getChats() {
        return chats;
    }
}
