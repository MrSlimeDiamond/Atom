package net.slimediamond.atom.command.telegram;

import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.reference.TelegramReference;
import net.slimediamond.telegram.Listener;
import net.slimediamond.telegram.events.MessageReceivedEvent;

import java.util.Arrays;

public class TelegramMessageListener implements Listener {
    private CommandManager commandManager;

    public TelegramMessageListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onMessage(MessageReceivedEvent event) {
        String message = event.getText().toLowerCase();
        String prefix = TelegramReference.prefix;
        boolean hidden = false;

        if (message.startsWith(prefix)) {
            // If there is a space after the prefix, remove it and extract the command
            String extracted = message.length() > prefix.length() && message.charAt(prefix.length() + (hidden ? 1 : 0)) == ' '
                    ? message.substring(prefix.length() + (hidden ? 2 : 1))
                    : message.substring(hidden ? prefix.length() + 1 : prefix.length());

            // Split into command and the remaining part
            String[] parts = extracted.split("\\s+", 2);
            String commandName = parts[0]; // The command itself
            String remaining = parts.length > 1 ? parts[1] : null; // Everything else after the command

            for (CommandMetadata command : commandManager.getCommands()) {
                if (!command.hasTelegram()) {
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

                    // TODO: Admin only stuff, and also group whitelisting

                    // command execution
                    try {
                        TelegramCommandSender sender = new TelegramCommandSender(event.getSender());
                        command.getTelegramCommand().getCommandExecutor().execute(new TelegramCommandContext(sender, args, command, commandManager, event.getChat(), event.getClient()));
                    } catch (Exception e) {
                        event.getChat().sendMessage("An error occurred: " + e.getMessage());
                    }
                }
            }
        }
    }
}
