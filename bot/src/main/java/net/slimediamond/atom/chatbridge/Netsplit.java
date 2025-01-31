package net.slimediamond.atom.chatbridge;

import java.util.ArrayList;

public class Netsplit {
    private BridgeEndpoint source;
    private String[] servers;

    private final ArrayList<String> quits = new ArrayList<>();
    private final ArrayList<String> joins = new ArrayList<>();

    public static final int NETSPLIT_WAIT_TIME = 10;

    public Netsplit(BridgeEndpoint source, String[] servers) {
        this.source = source;
        this.servers = servers;
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

    public void addQuit(String username) {
        this.quits.add(username);
    }

    public void addJoin(String username) {
        this.joins.add(username);
    }
}
