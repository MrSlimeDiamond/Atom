package net.zenoc.atom.services;

import net.engio.mbassy.listener.Handler;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.commands.IRCCommand;
import net.zenoc.atom.ircbot.CommandHandler;
import net.zenoc.atom.ircbot.commands.BridgeCommand;
import net.zenoc.atom.ircbot.commands.ChannelCommand;
import net.zenoc.atom.ircbot.commands.PingCommand;
import net.zenoc.atom.reference.IRCReference;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.auth.NickServ;

public class IRC implements Service {
    public static Client client;
    @Override
    public void startService() throws Exception {
        client = Client.builder()
                .nick(IRCReference.nickname)
                .user(IRCReference.username)
                .realName(IRCReference.realname)
                .server()
                .host(IRCReference.host)
                .port(IRCReference.port)
                .secure(IRCReference.ssl)
                .then().buildAndConnect();

        //client.getAuthManager().addProtocol(NickServ.builder(client).account(IRCReference.nickServUsername).password(IRCReference.nickServPassword).build());;
        Atom.database.joinAllIRCChannels();

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.registerCommand(new PingCommand());
        commandHandler.registerCommand(new BridgeCommand());
        commandHandler.registerCommand(new ChannelCommand());

        client.getEventManager().registerEventListener(new IRCCommand());

    }

    @Override
    public void shutdownService() {
        client.shutdown("Service stopped");
    }

    @Override
    public void reloadService() {
        client.reconnect("Service is reloading");
    }
}
