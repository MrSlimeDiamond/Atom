package net.slimediamond.atom.command;

import net.slimediamond.atom.command.discord.DiscordCommand;
import net.slimediamond.atom.command.exceptions.CommandBuildException;
import net.slimediamond.atom.command.irc.IRCCommand;
import net.slimediamond.atom.command.telegram.TelegramCommand;

import java.util.ArrayList;

public class CommandBuilder {
    private ArrayList<String> aliases = new ArrayList<>();
    private String description;
    private String usage;
    private boolean adminOnly = false;
    private ArrayList<CommandMetadata> children = new ArrayList<>();
    private DiscordCommand discordCommand;
    private IRCCommand ircCommand;
    private TelegramCommand telegramCommand;
    private CommandBuilder commandBuilder = this;

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

    public CommandBuilder setAliases(ArrayList<String> aliases) {
        this.aliases = aliases;
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
        if (this.discordCommand == null) {
            this.discordCommand = new DiscordCommand(this);
        }
        return this.discordCommand;
    }

    public CommandBuilder discord(DiscordCommand discordCommand) {
        this.discordCommand = discordCommand;
        return this;
    }

    public IRCCommand irc() {
        if (this.ircCommand == null) {
            this.ircCommand = new IRCCommand(this);
        }
        return this.ircCommand;
    }

    public CommandBuilder irc(IRCCommand ircCommand) {
        this.ircCommand = ircCommand;
        return this;
    }

    public TelegramCommand telegram() {
        if (this.telegramCommand == null) {
            this.telegramCommand = new TelegramCommand(this);
        }
        return this.telegramCommand;
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

    public CommandBuilder setChildren(ArrayList<CommandMetadata> children) {
        this.children = children;
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
        } else if (discordCommand == null && ircCommand == null && telegramCommand == null) {
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
                public DiscordCommand getDiscordCommand() {
                    if (discordCommand != null) {
                        return discordCommand;
                    }
                    return null;
                }

                @Override
                public IRCCommand getIRCCommand() {
                    if (ircCommand != null) {
                        return ircCommand;
                    }
                    return null;
                }

                @Override
                public TelegramCommand getTelegramCommand() {
                    if (telegramCommand != null) {
                        return telegramCommand;
                    }
                    return null;
                }
            };
        }
    }
}
