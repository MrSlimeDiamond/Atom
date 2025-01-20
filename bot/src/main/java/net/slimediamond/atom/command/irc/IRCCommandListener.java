package net.slimediamond.atom.command.irc;

import net.engio.mbassy.listener.Handler;
import net.slimediamond.atom.command.CommandManager;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.common.annotations.GetService;
import net.slimediamond.atom.database.Database;
import net.slimediamond.atom.irc.McObotMessageParser;
import net.slimediamond.atom.reference.IRCReference;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

import java.sql.SQLException;
import java.util.Arrays;

public class IRCCommandListener {
    @GetService
    private Database database;

    private CommandManager commandManager;
    public IRCCommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) throws SQLException {
        String message = event.getMessage().toLowerCase();
        String prefix = IRCReference.prefix;
        boolean hidden = false;

        McObotMessageParser mcobotParser = new McObotMessageParser(event.getActor(), event.getMessage());
        if (
                event.getMessage().startsWith(prefix) ||
                event.getMessage().startsWith("#" + prefix) && event.getChannel().getName().equals("#minecraftonline") ||
                mcobotParser.isCommandMessage()
        ) {
            if (mcobotParser.isCommandMessage()) {
                message = mcobotParser.getMessageContent();
            }

            if (message.startsWith("#" + prefix)) {
                hidden = true;
            }

            // If there is a space after the prefix, remove it and extract the command
            String extracted = message.length() > prefix.length() && message.charAt(prefix.length() + (hidden ? 1 : 0)) == ' '
                    ? message.substring(prefix.length() + (hidden ? 2 : 1))
                    : message.substring(hidden ? prefix.length() + 1 : prefix.length());

            // Split into command and the remaining part
            String[] parts = extracted.split("\\s+", 2);
            String commandName = parts[0]; // The command itself
            String remaining = parts.length > 1 ? parts[1] : null; // Everything else after the command

            for (CommandMetadata command : commandManager.getCommands()) {
                // Only pay attention to IRC commands here
                if (!command.hasIRC()) {
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
                        if (!database.isIRCAdmin(event.getActor())) {
                            event.sendReply("You do not have permission to do this!");
                            return;
                        }
                    }

                    IRCCommandExecutor commandExecutor = (IRCCommandExecutor)command.getIRCCommand().getCommandExecutor();
                    try {
                        commandExecutor.execute(new IRCCommandContext(event, command, mcobotParser, args, hidden, commandManager));
                    } catch (Exception e) {
                        event.sendReply("An error occurred: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
