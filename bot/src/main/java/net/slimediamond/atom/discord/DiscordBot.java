package net.slimediamond.atom.discord;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.discord.DiscordCommandListener;
import net.slimediamond.atom.command.discord.args.DiscordArgsBuilder;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.commands.*;
import net.slimediamond.atom.discord.commands.amplicity.AmplicityTimeplayed;
import net.slimediamond.atom.discord.commands.bridge.BridgePipeCommand;
import net.slimediamond.atom.discord.commands.bridge.BridgeSetCommand;
import net.slimediamond.atom.discord.commands.info.InfoBotCommand;
import net.slimediamond.atom.discord.commands.info.InfoHostCommand;
import net.slimediamond.atom.discord.commands.irc.IRCNamesCommand;
import net.slimediamond.atom.discord.commands.irc.IRCWhoisCommand;
import net.slimediamond.atom.discord.commands.logger.LoggerChannelSetCommand;
import net.slimediamond.atom.discord.commands.logger.LoggerChannelUnsetCommand;
import net.slimediamond.atom.discord.commands.minecraftonline.*;
import net.slimediamond.atom.discord.commands.pinnerino.PinnerinoBlacklistCommand;
import net.slimediamond.atom.discord.commands.pinnerino.PinnerinoChannelCommand;
import net.slimediamond.atom.discord.commands.pinnerino.PinnerinoEmojiCommand;
import net.slimediamond.atom.discord.commands.pinnerino.PinnerinoThresholdCommand;
import net.slimediamond.atom.discord.commands.streams.StreamsAddCommand;
import net.slimediamond.atom.discord.commands.streams.StreamsChannelCommand;
import net.slimediamond.atom.discord.commands.streams.StreamsRemoveCommand;
import net.slimediamond.atom.irc.IRC;
import net.slimediamond.atom.reference.MCOReference;
import net.slimediamond.atom.util.EmbedUtil;
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
                .setExecutor(new PingCommand())
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
                .addChild(new CommandBuilder()
                        .addAliases("child2")
                        .setDescription("this is a child command of a parent")
                        .setUsage("parent child2")
                        .discord()
                        .setExecutor(context -> context.reply("child2 command. args: " + Arrays.toString(context.getArgs())))
                        .then()
                        .build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("info")
                .setDescription("Get information about the bot or its host system")
                .setUsage("info <host|bot>")
                .discord()
                .setExecutor(new InfoBotCommand())
                .then().addChild(new CommandBuilder()
                        .addAliases("bot")
                        .setDescription("Information about the bot")
                        .setUsage("info bot")
                        .discord().setExecutor(new InfoBotCommand())
                        .then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("host")
                        .setDescription("Information about the host system")
                        .setUsage("info host")
                        .discord().setExecutor(new InfoHostCommand())
                        .then().build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("bridge")
                .setDescription("Manage chat bridges")
                .setUsage("bridge <set|unset|pipe>")
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> ctx.reply("Usage: " + ctx.getCommandMetadata().getCommandUsage()))
                .then().addChild(new CommandBuilder()
                        .addAliases("pipe")
                        .setDescription("Manage the bridge pipe")
                        .setUsage("bridge pipe <true|false|on|off>")
                        .discord()
                        .setExecutor(new BridgePipeCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setOptionType(OptionType.BOOLEAN)
                                .setName("status")
                                .setDescription("Bridge pipe status")
                                .setId(0)
                                .setRequired(true)
                                .build()
                        ).then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("set")
                        .setDescription("Set the channel to bridge to")
                        .setUsage("bridge set <channel>")
                        .discord()
                        .setExecutor(new BridgeSetCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setOptionType(OptionType.STRING)
                                .setName("channel")
                                .setId(0)
                                .setDescription("The channel to bridge to")
                                .setRequired(true)
                                .build()
                        ).then().build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("irc")
                .setDescription("Commands for IRC")
                .setUsage("irc <names|whois>")
                .discord()
                .setExecutor(ctx -> ctx.reply("Usage: " + ctx.getCommandMetadata().getCommandUsage()))
                .then()
                .addChild(new CommandBuilder()
                        .addAliases("names", "list")
                        .setDescription("Get a list of people in the bridged channel")
                        .setUsage("irc names")
                        .discord()
                        .setExecutor(new IRCNamesCommand())
                        .then()
                        .build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("whois")
                        .setDescription("Get information about a user on IRC")
                        .setUsage("irc whois <user>")
                        .discord()
                        .setExecutor(new IRCWhoisCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setName("user")
                                .setId(0)
                                .setDescription("The user to WHOIS")
                                .setOptionType(OptionType.STRING)
                                .setRequired(true)
                                .build()
                        )
                        .then()
                        .build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("restart")
                        .setDescription("Restarts the IRC bot")
                        .setUsage("irc restart")
                        .setAdminOnly(true)
                        .discord()
                        .setSlashCommand(false)
                        .setExecutor(ctx -> {
                            ctx.deferReply();
                            IRC irc = Atom.getServiceManager().getInstance(IRC.class);
                            try {
                                irc.reloadService();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            ctx.replyEmbeds(EmbedUtil.genericSuccessEmbed("Reloaded IRC service"));
                        })
                        .then()
                        .build()
                )
                .build()
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

        commandManager.register(new CommandBuilder()
                .addAliases("addguild", "unfuck_database")
                .setDescription("Add a guild to the database")
                .setUsage("addguild")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> {
                    try {
                        database.addGuild(ctx.getGuild().getIdLong());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    ctx.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added guild to database"));
                })
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("stop", "fuckoff")
                .setDescription("Stop the discord bot")
                .setUsage("stop")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> {
                    ctx.replyEmbeds(EmbedUtil.genericTextEmbed("Stopping bot..."));
                    try {
                        Atom.shutdown(DiscordBot.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("logger_channel", "log")
                .setDescription("Set the logger channel in a server")
                .setUsage("logger_channel <set|unset> [<channel>]")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> ctx.reply("Usage: " + ctx.getCommandMetadata().getCommandUsage()))
                .then().addChild(new CommandBuilder()
                        .addAliases("set")
                        .setDescription("Set the channel")
                        .setUsage("logger_channel set <channel>")
                        .discord()
                        .setExecutor(new LoggerChannelSetCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setName("channel")
                                .setDescription("The channel to send logs to")
                                .setOptionType(OptionType.CHANNEL)
                                .setRequired(true)
                                .setId(0)
                                .build()
                        ).then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("unset")
                        .setDescription("Disable the logger")
                        .setUsage("logger_channel unset")
                        .discord()
                        .setExecutor(new LoggerChannelUnsetCommand())
                        .then().build()
                ).build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("pinnerino", "pins")
                .setDescription("Pinnerino service management")
                .setUsage("pinnerino <blacklist|channel|emoji>")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> ctx.reply("Usage: " + ctx.getCommandMetadata().getCommandUsage()))
                .then()
                .addChild(new CommandBuilder()
                        .addAliases("blacklist")
                        .setDescription("Manage the pinnerino blacklist")
                        .setUsage("pinnerino blacklist <add|remove>")
                        .discord()
                        .setExecutor(new PinnerinoBlacklistCommand())
                        .then()
                        .build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("channel")
                        .setDescription("Set (or unset) the channel for pins")
                        .setUsage("pinnerino channel <set|unset>")
                        .discord()
                        .setExecutor(new PinnerinoChannelCommand())
                        .then()
                        .build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("emoji")
                        .setDescription("Set the pinnerino emoji")
                        .setUsage("pinnerino emoji set <emoji>")
                        .discord()
                        .setExecutor(new PinnerinoEmojiCommand())
                        .then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("threshold")
                        .setDescription("Set the pinnerino threshold")
                        .setUsage("pinnerino threshold set <threshold>")
                        .discord()
                        .setExecutor(new PinnerinoThresholdCommand())
                        .then()
                        .build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("memes", "memevoting")
                .setDescription("Manage memes voting service")
                .setUsage("memes channel <set|unset> [channel]")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> ctx.reply("Usage: " + ctx.getCommandMetadata().getCommandUsage()))
                .then()
                .addChild(new CommandBuilder()
                        .addAliases("channel")
                        .setDescription("Set (or unset) the meme voting channel")
                        .setUsage("memes channel <set|unset> [channel]")
                        .discord()
                        .setExecutor(new MemesCommand())
                        .then()
                        .build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("portal2")
                .setDescription("Portal 2 speedrunning commands")
                .setUsage("portal2 cm [username]")
                .discord()
                .addWhitelistedGuilds(696218632618901504L, 1004897099017637979L)
                .setExecutor(ctx -> ctx.reply("Usage: " + ctx.getCommandMetadata().getCommandUsage()))
                .then()
                .addChild(new CommandBuilder()
                        .addAliases("cm")
                        .setDescription("Show CM individual level stats for a user")
                        .setUsage("portal2 cm <user>")
                        .discord()
                        .setExecutor(new Portal2BoardCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setName("username")
                                .setId(0)
                                .setDescription("Username of the player to lookup")
                                .setOptionType(OptionType.STRING)
                                .setRequired(false)
                                .build()
                        )
                        .then()
                        .build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("reactionroles", "rr")
                .setDescription("Manage reaction roles")
                .setUsage("reactionroles <add|remove|modify>")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(new ReactionRolesCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("streams")
                .setDescription("Manage the stream listener service")
                .setAdminOnly(true)
                .setUsage("streams <channel/add/remove>")
                .discord()
                .setSlashCommand(false)
                .setExecutor(ctx -> ctx.reply("Usage: " + ctx.getCommandMetadata().getCommandUsage()))
                .then()
                .addChild(new CommandBuilder()
                        .addAliases("channel")
                        .setDescription("Set/unset streams channel")
                        .setUsage("channel <set/unset>")
                        .discord()
                        .setExecutor(new StreamsChannelCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setName("channel")
                                .setId(0)
                                .setDescription("The channel to use")
                                .setOptionType(OptionType.CHANNEL)
                                .setRequired(true)
                                .build()
                        )
                        .then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("add")
                        .setDescription("Add a user to the streams notifier")
                        .setUsage("streams add <login>")
                        .discord()
                        .setExecutor(new StreamsAddCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setName("login")
                                .setId(0)
                                .setDescription("The login of the user (.tv/<this>)")
                                .setOptionType(OptionType.STRING)
                                .setRequired(true)
                                .build()
                        )
                        .then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("remove")
                        .setDescription("Remove a user from the streams notifier")
                        .setUsage("streams remove <login>")
                        .discord()
                        .setExecutor(new StreamsRemoveCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setName("login")
                                .setId(0)
                                .setDescription("The login of the user (.tv/<this>)")
                                .setOptionType(OptionType.STRING)
                                .setRequired(true)
                                .build()
                        )
                        .then().build()
                )
                .build()
        );


        commandManager.register(new CommandBuilder()
                .addAliases("timeplayed", "playtime", "pt", "tp")
                .setDescription("Get a player's hour count on Amplicity")
                .setUsage("timeplayed <player>")
                .discord()
                .addWhitelistedGuilds(1048920042655449138L)
                .setExecutor(new AmplicityTimeplayed())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to check")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("help")
                .setDescription("Show commands and what they do")
                .setUsage("help")
                .discord()
                .setExecutor(new HelpCommand())
                .then().build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("whoami")
                .setDescription("Debug command to check who the bot thinks you are")
                .setUsage("whoami")
                .discord()
                .setExecutor(new WhoamiCommand())
                .then().build()
        );

        // Define MCO commands
        CommandBuilder firstseen = new CommandBuilder()
                .addAliases("firstseen", "fs", "fj")
                .setDescription("Get first join date of a user")
                .setUsage("mco firstseen [username]")
                .discord()
                .setExecutor(new FirstseenCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then();

        CommandBuilder lastseen = new CommandBuilder()
                .addAliases("lastseen", "ls", "lj")
                .setDescription("Get last join date of a user")
                .setUsage("mco lastseen [username]")
                .discord()
                .setExecutor(new LastseenCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then();

        CommandBuilder playtime = new CommandBuilder()
                .addAliases("playtime", "pt", "tp", "timeplayed")
                .setDescription("Get hours played of a user")
                .setUsage("mco playtime [username]")
                .discord()
                .setExecutor(new PlaytimeCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then();

        CommandBuilder bancount = new CommandBuilder()
                .addAliases("bans")
                .setDescription("Get the amount of bans on MCO")
                .setUsage("mco bans")
                .discord()
                .setExecutor(new BansCommand())
                .then();

        CommandBuilder banwhy = new CommandBuilder()
                .addAliases("banwhy", "why")
                .setDescription("Get a user's ban information")
                .setUsage("mco banwhy [username]")
                .discord()
                .setExecutor(new BanwhyCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then();

        // MINECRAFTONLINE COMMAND ROOT
        commandManager.register(new CommandBuilder()
                .addAliases("mco", "minecraftonline")
                .setDescription("Commands for MinecraftOnline")
                .setUsage("mco <firstseen|lastseen|playtime|bans|banwhy> [username]")
                .discord()
                .setSlashCommand(true)
                .addWhitelistedGuilds(MCOReference.whitelistedDiscord)
                .setExecutor(ctx -> {
                    ctx.reply(ctx.getCommandMetadata().getCommandUsage());
                })
                .then()
                .addChild(firstseen.build())
                .addChild(lastseen.build())
                .addChild(playtime.build())
                .addChild(bancount.build())
                .addChild(banwhy.build())
                .build()
        );
        // MCO COMMAND ENDS

        // HACKHACK: Register subcommands of mco as non-subcommands and non-slash commands
        commandManager.register(lastseen.discord().setSlashCommand(false).then().build());
        commandManager.register(firstseen.discord().setSlashCommand(false).then().build());
        commandManager.register(playtime.discord().setSlashCommand(false).then().build());
        commandManager.register(bancount.discord().setSlashCommand(false).then().build());
        commandManager.register(banwhy.discord().setSlashCommand(false).then().build());

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
