package net.slimediamond.atom.util;

public class DiscordUtil {
    public static String removeFormatting(String input) {
        return input.replaceAll("([*_~`@])", "\\\\$1")
                .replace("@everyone", "*I tried to ping everyone and failed. :(*")
                .replace("@here", "*I tried to ping here and failed. :(*");
    }
}
