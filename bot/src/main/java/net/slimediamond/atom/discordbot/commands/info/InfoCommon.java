package net.slimediamond.atom.discordbot.commands.info;

import oshi.SystemInfo;

import java.time.Duration;
import java.util.StringJoiner;

public class InfoCommon {
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
}
