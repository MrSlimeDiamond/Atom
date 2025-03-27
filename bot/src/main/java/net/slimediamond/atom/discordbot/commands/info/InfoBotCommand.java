package net.slimediamond.atom.discordbot.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.time.Duration;

public class InfoBotCommand implements DiscordCommandExecutor {
    @Override
    public void execute(DiscordCommandContext context) throws Exception {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#4FEB34"))
                .setTitle("Info / Bot")
                .addField("Uptime", InfoCommon.formatDuration(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime())), false)
                .addField("Guild Count", String.valueOf(context.getAPI().getGuilds().size()), false)
                .build();
        context.replyEmbeds(embed);
    }
}
