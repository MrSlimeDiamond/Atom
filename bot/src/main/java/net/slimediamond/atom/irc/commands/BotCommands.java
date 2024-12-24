package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.irc.IRCCommand;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.irc.CommandEvent;

import java.util.StringJoiner;

public class BotCommands {
    @Command(
            name = "ping",
            description = "Respond with pong",
            usage = "ping"
    )
    public void pingCommand(CommandEvent event) {
        event.reply("Pong!");
    }

    @Command(
            name = "help",
            description = "Get help!",
            usage = "help"
    )
    public void helpCommand(CommandEvent event) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (IRCCommand command : event.getCommandHandler().getCommands()) {
            // ignoring admin-only commands
            // TODO: Show admins admin-only commands
            if(!command.getCommand().adminOnly()) {
                stringJoiner.add(command.getCommand().name());
            }
        }

        event.reply("My commands are: " + stringJoiner);
    }
}
