package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.irc.annotations.Command;
import net.slimediamond.atom.irc.CommandEvent;
public class PingCommand {
    @Command(
            name = "ping",
            description = "Respond with pong",
            usage = "ping"
    )
    public void pingCommand(CommandEvent event) {
        event.reply("Pong!");
    }
}
