package net.slimediamond.atom.command.discord;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.slimediamond.atom.command.*;
import net.slimediamond.atom.command.discord.args.DiscordArgumentMetadata;
import net.slimediamond.atom.command.exceptions.ArgumentException;

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

    public <T> T getArgument(int id) {
        if (interactionEvent.isTextCommand()) {
            // Take the arguments, then grab whichever index it's at
            DiscordArgumentMetadata arg = metadata.getDiscordCommand().getArgs().get(id);
            if (arg.getOptionType() == OptionType.STRING) {
                return (T) args[id];
            }
        }

        return null; // TODO: Support everything
    }

    public <T> T getArgument(String name) {
        for (DiscordArgumentMetadata arg : metadata.getDiscordCommand().getArgs()) {
            if (arg.getName().equalsIgnoreCase(name)) {
                return getArgument(arg.getId());
            }
        }
        throw new ArgumentException("Could not find an argument with that name");
    }
}
