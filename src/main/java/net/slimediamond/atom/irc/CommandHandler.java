package net.slimediamond.atom.irc;

import net.engio.mbassy.listener.Handler;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.reference.IRCReference;
import net.slimediamond.atom.services.system.GetServiceProcessor;
import net.slimediamond.atom.Atom;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class CommandHandler {
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    ArrayList<IRCCommand> commands = new ArrayList<>();
    public CommandHandler() {
        IRC.client.getEventManager().registerEventListener(this);
    }

    public void registerCommand(Object command) {
        Arrays.stream(command.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Command.class))
                .map(method -> {
                    // Allow for @GetService
                    GetServiceProcessor.processAnnotations(command);
                    return new IRCCommand(method.getAnnotation(Command.class), command, method);
                })
                .forEach(commands::add);
    }
    @Handler
    public void onMessage(ChannelMessageEvent event) {
        McObotMessageParser mcobotParser =  new McObotMessageParser(event.getActor(), event.getMessage());
        if (
                event.getMessage().startsWith(IRCReference.prefix) ||
                event.getMessage().startsWith("#" + IRCReference.prefix) && event.getChannel().getName().equals("#minecraftonline") ||
                mcobotParser.isCommandMessage()
        ) {
            // It's a command
            boolean hidden = event.getMessage().startsWith("#" + IRCReference.prefix);
            String[] args = event.getMessage().split(IRCReference.prefix);
            AtomicReference<String> commandName = new AtomicReference<>();

            if (mcobotParser.isChatMessage()) {
                // Try to parse the command from an ingame chat message
                commandName.set(args[1].split(" ")[0]);
            } else {
                commandName.set(args[1].split(" ")[0].replace("#", ""));
            }

            log.debug(commandName.get());

            commands.forEach(command -> {
                log.debug(command.getCommand().name());
                boolean correctCommand = command.getCommand().name().equalsIgnoreCase(commandName.get());
                for (String commandAlias : command.getCommand().aliases()) {
                    if (commandAlias.equalsIgnoreCase(commandName.get())) {
                        correctCommand = true;
                        break;
                    }
                }
                if (correctCommand) {
                    boolean shouldExecute = false;
                    if (command.getCommand().whitelistedChannels().length > 1) {
                        for (String whitelistedChannel : command.getCommand().whitelistedChannels()) {
                            if (!whitelistedChannel.equalsIgnoreCase(event.getChannel().getName())) continue;
                            shouldExecute = true;
                            break;
                        }
                    } else {
                        shouldExecute = true;
                    }
                    if (!shouldExecute) return;
                    try {
                        if (command.getCommand().adminOnly() && !Atom.database.isIRCAdmin(event.getActor())) {
                            event.getChannel().sendMessage("You do not have permission to do this.");
                            return;
                        }
                    } catch (SQLException e) {
                        event.getChannel().sendMessage("SQLException! Is the database down? Tell an admin!");
                        throw new RuntimeException(e);
                    }
                    executeCommand(command, event, hidden);
                }
            });
        }
    }

    public void executeCommand(IRCCommand command, ChannelMessageEvent event, boolean hidden) {
        CommandEvent commandEvent = new CommandEvent(event, hidden);

        Method method = command.getMethod();

        log.debug("Invoking!");

        // new thread for commands for more fast
        new Thread(() -> {
                    Thread.currentThread().setName("Discord Command Executor");
                    try {
                        method.invoke(command.getCaller(), commandEvent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e.getCause().equals(new SQLException())) {
                            event.getChannel().sendMessage("SQLException! Is the database down? Tell an admin!");
                        } else {
                            event.getChannel().sendMessage("An error occurred!");
                        }
                    }
                }
        ).start();
    }
}
