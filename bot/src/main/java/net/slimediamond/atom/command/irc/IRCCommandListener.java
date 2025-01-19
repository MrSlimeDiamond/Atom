package net.slimediamond.atom.command.irc;

import net.engio.mbassy.listener.Handler;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.command.CommandPlatform;
import net.slimediamond.atom.reference.IRCReference;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

import java.util.Arrays;

public class IRCCommandListener {
    private CommandManager commandManager;
    public IRCCommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        String message = event.getMessage().toLowerCase();
        String prefix = IRCReference.prefix;

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
                // Only pay attention to IRC commands here
                if (command.getCommandPlatform() != CommandPlatform.IRC) {
                    continue;
                }

                if (command.getAliases().contains(commandName.strip())) {
                    String[] args = remaining.split(" ");
                    // execute the command
                    if (!command.getChildren().isEmpty()) {
                        if (remaining.length() > 1) {
                            // it might contain a subcommand. Let's check.
                            for (CommandMetadata child : command.getChildren()) {
                                if (child.getAliases().get(0).equalsIgnoreCase(remaining.split(" ")[0])) {
                                    args = Arrays.copyOfRange(args, 1, args.length);
                                    command = child;
                                }
                            }
                        }
                    }

                    IRCCommandExecutor commandExecutor = (IRCCommandExecutor)command.getCommandExecutor();
                    commandExecutor.execute(new IRCCommandContext(event, command, args));
                    break;
                }
            }
        }
    }
}
