package net.slimediamond.atom.irc;

import jakarta.inject.Inject;
import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.CommandPlatform;
import net.slimediamond.atom.command.irc.IRCCommandListener;
import net.slimediamond.atom.reference.IRCReference;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.auth.NickServ;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

@Service(value = "irc", priority = 999, enabled = true)
public class IRC {
    public static Client client;

    @Inject
    private CommandManager commandManager;

    @Inject
    private Logger log;

    @GetService
    private Database database;
    
    @Service.Start
    public void startService() throws Exception {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);

        client = Client.builder()
                .nick(IRCReference.nickname)
                .user(IRCReference.username)
                .realName(IRCReference.realname)
//                .listeners().input(line -> log.debug(' ' + "[I] {}", line)).then()
//                .listeners().output(line -> log.debug(' ' + "[O] {}", line)).then()
                .server()
                .host(IRCReference.host)
                .port(IRCReference.port)
                .secure(IRCReference.ssl)
                .then().buildAndConnect();

        client.getAuthManager().addProtocol(NickServ.builder(client).account(IRCReference.nickServUsername).password(IRCReference.nickServPassword).build());
        database.joinAllIRCChannels();

//        CommandHandler commandHandler = new CommandHandler();
//        commandHandler.registerCommand(new BotCommands());
//        commandHandler.registerCommand(new BridgeCommand());
//        commandHandler.registerCommand(new ChannelCommand());
//        commandHandler.registerCommand(new MCOFirstseen());
//        commandHandler.registerCommand(new MCOLastseen());
//        commandHandler.registerCommand(new MCOPlaytime());
//        commandHandler.registerCommand(new RefreshLastseen());
//        commandHandler.registerCommand(new BansCommand());
//        commandHandler.registerCommand(new GoodnightCommand());
//        commandHandler.registerCommand(new RandomPlayerCommand());
//
//        client.getEventManager().registerEventListener(new IRCCommand());
//        client.getEventManager().registerEventListener(new MCOEventHandler());

        client.getEventManager().registerEventListener(new IRCCommandListener(commandManager));

        commandManager.register(new CommandBuilder()
                .addAliases("ping")
                .setDescription("replies with pong!")
                .setUsage("ping")
                .setCommandPlatform(CommandPlatform.IRC)
                .setExecutor(ctx -> ctx.reply("Pong!"))
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("parent")
                .setDescription("test command: parent command and subcommand")
                .setUsage("parent")
                .setCommandPlatform(CommandPlatform.IRC)
                .setExecutor(context -> context.reply("parent command. args: " + Arrays.toString(context.getArgs())))
                .addChild(new CommandBuilder()
                        .addAliases("child")
                        .setDescription("this is a child command of a parent")
                        .setUsage("parent child")
                        .setCommandPlatform(CommandPlatform.IRC)
                        .setExecutor(context -> context.reply("child command. args: " + Arrays.toString(context.getArgs())))
                        .build()
                )
                .build()
        );

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
