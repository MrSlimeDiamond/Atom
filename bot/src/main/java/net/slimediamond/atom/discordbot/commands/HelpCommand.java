package net.slimediamond.atom.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;

import java.awt.*;

public class HelpCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (CommandMetadata command : context.getCommandManager().getCommands()) {
            if (command.isAdminOnly()) continue;
            builder
                    .append(command.getName())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#4feb34"))
                .setTitle("Atom / Help")
                .setDescription(builder.toString())
                .build();

        context.replyEmbeds(embed);
    }
}
