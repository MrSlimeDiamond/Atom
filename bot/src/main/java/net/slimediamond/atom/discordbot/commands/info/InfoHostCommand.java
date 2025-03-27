package net.slimediamond.atom.discordbot.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.command.discord.DiscordCommandContext;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.util.network.NetworkUtils;

import java.awt.*;
import java.net.InetAddress;
import java.time.Duration;

public class InfoHostCommand implements DiscordCommandExecutor {

    @Override
    public void execute(DiscordCommandContext ctx) throws Exception {
        ctx.deferReply();

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#4FEB34"))
                .setTitle("Info / Host")
                .addField("Hostname", InetAddress.getLocalHost().getHostName(), true)
                .addField("OS", System.getProperty("os.name"), true)
                .addField("Public IP", NetworkUtils.getIP(), true)
                .addField("Local IP", InetAddress.getLocalHost().getHostAddress(), true)
                .addField("Uptime", InfoCommon.formatDuration(Duration.ofSeconds(InfoCommon.getSystemUptime())), true)
                .build();
        ctx.replyEmbeds(embed);
    }
}
