package net.slimediamond.atom.command;

import java.util.ArrayList;

public class CommandManager {
    private ArrayList<CommandMetadata> commands = new ArrayList<>();

    public void register(CommandMetadata metadata) {
        System.out.println("Registering command: " + metadata.getAliases().get(0));
        this.commands.add(metadata);
        // TODO: Specific stuff for Discord. (slash commands)
    }

    public ArrayList<CommandMetadata> getCommands() {
        return this.commands;
    }
}
