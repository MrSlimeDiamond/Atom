package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;

import java.awt.*;

public class PingCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#4feb34"))
                .setTitle("Pong!")
                .setDescription("Latency: " + context.getJDA().getGatewayPing() + "ms")
                .build();

        context.replyEmbeds(embed);
    }
}
