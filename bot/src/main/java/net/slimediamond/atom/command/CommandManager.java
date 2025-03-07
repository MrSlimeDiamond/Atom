package net.slimediamond.atom.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.slimediamond.atom.command.discord.DiscordCommand;
import net.slimediamond.atom.services.system.GetServiceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CommandManager {
    private static final Logger log = LoggerFactory.getLogger("command manager");

    private ArrayList<CommandMetadata> commands = new ArrayList<>();

    public void register(CommandMetadata metadata) {
        // allow @GetService

        processAnnotations(metadata);
        if (!metadata.getChildren().isEmpty()) {
            metadata.getChildren().forEach(this::processAnnotations);
        }

        this.commands.add(metadata);
    }

    private void processAnnotations(CommandMetadata metadata) {
        if (metadata.hasIRC()) {
            GetServiceProcessor.processAnnotations(metadata.getIRCCommand().getCommandExecutor());
        }

        if (metadata.hasDiscord()) {
            GetServiceProcessor.processAnnotations(metadata.getDiscordCommand().getCommandExecutor());
        }
    }

    public ArrayList<CommandMetadata> getCommands() {
        return this.commands;
    }

    public void refreshDiscordSlashCommands(JDA jda) {
        // HACKHACK: Clear all existing commands to prevent duplicates
        jda.updateCommands().queue();
        jda.getGuilds().forEach(guild -> guild.updateCommands().queue());

        log.info("Reloading slash commands!");

        for (CommandMetadata metadata : commands) {
            if (metadata.hasDiscord()) {
                if (metadata.getDiscordCommand().isSlashCommand()) {
                    DiscordCommand command = metadata.getDiscordCommand();
                    ArrayList<OptionData> options = new ArrayList<>();
                    ArrayList<SubcommandData> subcommands = new ArrayList<>();

                    addOptions(command, options);
                    for (CommandMetadata child : metadata.getChildren()) {
                        if (child.hasDiscord()) {
                            if (child.getDiscordCommand().isSlashCommand()) {
                                ArrayList<OptionData> childOptions = new ArrayList<>();
                                addOptions(child.getDiscordCommand(), childOptions);
                                subcommands.add(new SubcommandData(child.getName(), child.getDescription()).addOptions(childOptions));
                            }
                        }
                    }

                    CommandData commandData = new CommandDataImpl(metadata.getName(), metadata.getDescription())
                            .addOptions(options)
                            .addSubcommands(subcommands);

                    if (command.getWhitelistedGuilds().isEmpty()) {
                        //jda.upsertCommand(commandData).queue();
                        jda.getGuilds().forEach(guild -> guild.upsertCommand(commandData).queue());
                    } else {
                        // Only add it to guilds in the whitelist
                        command.getWhitelistedGuilds().forEach(guildId -> {
                            Guild guild = jda.getGuildById(guildId);
                            if (guild != null) {
                                guild.upsertCommand(commandData).queue();
                            }
                        });
                    }
                }
            }
        }
    }

    // helper function
    private void addOptions(DiscordCommand command, ArrayList<OptionData> options) {
        command.getArgs().forEach(arg -> options.add(new OptionData(arg.getOptionType(), arg.getName(), arg.getDescription(), arg.isRequired())));
    }
}
