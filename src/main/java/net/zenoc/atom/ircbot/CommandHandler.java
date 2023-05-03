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
        if (event.getMessage().startsWith(IRCReference.prefix) || event.getMessage().startsWith("#" + IRCReference.prefix) && event.getChannel().getName().equals("#minecraftonline")) {
            // It's a command
            boolean hidden = event.getMessage().startsWith("#" + IRCReference.prefix);
            String[] args = event.getMessage().split(IRCReference.prefix);
            String commandName = args[1].split(" ")[0].replace("#", "");
            log.debug(commandName);

            commands.forEach(command -> {
                log.debug(command.getCommand().name());
                if (command.getCommand().name().equals(commandName) || command.getCommand().name().contains(commandName)) {
                    try {
                        if (command.getCommand().adminOnly() && !Atom.database.isIRCAdmin(event.getActor())) {
                            event.getChannel().sendMessage("You do not have permission to do this.");
                            return;
                        }
                    } catch (SQLException e) {
                        event.getChannel().sendMessage("SQLException! Is the database down? Tell an admin!");
                        throw new RuntimeException(e);
                    }
                    try {
                        executeCommand(command, event, hidden);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public void executeCommand(IRCCommand command, ChannelMessageEvent event, boolean hidden) throws InvocationTargetException, IllegalAccessException {
        CommandEvent commandEvent = new CommandEvent(event, hidden);

        Method method = command.getMethod();

        log.debug("Invoking");

        method.invoke(command.getCaller(), commandEvent);
    }
}
