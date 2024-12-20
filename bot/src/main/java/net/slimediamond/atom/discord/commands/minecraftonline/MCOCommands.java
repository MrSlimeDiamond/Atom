package net.slimediamond.atom.discord.commands.minecraftonline;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.discord.annotations.Option;
import net.slimediamond.atom.discord.annotations.Subcommand;
import net.slimediamond.atom.reference.DiscordReference;
import net.slimediamond.atom.reference.EmbedReference;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.atom.util.MCOPlayer;
import net.slimediamond.atom.util.MinecraftOnlineAPI;
import net.slimediamond.atom.util.UnknownPlayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MCOCommands {
    private static final Logger log = LoggerFactory.getLogger(MCOCommands.class);

    @Command(
            name = "mco",
            aliases = {"minecraftonline"},
            usage = "mco <firstseen|lastseen|playtime> [username]",
            description = "Commands for MinecraftOnline",
            whitelistedGuilds = {696218632618901504L, 288050647998136323L, 972936631558488094L},
            subcommands = {
                    @Subcommand(
                            name = "firstseen",
                            aliases = {"fs", "fj"},
                            description = "Get first join date of a user",
                            usage = "mco firstseen [username]",
                            options = {
                                    @Option(
                                            name = "player",
                                            id = 0,
                                            type = OptionType.STRING,
                                            description = "Player to get firstseen information for",
                                            required = true
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "lastseen",
                            aliases = {"ls", "lj"},
                            description = "Get last join date of a user",
                            usage = "mco lastseen [username]",
                            options = {
                                    @Option(
                                            name = "player",
                                            id = 0,
                                            type = OptionType.STRING,
                                            description = "Player to get lastseen information for",
                                            required = true
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "playtime",
                            aliases = {"pt", "tp", "timeplayed"},
                            description = "Get hours played of a user",
                            usage = "mco playtime [username]",
                            options = {
                                    @Option(
                                            name = "player",
                                            id = 0,
                                            type = OptionType.STRING,
                                            description = "Player to get playtime information for",
                                            required = true
                                    )
                            }
                    ),
                    @Subcommand(
                            name = "bans",
                            usage = "mco bans",
                            description = "Get the amount of bans on MCO"
                    ),
                    @Subcommand(
                            name = "banwhy",
                            aliases = "why",
                            description = "Get a user's ban information",
                            usage = "mco banwhy <username>"
                    )
            }
    )
    public void minecraftonlineCommands(CommandEvent event) throws Exception {
        event.deferReply();
        if (event.isSubCommand()) {
            if (event.getSubcommandName().equals("bans")) {
                bansCommand(event);
                return;
            }
            String username;
            String givenName = event.getStringOption("player");
            if (givenName == null) {
                username = event.getAuthor().getName();
            } else {
                username = givenName;
            }
            AtomicReference<String> correctname = new AtomicReference<>();
            AtomicBoolean shouldContinue = new AtomicBoolean(true);
            MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> shouldContinue.set(false));

            if (!shouldContinue.get()) {
                event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that player!"));
                return;
            }

            if (event.getSubcommandName().equals("firstseen")) {
                firstseenCommand(event, correctname.get());
            } else if (event.getSubcommandName().equals("lastseen")) {
                lastseenCommand(event, correctname.get());
            } else if (event.getSubcommandName().equals("playtime")) {
                playtimeCommand(event, correctname.get());
            } else if (event.getSubcommandName().equals("banwhy")) {
                banwhyCommand(event, correctname.get());
            }
        }
    }

    @Command(
            name = "firstseen",
            aliases = {"fs", "fj", "firstjoin"},
            description = "Get the first join date of a user",
            usage = "mco firstseen [username]",
            slashCommand = false,
            whitelistedGuilds = {696218632618901504L, 288050647998136323L, 972936631558488094L}/*,
            options = {
                    @Option(
                            name = "player",
                            id = 0,
                            description = "The player to look up",
                            type = OptionType.STRING
                    )
            }*/
    )
    public void firstseenCommand(CommandEvent event) throws IOException, SQLException {
        String username;
        event.sendIncorrectUsageForCommandArgs(false);
        if (event.getCommandArgs() == null) {
            username = event.getAuthor().getName();
        } else {
            username = event.getCommandArgs()[0];
        }
        AtomicReference<String> correctname = new AtomicReference<>();
        AtomicBoolean shouldContinue = new AtomicBoolean(true);
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> shouldContinue.set(false));

        if (!shouldContinue.get()) {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that player!"));
            return;
        }

        firstseenCommand(event, correctname.get());
    }

    public void firstseenCommand(CommandEvent event, String correctname) throws IOException, SQLException {
        try {
            MCOPlayer player = new MCOPlayer(correctname);
            player.getFirstseen().ifPresentOrElse(firstseen -> {
                this.sendFirstseenResponse(correctname, firstseen, event);
            }, () -> {
                event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Player " + correctname + " does not exist"));
            });
        } catch (UnknownPlayerException e) {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Player " + correctname + " does not exist"));
        }
    }

    @Command(
            name = "lastseen",
            aliases = {"ls", "lj", "lastjoin"},
            description = "Get the first join date of a user",
            usage = "mco lastseen [username]",
            slashCommand = false,
            whitelistedGuilds = {696218632618901504L, 288050647998136323L, 972936631558488094L}/*,
            options = {
                    @Option(
                            name = "player",
                            id = 0,
                            description = "The player to look up",
                            type = OptionType.STRING
                    )
            }*/
    )
    public void lastseenCommand(CommandEvent event) throws IOException, SQLException {
        String username;
        event.sendIncorrectUsageForCommandArgs(false);
        if (event.getCommandArgs() == null) {
            username = event.getAuthor().getName();
        } else {
            username = event.getCommandArgs()[0];
        }
        AtomicReference<String> correctname = new AtomicReference<>();
        AtomicBoolean shouldContinue = new AtomicBoolean(true);
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> shouldContinue.set(false));

        if (!shouldContinue.get()) {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Could not find that player!"));
            return;
        }

        lastseenCommand(event, correctname.get());
    }

    public void lastseenCommand(CommandEvent event, String correctname) throws IOException, SQLException {
        try {
            MCOPlayer player = new MCOPlayer(correctname);
            player.getLastseen().ifPresentOrElse(lastseen -> {
                this.sendLastseenResponse(correctname, lastseen, event);
            }, () -> {
                event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Player " + correctname + " does not exist"));
            });
        } catch (UnknownPlayerException e) {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("Player " + correctname + " does not exist"));
        }
    }

    @Command(
            name = "playtime",
            aliases = { "pt", "timeplayed", "tp" },
            description = "Get playtime data for a user",
            usage = "mco playtime [username]",
            slashCommand = false,
            whitelistedGuilds = {696218632618901504L, 288050647998136323L, 972936631558488094L}
    )
    public void playtimeCommand(CommandEvent event) throws Exception {
        String username;
        event.sendIncorrectUsageForCommandArgs(false);
        if (event.getCommandArgs() == null) {
            username = event.getAuthor().getName();
        } else {
            username = event.getCommandArgs()[0];
        }
        playtimeCommand(event, username);
    }

    public void playtimeCommand(CommandEvent event, String username) throws Exception {
        AtomicReference<String> correctname = new AtomicReference<>();
        MinecraftOnlineAPI.getCorrectUsername(username).ifPresentOrElse(correctname::set, () -> event.reply("Could not find that player!"));
        if (correctname.get() == null) return;
        MCOPlayer player = new MCOPlayer(correctname.get());
        player.getPlaytime().ifPresent(playtime -> {
            BigDecimal hours = new BigDecimal(playtime).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
            this.sendPlaytimeResponse(correctname.get(), hours, event);
        });
    }

    @Command(
            name = "bans",
            usage = "mco bans",
            aliases = {"bancount"},
            description = "Get the amount of bans on MCO",
            slashCommand = false,
            whitelistedGuilds = {696218632618901504L, 288050647998136323L, 972936631558488094L}
    )
    public void bansCommand(CommandEvent event) throws IOException {
        MinecraftOnlineAPI.getBanCount().ifPresentOrElse(bans -> {
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(0x00BEBE)
                    .setTitle("MinecraftOnline ban count")
                    .setThumbnail(EmbedReference.mcoBanhammer)
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon);
            builder.setDescription(bans + " players have been banished from Freedonia!");

            event.replyEmbeds(builder.build());
        }, () -> {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("MinecraftOnlineAPI::getBanCount Optional was not present! What the fuck happened? Tell an admin!"));
        });
    }

    @Command(
            name = "banwhy",
            aliases = "why",
            description = "Get a user's ban information",
            usage = "mco banwhy <username>",
            whitelistedGuilds = {696218632618901504L, 288050647998136323L, 972936631558488094L}
    )
    public void banwhyCommand(CommandEvent event) {
        String username;

        if (event.getCommandArgs() == null) {
            username = event.getAuthor().getName();
        } else {
            username = event.getCommandArgs()[0];
        }

        banwhyCommand(event, username);
    }

    @Command(
            name = "randomplayer",
            description = "Get a random online player",
            usage = "randomplayer",
            whitelistedGuilds = {696218632618901504L, 288050647998136323L, 972936631558488094L}
    )
    public void randomPlayerCommand(CommandEvent event) throws IOException {
        MinecraftOnlineAPI.getOnlinePlayers().ifPresentOrElse(players -> {
            String randomPlayer = players.get(ThreadLocalRandom.current().nextInt(players.size()));
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(randomPlayer, null, "https://mc-heads.net/avatar/" + randomPlayer)
                    .setDescription("Random online player: **" + randomPlayer + "**")
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .setThumbnail("https://mc-heads.net/avatar/" + randomPlayer)
                    .build()
            );
        }, () -> {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor("Could not find any online players!")
                    .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                    .build()
            );
        });
    }

    public void banwhyCommand(CommandEvent event, String username) {
        try {
            MCOPlayer player = new MCOPlayer(username);

            if (!player.isBanned()) {
                event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setAuthor(username, null, "https://mc-heads.net/avatar/" + username)
                        .setDescription(username + " is not banned!")
                        .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                        .build());

            } else {
                player.getBanDate().ifPresentOrElse(date -> {
                    event.replyEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setAuthor(player.getName(), null, "https://mc-heads.net/avatar/" + player.getName())
                            .setTitle(player.getName() + " is naughty!")
                            .addField("Ban reason", player.getBanReason().orElseThrow(), false)
                            .addField("Ban time", "<t:" + date.toInstant().getEpochSecond() + ":f> [<t:" + date.toInstant().getEpochSecond() + ":R>]", false)
                            .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                            .build()
                    );
                }, () -> {
                    event.replyEmbeds(EmbedUtil.genericErrorEmbed());
                });
            }
        } catch(UnknownPlayerException e){
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("User not found"));
        }
    }

    public void sendFirstseenResponse(String username, Date date, CommandEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(username, null, "https://mc-heads.net/avatar/" + username)
                .setDescription(username + " first visited Freedonia at <t:" + date.toInstant().getEpochSecond() + ":f> [<t:" + date.toInstant().getEpochSecond() + ":R>]")
                .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                .build()
        );
    }

    public void sendLastseenResponse(String username, Date date, CommandEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(username, null, "https://mc-heads.net/avatar/" + username)
                .setDescription(username + " last visited Freedonia at <t:" + date.toInstant().getEpochSecond() + ":f> [<t:" + date.toInstant().getEpochSecond() + ":R>]")
                .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                .build()
        );
    }

    public void sendPlaytimeResponse(String username, BigDecimal hours, CommandEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(username, null, "https://mc-heads.net/avatar/" + username)
                .setDescription(username + " has played on Freedonia for " + hours.toString() + " hours")
                .setFooter(EmbedReference.mcoFooter, EmbedReference.mcoIcon)
                .build()
        );
    }
}
