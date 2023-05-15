package net.zenoc.atom.ircbot;

import net.engio.mbassy.listener.Handler;
import net.zenoc.atom.Atom;
import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.reference.IRCReference;
import net.zenoc.atom.services.IRC;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.helper.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
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
                .map(method -> new IRCCommand(method.getAnnotation(Command.class), command, method))
                .forEach(commands::add);
    }
    @Handler
    public void onMessage(ChannelMessageEvent event) {
        McObotMessageParser mcobotParser =  new McObotMessageParser(event.getActor(), event.getMessage());
        if (
                event.getMessage().startsWith(IRCReference.prefix) ||
                event.getMessage().startsWith("#" + IRCReference.prefix) && event.getChannel().getName().equals("#minecraftonline") ||
                mcobotParser.isCommandMessage()
        )
        {
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
                boolean correctCommand = command.getCommand().name().equals(commandName.get());
                for (String commandAlias : command.getCommand().aliases()) {
                    if (commandAlias.equals(commandName.get())) {
                        correctCommand = true;
                        break;
                    }
                }
                if (correctCommand) {
                    boolean shouldExecute = false;
                    if (command.getCommand().whitelistedChannels().length > 1) {
                        for (String whitelistedChannel : command.getCommand().whitelistedChannels()) {
                            if (!whitelistedChannel.equals(event.getChannel().getName())) continue;
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
        new Thread(
                () -> {
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
