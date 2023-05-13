package net.zenoc.atom.ircbot.commands;

import net.zenoc.atom.ircbot.annotations.Command;
import net.zenoc.atom.ircbot.CommandEvent;
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
