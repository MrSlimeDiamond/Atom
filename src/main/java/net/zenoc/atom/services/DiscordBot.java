package net.zenoc.atom.services;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.zenoc.atom.Atom;
import net.zenoc.atom.annotations.GetService;
import net.zenoc.atom.annotations.Service;
import net.zenoc.atom.database.Database;
import net.zenoc.atom.discordbot.CommandHandler;
import net.zenoc.atom.discordbot.commands.*;
import net.zenoc.atom.discordbot.commands.minecraftonline.MCOCommands;
import net.zenoc.atom.reference.DiscordReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.SQLException;

@Service(value = "discord", priority = 999)
public class DiscordBot {
    private static final Logger log = LoggerFactory.getLogger(DiscordBot.class);

    @Inject
    private JDA jda;

    @GetService
    private Database database;

    @Service.Start
    public void startService() throws IOException, InterruptedException, SQLException {
        jda.awaitReady();

        CommandHandler commandHandler = new CommandHandler(jda, DiscordReference.prefix);

        commandHandler.registerCommand(new BotCommands());
        commandHandler.registerCommand(new InformationCommands());
        commandHandler.registerCommand(new LoggerCommand());
        commandHandler.registerCommand(new PinnerinoCommand());
        commandHandler.registerCommand(new IRCCommand());
        commandHandler.registerCommand(new BridgeCommand());
        commandHandler.registerCommand(new PortalCommands());
        commandHandler.registerCommand(new MCOCommands());
        commandHandler.registerCommand(new ReactionRolesCommand());
        commandHandler.registerCommand(new StreamsCommand());
        commandHandler.registerCommand(new MemesCommand());

        jda.addEventListener(commandHandler);

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
}
