package net.slimediamond.atom.command;

public interface CommandContext {
    /**
     * Get the command sender
     * @return command sender
     */
    CommandSender getSender();

    /**
     * Get command arguments
     * @return command arguments
     */
    String[] getArgs();

    /**
     * Get the command platform
     * @return command platform
     */
    CommandPlatform getPlatform();

    /**
     * Get command metadata
     * @return command metadata object
     */
    CommandMetadata getCommandMetadata();

    /**
     * Reply to the command
     * @param message
     */
    void reply(String message);
}
