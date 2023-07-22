package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.discord.annotations.Subcommand;

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
    // https://stackoverflow.com/a/14800849
    public static long getSystemUptime() throws Exception {
        long uptime = -1;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Process uptimeProc = Runtime.getRuntime().exec("net stats srv");
            BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("Statistics since")) {
                    SimpleDateFormat format = new SimpleDateFormat("'Statistics since' MM/dd/yyyy hh:mm:ss a");
                    Date boottime = format.parse(line);
                    uptime = System.currentTimeMillis() - boottime.getTime();
                    break;
                }
            }
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            Process uptimeProc = Runtime.getRuntime().exec("uptime");
            BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
            String line = in.readLine();
            if (line != null) {
                Pattern parse = Pattern.compile("((\\d+) days,)? (\\d+):(\\d+)");
                Matcher matcher = parse.matcher(line);
                if (matcher.find()) {
                    String _days = matcher.group(2);
                    String _hours = matcher.group(3);
                    String _minutes = matcher.group(4);
                    int days = _days != null ? Integer.parseInt(_days) : 0;
                    int hours = _hours != null ? Integer.parseInt(_hours) : 0;
                    int minutes = _minutes != null ? Integer.parseInt(_minutes) : 0;
                    uptime = (minutes * 60000) + (hours * 60000 * 60) + (days * 6000 * 60 * 24);
                }
            }
        }
        return uptime;
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

            Atom.refreshIP();

            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.decode("#4FEB34"))
                    .setTitle("Info / Host")
                    .addField("Hostname", InetAddress.getLocalHost().getHostName(), true)
                    .addField("OS", System.getProperty("os.name"), true)
                    .addField("Public IP", Atom.ip, true)
                    .addField("Local IP", InetAddress.getLocalHost().getHostAddress(), true)
                    .addField("Uptime", formatDuration(Duration.ofMillis(getSystemUptime())), true)
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
