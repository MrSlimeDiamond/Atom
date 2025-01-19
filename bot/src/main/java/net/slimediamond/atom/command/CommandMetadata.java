package net.slimediamond.atom.command;

import java.util.ArrayList;

public interface CommandMetadata {
    /**
     * Get aliases for the command
     * the 0th entry is the "name"
     * @return command aliases
     */
    ArrayList<String> getAliases();

    /**
     * Get command description
     * @return command description
     */
    String getDescription();

    /**
     * Command platform
     * @return command platform
     */
    CommandPlatform getCommandPlatform();

    /**
     * Get the command usage
     * @return command usage
     */
    String getCommandUsage();

    /**
     * Whether the command is admin only
     * @return whether the command is admin only
     */
    default boolean isAdminOnly() {
        return false;
    }

    /**
     * Get this command's children (subcommands)
     * @return {@link ArrayList} of subcommands
     */
    ArrayList<CommandMetadata> getChildren();

    /**
     * Get the command executor
     * @return command executor
     */
    CommandExecutor getCommandExecutor();
}
