package net.slimediamond.atom.chatbridge;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Netsplit {
    private boolean active;
    private boolean rejoinsHappened;
    private BridgeEndpoint source;
    private String[] servers;

    private final ArrayList<String> quits = new ArrayList<>();
    private final ArrayList<String> joins = new ArrayList<>();

    public static final int NETSPLIT_WAIT_TIME = 10;

    public Netsplit(BridgeEndpoint source, String[] servers) {
        this.source = source;
        this.active = true;
        this.rejoinsHappened = false;
        this.servers = servers;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isRejoinsHappened() {
        return rejoinsHappened;
    }

    public BridgeEndpoint getSource() {
        return source;
    }

    public String[] getServers() {
        return servers;
    }

    public ArrayList<String> getQuits() {
        return quits;
    }

    public ArrayList<String> getJoins() {
        return joins;
    }

    public void setRejoinsHappened(boolean rejoinsHappened) {
        this.rejoinsHappened = rejoinsHappened;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addQuit(String username) {
        this.quits.add(username);
    }

    public void addJoin(String username) {
        this.joins.add(username);
    }
}
