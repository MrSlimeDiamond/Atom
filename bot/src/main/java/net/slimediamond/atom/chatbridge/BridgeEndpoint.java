package net.slimediamond.atom.chatbridge;

public interface BridgeEndpoint {
    void sendMessage(BridgeMessage message, BridgeEndpoint source);
    void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment);
    void sendActionMessage(BridgeMessage message, BridgeEndpoint source);
    void sendReconnectMessage();
    String getName();
    String getShortName();
    String getType();
    String getChannelName();
    String getUniqueIdentifier();
    int getId();
}
