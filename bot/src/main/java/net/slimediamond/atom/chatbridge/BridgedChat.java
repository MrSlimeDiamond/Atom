package net.slimediamond.atom.chatbridge;

import java.util.ArrayList;

public class BridgedChat {
    private ArrayList<BridgeEndpoint> endpoints = new ArrayList<>();
    private boolean isEnabled;
    private int id;
    private String name;

    public BridgedChat(boolean isEnabled, int id, String name) {
        this.isEnabled = isEnabled;
        this.id = id;
        this.name = name;
    }

    public void addEndpoint(BridgeEndpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    public ArrayList<BridgeEndpoint> getEndpoints() {
        return this.endpoints;
    }

    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {
        if (!isEnabled) return;
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.sendMessage(message, source);
            }
        }
    }

    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {
        if (!isEnabled) return;
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.sendActionMessage(message, source);
            }
        }
    }

    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {
        if (!isEnabled) return;
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.sendUpdate(eventType, username, source, comment);
            }
        }
    }

    public void netsplitQuits(Netsplit netsplit, BridgeEndpoint source) {
        if (!isEnabled) return;
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.netsplitQuits(netsplit, source);
            }
        }
    }

    public void netsplitJoins(Netsplit netsplit, BridgeEndpoint source) {
        if (!isEnabled) return;
        for (BridgeEndpoint endpoint : endpoints) {
            if (endpoint != source) {
                endpoint.netsplitJoins(netsplit, source);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return id;
    }
}
