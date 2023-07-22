package net.slimediamond.atom.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbedUtil {
    public static MessageEmbed genericTextEmbed(String content) {
        return new EmbedBuilder()
                .setColor(Color.decode("#4feb34"))
                .setTitle(content)
                .build();
    }
    public static MessageEmbed genericErrorEmbed() {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("An error occured!")
                .build();
    }
    public static MessageEmbed genericPermissionDeniedError() {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Permission Denied")
                .setDescription("You do not have permission to do this.")
                .build();
    }
    public static MessageEmbed genericIncorrectUsageEmbed(String usage) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Incorrect usage")
                .setDescription("Usage: " + usage)
                .build();
    }
    public static MessageEmbed genericSuccessEmbed(String... message) {
        EmbedBuilder builder =  new EmbedBuilder()
                .setColor(Color.decode("#4FEB34"))
                .setTitle("Success");

        if (message != null) {
            builder.setDescription(message[0]);
        }

        return builder.build();
    }

    public static MessageEmbed expandedErrorEmbed(String message) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("An error occured!")
                .setDescription(message)
                .build();
    }
}
