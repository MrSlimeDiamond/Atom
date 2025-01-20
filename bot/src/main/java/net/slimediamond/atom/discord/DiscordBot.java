package net.slimediamond.atom.discord;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.discord.DiscordCommandListener;
import net.slimediamond.atom.command.discord.args.DiscordArgsBuilder;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

@Service(value = "discord", priority = 999, enabled = true)
public class DiscordBot {
    private static final Logger log = LoggerFactory.getLogger(DiscordBot.class);

    @Inject
    private JDA jda;

    @Inject
    private CommandManager commandManager;

    @GetService
    private Database database;

    @Service.Start
    public void startService() throws IOException, InterruptedException, SQLException {
        jda.awaitReady();

//        CommandHandler commandHandler = new CommandHandler(jda, DiscordReference.prefix);

//        if (AmplicityTimeplayed.PLAYERDATA_FILE.exists()) {
//            commandHandler.registerCommand(new AmplicityTimeplayed());
//        }

//        commandHandler.registerCommand(new BotCommands());
//        commandHandler.registerCommand(new InformationCommands());
//        commandHandler.registerCommand(new LoggerCommand());
//        commandHandler.registerCommand(new PinnerinoCommand());
//        commandHandler.registerCommand(new IRCCommand());
//        commandHandler.registerCommand(new BridgeCommand());
//        commandHandler.registerCommand(new PortalCommands());
//        commandHandler.registerCommand(new MCOCommands());
//        commandHandler.registerCommand(new ReactionRolesCommand());
//        commandHandler.registerCommand(new StreamsCommand());
//        commandHandler.registerCommand(new MemesCommand());

//        jda.addEventListener(commandHandler);

        DiscordCommandListener commandListener = new DiscordCommandListener(commandManager);
        jda.addEventListener(commandListener);

        commandManager.register(new CommandBuilder()
                .addAliases("ping")
                .setDescription("Replies with pong")
                .setUsage("ping")
                .discord()
                .setExecutor(ctx -> ctx.reply("Pong!"))
                .then()
                .build()
        );

        // Mostly a debug command
        commandManager.register(new CommandBuilder()
                .addAliases("parent")
                .setDescription("test command: parent command and subcommand")
                .setUsage("parent")
                .discord()
                .setExecutor(context -> context.reply("parent command. args: " + Arrays.toString(context.getArgs())))
                .then()
                .addChild(new CommandBuilder()
                        .addAliases("child")
                        .setDescription("this is a child command of a parent")
                        .setUsage("parent child")
                        .discord()
                        .setExecutor(context -> context.reply("child command. args: " + Arrays.toString(context.getArgs())))
                        .then()
                        .build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("test")
                .setDescription("spit out a string you put in")
                .setUsage("test <string>")
                .discord()
                .setExecutor(ctx -> {
                    String output = ctx.getArguments().get(0).getAsString();
                    ctx.reply(output);
                })
                .addArgument(new DiscordArgsBuilder()
                        .addAliases("string")
                        .setId(0)
                        .setDescription("The string you want to have spit out")
                        .setOptionType(OptionType.STRING)
                        .setRequired(true)
                        .build()
                )
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("commands", "cmds")
                .setDescription("Command management")
                .setUsage("commands reload")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> {
                    commandManager.refreshDiscordSlashCommands(jda);
                    ctx.reply("Reloaded slash commands!");
                }).then().build()
        );

        // TODO: Automatically add guilds to the database
        jda.getGuilds().forEach(guild -> {
            if (!database.isGuildInDatabase(guild)) {
                log.warn("Guild with name " + guild.getName() + " is not in the database!!!");
            }
        });
    }

    @Service.Reload
    public void reloadService() throws IOException, InterruptedException, SQLException {
        log.info("Reloading...");
        this.shutdownService();
        this.startService();
    }

    @Service.Shutdown
    public void shutdownService() {
        log.info("Shutting down bot...");
        jda.shutdownNow();
    }

    // fuck DI
    public JDA getJDA() {
        return jda;
    }
}
