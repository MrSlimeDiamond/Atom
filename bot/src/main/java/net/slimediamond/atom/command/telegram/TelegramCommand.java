package net.slimediamond.atom.command.telegram;

import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandExecutor;
import net.slimediamond.atom.command.exceptions.CommandBuildException;

import java.util.function.Consumer;

public class TelegramCommand {
    private CommandBuilder commandBuilder;
    private TelegramCommandExecutor commandExecutor;

    public TelegramCommand(CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    public TelegramCommand setExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = (TelegramCommandExecutor) commandExecutor;
        return this;
    }

    public TelegramCommand setExecutor(Consumer<TelegramCommandContext> executor) {
        this.commandExecutor = executor::accept;
        return this;
    }

    public TelegramCommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public CommandBuilder then() {
        if (this.commandExecutor == null) {
            throw new CommandBuildException("Cannot create a Telegram command without an executor");
        }
        return commandBuilder;
    }
}
