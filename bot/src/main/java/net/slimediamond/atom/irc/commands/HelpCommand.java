package net.slimediamond.atom.irc.commands;

import net.slimediamond.atom.command.CommandContext;
import net.slimediamond.atom.command.CommandMetadata;
import net.slimediamond.atom.command.irc.IRCCommand;
import net.slimediamond.atom.command.irc.IRCCommandContext;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringJoiner;

public class HelpCommand implements IRCCommandExecutor {
    public void execute(IRCCommandContext ctx) throws SQLException {
        boolean isAdmin = ctx.getSender().isAdmin();

        StringJoiner stringJoiner = new StringJoiner(", ");
        ctx.getCommandManager().getCommands().stream()
                .filter(CommandMetadata::hasIRC)
                .filter(command -> !command.isAdminOnly() || isAdmin) // Check admin-only commands
                .filter(command -> {
                    IRCCommand ircCommand = command.getIRCCommand();
                    return ircCommand.getWhitelistedChannels().isEmpty() ||
                            ircCommand.getWhitelistedChannels().contains(ctx.getChannelName());
                })
                .map(CommandMetadata::getName) // Get the command name
                .forEach(stringJoiner::add);



        ctx.reply("My commands are: " + stringJoiner);
    }
}
