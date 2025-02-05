package net.slimediamond.atom.chatbridge.mco;

import net.slimediamond.atom.chatbridge.BridgeEndpoint;
import net.slimediamond.atom.chatbridge.BridgeMessage;
import net.slimediamond.atom.chatbridge.EventType;
import net.slimediamond.atom.chatbridge.Netsplit;
import net.slimediamond.atom.chatbridge.irc.IRCBridgeEndpoint;
import net.slimediamond.atom.reference.EmbedReference;

public class MCOBridgeSource implements BridgeEndpoint {
    private IRCBridgeEndpoint irc;

    public MCOBridgeSource(IRCBridgeEndpoint irc) {
        this.irc = irc;
    }

    @Override
    public void sendMessage(BridgeMessage message, BridgeEndpoint source) {

    }

    @Override
    public void sendUpdate(EventType eventType, String username, BridgeEndpoint source, String comment) {

    }

    @Override
    public void sendActionMessage(BridgeMessage message, BridgeEndpoint source) {

    }

    @Override
    public void netsplitQuits(Netsplit netsplit, BridgeEndpoint source) {

    }

    @Override
    public void netsplitJoins(Netsplit netsplit, BridgeEndpoint source) {

    }

    @Override
    public String getAvatarUrl() {
        return EmbedReference.mcoIconLarge;
    }

    @Override
    public String getName() {
        return "Minecraft";
    }

    @Override
    public String getShortName() {
        return "MCS";
    }

    @Override
    public String getType() {
        return irc.getType();
    }

    @Override
    public String getChannelName() {
        // this is what will be shown in discord
        return "McObot";
    }

    @Override
    public String getUniqueIdentifier() {
        return irc.getUniqueIdentifier();
    }

    @Override
    public int getId() {
        return irc.getId();
    }

    @Override
    public boolean isEnabled() {
        return irc.isEnabled();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        irc.setEnabled(isEnabled);
    }

    public IRCBridgeEndpoint getIRC() {
        return this.irc;
    }
}
