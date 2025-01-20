package net.slimediamond.atom.command;

import net.slimediamond.atom.command.discord.DiscordCommand;
import net.slimediamond.atom.command.discord.DiscordCommandExecutor;
import net.slimediamond.atom.command.irc.IRCCommand;
import net.slimediamond.atom.command.irc.IRCCommandExecutor;

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
}
