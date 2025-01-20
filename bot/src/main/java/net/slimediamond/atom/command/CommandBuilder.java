package net.slimediamond.atom.command;

import net.slimediamond.atom.command.discord.DiscordCommand;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.command.exceptions.CommandBuildException;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;

import java.util.ArrayList;

public class CommandBuilder {
    private final ArrayList<String> aliases = new ArrayList<>();
    private String description;
    private String usage;
    private boolean adminOnly = false;
    private final ArrayList<CommandMetadata> children = new ArrayList<>();
    private DiscordCommand discordCommand;
    private IRCCommandExecutor ircExecutor; // FIXME

    /**
     * Add one or more aliases to the command.
     * The first alias will be the command's "name"
     * @param aliases
     * @return this
     */
    public CommandBuilder addAliases(String... aliases) {
        for (String alias : aliases) {
            this.aliases.add(alias);
        }
        return this;
    }

    /**
     * Set the command's description
     * @param description
     * @return this
     */
    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set the usage for the command
     * @param usage
     * @return this
     */
    public CommandBuilder setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Set whether the command is admin only
     * @param adminOnly
     * @return this
     */
    public CommandBuilder setAdminOnly(boolean adminOnly) {
        this.adminOnly = adminOnly;
        return this;
    }

    public DiscordCommand discord() {
        this.discordCommand = new DiscordCommand(this);
        return this.discordCommand;
    }

    /**
     * Add a child command (subcommand)
     * @param metadata
     * @return this
     */
    public CommandBuilder addChild(CommandMetadata metadata) {
        children.add(metadata);
        return this;
    }

    public CommandMetadata build() {
        // Make sure everything is hunky-dory
        if (aliases.isEmpty()) {
            throw new CommandBuildException("Cannot create a command without any aliases");
        } else if (description == null) {
            throw new CommandBuildException("Cannot create a command without a description");
        } else if (usage == null) {
            throw new CommandBuildException("Cannot create a command without any usage hint");
        } else if (discordCommand == null && ircExecutor == null) {
            throw new CommandBuildException("Cannot create a command without an executor");
        } else {
            // I don't know if this is the best way of doing it
            return new CommandMetadata() {
                @Override
                public ArrayList<String> getAliases() {
                    return aliases;
                }

                @Override
                public String getDescription() {
                    return description;
                }

                @Override
                public String getCommandUsage() {
                    return usage;
                }

                @Override
                public boolean isAdminOnly() {
                    return adminOnly;
                }

                @Override
                public ArrayList<CommandMetadata> getChildren() {
                    return children;
                }

                @Override
                public DiscordCommandExecutor getDiscordCommandExecutor() {
                    return discordCommand.getCommandExecutor();
                }

                @Override
                public IRCCommandExecutor getIRCCommandExecutor() {
                    return ircExecutor;
                }
            };
        }
    }
}
