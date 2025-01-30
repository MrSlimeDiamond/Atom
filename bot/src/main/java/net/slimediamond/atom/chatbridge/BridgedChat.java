package net.slimediamond.atom.chatbridge;

import java.util.ArrayList;

public class BridgedChat {
    private ArrayList<BridgeEndpoint> endpoints = new ArrayList<>();

    public void addEndpoint(BridgeEndpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    public ArrayList<BridgeEndpoint> getEndpoints() {
        return this.endpoints;
    }

    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.sendMessage(message, source);
            }
        }
    }

    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.sendActionMessage(message, source);
            }
        }
    }

    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.sendUpdate(eventType, username, source, comment);
            }
        }
    }
}
