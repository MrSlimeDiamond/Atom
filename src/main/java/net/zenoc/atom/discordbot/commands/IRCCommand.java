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
import net.zenoc.atom.discordbot.exceptions.IncorrectUsageException;
import net.zenoc.atom.discordbot.util.EmbedUtil;
import net.zenoc.atom.services.DiscordBot;
import net.zenoc.atom.services.IRC;
import org.kitteh.irc.client.library.command.WhoisCommand;
import org.kitteh.irc.client.library.event.user.WhoisEvent;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class IRCCommand {
    private AtomicReference<CommandEvent> commandEvent = new AtomicReference<>(null);
    @Command(
            name = "irc",
            description = "Commands for IRC",
            usage = "irc names",
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
                                            type = OptionType.STRING
                                    )
                            }
                    )
            }
    )
    public void ircCommand(CommandEvent event) throws SQLException {
        if (Atom.database.isChannelBridged(event.getChannel().getIdLong()) && event.getSubcommandName() != "whois") {
            if (event.getSubcommandName().equals("names")) {
                IRC.client.getChannel(Atom.database.getIRCBridgeChannel(event.getChannel().getIdLong())).ifPresent(channel -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    AtomicInteger ops = new AtomicInteger();
                    AtomicInteger voice = new AtomicInteger();
                    AtomicInteger total = new AtomicInteger();
                    channel.getUsers().forEach(user -> {
                          stringBuilder.append(user.getNick()).append(", ");
                          if (channel.getUserModes(user).isPresent()) {
                              channel.getUserModes(user).get().forEach(usermode -> {
                                  total.getAndIncrement();
                                  if (usermode.getNickPrefix() == '@') {
                                      ops.getAndIncrement();
                                  } else if (usermode.getNickPrefix() == '+') {
                                      voice.getAndIncrement();
                                  }
                              });
                          }
                    });
                    MessageEmbed embed = new EmbedBuilder()
                            .setAuthor("Users in " + channel.getName())
                            .setTitle(ops.get() + " ops, " + voice.get() + " voiced, " + total.get() + " total")
                            .setDescription(stringBuilder.toString())
                            .build();
                    event.replyEmbeds(embed);
                });
            } else if (event.getSubcommandName().equals("whois")) {
                try {
                    String name = event.getStringOption("user");
                    event.deferReply();
                    commandEvent.set(event);
                    System.out.println(commandEvent);
                    new WhoisCommand(IRC.client).target(name).execute();
                } catch (IncorrectUsageException e) {
                    event.replyEmbeds(EmbedUtil.genericIncorrectUsageEmbed("irc whois <username>"));
                }
            }
        } else {
            event.replyEmbeds(EmbedUtil.expandedErrorEmbed("This channel is not bridged"));
        }
    }

    @Handler
    public void whoisEvent(WhoisEvent event) {
        System.out.println(commandEvent.get());
        if (commandEvent.get() == null) return;

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
        event.getWhoisData().getIdleTime().ifPresent(idletime -> builder.addField("Idle", new Date(idletime).toString(), true));
        event.getWhoisData().getOperatorInformation().ifPresent(operatorInfo -> builder.addField("Operator", operatorInfo, false));

        String channels = stringBuilder.toString();
        builder.addField("Channels", channels, false);

        commandEvent.get().replyEmbeds(builder.build());
        //DiscordBot.jda.getTextChannelById(1089006427890274344L).sendMessageEmbeds(builder.build()).queue();
    }
}