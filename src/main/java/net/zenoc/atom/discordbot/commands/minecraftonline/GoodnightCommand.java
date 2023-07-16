package net.zenoc.atom.discordbot.commands.minecraftonline;

import net.zenoc.atom.ircbot.CommandEvent;
import net.zenoc.atom.ircbot.annotations.Command;

public class GoodnightCommand {
    @Command(name = "goodnight", aliases = "gn", description = "Says goodnight!", usage = "goodnight")
    public void goodnightCommand(CommandEvent event) {
        // Special characters are funny
        event.getChannel().sendMessage("\u0001ACTION says goodnight\u0001");
    }
}
