package net.slimediamond.atom.irc;

import net.slimediamond.atom.irc.commands.minecraftonline.*;
import net.slimediamond.atom.reference.IRCReference;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.commands.IRCCommand;
import net.slimediamond.atom.irc.commands.minecraftonline.GoodnightCommand;
import net.slimediamond.atom.irc.commands.BridgeCommand;
import net.slimediamond.atom.irc.commands.ChannelCommand;
import net.slimediamond.atom.irc.commands.PingCommand;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.auth.NickServ;

import java.sql.SQLException;

@Service(value = "irc", priority = 999)
public class IRC {
    public static Client client;

    @GetService
    private Database database;
    
    @Service.Start
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

        client.getAuthManager().addProtocol(NickServ.builder(client).account(IRCReference.nickServUsername).password(IRCReference.nickServPassword).build());
        database.joinAllIRCChannels();

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.registerCommand(new PingCommand());
        commandHandler.registerCommand(new BridgeCommand());
        commandHandler.registerCommand(new ChannelCommand());
        commandHandler.registerCommand(new MCOFirstseen());
        commandHandler.registerCommand(new MCOLastseen());
        commandHandler.registerCommand(new MCOPlaytime());
        commandHandler.registerCommand(new RefreshLastseen());
        commandHandler.registerCommand(new BansCommand());
        commandHandler.registerCommand(new GoodnightCommand());

        client.getEventManager().registerEventListener(new IRCCommand());
        client.getEventManager().registerEventListener(new MCOEventHandler());

    }

    @Service.Shutdown
    public void shutdownService() {
        client.shutdown("Service stopped");
    }

    @Service.Reload
    public void reloadService() throws SQLException {
        client.reconnect("Service reloading!");
        database.joinAllIRCChannels();
    }
}
