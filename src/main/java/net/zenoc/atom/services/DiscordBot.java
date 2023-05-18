package net.zenoc.atom.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CommandHandler;
import net.zenoc.atom.discordbot.commands.*;
import net.zenoc.atom.discordbot.commands.minecraftonline.MCOCommands;
import net.zenoc.atom.reference.DiscordReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DiscordBot implements Service {
    private static final Logger log = LoggerFactory.getLogger(DiscordBot.class);
    public static JDA jda;
    @Override
    public void startService() throws IOException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(DiscordReference.token);
        builder.enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
        );
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);

        jda = builder.build();
        jda.awaitReady();

        CommandHandler commandHandler = new CommandHandler(jda, DiscordReference.prefix);

        commandHandler.registerCommand(new BotCommands());
        commandHandler.registerCommand(new TestCommands());
        commandHandler.registerCommand(new InformationCommands());
        commandHandler.registerCommand(new LoggerCommand());
        commandHandler.registerCommand(new PinnerinoCommand());
        commandHandler.registerCommand(new IRCCommand());
        commandHandler.registerCommand(new BridgeCommand());
        commandHandler.registerCommand(new PortalCommands());
        commandHandler.registerCommand(new MCOCommands());
        commandHandler.registerCommand(new ReactionRolesCommand());

        jda.addEventListener(commandHandler);

        jda.getGuilds().forEach(guild -> {
            if (!Atom.database.isGuildInDatabase(guild)) {
                log.warn("Guild with name " + guild.getName() + " is not in the database!!!");
            }
        });
    }

    @Override
    public void reloadService() throws IOException, InterruptedException {
        log.info("Reloading...");
        this.shutdownService();
        this.startService();
    }

    @Override
    public void shutdownService() {
        log.info("Shutting down bot...");
        jda.shutdownNow();
    }
}
