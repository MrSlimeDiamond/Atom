package net.zenoc.atom.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.zenoc.atom.Atom;
import net.zenoc.atom.discordbot.AtomCommand;
import net.zenoc.atom.discordbot.CommandEvent;
import net.zenoc.atom.discordbot.util.EmbedUtil;
import net.zenoc.atom.discordbot.annotations.Command;
import net.zenoc.atom.discordbot.annotations.Subcommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;

public class BotCommands {
    private static final Logger log = LoggerFactory.getLogger(BotCommands.class);
    @Command(name = "ping", description = "Replies with Pong", usage = "ping")
    public void pingCommand(CommandEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#4feb34"))
                .setTitle("Pong!")
                .setDescription("Latency: " + event.getJDA().getGatewayPing() + "ms")
                .build();

        event.replyEmbeds(embed);
    }

    @Command(name = "help", description = "Show bot commands and what they do", usage = "help")
    public void helpCommand(CommandEvent event) {
        StringBuilder builder = new StringBuilder();
        for (AtomCommand command : event.getCommandHandler().getCommands()) {
            if (command.getCommand().adminOnly()) continue;
            builder
                .append(command.getCommand().name())
                .append(" - ")
                .append(command.getCommand().description())
                .append("\n");
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#4feb34"))
                .setTitle("Atom / Help")
                .setDescription(builder.toString())
                .build();

        event.replyEmbeds(embed);
    }

    @Command(name = "stop", description = "Stop the bot", slashCommand = false, adminOnly = true, aliases = {"fuckoff"}, usage = "stop")
    public void stopCommand(CommandEvent event) throws Exception {
        event.replyEmbeds(EmbedUtil.genericTextEmbed("Stopping bot..."));
        Atom.shutdown(BotCommands.class);
    }

    @Command(
            name = "commands",
            description = "Commands command lol",
            slashCommand = false,
            adminOnly = true,
            aliases = {"cmds"},
            usage = "cmds",
            subcommands = {
                    @Subcommand(
                            name = "reload",
                            description = "Reload slash commands",
                            slashCommand = false,
                            adminOnly = true,
                            usage = "commands reload slash"
                    )
            }
    )
    public void commandsCommand(CommandEvent event) {
        if (event.getSubcommandName().equals("reload")) {
            event.getCommandHandler().refreshSlashCommands();
            event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Reloaded slash commands"));
        }
    }

    @Command(
            name = "add_server",
            description = "Add a server to the database",
            slashCommand = false,
            adminOnly = true,
            aliases = {"unfuck_database"},
            usage = "add_server"
    )
    public void addGuildCommand(CommandEvent event) throws SQLException {
        Atom.database.addGuild(event.getGuild().getIdLong());
        event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added guild to database"));
    }

}
