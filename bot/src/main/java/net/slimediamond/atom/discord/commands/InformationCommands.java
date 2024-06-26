package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.discord.annotations.Subcommand;
import net.slimediamond.util.network.NetworkUtils;
import oshi.SystemInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.Duration;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InformationCommands {
    public static String formatDuration(Duration d) {
        final long days = d.toDays();
        d = d.minusDays(days);
        final long hours = d.toHours();
        d = d.minusHours(hours);
        final long minutes = d.toMinutes();
        d = d.minusMinutes(minutes);
        final long seconds = d.getSeconds();
        final StringJoiner joiner = new StringJoiner(", ");
        if (days > 0) joiner.add(days + " days");
        if (hours > 0) joiner.add(hours + " hours");
        if (minutes > 0) joiner.add(minutes + " minutes");
        if (seconds > 0) joiner.add(seconds + " seconds");
        return joiner.toString();
    }


    public static long getSystemUptime() {
        return new SystemInfo().getOperatingSystem().getSystemUptime();
    }

    @Command(
            name = "info",
            description = "Show information about the bot",
            usage = "info <host|bot>",
            subcommands = {
                    @Subcommand(
                            name = "bot",
                            description = "Show information about the bot",
                            usage = "info bot"
                    ),
                    @Subcommand(
                            name = "host",
                            description = "Show information about the host machine running the bot",
                            usage = "info host"
                    )
            }
    )
    public void infoCommand(CommandEvent event) throws Exception {
        if (event.getSubcommandName().equals("host")) {
            event.deferReply();

            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.decode("#4FEB34"))
                    .setTitle("Info / Host")
                    .addField("Hostname", InetAddress.getLocalHost().getHostName(), true)
                    .addField("OS", System.getProperty("os.name"), true)
                    .addField("Public IP", NetworkUtils.getIP(), true)
                    .addField("Local IP", InetAddress.getLocalHost().getHostAddress(), true)
                    .addField("Uptime", formatDuration(Duration.ofSeconds(getSystemUptime())), true)
                    .build();
            event.replyEmbeds(embed);
        } else {
            // Assume bot information if no subcommands are given
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.decode("#4FEB34"))
                    .setTitle("Info / Bot")
                    .addField("Uptime", formatDuration(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime())), false)
                    .addField("Guild Count", String.valueOf(event.getJDA().getGuilds().size()), false)
                    .build();
            event.replyEmbeds(embed);
        }
    }
}
