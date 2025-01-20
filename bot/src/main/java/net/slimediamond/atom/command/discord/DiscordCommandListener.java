package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.slimediamond.atom.Atom;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.reference.DiscordReference;

import java.sql.SQLException;
import java.util.Arrays;

public class DiscordCommandListener extends ListenerAdapter {
    private CommandManager commandManager;
    private Database database;

    public DiscordCommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.database = Atom.getServiceManager().getInstance(Database.class);
    }

    // Just context commands for now
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();
        String prefix = DiscordReference.prefix;

        if (message.startsWith(prefix)) {
            // If there is a space after the prefix, remove it and extract the command
            String extracted = message.length() > prefix.length() && message.charAt(prefix.length()) == ' '
                    ? message.substring(prefix.length() + 1)
                    : message.substring(prefix.length());

            // Split into command and the remaining part
            String[] parts = extracted.split("\\s+", 2);
            String commandName = parts[0]; // The command itself
            String remaining = parts.length > 1 ? parts[1] : ""; // Everything else after the command

            for (CommandMetadata command : commandManager.getCommands()) {
                // Only pay attention to Discord commands here
                if (!command.hasDiscord()) {
                    continue;
                }

                if (command.getAliases().contains(commandName.strip())) {
                    String[] args = new String[0];

                    if (remaining != null) {
                        args = remaining.split(" ");
                    }
                    // execute the command
                    if (!command.getChildren().isEmpty()) {
                        if (remaining != null) {
                            // it might contain a subcommand. Let's check.
                            for (CommandMetadata child : command.getChildren()) {
                                if (child.getAliases().get(0).equalsIgnoreCase(remaining.split(" ")[0])) {
                                    args = Arrays.copyOfRange(args, 1, args.length);
                                    command = child;
                                }
                            }
                        }
                    }

                    if (command.isAdminOnly()) {
                        try {
                            if (!database.isDiscordAdminByID(event.getAuthor().getIdLong())) {
                                event.getChannel().sendMessage("You do not have permission to do this!");
                                return;
                            }
                        } catch (SQLException e) {
                            event.getChannel().sendMessage("The database seems down! (SQLException occurred) - unable to determine whether you have admin permissions.").queue();
                            e.printStackTrace();
                        }
                    }

                    if (!command.getDiscordCommand().getWhitelistedGuilds().isEmpty()) {
                        if (event.isFromGuild()) {
                            if (!command.getDiscordCommand().getWhitelistedGuilds().contains(event.getGuild().getIdLong())) {
                                return;
                            }
                        } else return;
                    }

                    DiscordCommandExecutor commandExecutor = command.getDiscordCommand().getCommandExecutor();
                    try {
                        commandExecutor.execute(new DiscordCommandContext(new AtomDiscordCommandEvent(event), command, args, commandManager));
                    } catch (Exception e) {
                        event.getChannel().sendMessage("An error occurred: " + e.getMessage()).queue();
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (CommandMetadata command : commandManager.getCommands()) {
            if (command.getAliases().contains(event.getName())) {
                if (!command.hasDiscord()) continue;
                // valid command from this bot

                if (command.isAdminOnly()) {
                    try {
                        if (!database.isDiscordAdminByID(event.getUser().getIdLong())) {
                            event.reply("You do not have permission to do this!");
                        }
                    } catch (SQLException e) {
                        event.reply("The database seems down! (SQLException occurred) - unable to determine whether you have admin permissions.").queue();
                        e.printStackTrace();
                    }
                }

                if (!command.getDiscordCommand().getWhitelistedGuilds().isEmpty()) {
                    if (event.isFromGuild()) {
                        if (!command.getDiscordCommand().getWhitelistedGuilds().contains(event.getGuild().getIdLong())) {
                            return;
                        }
                    } else return;
                }

                // Args for compatibility's sake
                String[] args = event.getOptions().stream().map(OptionMapping::getAsString).toArray(String[]::new);

                DiscordCommandExecutor commandExecutor = command.getDiscordCommand().getCommandExecutor();
                try {
                    commandExecutor.execute(new DiscordCommandContext(new AtomDiscordCommandEvent(event), command, args, commandManager));
                } catch (Exception e) {
                    event.reply("An error occurred! " + e.getMessage()).queue();
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
