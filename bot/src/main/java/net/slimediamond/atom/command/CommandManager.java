package net.slimediamond.atom.command;

import net.slimediamond.atom.services.system.GetServiceProcessor;

import java.util.ArrayList;

public class CommandManager {
    private ArrayList<CommandMetadata> commands = new ArrayList<>();

    public void register(CommandMetadata metadata) {
        System.out.println("Registering command: " + metadata.getAliases().get(0));
        // allow @GetService

        if (metadata.hasIRC()) {
            GetServiceProcessor.processAnnotations(metadata.getIRCCommand().getCommandExecutor());
        }

        if (metadata.hasDiscord()) {
            GetServiceProcessor.processAnnotations(metadata.getDiscordCommand().getCommandExecutor());
        }

        this.commands.add(metadata);
        // TODO: Specific stuff for Discord. (slash commands)
    }

    public ArrayList<CommandMetadata> getCommands() {
        return this.commands;
    }
}
