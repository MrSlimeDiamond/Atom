package net.slimediamond.atom.telegram;

import com.google.inject.Inject;
import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.telegram.TelegramMessageListener;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.reference.TelegramReference;
import net.slimediamond.atom.telegram.commands.DebugCommand;
import net.slimediamond.atom.telegram.commands.minecraftonline.*;
import net.slimediamond.telegram.TelegramClient;

@Service(value = "telegram", priority = 999)
public class Telegram {
    @Inject
    CommandManager commandManager;

    private static TelegramClient client;

    @Service.Start
    public void onStart() {
        client = new TelegramClient(TelegramReference.token);
        client.addListener(new TelegramMessageListener(commandManager));

        commandManager.register(new CommandBuilder()
                .addAliases("ping")
                .setDescription("replies with pong")
                .setUsage("ping")
                .telegram()
                .setExecutor(ctx -> ctx.reply("Pong!"))
                .then().build()
        );

        // MCO COMMANDS
        commandManager.register(new CommandBuilder()
                .addAliases("bans", "bancount")
                .setDescription("Get ban count for MinecraftOnline")
                .setUsage("bans")
                .telegram().setExecutor(new BansCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("firstseen", "firstjoin", "fs", "fj")
                .setDescription("Get the first join date of a MinecraftOnline player")
                .setUsage("firstseen [player]")
                .telegram()
                .setExecutor(new MCOFirstseen())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("lastseen", "lastjoin", "ls", "lj")
                .setDescription("Get the last join date of a MinecraftOnline player")
                .setUsage("lastseen [player]")
                .telegram()
                .setExecutor(new MCOLastseen())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("playtime", "timeplayed", "tp", "pt")
                .setDescription("Get the hour count of a MinecraftOnline player")
                .setUsage("playtime [player]")
                .telegram()
                .setExecutor(new MCOPlaytime())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("randomplayer", "rp")
                .setDescription("Get a random player who is currently online on MCO")
                .setUsage("randomplayer")
                .telegram()
                .setExecutor(new RandomPlayerCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("debug")
                .setDescription("Debug command")
                .setUsage("debug")
                .telegram().setExecutor(new DebugCommand())
                .then().build()
        );

//        commandManager.register(new CommandBuilder()
//                .addAliases("bridge")
//                .setDescription("Manage chat bridges")
//                .setUsage("bridge <create|delete|list>")
//                .telegram()
//                .setExecutor(CommandContext::sendUsage)
//                .then().addChild(new CommandBuilder()
//                        .addAliases("create")
//                        .setDescription("Create a chat bridge")
//                        .setUsage("bridge create [name]")
//                        .telegram()
//                        .setExecutor(new BridgeCreateCommand())
//                        .then().build()
//                )
//                .addChild(new CommandBuilder()
//                        .addAliases("delete", "remove", "nuke")
//                        .setDescription("Remove a chat bridge")
//                        .setUsage("bridge delete [name|id]")
//                        .telegram()
//                        .setExecutor(new BridgeDeleteCommand())
//                        .then().build()
//                )
//                .build()
//        );
//
//        commandManager.register(new CommandBuilder()
//                .addAliases("pipe")
//                .setDescription("Toggle bridge pipe to other endpoints")
//                .setUsage("pipe <on|off>")
//                .telegram()
//                .setExecutor(new EndpointPipeCommand())
//                .then().build()
//        );
    }

    public static TelegramClient getClient() {
        return client;
    }
}
