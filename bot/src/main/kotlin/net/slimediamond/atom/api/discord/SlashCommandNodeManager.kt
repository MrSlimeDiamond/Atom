package net.slimediamond.atom.api.discord

import net.slimediamond.atom.api.command.CommandNode

/**
 * The slash command manager for [CommandNode]s.
 *
 * Only [CommandNode]s support slash commands
 */
interface SlashCommandNodeManager {

    /**
     * Register a slash command
     *
     * @param command The command to register, in the form
     * of a slash command
     */
    suspend fun register(command: CommandNode)

    /**
     * Remove/unregister all slash commands
     */
    suspend fun removeAll(): Int

    /**
     * Reload all slash commands
     */
    suspend fun reload()

}