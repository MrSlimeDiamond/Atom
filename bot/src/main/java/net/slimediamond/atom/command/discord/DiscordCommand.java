package net.slimediamond.atom.command.discord;

import net.slimediamond.atom.command.CommandBuilder;
import net.slimediamond.atom.command.CommandExecutor;
import net.slimediamond.atom.command.discord.args.DiscordArgumentMetadata;
import net.slimediamond.atom.command.exceptions.CommandBuildException;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DiscordCommand {
    private CommandBuilder commandBuilder;
    private ArrayList<DiscordArgumentMetadata> args = new ArrayList<>();
    private boolean slashCommand = true;
    private DiscordCommandExecutor commandExecutor;
    private ArrayList<Long> whitelistedGuilds = new ArrayList<>();

    public DiscordCommand(CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    public DiscordCommand addArgument(DiscordArgumentMetadata discordArgumentMetadata) {
        int index = discordArgumentMetadata.getId();

        // Ensure the list is large enough to hold the element at the desired index
        while (args.size() <= index) {
            args.add(null); // Add null placeholders if needed
        }

        // Insert the discordArgumentMetadata at the correct index
        args.set(index, discordArgumentMetadata);
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

    public DiscordCommand addWhitelistedGuilds(long... guilds) {
        for (long guild : guilds) {
            whitelistedGuilds.add(guild);
        }

        return this;
    }

    public DiscordCommand addWhitelistedGuilds(Long[] guilds) {
        for (long guild : guilds) {
            whitelistedGuilds.add(guild);
        }
        return this;
    }

    // TODO: make this return an actual Guild object
    // TOOD: hard to do atm due to the lack of the jda object
    public ArrayList<Long> getWhitelistedGuilds() {
        return this.whitelistedGuilds;
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

    public ArrayList<DiscordArgumentMetadata> getArgs() {
        return args;
    }

    public boolean isSlashCommand() {
        return slashCommand;
    }
}
