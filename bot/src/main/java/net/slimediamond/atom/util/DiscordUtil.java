package net.slimediamond.atom.util;

public class DiscordUtil {
    public static String removeFormatting(String input) {
        // Remove bold, italic, strikethrough, underlines, and code blocks
        return input.replaceAll("(?i)\\*\\*([^*]+)\\*\\*", "$1")  // Remove **bold**
                .replaceAll("(?i)\\*([^*]+)\\*", "$1")       // Remove *italic*
                .replaceAll("~~([^~]+)~~", "$1")              // Remove ~~strikethrough~~
                .replaceAll("__([^_]+)__", "$1")              // Remove __underline__
                .replaceAll("`([^`]+)`", "$1")                // Remove `inline code`
                .replaceAll("```([^`]+)```", "$1");           // Remove ```code blocks```
    }
}
