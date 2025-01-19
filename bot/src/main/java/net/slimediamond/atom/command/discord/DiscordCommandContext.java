package net.slimediamond.atom.command.discord;

import net.slimediamond.atom.command.*;

public class DiscordCommandContext implements CommandContext {
    private AtomDiscordCommandEvent interactionEvent;
    private CommandMetadata metadata;
    private String[] args;
    private CommandManager commandManager;

    public DiscordCommandContext(AtomDiscordCommandEvent interactionEvent, CommandMetadata metadata, String[] args, CommandManager commandManager) {
        this.interactionEvent = interactionEvent;
        this.metadata = metadata;
        this.args = args;
        this.commandManager = commandManager;
    }

    @Override
    public CommandSender getSender() {
        return new DiscordCommandSender(interactionEvent.getUser().getEffectiveName(), interactionEvent.getUser());
    }

    @Override
    public String[] getArgs() {
        return this.args;
    }

    @Override
    public CommandPlatform getPlatform() {
        return CommandPlatform.DISCORD;
    }

    @Override
    public CommandMetadata getCommandMetadata() {
        return this.metadata;
    }

    @Override
    public void reply(String message) {
        interactionEvent.reply(message);
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public String getDesiredCommandUsername() {
        // TODO
        return getSender().getName();
    }
}
