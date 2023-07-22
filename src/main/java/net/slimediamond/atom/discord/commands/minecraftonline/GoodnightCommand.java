package net.slimediamond.atom.discord.commands.minecraftonline;

import net.slimediamond.atom.irc.CommandEvent;
import net.slimediamond.atom.irc.annotations.Command;

public class GoodnightCommand {
    @Command(name = "goodnight", aliases = "gn", description = "Says goodnight!", usage = "goodnight")
    public void goodnightCommand(CommandEvent event) {
        // Special characters are funny
        event.getChannel().sendMessage("\u0001ACTION says goodnight\u0001");
    }
}
