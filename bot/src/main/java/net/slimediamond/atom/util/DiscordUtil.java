package net.slimediamond.atom.util;

public class DiscordUtil {
    public static String removeFormatting(String input) {
        return input.replaceAll("([*_~`])", "\\\\$1");
    }
}
