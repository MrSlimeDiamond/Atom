package net.slimediamond.atom.inject.providers;

import com.google.inject.Provider;
import net.slimediamond.atom.command.CommandManager;

public class CommandManagerProvider implements Provider<CommandManager> {
    private static CommandManager instance;

    @Override
    public CommandManager get() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }
}
