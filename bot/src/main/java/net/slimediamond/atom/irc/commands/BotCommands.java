package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.irc.IRCCommand;
import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.irc.CommandEvent;
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
        StringBuilder stringBuilder = new StringBuilder("My commands are: ");
        for (IRCCommand command : event.getCommandHandler().getCommands()) {
            stringBuilder.append(command);
        }

        event.reply(stringBuilder.toString());
    }
}
