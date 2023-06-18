package net.zenoc.atom.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.engio.mbassy.listener.Handler;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.CommandEvent;
import net.zenoc.atom.discordbot.annotations.Command;
import net.zenoc.atom.discordbot.annotations.Option;
import net.zenoc.atom.discordbot.annotations.Subcommand;
import net.zenoc.atom.reference.IRCReference;
import net.zenoc.atom.util.DateUtil;
import net.zenoc.atom.util.EmbedUtil;
import net.zenoc.atom.services.IRC;
import org.kitteh.irc.client.library.command.WhoisCommand;
import org.kitteh.irc.client.library.event.user.WhoisEvent;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class IRCCommand {
    private static CommandEvent commandEvent;
    @Command(
            name = "irc",
            description = "Commands for IRC",
            usage = "irc <names|whois>",
            subcommands = {
                    @Subcommand(
                            name = "names",
                            description = "Get a list of people in the bridged channel",
                            usage = "irc names"
                    ),
                    @Subcommand(
                            name = "whois",
                            description = "Get information about a user on IRC",
                            usage = "irc whois <user>",
                            options = {
                                    @Option(
                                            name = "user",
                                            description = "The user to WHOIS",
                                            id = 0,
                                            type = OptionType.STRING,
                                            required = true
                                    )
                            }
                    )
            }
    )
    public void ircCommand(CommandEvent event) throws SQLException {
        if (!event.isSubCommand()) {
            event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("irc <names|whois>"));
        }
        if (Atom.database.isChannelBridged(event.getChannel().getIdLong()) && event.getSubcommandName() != "whois") {
            if (event.getSubcommandName().equals("names")) {
                IRC.client.getChannel(Atom.database.getIRCBridgeChannel(event.getChannel().getIdLong())).ifPresent(channel -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    AtomicInteger ops = new AtomicInteger();
                    AtomicInteger voice = new AtomicInteger();
                    AtomicInteger total = new AtomicInteger();
                    channel.getUsers().forEach(user -> {
                          if (channel.getUserModes(user).isPresent()) {
                              channel.getUserModes(user).get().forEach(usermode -> {

                                  total.getAndIncrement();
                                  if (usermode.getNickPrefix() == '@') {
                                      stringBuilder.append("@");
                                      ops.getAndIncrement();
                                  } else if (usermode.getNickPrefix() == '+') {
                                      stringBuilder.append("+");
                                      voice.getAndIncrement();
                                  }
                              });
                          }
                        stringBuilder.append(user.getNick().replace("_", "\\_")).append(", ");
                    });
                    MessageEmbed embed = new EmbedBuilder()
                            .setAuthor("Users in " + channel.getName())
                            .setTitle(ops.get() + " ops, " + voice.get() + " voiced, " + total.get() + " total")
                            .setDescription(stringBuilder.toString())
                            .build();
                    event.replyEmbeds(embed);
                });
            } else if (event.getSubcommandName().equals("whois")) {
                String name = event.getStringOption("user");
                event.deferReply();
                commandEvent = event;
                new WhoisCommand(IRC.client).target(name).execute();
            }
        } else {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("This channel is not bridged"));
        }
    }

    @Handler
    public void whoisEvent(WhoisEvent event) {
        if (commandEvent == null) return;
        if (!event.getWhoisData().getServer().isPresent()) {
            commandEvent.replyEmbeds(EmbedUtil.expandedErrorEmbed("This user is not on " + IRCReference.host));
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(event.getWhoisData().getNick())
                .setTitle(event.getWhoisData().getName());

        StringBuilder stringBuilder = new StringBuilder();
        event.getWhoisData().getRealName().ifPresent(builder::setDescription);
        event.getWhoisData().getChannels().forEach(channel -> {
            stringBuilder.append(channel).append(" ");
        });
        event.getWhoisData().getServer().ifPresent(server -> {
            AtomicReference<String> serverDescription = new AtomicReference<>("*No server description*");
            event.getWhoisData().getServerDescription().ifPresent(serverDescription::set);
            builder.addField("Server", server + " : " + serverDescription, false);
        });
        event.getWhoisData().getAccount().ifPresent(account -> builder.addField("Account", "Logged in as " + account, true));
        event.getWhoisData().getIdleTime().ifPresent(idletime -> builder.addField("Idle", new Date(System.currentTimeMillis() - idletime) + " (" + DateUtil.formatDuration(Duration.ofSeconds(idletime)) + ")", true));
        event.getWhoisData().getOperatorInformation().ifPresent(operatorInfo -> builder.addField("Operator", operatorInfo, false));

        event.getWhoisData().getIdleTime().ifPresent(System.out::println);

        String channels = stringBuilder.toString();
        builder.addField("Channels", channels, false);

        commandEvent.replyEmbeds(builder.build());
        //DiscordBot.jda.getTextChannelById(1089006427890274344L).sendMessageEmbeds(builder.build()).queue();
    }
}
