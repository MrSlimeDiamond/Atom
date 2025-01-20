package net.slimediamond.atom.command.irc;

import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandExecutor;
import net.slimediamond.atom.command.exceptions.CommandBuildException;

import java.util.function.Consumer;

public class IRCCommand {
    private CommandBuilder commandBuilder;
    private IRCCommandExecutor commandExecutor;

    public IRCCommand(CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    public IRCCommand setExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = (IRCCommandExecutor) commandExecutor;
        return this;
    }

    public IRCCommand setExecutor(Consumer<IRCCommandContext> executor) {
        this.commandExecutor = executor::accept;
        return this;
    }

    public IRCCommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public CommandBuilder then() {
        if (this.commandExecutor == null) {
            throw new CommandBuildException("Cannot create an IRC command without an executor");
        }
        return commandBuilder;
    }
}
