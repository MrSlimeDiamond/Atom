package net.slimediamond.atom.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.discord.AtomCommand;
import net.slimediamond.atom.discord.CommandEvent;
import net.slimediamond.atom.discord.annotations.Command;
import net.slimediamond.atom.discord.annotations.Subcommand;
import net.slimediamond.atom.util.EmbedUtil;
import net.slimediamond.atom.util.UserUtil;

import java.awt.*;
import java.sql.SQLException;

public class BotCommands {
    @GetService
    private Database database;

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
        database.addGuild(event.getGuild().getIdLong());
        event.replyEmbeds(EmbedUtil.genericSuccessEmbed("Added guild to database"));
    }

    @Command(name = "whoami", description = "Check who the bot thinks you are", slashCommand = false, usage = "whoami")
    public void whoamiCommand(CommandEvent event) throws SQLException {
        boolean admin = database.isDiscordAdminByID(event.getAuthor().getIdLong());
        String used = UserUtil.getUserName(event.getAuthor());
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setAuthor(used, null, event.getAuthor().getAvatarUrl())
                .addField("Atom Admin", (admin ? "Yes" : "No"), true)
                .addField("Discriminated name", event.getAuthor().getAsTag(), true)
                .addField("Username", event.getAuthor().getName(), true)
                .addField("Display Name", event.getAuthor().getGlobalName(), true)
                .addField("Server Display Name", event.getAuthor().getEffectiveName(), true)
                .addField("Used Discriminator", used, true);

        event.replyEmbeds(builder.build());
    }

}
