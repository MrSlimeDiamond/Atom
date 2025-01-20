package net.slimediamond.atom.command.discord;

import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandExecutor;
import net.slimediamond.atom.command.discord.args.DiscordArgumentMetadata;
import net.slimediamond.atom.command.exceptions.CommandBuildException;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DiscordCommand {
    private CommandBuilder commandBuilder;
    private ArrayList<DiscordArgumentMetadata>args;
    private boolean slashCommand = false;
    private DiscordCommandExecutor commandExecutor;

    public DiscordCommand(CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    public DiscordCommand addArgument(DiscordArgumentMetadata discordArgumentMetadata) {
        args.add(discordArgumentMetadata);
        return this;
    }

    public DiscordCommand setSlashCommand(boolean slashCommand) {
        this.slashCommand = slashCommand;
        return this;
    }

    public DiscordCommand setExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = (DiscordCommandExecutor) commandExecutor;
        return this;
    }

    public DiscordCommand setExecutor(Consumer<DiscordCommandContext> executor) {
        this.commandExecutor = executor::accept;
        return this;
    }

    public DiscordCommandExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public CommandBuilder then() {
        if (this.commandExecutor == null) {
            throw new CommandBuildException("Cannot create a discord command without an executor");
        }
        return commandBuilder;
    }
}
