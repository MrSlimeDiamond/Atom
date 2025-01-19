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

    /**
     * Get the command manager
     * @return command manager
     */
    CommandManager getCommandManager();

    /**
     * Get the desired username of the sender.
     *
     * If ingame on MCO, this will be the player's username.
     * Or if they are in Discord, Telegram, etc.
     *
     * Otherwise it's just the sender name
     *
     * @return desired command sender name
     */
    String getDesiredCommandUsername();
}
