package net.slimediamond.atom.command;

import net.slimediamond.atom.command.discord.DiscordCommand;
import net.slimediamond.atom.command.irc.IRCCommand;
import net.slimediamond.atom.command.telegram.TelegramCommand;

import java.util.ArrayList;

public interface CommandMetadata {
    /**
     * Get aliases for the command
     * the 0th entry is the "name"
     * @return command aliases
     */
    ArrayList<String> getAliases();

    /**
     * Get the command name
     * @return command name
     */
    default String getName() {
        return getAliases().get(0);
    }

    /**
     * Get command description
     * @return command description
     */
    String getDescription();

    /**
     * Get the command usage
     * @return command usage
     */
    String getCommandUsage();

    /**
     * Whether the command is admin only
     * @return whether the command is admin only
     */
    boolean isAdminOnly();

    /**
     * Get this command's children (subcommands)
     * @return {@link ArrayList} of subcommands
     */
    ArrayList<CommandMetadata> getChildren();

    /**
     * Get the Discord command
     * @return Discord command
     */
    DiscordCommand getDiscordCommand();

    /**
     * Get the IRC command
     * @return IRC command
     */
    IRCCommand getIRCCommand();

    /**
     * Get telegram command
     * @return Telegram command
     */
    TelegramCommand getTelegramCommand();

    /**
     * Whether the command is Discord-compatible
     * @return has discord
     */
    default boolean hasDiscord() {
        return getDiscordCommand() != null;
    }

    /**
     * Whether the command is IRC-compatible
     * @return has IRC
     */
    default boolean hasIRC() {
        return getIRCCommand() != null;
    }

    /**
     * Whether the command is Telegram-compatible
     * @return has Telegram
     */
    default boolean hasTelegram() {
        return getTelegramCommand() != null;
    }

    // this is a super dumb way of making a clone of the object
    default CommandBuilder toBuilder() {
        CommandBuilder commandBuilder = new CommandBuilder()
                .setAliases(getAliases())
                .setDescription(getDescription())
                .setUsage(getCommandUsage())
                .setAdminOnly(isAdminOnly())
                .setChildren(getChildren());

        if (this.hasDiscord()) {
            commandBuilder.discord(
                    new DiscordCommand(commandBuilder)
                            .setSlashCommand(getDiscordCommand().isSlashCommand())
                            .setExecutor(getDiscordCommand().getCommandExecutor())
                            .setArguments(getDiscordCommand().getArgs())
                            .setWhitelistedGuilds(getDiscordCommand().getWhitelistedGuilds())
            );
        }

        if (this.hasIRC()) {
            commandBuilder.irc(
                    new IRCCommand(commandBuilder)
                            .setWhitelistedChannels(getIRCCommand().getWhitelistedChannels())
                            .setExecutor(getIRCCommand().getCommandExecutor())
            );
        }

        return commandBuilder;
    }
}
