package net.slimediamond.atom.irc;

import jakarta.inject.Inject;
import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.irc.IRCCommandListener;
import net.slimediamond.atom.irc.commands.BridgeCommand;
import net.slimediamond.atom.irc.commands.ChannelCommand;
import net.slimediamond.atom.irc.commands.HelpCommand;
import net.slimediamond.atom.irc.commands.bridge.BridgeCreateCommand;
import net.slimediamond.atom.irc.commands.bridge.BridgeDeleteCommand;
import net.slimediamond.atom.irc.commands.endpoint.EndpointPipeCommand;
import net.slimediamond.atom.irc.commands.minecraftonline.*;
import net.slimediamond.atom.reference.IRCReference;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.reference.MCOReference;
import net.slimediamond.atom.services.system.GetServiceProcessor;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.feature.auth.NickServ;
import org.slf4j.Logger;

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

        IRCCommandListener ircCommandListener = new IRCCommandListener(commandManager);
        GetServiceProcessor.processAnnotations(ircCommandListener);
        client.getEventManager().registerEventListener(ircCommandListener);

        commandManager.register(new CommandBuilder()
                .addAliases("ping")
                .setDescription("replies with pong!")
                .setUsage("ping")
                .irc()
                .setExecutor(ctx -> ctx.reply("Pong!"))
                .then()
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("help")
                .setDescription("Help command, shows commands which are available")
                .setUsage("help")
                .irc()
                .setExecutor(new HelpCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("bans", "bancount")
                .setDescription("Get the amount of bans on MinecraftOnline")
                .setUsage("bans")
                .irc()
                .addWhitelistedChannels(MCOReference.whitelistedIRC)
                .setExecutor(new BansCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("goodnight", "gn")
                .setDescription("Say goodnight in an IRC channel")
                .setUsage("goodnight")
                .irc()
                .setExecutor(new GoodnightCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("firstseen", "firstjoin", "fs", "fj")
                .setDescription("Get the first join date of a MinecraftOnline player")
                .setUsage("firstseen [player]")
                .irc()
                .addWhitelistedChannels(MCOReference.whitelistedIRC)
                .setExecutor(new MCOFirstseen())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("lastseen", "lastjoin", "ls", "lj")
                .setDescription("Get the last join date of a MinecraftOnline player")
                .setUsage("lastseen [player]")
                .irc()
                .addWhitelistedChannels(MCOReference.whitelistedIRC)
                .setExecutor(new MCOLastseen())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("playtime", "timeplayed", "tp", "pt")
                .setDescription("Get the hour count of a MinecraftOnline player")
                .setUsage("playtime [player]")
                .irc()
                .addWhitelistedChannels(MCOReference.whitelistedIRC)
                .setExecutor(new MCOPlaytime())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("randomplayer", "rp")
                .setDescription("Get a random player who is currently online on MCO")
                .setUsage("randomplayer")
                .irc()
                .addWhitelistedChannels(MCOReference.whitelistedIRC)
                .setExecutor(new RandomPlayerCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("refreshlastseen")
                .setDescription("Refresh the lastseen date of a MinecraftOnline player")
                .setUsage("refreshlastseen [player]")
                .irc()
                .addWhitelistedChannels(MCOReference.whitelistedIRC)
                .setExecutor(new RefreshLastseen())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("bridge")
                .setDescription("Manage chat bridges")
                .setUsage("bridge <channel|pipe|blacklist>")
                .setAdminOnly(true)
                .irc()
                .setExecutor(new BridgeCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("channel")
                .setDescription("Manage IRC channels")
                .setUsage("channel <add|modify|join|part> <channel>")
                .setAdminOnly(true)
                .irc()
                .setExecutor(new ChannelCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("bridge")
                .setDescription("Manage chat bridges")
                .setUsage("bridge <create|delete|list>")
                .setAdminOnly(true)
                .irc()
                .setExecutor(CommandContext::sendUsage)
                .then().addChild(new CommandBuilder()
                        .addAliases("create")
                        .setDescription("Create a chat bridge")
                        .setUsage("bridge create [name]")
                        .irc()
                        .setExecutor(new BridgeCreateCommand())
                        .then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("delete", "remove", "nuke")
                        .setDescription("Remove a chat bridge")
                        .setUsage("bridge delete [name|id]")
                        .irc()
                        .setExecutor(new BridgeDeleteCommand())
                        .then().build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("pipe")
                .setDescription("Toggle bridge pipe to other endpoints")
                .setUsage("pipe <on|off>")
                .setAdminOnly(true)
                .irc()
                .setExecutor(new EndpointPipeCommand())
                .then().build()
        );

    }

    @Service.Shutdown
    public void shutdownService() {
        client.shutdown("Service stopped");
    }

    @Service.Reload
    public void reloadService() throws Exception {
        client.reconnect("Service reloading!");
        database.joinAllIRCChannels();
    }
}
