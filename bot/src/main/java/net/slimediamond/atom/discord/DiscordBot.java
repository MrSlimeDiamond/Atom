package net.slimediamond.atom.discord;

import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.command.discord.DiscordCommandListener;
import net.slimediamond.atom.command.discord.args.DiscordArgsBuilder;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.commands.*;
import net.slimediamond.atom.discord.commands.amplicity.AmplicityTimeplayed;
import net.slimediamond.atom.discord.commands.bridge.BridgeCreateCommand;
import net.slimediamond.atom.discord.commands.bridge.BridgeDeleteCommand;
import net.slimediamond.atom.discord.commands.endpoint.EndpointAddCommand;
import net.slimediamond.atom.discord.commands.endpoint.EndpointListCommand;
import net.slimediamond.atom.discord.commands.endpoint.EndpointPipeCommand;
import net.slimediamond.atom.discord.commands.endpoint.EndpointRemoveCommand;
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
                .setUsage("bridge <create|delete|list>")
                .setAdminOnly(true)
                .discord()
                .setSlashCommand(false)
                .setExecutor(CommandContext::sendUsage)
                .then().addChild(new CommandBuilder()
                        .addAliases("create")
                        .setDescription("Create a chat bridge")
                        .setUsage("bridge create [name]")
                        .discord()
                        .setExecutor(new BridgeCreateCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setOptionType(OptionType.STRING)
                                .setName("name")
                                .setDescription("Name of the bridge")
                                .setId(0)
                                .setRequired(false)
                                .build()
                        ).then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("delete", "remove", "nuke")
                        .setDescription("Remove a chat bridge")
                        .setUsage("bridge delete [name|id]")
                        .discord()
                        .setExecutor(new BridgeDeleteCommand())
                        .then().build()
                )
                .build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("endpoint", "endpoints")
                .setDescription("Manage a bridged chat room's endpoints")
                .setUsage("endpoint <add|remove|removeall|pipe|setavatar|list>")
                .discord()
                .setSlashCommand(false)
                .setExecutor(CommandContext::sendUsage)
                .then().addChild(new CommandBuilder()
                        .addAliases("add")
                        .setDescription("Add a chat bridge endpoint")
                        .setUsage("endpoint add <type> <uniqueIdentifier> [chatId]")
                        .discord().setExecutor(new EndpointAddCommand())
                        .then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("remove")
                        .setDescription("Remove a chat bridge endpoint")
                        .setUsage("remove [uniqueIdentifier|id]")
                        .discord().setExecutor(new EndpointRemoveCommand())
                        .then().build()
                )
//                .addChild(new CommandBuilder()
//                        .addAliases("removeall")
//                        .setDescription("Remove all endpoints")
//                        .setUsage("removeall [type]")
//                        .discord().setExecutor(new EndpointRemoveAllCommand())
//                        .then().build()
//                ) // TODO
                .addChild(new CommandBuilder()
                        .addAliases("pipe")
                        .setDescription("Toggle a bridge endpoint's working status")
                        .setUsage("pipe <on|off>")
                        .discord()
                        .setExecutor(new EndpointPipeCommand())
                        .addArgument(new DiscordArgsBuilder()
                                .setOptionType(OptionType.BOOLEAN)
                                .setName("status")
                                .setDescription("Whether messages should be sent/received in this channel.")
                                .setId(0)
                                .setRequired(true)
                                .build()
                        )
                        .then().build()
                )
                .addChild(new CommandBuilder()
                        .addAliases("list")
                        .setDescription("List all endpoints for this chat")
                        .setUsage("endpoint list")
                        .discord().setExecutor(new EndpointListCommand())
                        .then().build()
                ).build()
        );

        commandManager.register(new CommandBuilder()
                .addAliases("irc")
                .setDescription("Commands for IRC")
                .setUsage("irc <names|whois>")
                .discord()
                .setExecutor(CommandContext::sendUsage)
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
                            } catch (Exception e) {
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
                .setExecutor(CommandContext::sendUsage)
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
                .setExecutor(CommandContext::sendUsage)
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
                .setExecutor(CommandContext::sendUsage)
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
                .setExecutor(CommandContext::sendUsage)
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
                .setExecutor(CommandContext::sendUsage)
                .then()
                .addChild(new CommandBuilder()
                        .addAliases("channel")
                        .setDescription("Set/unset streams channel")
                        .setUsage("channel <set/unset>")
                        .discord()
                        .setExecutor(new StreamsChannelCommand())
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
        CommandMetadata firstseen = new CommandBuilder()
                .addAliases("firstseen", "fs", "fj")
                .setDescription("Get first join date of a user")
                .setUsage("mco firstseen [username]")
                .discord()
                .setSlashCommand(true)
                .setExecutor(new FirstseenCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then().build();

        CommandMetadata lastseen = new CommandBuilder()
                .addAliases("lastseen", "ls", "lj")
                .setDescription("Get last join date of a user")
                .setUsage("mco lastseen [username]")
                .discord()
                .setSlashCommand(true)
                .setExecutor(new LastseenCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then().build();

        CommandMetadata playtime = new CommandBuilder()
                .addAliases("playtime", "pt", "tp", "timeplayed")
                .setDescription("Get hours played of a user")
                .setUsage("mco playtime [username]")
                .discord()
                .setSlashCommand(true)
                .setExecutor(new PlaytimeCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then().build();

        CommandMetadata bancount = new CommandBuilder()
                .addAliases("bans")
                .setDescription("Get the amount of bans on MCO")
                .setUsage("mco bans")
                .discord()
                .setSlashCommand(true)
                .setExecutor(new BansCommand())
                .then().build();

        CommandMetadata banwhy = new CommandBuilder()
                .addAliases("banwhy", "why")
                .setDescription("Get a user's ban information")
                .setUsage("mco banwhy [username]")
                .discord()
                .setSlashCommand(true)
                .setExecutor(new BanwhyCommand())
                .addArgument(new DiscordArgsBuilder()
                        .setName("username")
                        .setId(0)
                        .setDescription("The username to look up")
                        .setOptionType(OptionType.STRING)
                        .setRequired(false)
                        .build()
                )
                .then().build();

        // MINECRAFTONLINE COMMAND ROOT
        commandManager.register(new CommandBuilder()
                .addAliases("mco", "minecraftonline")
                .setDescription("Commands for MinecraftOnline")
                .setUsage("mco <firstseen|lastseen|playtime|bans|banwhy> [username]")
                .discord()
                .setSlashCommand(true)
                .addWhitelistedGuilds(MCOReference.whitelistedDiscord)
                .setExecutor(ctx -> ctx.reply(ctx.getCommandMetadata().getCommandUsage()))
                .then()
                .addChild(firstseen)
                .addChild(lastseen)
                .addChild(playtime)
                .addChild(bancount)
                .addChild(banwhy)
                .build()
        );
        // MCO COMMAND ENDS

        // HACKHACK: Register subcommands of mco as non-subcommands and non-slash commands
        commandManager.register(lastseen.toBuilder().discord().setSlashCommand(false).then().build());
        commandManager.register(firstseen.toBuilder().discord().setSlashCommand(false).then().build());
        commandManager.register(playtime.toBuilder().discord().setSlashCommand(false).then().build());
        commandManager.register(bancount.toBuilder().discord().setSlashCommand(false).then().build());
        commandManager.register(banwhy.toBuilder().discord().setSlashCommand(false).then().build());

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
