package net.slimediamond.atom.chatbridge;

import java.util.HashMap;

public class BridgeStore {
    private static HashMap<Integer, BridgedChat> chats = new HashMap<>();

    public static HashMap<Integer, BridgedChat> getChats() {
        return chats;
    }

    public static BridgeEndpoint getEndpointByIdentifier(BridgedChat chat, String identifier) {
        return chat.getEndpoints().stream()
                .filter(endpoint -> identifier.equals(endpoint.getUniqueIdentifier()))
                .findFirst() // Returns an Optional<BridgeEndpoint>
                .orElseThrow(() -> new RuntimeException("No matching endpoint found for identifier: " + identifier));
    }
}
